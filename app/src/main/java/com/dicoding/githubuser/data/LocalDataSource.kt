package com.dicoding.githubuser.data

import com.dicoding.githubuser.data.db.GithubUserDao
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val githubUserDao: GithubUserDao
) {
    //suggestion
    fun readSuggestions(): Flow<List<SuggestionEntity>> {
        return githubUserDao.readSuggestions()
    }

    suspend fun insertQuery(suggestionEntity: SuggestionEntity) {
        githubUserDao.insertQuery(suggestionEntity)
    }

    fun searchSuggestions(query: String): Flow<List<SuggestionEntity>> {
        return githubUserDao.searchSuggestions(query)
    }

    suspend fun deleteAllSuggestions() {
        return githubUserDao.deleteAllSuggestions()
    }

    //favorite
    fun readFavorites(): Flow<List<FavoriteEntity>> {
        return githubUserDao.readFavorites()
    }

    fun getFavorite(id: Int?): Flow<FavoriteEntity>? {
        return githubUserDao.getFavorite(id)
    }

    suspend fun insertFavorite(favoriteEntity: FavoriteEntity) {
        return githubUserDao.insertFavorite(favoriteEntity)
    }

    suspend fun deleteFavorite(id: Int) {
        return githubUserDao.deleteFavorite(id)
    }

    suspend fun deleteAllFavorites() {
        return githubUserDao.deleteAllFavorites()
    }
}