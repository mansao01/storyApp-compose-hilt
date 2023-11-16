package com.mansao.mystoryappcomposehilt.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyRepository: StoryRepositoryImpl) :
    ViewModel() {
    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun getStories() {
        viewModelScope.launch {
            _isLoading.value = true
            uiState = HomeUiState.Loading
            uiState = try {
                val result = storyRepository.getStories()
                val username = storyRepository.getUsername()
                _isLoading.value = false
                HomeUiState.Success(result, username!!)
            } catch (e: Exception) {
                _isLoading.value = false
                HomeUiState.Error(e.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            uiState = HomeUiState.Loading
            storyRepository.clearTokens()
            storyRepository.saveIsLoginState(false)
            storyRepository.clearUsername()
        }
    }
}