package com.mansao.mystoryappcomposehilt.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val storyRepository: StoryRepositoryImpl) :ViewModel(){

    var uiState: LoginUiState by mutableStateOf(LoginUiState.StandBy)
        private set

    fun getUiState() {
        uiState = LoginUiState.StandBy
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            uiState = try {
                val result = storyRepository.login(email, password)
                storyRepository.saveAccessToken(result.loginResult.token)
                storyRepository.saveIsLoginState(true)
                storyRepository.saveUsername(result.loginResult.name)
                LoginUiState.Success(result)
            } catch (e: Exception) {
                LoginUiState.Error(e.message.toString())
            }
        }
    }
}