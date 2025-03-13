package com.example.feedarticlesjetpackcompose.design_system

import com.example.feedarticlesjetpackcompose.R
import com.example.feedarticlesjetpackcompose.ui.theme.BlueJose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun CategoryPreview() {
    val categoryOptions = listOf(R.string.all, R.string.sport, R.string.manga, R.string.various)
    CategorySelector(
        categoryResourceIds = categoryOptions,
        defaultCategoryResId = R.string.all
    ) {}
}

@Composable
fun CategorySelector(
    categoryResourceIds: List<Int> = emptyList(),
    defaultCategoryResId: Int = 0,
    onCategorySelected: (Int) -> Unit
) {
    val context = LocalContext.current

    var selectedCategoryResId by remember {
        mutableIntStateOf(defaultCategoryResId)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .selectableGroup()
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        categoryResourceIds.forEach { categoryResId ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = categoryResId == selectedCategoryResId,
                    onClick = {
                        selectedCategoryResId = categoryResId
                        onCategorySelected(categoryResId)
                    },
                    role = Role.RadioButton
                )
            ) {
                RadioButton(
                    selected = categoryResId == selectedCategoryResId,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = BlueJose,
                        unselectedColor = BlueJose
                    ),
                    onClick = null
                )
                Text(text = context.getString(categoryResId))
            }
        }
    }
}
