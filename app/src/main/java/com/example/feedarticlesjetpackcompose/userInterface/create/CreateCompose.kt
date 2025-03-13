package com.example.feedarticlesjetpackcompose.userInterface.create

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

@Composable
fun CreateContent(
    articleTitle: String,
    onTitleChanged: (String) -> Unit,
    articleBody: String,
    onBodyChanged: (String) -> Unit,
    articleImageUrl: String,
    onImageUrlChanged: (String) -> Unit,
    selectedCategoryRes: Int,
    onCategoryChanged: (Int) -> Unit,
    onSubmitArticle: () -> Unit
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

        Text(
            text = stringResource(R.string.new_article),
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
            value = articleBody,
            onValueChange = onBodyChanged,
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

        MyAsyncImage(articleImageUrl)
        Spacer(modifier = Modifier.height(32.dp))
        CategorySelector(
            categoryResourceIds = listOf(R.string.sport, R.string.manga, R.string.various),
            defaultCategoryResId = selectedCategoryRes,
            onCategorySelected = onCategoryChanged
        )
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onSubmitArticle,
            colors = ButtonDefaults.buttonColors(containerColor = BlueJose),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = stringResource(R.string.submit))
        }
    }
}

@Composable
fun MyAsyncImage(imageUrl: String) {
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

@Composable
fun CreateScreen(navController: NavHostController, vm: CreateViewModel = hiltViewModel()) {
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        vm.messageFlow.collect { resId ->
            Toast.makeText(ctx, ctx.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        vm.navigateToMainFlow.collect { navigate ->
            if (navigate) {
                navController.popBackStack()
            }
        }
    }

    var articleTitle by remember { mutableStateOf("") }
    var articleBody by remember { mutableStateOf("") }
    var articleImageUrl by remember { mutableStateOf("") }
    var currentCategoryRes by remember { mutableIntStateOf(R.string.sport) }

    CreateContent(
        articleTitle = articleTitle,
        onTitleChanged = { articleTitle = it },
        articleBody = articleBody,
        onBodyChanged = { articleBody = it },
        articleImageUrl = articleImageUrl,
        onImageUrlChanged = { articleImageUrl = it },
        selectedCategoryRes = currentCategoryRes,
        onCategoryChanged = { currentCategoryRes = it },
        onSubmitArticle = {
            vm.createNewArticle(
                title = articleTitle,
                description = articleBody,
                imageUrl = articleImageUrl,
                categoryButtonId = currentCategoryRes
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreatePreview() {
    CreateContent(
        articleTitle = "",
        onTitleChanged = {},
        articleBody = "",
        onBodyChanged = {},
        articleImageUrl = "",
        onImageUrlChanged = {},
        selectedCategoryRes = R.string.sport,
        onCategoryChanged = {},
        onSubmitArticle = {}
    )
}
