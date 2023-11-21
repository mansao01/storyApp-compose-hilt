package com.mansao.mystoryappcomposehilt.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val storyRepositoryImpl: StoryRepositoryImpl
) : ViewModel() {
    val uiState: StateFlow<SettingUiState.SettingUiState> =
        storyRepositoryImpl.isDarkMode.map { isDarkMode ->
            SettingUiState.SettingUiState(isDarkMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SettingUiState.SettingUiState()
        )

    fun selectedTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            storyRepositoryImpl.saveThemePreferences(isDarkMode)
        }
    }
}