package com.example.feedarticlesjetpackcompose.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feedarticlesjetpackcompose.userInterface.create.CreateScreen
import com.example.feedarticlesjetpackcompose.userInterface.create.CreateViewModel
import com.example.feedarticlesjetpackcompose.userInterface.edit.EditScreen
import com.example.feedarticlesjetpackcompose.userInterface.edit.EditViewModel
import com.example.feedarticlesjetpackcompose.userInterface.login.LoginScreen
import com.example.feedarticlesjetpackcompose.userInterface.login.LoginViewModel
import com.example.feedarticlesjetpackcompose.userInterface.main.MainScreen
import com.example.feedarticlesjetpackcompose.userInterface.main.MainViewModel
import com.example.feedarticlesjetpackcompose.userInterface.register.RegisterScreen
import com.example.feedarticlesjetpackcompose.userInterface.register.RegisterViewModel
import com.example.feedarticlesjetpackcompose.userInterface.splash.SplashScreen
import com.example.feedarticlesjetpackcompose.userInterface.splash.SplashViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Create : Screen("create")
    object Edit : Screen("edit")
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = splashViewModel)
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navController = navController, loginViewModel)
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(navController = navController, registerViewModel)
        }

        composable(Screen.Main.route) {
            val mainViewModel: MainViewModel = hiltViewModel()
            MainScreen(navController = navController, mainViewModel)
        }

        composable(Screen.Create.route) {
            val createViewModel: CreateViewModel = hiltViewModel()
            CreateScreen(navController = navController,createViewModel)
        }

        composable(Screen.Edit.route+"/{idArticle}",
            arguments = listOf(navArgument(name= "idArticle"){
                type = NavType.LongType
            })
        ) {
            it.arguments?.getLong("idArticle")?.let {idArticle ->
                val editViewModel: EditViewModel = hiltViewModel()
                EditScreen(navController, editViewModel, idArticle)
            }
        }
    }
}