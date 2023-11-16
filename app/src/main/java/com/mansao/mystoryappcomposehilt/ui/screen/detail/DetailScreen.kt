package com.mansao.mystoryappcomposehilt.ui.screen.detail

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem
import com.mansao.mystoryappcomposehilt.ui.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    sharedViewModel: SharedViewModel,
    navigateToHome: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val story = sharedViewModel.storyItem
    Scaffold(
        topBar = {
            story?.name?.let {
                DetailTopBar(
                    scrollBehavior = scrollBehavior,
                    navigateToHome = { navigateToHome() },
                    username = it
                )
            }
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            DetailScreenContent(storyItem = story)
        }
    }

}

@Composable
fun DetailScreenContent(storyItem: ListStoryItem?) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    val offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)

    }
    Column {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(storyItem?.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "story photo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(state)
                    .size(300.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            storyItem?.name?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold

                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            storyItem?.description?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyMedium

                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToHome: () -> Unit,
    username: String
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = "$username's story") },
        navigationIcon = {
            IconButton(onClick = { navigateToHome() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}