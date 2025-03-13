package com.example.feedarticlesjetpackcompose.userInterface.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.design_system.ArticleItemExpandedContent
import com.example.feedarticlesjetpackcompose.design_system.ArticleItemWhenAuthorContent
import com.example.feedarticlesjetpackcompose.design_system.CategorySelector
import com.example.feedarticlesjetpackcompose.navigation.Screen
import com.example.feedarticlesjetpackcompose.network.dtosResponse.ArticleDto
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose
import com.example.feedarticlesjetpackcompose.utils.CategoryManager

@Composable
fun MainScreen(
    navController: NavHostController,
    vm: MainViewModel = hiltViewModel()
) {
    val localContext = LocalContext.current
    val articlesList by vm.articlesListStateFlow.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()
    var currentArticleId by remember { mutableStateOf(0L) }
    var selectedCategoryResId by rememberSaveable { mutableStateOf(R.string.all) }

    LaunchedEffect(Unit) {
        vm.fetchAllArticles()
    }
    LaunchedEffect(Unit) {
        vm.navigateToRegisterFlow.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.navigateToEditFlow.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(Screen.Edit.route + "/$currentArticleId")
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.messageFlow.collect { resId ->
            Toast.makeText(localContext, localContext.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    MainContent(
        articles = articlesList,
        isRefreshing = isRefreshing,
        onAddArticleClick = { navController.navigate(Screen.Create.route) },
        onLogoutClick = { vm.disconnectUser() },
        currentUserId = vm.getUserId(),
        onCategorySelected = { newCategoryResId ->
            selectedCategoryResId = newCategoryResId
            vm.filterArticlesByCategory(newCategoryResId)
        },
        onRefreshArticles = { vm.fetchAllArticles() },
        onDeleteArticle = { articleId ->
            vm.deleteArticle(articleId)
        },
        onArticleClick = { articleId ->
            currentArticleId = articleId
            vm.triggerNavigationToEdit()
        },
        selectedCategory = selectedCategoryResId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    articles: List<ArticleDto> = emptyList(),
    isRefreshing: Boolean,
    onAddArticleClick: () -> Unit,
    onLogoutClick: () -> Unit,
    currentUserId: Long,
    onCategorySelected: (Int) -> Unit,
    onRefreshArticles: () -> Unit,
    onDeleteArticle: (Long) -> Unit,
    onArticleClick: (Long) -> Unit,
    selectedCategory: Int
) {
    val localContext = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .systemBarsPadding()
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = localContext.getString(R.string.add),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { onAddArticleClick() }
                )
                Icon(
                    imageVector = Icons.Rounded.PowerSettingsNew,
                    contentDescription = localContext.getString(R.string.logout),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { onLogoutClick() }
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { onRefreshArticles() },
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = isRefreshing,
                            containerColor = Color.White,
                            color = BlueJose,
                            state = pullToRefreshState
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn {
                        items(items = articles) { article ->
                            if (article.idU == currentUserId)
                                ArticleItemWhenAuthorContent(
                                    article,
                                    onDeleteArticle,
                                    onArticleClick
                                )
                            else
                                ArticleItemExpandedContent(article)
                        }
                    }
                }
                CategorySelector(
                    categoryResourceIds = CategoryManager.categoryResourceIdsIncludingAll,
                    defaultCategoryResId = selectedCategory,
                    onCategorySelected = { newCategoryResId ->
                        onCategorySelected(newCategoryResId)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainContent(
        articles = emptyList(),
        isRefreshing = false,
        onAddArticleClick = {},
        onLogoutClick = {},
        currentUserId = 0L,
        onCategorySelected = {},
        onRefreshArticles = {},
        onDeleteArticle = {},
        onArticleClick = {},
        selectedCategory = R.string.all
    )
}
