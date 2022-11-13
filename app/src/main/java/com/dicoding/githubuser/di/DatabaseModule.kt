package com.dicoding.githubuser.di

import android.content.Context
import androidx.room.Room
import com.dicoding.githubuser.data.db.GithubUserDatabase
import com.dicoding.githubuser.utils.Constants.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        GithubUserDatabase::class.java,
        DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: GithubUserDatabase) = database.githubUserDao()
}