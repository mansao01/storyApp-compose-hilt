package com.mansao.mystoryappcomposehilt.ui.screen.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mansao.mystoryappcomposehilt.data.StoryRepositoryImpl
import com.mansao.mystoryappcomposehilt.ui.common.RegisterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val storyRepository: StoryRepositoryImpl
):ViewModel() {
    var uiState: RegisterUiState by mutableStateOf(RegisterUiState.StandBy)
        private set

    fun getUiState() {
        uiState = RegisterUiState.StandBy
    }

    fun register(name:String, email:String, password:String){
        viewModelScope.launch {
            uiState = RegisterUiState.Loading
            uiState = try {
                val  result = storyRepository.register(name, email, password)
                RegisterUiState.Success(result)
            }catch (e:Exception){
                val errorMessage = when (e) {
                    is IOException -> "Network error occurred"
                    is HttpException -> {
                        when (e.code()) {
                            400 -> e.response()?.errorBody()?.string().toString()
                            // Add more cases for specific HTTP error codes if needed
                            else -> "HTTP error: ${e.code()}"
                        }
                    }

                    else -> "An unexpected error occurred"
                }
                RegisterUiState.Error(errorMessage)
            }
        }
    }
}