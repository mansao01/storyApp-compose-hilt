package com.mansao.mystoryappcomposehilt.di

import android.content.Context
import androidx.room.Room
import com.mansao.mystoryappcomposehilt.BuildConfig
import com.mansao.mystoryappcomposehilt.data.local.StoryDatabase
import com.mansao.mystoryappcomposehilt.data.network.ApiConst
import com.mansao.mystoryappcomposehilt.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context,
            StoryDatabase::class.java,
            "story_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCharacterDao(database: StoryDatabase) = database.storyDao()


    @Singleton
    @Provides
    fun getApiService(): Retrofit {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(ApiConst.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }
        return apiService
    }
}