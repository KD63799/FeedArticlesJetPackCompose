package com.example.feedarticlesjetpackcompose.userInterface.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.network.PreferencesManager
import com.example.feedarticlesjetpackcompose.network.dtosResponse.ArticleDto
import com.example.feedarticlesjetpackcompose.network.dtosResponse.UpdateArticleDto
import com.example.feedarticlesjetpackcompose.utils.CategoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _messageFlow = MutableSharedFlow<Int>()
    val messageFlow = _messageFlow.asSharedFlow()

    private val _navigateToMainFlow = MutableSharedFlow<Boolean>()
    val navigateToMainFlow = _navigateToMainFlow.asSharedFlow()

    private val _currentArticleFlow = MutableStateFlow<ArticleDto?>(null)
    val currentArticleFlow = _currentArticleFlow.asStateFlow()

    fun fetchArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getArticle(articleId, preferences.authToken!!)
                }
                val articleData = response?.body()
                when {
                    response == null -> _messageFlow.emit(R.string.no_response_database)
                    response.code() != 0 -> {
                        when (response.code()) {
                            200 -> _currentArticleFlow.value = articleData!!
                            303 -> {
                                _navigateToMainFlow.emit(true)
                                _messageFlow.emit(R.string.article_not_found)
                            }
                            400 -> _messageFlow.emit(R.string.please_check_the_fields)
                            401 -> {
                                _navigateToMainFlow.emit(true)
                                _messageFlow.emit(R.string.please_logout)
                            }
                            503 -> _messageFlow.emit(R.string.error_from_database)
                            else -> return@launch
                        }
                    }
                }
            } catch (ex: ConnectException) {
                viewModelScope.launch {
                    _messageFlow.emit(R.string.error_from_database)
                }
            }
        }
    }

    fun updateArticle(
        title: String,
        description: String,
        imageUrl: String,
        categoryButtonResId: Int
    ) {
        val cleanedTitle = title.trim()
        val cleanedDescription = description.trim()
        val cleanedImageUrl = imageUrl.trim()
        val categoryResId = CategoryManager.getCategoryResourceId(categoryButtonResId)

        if (cleanedTitle.isNotEmpty() &&
            cleanedDescription.isNotEmpty() &&
            cleanedImageUrl.isNotEmpty() &&
            categoryResId != 0
        ) {
            viewModelScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        apiService.updateArticle(
                            _currentArticleFlow.value!!.id,
                            preferences.authToken!!,
                            UpdateArticleDto(
                                _currentArticleFlow.value!!.id,
                                cleanedTitle,
                                cleanedDescription,
                                cleanedImageUrl,
                                categoryResId
                            )
                        )
                    }
                    val resultMessage = when {
                        response == null -> R.string.no_response_database
                        response.code() != 0 -> {
                            when (response.code()) {
                                201 -> {
                                    _navigateToMainFlow.emit(true)
                                    R.string.article_updated
                                }
                                303 -> R.string.article_not_created
                                304 -> R.string.article_content_same
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
                    viewModelScope.launch {
                        _messageFlow.emit(R.string.error_from_database)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                _messageFlow.emit(R.string.please_fill_in_all_fields)
            }
        }
    }
}
