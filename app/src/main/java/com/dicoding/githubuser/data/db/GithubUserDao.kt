package com.dicoding.githubuser.data.db

import androidx.room.*
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GithubUserDao {
    //suggestion
    @Query("SELECT * FROM suggestion")
    fun readSuggestions(): Flow<List<SuggestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(suggestionEntity: SuggestionEntity)

    @Query("SELECT * FROM suggestion WHERE suggestion LIKE :queries")
    fun searchSuggestions(queries: String): Flow<List<SuggestionEntity>>

    @Query("DELETE FROM suggestion")
    suspend fun deleteAllSuggestions()

    //favorite
    @Query("SELECT * FROM favorite")
    fun readFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite WHERE id = :id")
    fun getFavorite(id: Int?): Flow<FavoriteEntity>?

    @Insert
    suspend fun insertFavorite(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteFavorite(id: Int)

    @Query("DELETE FROM favorite")
    suspend fun deleteAllFavorites()
}