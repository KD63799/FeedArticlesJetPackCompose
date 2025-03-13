package com.example.feedarticlesjetpackcompose.userInterface.login

import MyTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
fun LoginScreen(
    navController: NavHostController,
    vm: LoginViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        vm.messageFlow.collect { msg ->
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        vm.navigateToMain.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }

    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    LoginContent(
        onLoginPressed = { username, password ->
            vm.performLogin(username, password)
        },
        onRegisterNavigation = {
            navController.navigate(Screen.Register.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    )
}

@Composable
fun LoginContent(
    onLoginPressed: (String, String) -> Unit,
    onRegisterNavigation: () -> Unit
) {
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 48.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.please_login),
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BlueJose),
            modifier = Modifier.padding(bottom = 96.dp)
        )
        MyTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            hint = stringResource(R.string.login),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            hint = stringResource(R.string.password),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth(),
            isPassword = true
        )
        Spacer(modifier = Modifier.height(164.dp))
        Button(
            onClick = { onLoginPressed(usernameInput, passwordInput) },
            colors = ButtonDefaults.buttonColors(containerColor = BlueJose),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = stringResource(R.string.login))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_account_yet_register),
            color = BlueJose,
            modifier = Modifier.clickable { onRegisterNavigation() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginContent(onLoginPressed = { _, _ -> }, onRegisterNavigation = {})
}
