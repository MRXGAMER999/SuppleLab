package com.example.supplelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.presentation.screens.authentication.AuthScreen
import com.example.supplelab.presentation.screens.home.HomeScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable


@Serializable
object AuthScreenKey: NavKey

@Serializable
object HomeScreenKey: NavKey



@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
){
    val isUserAuthenticated = remember {
        mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
    }

    val startDestination = if (isUserAuthenticated.value) {
        HomeScreenKey
    } else {
        AuthScreenKey
    }
    val backStack = rememberNavBackStack(startDestination)

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
                                backStack.remove(key)
                                backStack.add(AuthScreenKey)
                            }
                        )
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}