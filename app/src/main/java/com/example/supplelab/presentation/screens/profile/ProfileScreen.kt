package com.example.supplelab.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.presentation.componenets.ErrorCard
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.presentation.componenets.TopNotification
import com.example.supplelab.presentation.profile.LoadCard
import com.example.supplelab.presentation.profile.ProfileForm
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.util.DisplayResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigationIconClicked: () -> Unit,
){
    val viewModel: ProfileViewModel = koinViewModel()
    val screenState = viewModel.screenState
    val screenReady = viewModel.screenReady
    val coroutineScope = rememberCoroutineScope()
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    val isFormValid = viewModel.isFormValid

    LaunchedEffect(showNotification) {
        if (showNotification) {
            delay(2000)
            showNotification = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Surface,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Profile",
                            fontFamily = BebasNeueFont(),
                            fontSize = FontSize.LARGE,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onNavigationIconClicked()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.back_arrow),
                                contentDescription = "Menu Icon",
                                tint = IconPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Surface,
                        scrolledContainerColor = Surface,
                        navigationIconContentColor = IconPrimary,
                        titleContentColor = TextPrimary,
                        actionIconContentColor = IconPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .padding(
                        top = 12.dp,
                        bottom = 24.dp)
            ) {
                screenReady.DisplayResult(
                    onLoading = {
                        LoadCard(modifier = Modifier.fillMaxSize())
                    },
                    onSuccess = { state ->
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            ProfileForm(
                                modifier = Modifier.weight(1f),
                                country = screenState.country,
                                onCountrySelect = viewModel::updateCountry,
                                firstName = screenState.firstName,
                                onFirstNameChange = viewModel::updateFirstName,
                                lastName = screenState.lastName,
                                onLastNameChange = viewModel::updateLastName,
                                email = screenState.email,
                                city = screenState.city,
                                onCityChange = viewModel::updateCity,
                                postalCode = screenState.postalCode,
                                onPostalCodeChange = viewModel::updatePostalCode,
                                address = screenState.address,
                                onAddressChange = viewModel::updateAddress,
                                phoneNumber = screenState.phoneNumber?.number,
                                onPhoneNumberChange = viewModel::updatePhoneNumber,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PrimaryButton(
                                text = "Update",
                                icon = R.drawable.check,
                                enabled = isFormValid,
                                onClick = {
                                    viewModel.UpdateCustomer(
                                        onSuccess = {
                                            coroutineScope.launch {
                                                isSuccess = true
                                                notificationMessage = "Profile updated successfully!"
                                                showNotification = true
                                            }
                                        },
                                        onError = {message ->
                                            coroutineScope.launch {
                                                isSuccess = false
                                                notificationMessage = "Update failed: $message"
                                                showNotification = true
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    },
                    onError = {message ->
                        ErrorCard(
                            modifier = Modifier.fillMaxSize(),
                            message = message,
                            fontSize = FontSize.REGULAR
                        )
                    }
                )

            }
        }

        // Top notification banner
        TopNotification(
            visible = showNotification,
            message = notificationMessage,
            isSuccess = isSuccess,
            onDismiss = { showNotification = false },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}