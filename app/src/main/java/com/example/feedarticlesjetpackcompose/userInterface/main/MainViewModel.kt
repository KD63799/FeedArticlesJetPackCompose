package com.example.feedarticlesjetpackcompose.userInterface.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.network.ApiService
import com.example.feedarticlesjetpackcompose.network.PreferencesManager
import com.example.feedarticlesjetpackcompose.network.dtosResponse.ArticleDto
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
class MainViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val apiService: ApiService
) : ViewModel() {

    private var articleCache = arrayListOf<ArticleDto>()

    private val _articlesListStateFlow = MutableStateFlow<List<ArticleDto>>(emptyList())
    val articlesListStateFlow = _articlesListStateFlow.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiMessageFlow = MutableSharedFlow<Int>()
    val uiMessageFlow = _uiMessageFlow.asSharedFlow()

    private val _navigateToLoginFlow = MutableSharedFlow<Boolean>()
    val navigateToRegisterFlow = _navigateToLoginFlow.asSharedFlow()

    private val _navigateToEditFlow = MutableSharedFlow<Boolean>()
    val navigateToEditFlow = _navigateToEditFlow.asSharedFlow()

    private var currentFilterCategory = 0

    init {
        fetchAllArticles()
    }

    fun disconnectUser() {
        preferences.authToken = null
        preferences.currentUserId = 0
        viewModelScope.launch {
            _navigateToLoginFlow.emit(true)
        }
    }

    fun fetchAllArticles() {
        preferences.authToken?.let { token ->
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    val response = withContext(Dispatchers.IO) { apiService.getAllArticles(token) }
                    val articles = response?.body()
                    when {
                        response == null -> _uiMessageFlow.emit(R.string.no_response_database)
                        response.code() != 0 -> when (response.code()) {
                            200 -> {
                                _articlesListStateFlow.value = if (currentFilterCategory != 0)
                                    articles!!.filter { it.categorie == currentFilterCategory }
                                else articles!!
                                articleCache.clear()
                                articleCache.addAll(articles)
                            }
                            400, 401 -> {
                                disconnectUser()
                                _uiMessageFlow.emit(R.string.error_from_database_redirection)
                            }
                            503 -> _uiMessageFlow.emit(R.string.no_response_database)
                            else -> return@launch
                        }
                    }
                } catch (e: ConnectException) {
                    _uiMessageFlow.emit(R.string.error_from_database)
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun filterArticlesByCategory(categoryResId: Int) {
        val numericCategory = CategoryManager.getCategoryNumericId(categoryResId)
        currentFilterCategory = numericCategory
        _articlesListStateFlow.value =
            if (numericCategory != 0)
                articleCache.filter { it.categorie == numericCategory }
            else articleCache
    }

    fun getUserId(): Long = preferences.currentUserId

    fun triggerNavigationToEdit() {
        viewModelScope.launch {
            _navigateToEditFlow.emit(true)
        }
    }

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.deleteArticle(articleId, preferences.authToken!!)
                }
                when {
                    response == null -> _uiMessageFlow.emit(R.string.no_response_database)
                    response.code() != 0 -> when (response.code()) {
                        201 -> {
                            _uiMessageFlow.emit(R.string.article_deleted)
                            fetchAllArticles()
                        }
                        304 -> _uiMessageFlow.emit(R.string.article_not_deleted)
                        400, 401 -> {
                            disconnectUser()
                            _uiMessageFlow.emit(R.string.error_from_database_redirection)
                        }
                        503 -> _uiMessageFlow.emit(R.string.no_response_database)
                        else -> return@launch
                    }
                }
            } catch (e: ConnectException) {
                _uiMessageFlow.emit(R.string.error_from_database)
            }
        }
    }
}
