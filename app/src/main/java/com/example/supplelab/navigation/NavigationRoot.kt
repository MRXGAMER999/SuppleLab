package com.example.supplelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.presentation.screens.authentication.AuthScreen
import kotlinx.serialization.Serializable


@Serializable
object AuthScreenKey: NavKey



@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
){
    val backStack = rememberNavBackStack(AuthScreenKey)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when(key){
                is AuthScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        AuthScreen()
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}