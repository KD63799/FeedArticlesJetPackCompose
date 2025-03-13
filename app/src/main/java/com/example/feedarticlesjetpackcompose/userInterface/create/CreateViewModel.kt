package com.example.feedarticlesjetpackcompose.userInterface.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.network.PreferencesManager
import com.example.feedarticlesjetpackcompose.network.dtosResponse.NewArticleDto
import com.example.feedarticlesjetpackcompose.utils.CategoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _messageFlow = MutableSharedFlow<Int>()
    val messageFlow = _messageFlow.asSharedFlow()

    private val _navigateToMainFlow = MutableSharedFlow<Boolean>()
    val navigateToMainFlow = _navigateToMainFlow.asSharedFlow()

    fun createNewArticle(
        title: String,
        description: String,
        imageUrl: String,
        categoryButtonId: Int
    ) {
        val cleanedTitle = title.trim()
        val cleanedDescription = description.trim()
        val cleanedImageUrl = imageUrl.trim()
        val numericCategory = CategoryManager.getCategoryNumericId(categoryButtonId)

        if (cleanedTitle.isNotEmpty() &&
            cleanedDescription.isNotEmpty() &&
            cleanedImageUrl.isNotEmpty() &&
            numericCategory != 0
        ) {
            viewModelScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        apiService.createArticle(
                            preferences.authToken!!,
                            NewArticleDto(
                                preferences.currentUserId,
                                cleanedTitle,
                                cleanedDescription,
                                cleanedImageUrl,
                                numericCategory
                            )
                        )
                    }
                    val resultMessage = when {
                        response == null -> R.string.no_response_database
                        response.code() != 0 -> {
                            when (response.code()) {
                                201 -> {
                                    _navigateToMainFlow.emit(true)
                                    R.string.article_created
                                }
                                304 -> R.string.article_not_created
                                400 -> R.string.please_check_the_fields
                                401 -> R.string.please_logout
                                503 -> R.string.error_from_database
                                else -> return@launch
                            }
                        }
                        else -> return@launch
                    }
                    _messageFlow.emit(resultMessage)
                } catch (ex: ConnectException) {
                    _messageFlow.emit(R.string.error_from_database)
                }
            }
        } else {
            viewModelScope.launch {
                _messageFlow.emit(R.string.please_fill_in_all_fields)
            }
        }
    }
}
