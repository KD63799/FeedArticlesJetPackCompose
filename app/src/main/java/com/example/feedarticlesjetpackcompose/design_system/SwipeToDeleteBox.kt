package com.example.feedarticlesjetpackcompose.design_system


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feedarticlesjetpackcompose.R

@Composable
fun SwipeToDeleteBox(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.25f }
    )

    val backgroundColor = when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF0000)
        SwipeToDismissBoxValue.StartToEnd,
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    contentDescription = context.getString(R.string.delete),
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SwipeToDeleteBoxPreview() {
    SwipeToDeleteBox(
        onDelete = {},
        content = {}
    )
}
