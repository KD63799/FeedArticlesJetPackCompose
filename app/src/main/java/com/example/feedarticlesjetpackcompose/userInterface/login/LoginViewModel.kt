package com.example.feedarticlesjetpackcompose.userInterface.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.network.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val apiService: ApiService,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _uiMessageFlow = MutableSharedFlow<String>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()

    private val _navigateToMain = MutableSharedFlow<Boolean>()
    val navigateToMain = _navigateToMain.asSharedFlow()

    fun performLogin(username: String, password: String) {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isEmpty() || cleanPassword.isEmpty()) {
            viewModelScope.launch {
                _uiMessageFlow.emit(appContext.getString(R.string.please_fill_in_all_fields))
            }
            return
        }

        loginUser(cleanUsername, cleanPassword)
    }

    private fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.login(username, password)
                }
                val responseBody = response?.body()
                if (response == null) {
                    _uiMessageFlow.emit(appContext.getString(R.string.no_response_from_server))
                } else {
                    when (response.code()) {
                        200 -> {
                            preferences.authToken = responseBody!!.token
                            preferences.currentUserId = responseBody.id
                            _navigateToMain.emit(true)
                            _uiMessageFlow.emit(appContext.getString(R.string.login_successful))
                        }
                        304 -> _uiMessageFlow.emit(appContext.getString(R.string.internal_error))
                        400 -> _uiMessageFlow.emit(appContext.getString(R.string.please_check_the_fields))
                        401 -> _uiMessageFlow.emit(appContext.getString(R.string.incorrect_information))
                        503 -> _uiMessageFlow.emit(appContext.getString(R.string.service_unavailable))
                        else -> return@launch
                    }
                }
            } catch (ex: ConnectException) {
                _uiMessageFlow.emit(appContext.getString(R.string.connection_error))
            }
        }
    }
}
