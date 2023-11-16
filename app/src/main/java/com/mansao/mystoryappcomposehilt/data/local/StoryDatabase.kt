package com.mansao.mystoryappcomposehilt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mansao.mystoryappcompose.data.local.dao.RemoteKeysDao
import com.mansao.mystoryappcompose.data.local.dao.StoryDao
import com.mansao.mystoryappcompose.data.local.model.StoryRemoteKeys
import com.mansao.mystoryappcompose.data.network.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, StoryRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}