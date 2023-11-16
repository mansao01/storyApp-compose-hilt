package com.mansao.mystoryappcomposehilt.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mansao.mystoryappcompose.data.local.model.StoryRemoteKeys
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem
import com.mansao.mystoryappcomposehilt.data.local.StoryDatabase
import com.mansao.mystoryappcomposehilt.data.network.ApiService
import com.mansao.mystoryappcomposehilt.data.preferences.StoryPreferences

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val storyPreferences: StoryPreferences
) : RemoteMediator<Int, ListStoryItem>() {

    private val storyDao = storyDatabase.storyDao()
    private val remoteKeysDao = storyDatabase.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstTime(state)
                    val prevPage = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }

            }
            val token = storyPreferences.getAccessToken()
            val response = apiService.getStories(
                token = "Bearer $token",
                page = currentPage,
                size = state.config.pageSize
            )
            val responseData = response.listStory
            val endOfPaginationReached = responseData.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDao.deleteAllStories()
                    remoteKeysDao.deleteRemoteKeys()
                }
                val keys = responseData.map { story ->
                    StoryRemoteKeys(
                        id = story.id,
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                }
                remoteKeysDao.insertAll(remoteKey = keys)
                storyDao.insertStory(story = responseData)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ListStoryItem>
    ): StoryRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeysDao.getRemoteKeysId(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstTime(
        state: PagingState<Int, ListStoryItem>
    ): StoryRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { story ->
                remoteKeysDao.getRemoteKeysId(id = story.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, ListStoryItem>
    ): StoryRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { story ->
                remoteKeysDao.getRemoteKeysId(id = story.id)
            }
    }
}