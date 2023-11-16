package com.mansao.mystoryappcomposehilt.ui.screen.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.AddUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val storyRepository: StoryRepositoryImpl
) : ViewModel() {
    var uiState: AddUiState by mutableStateOf(AddUiState.StandBy)
        private set

    fun updateUiState() {
        uiState = AddUiState.StandBy
    }

    fun postStory(file: File, description: String, lat: Float? = null, lon: Float? = null) {
        viewModelScope.launch {
            uiState = AddUiState.Loading
            val token = "Bearer ${storyRepository.getAccessToken()}"
            uiState = try {
                val result = storyRepository.postStory(
                    token,
                    file,
                    description,
                    lat,
                    lon
                )
                AddUiState.Success(result)
            } catch (e: Exception) {
                AddUiState.Error(e.toString())
            }

        }

    }
}