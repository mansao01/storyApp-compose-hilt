package com.mansao.mystoryappcomposehilt.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.paging.PagingData
import com.mansao.mystoryappcompose.data.network.response.GetStoriesWithLocationResponse
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem
import com.mansao.mystoryappcompose.data.network.response.LoginResponse
import com.mansao.mystoryappcompose.data.network.response.PostStoryResponse
import com.mansao.mystoryappcompose.data.network.response.RegisterResponse
import kotlinx.coroutines.flow.Flow


sealed interface RegisterUiState {
    object StandBy : RegisterUiState
    object Loading : RegisterUiState
    data class Success(val registerResponse: RegisterResponse) : RegisterUiState
    data class Error(val msg: String) : RegisterUiState

}

sealed interface LoginUiState {
    object StandBy : LoginUiState
    object Loading : LoginUiState
    data class Success(val loginResponse: LoginResponse) : LoginUiState
    data class Error(val msg: String) : LoginUiState

}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val getStoriesResponse: Flow<PagingData<ListStoryItem>>, val username: String) :
        HomeUiState

    data class Error(val msg: String) : HomeUiState

}

sealed interface MapUiState {
    object Loading : MapUiState
    data class Success(val getStoriesWithLocationResponse: GetStoriesWithLocationResponse) :
        MapUiState

    data class Error(val msg: String) : MapUiState

}

sealed interface AddUiState {
    object StandBy : AddUiState
    object Loading : AddUiState
    data class Success(val postStoryResponse: PostStoryResponse) : AddUiState
    data class Error(val msg: String) : AddUiState

}

sealed interface SettingUiState {
    data class SettingUiState(
        val isDarkMode: Boolean = false,
        val title: String = if (isDarkMode) "Dark Mode" else "Light Mode",
        val icon: ImageVector =
            if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode
    )

}

