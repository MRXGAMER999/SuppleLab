package com.example.supplelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.presentation.screens.authentication.AuthScreen
import com.example.supplelab.presentation.screens.home.HomeScreen
import com.example.supplelab.presentation.screens.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject


@Serializable
object AuthScreenKey: NavKey

@Serializable
object HomeScreenKey: NavKey

@Serializable
object ProfileScreenKey: NavKey



@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
){
    val customerRepository: CustomerRepository = koinInject()
    val isUserAuthenticated = remember {
        mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
    }
    
    var startDestination by remember { mutableStateOf<NavKey?>(null) }
    
    // Check profile completion status for authenticated users
    LaunchedEffect(isUserAuthenticated.value) {
        startDestination = if (isUserAuthenticated.value) {
            // User is authenticated, check if profile is complete
            val customerData = customerRepository.readCustomerFlow().firstOrNull()
            if (customerData != null && customerData.isSuccess()) {
                val customer = customerData.getSuccessData()
                if (customer.profileComplete) {
                    HomeScreenKey
                } else {
                    ProfileScreenKey
                }
            } else {
                HomeScreenKey // Fallback to home if we can't check
            }
        } else {
            AuthScreenKey
        }
    }
    
    // Wait until we determine the start destination
    if (startDestination == null) return
    
    val backStack = rememberNavBackStack(startDestination!!)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when(key){
                is AuthScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        AuthScreen(
                            onNavigateToHome = {
                                backStack.remove(key)
                                backStack.add(HomeScreenKey)
                            },
                            onNavigateToProfile = {
                                backStack.remove(key)
                                backStack.add(ProfileScreenKey)
                            }
                        )
                    }
                }
                is HomeScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        HomeScreen(
                            onSignOut = {
                                backStack.clear()
                                backStack.add(AuthScreenKey)
                            },
                            onProfileClick = {
                                backStack.add(ProfileScreenKey)
                            }
                        )
                    }
                }
                is ProfileScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        ProfileScreen(
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            },
                            onNavigateToHome = {
                                backStack.remove(key)
                                backStack.add(HomeScreenKey)
                            }
                        )
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}