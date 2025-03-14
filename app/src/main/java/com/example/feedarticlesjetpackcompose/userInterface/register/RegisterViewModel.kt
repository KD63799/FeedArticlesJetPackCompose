package com.example.feedarticlesjetpackcompose.userInterface.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.network.PreferencesManager
import com.example.feedarticlesjetpackcompose.network.dtosResponse.RegisterDto
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
    @ApplicationContext private val appContext: Context,
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) : ViewModel() {

    private val _uiMessageFlow = MutableSharedFlow<String>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()

    private val _homeNavigationFlow = MutableSharedFlow<Boolean>()
    val navigateToMainFlow = _homeNavigationFlow.asSharedFlow()

    fun performRegistration(userName: String, userPassword: String, confirmPassword: String) {
        val cleanUserName = userName.trim()
        val cleanUserPassword = userPassword.trim()
        val cleanConfirmPassword = confirmPassword.trim()

        if (cleanUserName.isEmpty() || cleanUserPassword.isEmpty() || cleanConfirmPassword.isEmpty()) {
            viewModelScope.launch {
                _uiMessageFlow.emit(appContext.getString(R.string.please_fill_in_all_fields))
            }
            return
        }
        if (cleanUserPassword != cleanConfirmPassword) {
            viewModelScope.launch {
                _uiMessageFlow.emit(appContext.getString(R.string.password_not_match))
            }
            return
        }
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.register(RegisterDto(cleanUserName, cleanUserPassword))
                }
                val responseBody = response?.body()
                if (response == null) {
                    _uiMessageFlow.emit(appContext.getString(R.string.no_response_from_server))
                } else {
                    when (response.code()) {
                        200 -> {
                            prefs.authToken = responseBody!!.token
                            prefs.currentUserId = responseBody.id
                            _homeNavigationFlow.emit(true)
                            _uiMessageFlow.emit(appContext.getString(R.string.account_created))
                        }
                        303 -> _uiMessageFlow.emit(appContext.getString(R.string.login_already_used))
                        304 -> _uiMessageFlow.emit(appContext.getString(R.string.account_not_created))
                        400 -> _uiMessageFlow.emit(appContext.getString(R.string.please_check_the_fields))
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
