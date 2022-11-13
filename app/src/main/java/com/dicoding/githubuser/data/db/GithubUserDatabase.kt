package com.dicoding.githubuser.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import com.dicoding.githubuser.utils.TypeConverter

@Database(
    entities = [SuggestionEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class GithubUserDatabase: RoomDatabase() {

    abstract fun githubUserDao(): GithubUserDao
}