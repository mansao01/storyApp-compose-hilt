package com.mansao.mystoryappcomposehilt.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mansao.mystoryappcompose.data.network.response.GetStoriesWithLocationResponse
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem
import com.mansao.mystoryappcompose.data.network.response.LoginResponse
import com.mansao.mystoryappcompose.data.network.response.PostStoryResponse
import com.mansao.mystoryappcompose.data.network.response.RegisterResponse
import com.mansao.mystoryappcomposehilt.data.local.StoryDatabase
import com.mansao.mystoryappcomposehilt.data.network.ApiService
import com.mansao.mystoryappcomposehilt.data.paging.StoryRemoteMediator
import com.mansao.mystoryappcomposehilt.data.preferences.StoryPreferences
import com.mansao.mystoryappcomposehilt.utils.CameraUtils
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

interface StoryRepository {
    suspend fun register(name: String, email: String, password: String): RegisterResponse
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun getStories(): Flow<PagingData<ListStoryItem>>
    suspend fun getStoriesWithLocation(token: String): GetStoriesWithLocationResponse
    suspend fun postStory(
        token: String,
        getFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ): PostStoryResponse

    suspend fun saveAccessToken(token: String)
    suspend fun saveIsLoginState(isLogin: Boolean)
    suspend fun saveUsername(userName: String)
    fun getIsLoginState(): Flow<Boolean>
    suspend fun getUsername(): String?
    suspend fun getAccessToken(): String?
    suspend fun clearTokens()
    suspend fun clearUsername()
}


class StoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val storyPreferences: StoryPreferences
) : StoryRepository {
    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getStories(): Flow<PagingData<ListStoryItem>> {
        val pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(
                apiService = apiService,
                storyDatabase = storyDatabase,
                storyPreferences = storyPreferences
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getStoriesWithLocation(token: String): GetStoriesWithLocationResponse {
        return apiService.getStoriesWithLocation(token)
    }

    override suspend fun postStory(
        token: String,
        getFile: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): PostStoryResponse {
        val file = CameraUtils.reduceFileImage(getFile)
        val descBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        return apiService.postStory(token, imageMultipart, descBody, lat, lon)
    }


    override suspend fun saveAccessToken(token: String) = storyPreferences.saveAccessToken(token)

    override suspend fun saveIsLoginState(isLogin: Boolean) =
        storyPreferences.saveIsLoginState(isLogin)

    override suspend fun saveUsername(userName: String) = storyPreferences.saveUsername(userName)

    override fun getIsLoginState(): Flow<Boolean> = storyPreferences.getIsLoginState()

    override suspend fun getUsername(): String? = storyPreferences.getUsername()
    override suspend fun getAccessToken(): String? = storyPreferences.getAccessToken()

    override suspend fun clearTokens() = storyPreferences.clearTokens()

    override suspend fun clearUsername() = storyPreferences.clearUsername()
}