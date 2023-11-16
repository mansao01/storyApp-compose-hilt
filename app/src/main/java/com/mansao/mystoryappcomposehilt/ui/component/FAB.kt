package com.mansao.mystoryappcomposehilt.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyFloatingActionButton(
    navigateToAdd: () -> Unit
) {
    FloatingActionButton(onClick = { navigateToAdd() }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
}