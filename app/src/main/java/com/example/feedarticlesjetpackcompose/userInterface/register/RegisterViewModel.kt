package com.example.feedarticlesjetpackcompose.userInterface.register


import com.example.feedarticlesjetpackcompose.network.dtosResponse.RegisterDto
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.R
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
class RegisterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _navigateToMain = MutableSharedFlow<Boolean>()
    val navigateToMain = _navigateToMain.asSharedFlow()

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow = _messageFlow.asSharedFlow()

    fun registerUser(username: String, password: String, confirmPassword: String) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirmPassword = confirmPassword.trim()

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirmPassword.isEmpty()) {
            viewModelScope.launch {
                _messageFlow.emit(context.getString(R.string.please_fill_in_all_fields))
            }
            return
        }

        if (trimmedPassword != trimmedConfirmPassword) {
            viewModelScope.launch {
                _messageFlow.emit(context.getString(R.string.password_not_match))
            }
            return
        }

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.register(RegisterDto(trimmedUsername, trimmedPassword))
                }
                val body = response?.body()
                if (response == null) {
                    _messageFlow.emit(context.getString(R.string.no_response_from_server))
                } else {
                    when (response.code()) {
                        200 -> {
                            preferences.authToken = body!!.token
                            preferences.currentUserId = body.id
                            _navigateToMain.emit(true)
                            context.getString(R.string.account_created)
                        }
                        303 -> context.getString(R.string.login_already_used)
                        304 -> context.getString(R.string.account_not_created)
                        400 -> context.getString(R.string.please_check_the_fields)
                        503 -> context.getString(R.string.service_unavailable)
                        else -> return@launch
                    }.let { _messageFlow.emit(it) }
                }
            } catch (e: ConnectException) {
                _messageFlow.emit(context.getString(R.string.connection_error))
            }
        }
    }
}
