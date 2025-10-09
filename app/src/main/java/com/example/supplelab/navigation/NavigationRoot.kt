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
import com.example.supplelab.presentation.screens.admin.AdminPanelScreen
import com.example.supplelab.presentation.screens.authentication.AuthScreen
import com.example.supplelab.presentation.screens.home.HomeScreen
import com.example.supplelab.presentation.screens.profile.ProfileScreen
import com.example.supplelab.util.RequestState
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

@Serializable
object AdminPanelScreenKey: NavKey




@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
){
    val customerRepository: CustomerRepository = koinInject()
    val isUserAuthenticated = FirebaseAuth.getInstance().currentUser != null
    
    var startDestination by remember { mutableStateOf<NavKey?>(null) }
    
    // Determine start destination based on auth and profile completion status
    LaunchedEffect(Unit) {
        startDestination = if (!isUserAuthenticated) {
            AuthScreenKey
        } else {
            // User is authenticated, check if profile is complete
            val customer = customerRepository.readCustomerFlow().firstOrNull()
            if (customer is RequestState.Success && !customer.data.profileComplete) {
                ProfileScreenKey
            } else {
                HomeScreenKey
            }
        }
    }
    
    // Show loading until start destination is determined
    if (startDestination == null) {
        return
    }
    
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
                            },
                            onAdminPanelClick = {
                                backStack.add(AdminPanelScreenKey)
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
                is AdminPanelScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        AdminPanelScreen(
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            }
                        )
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}