package com.example.supplelab.presentation.screens.authentication

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme // Added import
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.supplelab.presentation.componenets.GoogleButton
import com.example.supplelab.presentation.componenets.sign_in.GoogleAuthUiClient
import com.example.supplelab.presentation.componenets.sign_in.SignInViewModel
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.SurfaceBrand
import com.example.supplelab.ui.theme.SurfaceError
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary
import com.example.supplelab.ui.theme.TextWhite
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: SignInViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    var loading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isSuccess by remember { mutableStateOf(false) } // Add this line

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(24.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSuccess) SurfaceBrand else SurfaceError,
                    contentColor = if (isSuccess) TextPrimary else TextWhite
                )
            }
        }
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
                            viewModel.onSignInResult(signInResult)
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
                    isSuccess = true // Set to true for success
                    snackbarHostState.showSnackbar("Sign-in successful")
                    viewModel.resetState()
                } else if (state.signInError != null) {
                    isSuccess = false // Set to false for error
                    snackbarHostState.showSnackbar("Sign-in failed: ${state.signInError}")
                    viewModel.resetState()
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
}
