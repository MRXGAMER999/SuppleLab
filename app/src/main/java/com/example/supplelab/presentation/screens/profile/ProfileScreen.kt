package com.example.supplelab.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.supplelab.R
import com.example.supplelab.domain.model.Country
import com.example.supplelab.presentation.componenets.PrimaryButton
import com.example.supplelab.presentation.profile.ProfileForm
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.IconPrimary
import com.example.supplelab.ui.theme.Surface
import com.example.supplelab.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigationIconClicked: () -> Unit,
){
    var country by remember { mutableStateOf(Country.Egypt) }
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
            ProfileForm(
                modifier = Modifier.weight(1f),
                country = country,
                onCountrySelect = {
                    country = it
                },
                firstName = "",
                onFirstNameChange = {},
                lastName = "",
                onLastNameChange = {},
                email = "",
                city = "",
                onCityChange = {},
                postalCode = null,
                onPostalCodeChange = {},
                address = "",
                onAddressChange = {},
                phoneNumber = null,
                onPhoneNumberChange = {},
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = "Update",
                icon = R.drawable.check,
                onClick = {}
            )
        }
    }

}