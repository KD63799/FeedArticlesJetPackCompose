package com.example.feedarticlesjetpackcompose.utils

import androidx.compose.ui.graphics.Color
import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.ui.theme.Cream
import com.example.feedarticlesjetpackcompose.ui.theme.Latte
import com.example.feedarticlesjetpackcompose.ui.theme.DarkCoffee

object CategoryManager {

    val categoryResourceIdsIncludingAll = listOf(
        R.string.all,
        R.string.sport,
        R.string.manga,
        R.string.various
    )

    fun getCategoryNumericId(resourceId: Int): Int {
        return when (resourceId) {
            R.string.sport -> 1
            R.string.manga -> 2
            R.string.various -> 3
            else -> 0
        }
    }

    fun getCategoryColor(categoryId: Int): Color {
        return when (categoryId) {
            1 -> Cream
            2 -> Latte
            3 -> DarkCoffee
            else -> Color.Transparent
        }
    }

    fun getCategoryResourceId(categoryId: Int): Int {
        return when (categoryId) {
            1 -> R.string.sport
            2 -> R.string.manga
            3 -> R.string.various
            else -> 0
        }
    }
}
