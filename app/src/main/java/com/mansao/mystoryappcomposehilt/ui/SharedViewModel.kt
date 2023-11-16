package com.mansao.mystoryappcomposehilt.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem

class SharedViewModel : ViewModel() {

    var storyItem by mutableStateOf<ListStoryItem?>(null)
        private set

    fun addStory(newStory:ListStoryItem){
        storyItem = newStory
    }
}