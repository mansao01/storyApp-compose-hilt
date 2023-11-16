package com.mansao.mystoryappcomposehilt.ui.screen.maps

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepositoryImpl
) : ViewModel() {

    var uiState: MapUiState by mutableStateOf(MapUiState.Loading)

    init {
        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        viewModelScope.launch {
            uiState = MapUiState.Loading
            uiState = try {
                val token = "Bearer ${storyRepository.getAccessToken()}"
                val result =
                    storyRepository.getStoriesWithLocation(token)
                MapUiState.Success(result)
            } catch (e: Exception) {
                MapUiState.Error(e.message.toString())
            }

        }
    }
}