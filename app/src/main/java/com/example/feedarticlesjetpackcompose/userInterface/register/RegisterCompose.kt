package com.example.feedarticlesjetpackcompose.userInterface.register

import MyTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.navigation.Screen
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose

@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerVM: RegisterViewModel = hiltViewModel()
) {
    val localContext = LocalContext.current


    LaunchedEffect(Unit) {
        registerVM.messageFlow.collect { msg ->
            Toast.makeText(localContext, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        registerVM.navigateToMainFlow.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
    }


    RegisterContent(
        onSubmitRegistration = { userName, userPass, confirmPass ->
            registerVM.performRegistration(userName, userPass, confirmPass)
        },
        onNavigateToLogin = {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    )
}

@Composable
fun RegisterContent(
    onSubmitRegistration: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var userPass by remember { mutableStateOf("") }
    var userConfirmPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 48.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.please_register),
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BlueJose),
            modifier = Modifier.padding(bottom = 96.dp)
        )

        MyTextField(
            value = userName,
            onValueChange = { userName = it },
            hint = stringResource(R.string.login),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        MyTextField(
            value = userPass,
            onValueChange = { userPass = it },
            hint = stringResource(R.string.password),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth(),
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        MyTextField(
            value = userConfirmPass,
            onValueChange = { userConfirmPass = it },
            hint = stringResource(R.string.confirm_password),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth(),
            isPassword = true
        )
        Spacer(modifier = Modifier.height(164.dp))

        Button(
            onClick = { onSubmitRegistration(userName, userPass, userConfirmPass) },
            colors = ButtonDefaults.buttonColors(containerColor = BlueJose),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = stringResource(R.string.register))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.already_an_account_login),
            color = BlueJose,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserRegistrationPreview() {
    RegisterContent(onSubmitRegistration = { _, _, _ -> }, onNavigateToLogin = {})
}
