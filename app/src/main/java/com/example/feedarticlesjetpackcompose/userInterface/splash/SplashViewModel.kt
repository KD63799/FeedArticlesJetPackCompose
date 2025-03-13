package com.example.feedarticlesjetpackcompose.userInterface.splash

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.utils.TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _goToDestination = MutableSharedFlow<Boolean>()
    val goToDestination = _goToDestination.asSharedFlow()

    init {
        checkToken()
    }

    private fun checkToken() {
        viewModelScope.launch {
            delay(2000L)
            val token = sharedPreferences.getString(TOKEN, null)
            if (token != null) {
                _goToDestination.emit(true)
            } else {
                _goToDestination.emit(false)
            }
        }
    }
}
