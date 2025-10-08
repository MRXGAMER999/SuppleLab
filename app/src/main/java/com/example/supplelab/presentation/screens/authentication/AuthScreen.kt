package com.example.supplelab.presentation.screens.authentication

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.supplelab.presentation.componenets.GoogleButton
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.componenets.sign_in.GoogleAuthUiClient
import com.example.supplelab.presentation.componenets.sign_in.SignInViewModel
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(
    onNavigateToHome:() -> Unit,
    onNavigateToProfile:() -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = koinViewModel()
    val signInViewModel: SignInViewModel = koinViewModel()
    val state by signInViewModel.state.collectAsState()
    var loading by remember { mutableStateOf(false) }
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Surface
        ){ paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {


                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data
                        if (data != null) {
                            coroutineScope.launch {
                                val client = GoogleAuthUiClient(
                                    context = context,
                                    oneTapClient = Identity.getSignInClient(context)
                                )
                                val signInResult = client.signInWithIntent(data)
                                signInViewModel.onSignInResult(signInResult)
                                loading = false
                            }
                        } else {
                            loading = false
                        }
                    } else {
                        loading = false
                    }
                }

                LaunchedEffect(state.isSignInSuccessful, state.signInError) {
                    if (state.isSignInSuccessful) {
                        val currentUser = signInViewModel.getCurrentUser()

                        authViewModel.createCustomer(
                            user = currentUser,
                            onSuccess = { isNewUser ->
                                coroutineScope.launch {
                                    isSuccess = true
                                    notificationMessage = "Sign-in successful"
                                    showNotification = true
                                }
                                signInViewModel.resetState()
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(800)
                                    if (isNewUser) {
                                        // First time user - navigate to profile screen
                                        onNavigateToProfile()
                                    } else {
                                        // Existing user - navigate to home screen
                                        onNavigateToHome()
                                    }
                                }
                            },
                            onError = { error ->
                                coroutineScope.launch {
                                    isSuccess = false
                                    notificationMessage = "Error creating customer: $error"
                                    showNotification = true
                                    signInViewModel.resetState()
                                }
                            }
                        )
                    } else if (state.signInError != null) {
                        isSuccess = false
                        notificationMessage = "Sign-in failed: ${state.signInError}"
                        showNotification = true
                        signInViewModel.resetState()
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "SUPPLELAB",
                        textAlign = TextAlign.Center,
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextSecondary
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = "Sign in to continue",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextPrimary
                    )

                }

                GoogleButton(
                    loading = loading,
                    onClick = {
                        coroutineScope.launch {
                            loading = true
                            val client = GoogleAuthUiClient(
                                context = context,
                                oneTapClient = Identity.getSignInClient(context)
                            )
                            val intentSender = client.signIn()
                            if (intentSender != null) {
                                launcher.launch(
                                    IntentSenderRequest.Builder(intentSender).build()
                                )
                            } else {
                                loading = false
                            }
                        }
                    }
                )
            }
        }


        TopNotification(
            visible = showNotification,
            message = notificationMessage,
            isSuccess = isSuccess,
            onDismiss = { showNotification = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
