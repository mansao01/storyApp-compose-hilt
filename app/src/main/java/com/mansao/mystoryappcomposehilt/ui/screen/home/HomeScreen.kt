package com.mansao.mystoryappcomposehilt.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem
import com.mansao.mystoryappcompose.ui.component.ConnectivityStatus
import com.mansao.mystoryappcompose.ui.component.LoadingScreen
import com.mansao.mystoryappcompose.ui.component.MToast
import com.mansao.mystoryappcompose.ui.component.StoryItem
import com.mansao.mystoryappcomposehilt.ui.AuthViewModel
import com.mansao.mystoryappcomposehilt.ui.SharedViewModel
import com.mansao.mystoryappcomposehilt.ui.common.HomeUiState
import com.mansao.mystoryappcomposehilt.ui.component.MyFloatingActionButton
import com.mansao.mystoryappcomposehilt.utils.ConnectionStatus
import com.mansao.mystoryappcomposehilt.utils.currentConnectivityStatus
import com.mansao.mystoryappcomposehilt.utils.observeConnectivityAsFlow
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToLogin: () -> Unit,
    navigateToAdd: () -> Unit,
    navigateToDetail: () -> Unit,
    navigateToMap: () -> Unit

) {
    val isLogin by authViewModel.loginState.collectAsState()
    if (isLogin) {
        LaunchedEffect(Unit) {
            homeViewModel.getStories()
        }
    }

    val context = LocalContext.current
    when (uiState) {
        is HomeUiState.Loading -> LoadingScreen()
        is HomeUiState.Success -> {
            HomeScreenContent(
                uiState.getStoriesResponse,
                scrollBehavior = scrollBehavior,
                navigateToLogin = navigateToLogin,
                homeViewModel = homeViewModel,
                navigateToAdd = navigateToAdd,
                navigateToDetail = navigateToDetail,
                sharedViewModel = sharedViewModel,
                username = uiState.username,
                navigateToMap = navigateToMap
            )
        }

        is HomeUiState.Error -> MToast(context, uiState.msg)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    storyList: Flow<PagingData<ListStoryItem>>,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToLogin: () -> Unit,
    homeViewModel: HomeViewModel,
    navigateToAdd: () -> Unit,
    navigateToDetail: () -> Unit,
    sharedViewModel: SharedViewModel,
    username: String,
    navigateToMap: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                scrollBehavior = scrollBehavior,
                navigateToLogin = { navigateToLogin() },
                homeViewModel = homeViewModel,
                username = username,
                navigateToMap = navigateToMap
            )
        },
        floatingActionButton = { MyFloatingActionButton(navigateToAdd = { navigateToAdd() }) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Surface(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CheckConnectionStatus()
                SwipeRefreshStory(
                    storyList = storyList,
                    sharedViewModel = sharedViewModel,
                    navigateToDetail = navigateToDetail,
                    homeViewModel = homeViewModel
                )
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StoryList(
    storyList: Flow<PagingData<ListStoryItem>>,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    navigateToDetail: () -> Unit
) {
    val lazyPagingItems = storyList.collectAsLazyPagingItems()
    LazyColumn {
        items(count = lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            if (item != null) {
                StoryItem(
                    story = item,
                    modifier = modifier.clickable {
                        sharedViewModel.addStory(newStory = item)
                        navigateToDetail()
                    })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SwipeRefreshStory(
    storyList: Flow<PagingData<ListStoryItem>>,
    sharedViewModel: SharedViewModel,
    navigateToDetail: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val isLoading by homeViewModel.isLoading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { homeViewModel.getStories() }
    ) {
        StoryList(
            storyList = storyList,
            sharedViewModel = sharedViewModel,
            navigateToDetail = { navigateToDetail() })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navigateToLogin: () -> Unit,
    navigateToMap: () -> Unit,
    homeViewModel: HomeViewModel,
    username: String
) {
    val context = LocalContext.current
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = "Welcome, $username") },
        actions = {
            IconButton(onClick = {
                navigateToMap()
            }) {
                Icon(imageVector = Icons.Default.Map, contentDescription = "Maps")
            }
            IconButton(onClick = {
                context.imageLoader.memoryCache?.clear()
                context.imageLoader.diskCache?.clear()
                logout(homeViewModel, navigateToLogin)
            }) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
            }

        }
    )
}

@Composable
fun CheckConnectionStatus() {
    val connection by connectivityStatus()
    val isConnected = connection === ConnectionStatus.Available
    ConnectivityStatus(isConnected)

}

@Composable
fun connectivityStatus(): State<ConnectionStatus> {
    val context = LocalContext.current
    return produceState(initialValue = context.currentConnectivityStatus) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}

private fun logout(homeViewModel: HomeViewModel, navigateToLogin: () -> Unit) {
    homeViewModel.logout()
    navigateToLogin()
}