package com.example.feedarticlesjetpackcompose.userInterface.edit

import MyTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.design_system.CategorySelector
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose
import com.example.feedarticlesjetpackcompose.utils.CategoryManager
import kotlinx.coroutines.launch

@Composable
fun EditScreen(
    navController: NavHostController,
    viewModel: EditViewModel,
    articleId: Long
) {
    val context = LocalContext.current
    val currentArticle by viewModel.currentArticleFlow.collectAsState()

    LaunchedEffect(articleId) {
        viewModel.fetchArticle(articleId)
        launch {
            viewModel.uiMessageFlow.collect { resId ->
                Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
            }
        }
        launch {
            viewModel.navigateToMainFlow.collect { shouldNavigate ->
                if (shouldNavigate) {
                    navController.popBackStack()
                }
            }
        }
    }

    currentArticle?.let { article ->
        var updatedTitle by remember(article.id) { mutableStateOf(article.titre) }
        var updatedContent by remember(article.id) { mutableStateOf(article.descriptif) }
        var updatedImageUrl by remember(article.id) { mutableStateOf(article.urlImage) }
        var selectedCategoryResId by remember(article.id) {
            mutableIntStateOf(CategoryManager.getCategoryResourceId(article.categorie))
        }

        EditContent(
            articleTitle = updatedTitle,
            onTitleChanged = { updatedTitle = it },
            articleContent = updatedContent,
            onContentChanged = { updatedContent = it },
            articleImageUrl = updatedImageUrl,
            onImageUrlChanged = { updatedImageUrl = it },
            selectedCategoryResId = selectedCategoryResId,
            onCategoryChanged = { selectedCategoryResId = it },
            onSubmitEdit = {
                viewModel.updateArticle(
                    title = updatedTitle,
                    description = updatedContent,
                    imageUrl = updatedImageUrl,
                    categoryButtonResId = selectedCategoryResId
                )
            }
        )
    }
}

@Composable
fun EditContent(
    articleTitle: String,
    onTitleChanged: (String) -> Unit,
    articleContent: String,
    onContentChanged: (String) -> Unit,
    articleImageUrl: String,
    onImageUrlChanged: (String) -> Unit,
    selectedCategoryResId: Int,
    onCategoryChanged: (Int) -> Unit,
    onSubmitEdit: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.edit_article),
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BlueJose),
            modifier = Modifier.padding(top = 12.dp, bottom = 128.dp)
        )
        MyTextField(
            value = articleTitle,
            onValueChange = onTitleChanged,
            hint = stringResource(R.string.title),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyTextField(
            value = articleContent,
            onValueChange = onContentChanged,
            hint = stringResource(R.string.content),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        MyTextField(
            value = articleImageUrl,
            onValueChange = onImageUrlChanged,
            hint = stringResource(R.string.image_url),
            hintColor = BlueJose,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        MyAsyncImg(articleImageUrl)
        Spacer(modifier = Modifier.height(32.dp))
        CategorySelector(
            categoryResourceIds = listOf(R.string.sport, R.string.manga, R.string.various),
            defaultCategoryResId = selectedCategoryResId,
            onCategorySelected = onCategoryChanged
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onSubmitEdit,
            colors = ButtonDefaults.buttonColors(containerColor = BlueJose),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = stringResource(R.string.edit))
        }
    }
}

@Composable
fun MyAsyncImg(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = painterResource(id = R.drawable.feedarticles_logo),
        contentDescription = stringResource(R.string.a_downloaded_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(100.dp).clip(RectangleShape).padding(10.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun EditPreview() {
    EditContent(
        articleTitle = "",
        onTitleChanged = {},
        articleContent = "",
        onContentChanged = {},
        articleImageUrl = "",
        onImageUrlChanged = {},
        selectedCategoryResId = R.string.sport,
        onCategoryChanged = {},
        onSubmitEdit = {}
    )
}
