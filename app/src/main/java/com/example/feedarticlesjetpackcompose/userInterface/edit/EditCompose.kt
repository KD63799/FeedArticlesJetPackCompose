package com.example.feedarticlesjetpackcompose.userInterface.edit

import MyTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.design_system.CategorySelector
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose
import com.example.feedarticlesjetpackcompose.utils.CategoryManager

@Composable
fun EditScreen(
    navController: NavHostController,
    vm: EditViewModel,
    articleId: Long
) {
    val localContext = LocalContext.current
    val article by vm.currentArticleFlow.collectAsState()


    LaunchedEffect(articleId) {
        vm.fetchArticle(articleId)
    }


    LaunchedEffect(Unit) {
        vm.messageFlow.collect { resId ->
            Toast.makeText(localContext, localContext.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(Unit) {
        vm.navigateToMainFlow.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.popBackStack()
            }
        }
    }


    article?.let { art ->
        var editedTitle by remember { mutableStateOf(art.titre) }
        var editedContent by remember { mutableStateOf(art.descriptif) }
        var editedImageUrl by remember { mutableStateOf(art.urlImage) }
        var selectedCategoryResId by remember(art.id) {
            mutableIntStateOf(CategoryManager.getCategoryResourceId(art.categorie))
        }

        EditContent(
            articleTitle = editedTitle,
            onTitleChanged = { editedTitle = it },
            articleContent = editedContent,
            onContentChanged = { editedContent = it },
            articleImageUrl = editedImageUrl,
            onImageUrlChanged = { editedImageUrl = it },
            selectedCategoryResId = selectedCategoryResId,
            onCategoryChanged = { selectedCategoryResId = it },
            onSubmitEdit = {
                vm.updateArticle(
                    title = editedTitle,
                    description = editedContent,
                    imageUrl = editedImageUrl,
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
    val localContext = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Titre de l'écran
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
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
        modifier = Modifier
            .size(100.dp)
            .clip(RectangleShape)
            .padding(10.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun EditPreview() {
    EditContent(
        articleTitle = "Sample Title",
        onTitleChanged = {},
        articleContent = "Sample Content",
        onContentChanged = {},
        articleImageUrl = "https://example.com/image.png",
        onImageUrlChanged = {},
        selectedCategoryResId = R.string.sport,
        onCategoryChanged = {},
        onSubmitEdit = {}
    )
}
