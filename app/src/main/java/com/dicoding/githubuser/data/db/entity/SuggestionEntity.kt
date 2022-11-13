package com.dicoding.githubuser.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.githubuser.utils.Constants.Companion.SUGGESTION_TABLE

@Entity(tableName = SUGGESTION_TABLE)
data class SuggestionEntity(
    @PrimaryKey(autoGenerate = false)
    var suggestion: String
)