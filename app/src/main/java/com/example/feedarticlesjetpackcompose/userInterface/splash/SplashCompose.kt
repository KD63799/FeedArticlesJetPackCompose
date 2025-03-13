package com.example.feedarticlesjetpackcompose.userInterface.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.navigation.Screen
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose

@Composable
fun SplashScreen(navController: NavHostController, viewModel: SplashViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = true) {
        viewModel.goToDestination.collect { hasToken ->
            if (hasToken) {
                navController.navigate(Screen.Main.route){
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueJose),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.feedarticles_logo),
            contentDescription = stringResource(R.string.feedarticles_logo)
        )
        Text(
            text = stringResource(R.string.name_app),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 32.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Preview
@Composable
fun SplashPreview() {
    SplashContent()
}