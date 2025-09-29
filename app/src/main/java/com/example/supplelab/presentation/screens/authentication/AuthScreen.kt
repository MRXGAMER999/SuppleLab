package com.example.supplelab.presentation.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme // Added import
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.supplelab.presentation.componenets.GoogleButton
import com.example.supplelab.ui.theme.BebasNeueFont
import com.example.supplelab.ui.theme.FontSize
import com.example.supplelab.ui.theme.TextPrimary
import com.example.supplelab.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Added this line
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
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
                loading = false,
                onClick = { }
            )
        }
    }
}
