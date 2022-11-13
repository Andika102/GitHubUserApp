package com.dicoding.githubuser.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.model.UserResponse
import com.dicoding.githubuser.utils.Constants.Companion.FAVORITE_TABLE
import com.dicoding.githubuser.utils.Constants.Companion.FOLLOWER_TABLE
import com.dicoding.githubuser.utils.Constants.Companion.FOLLOWING_TABLE
import kotlinx.parcelize.Parcelize

@Entity(tableName = FAVORITE_TABLE)
@Parcelize
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Int?,

    @ColumnInfo(name = FAVORITE_TABLE)
    var favorite: UserResponse?,

    @ColumnInfo(name = FOLLOWER_TABLE)
    var follower: List<FollowerResponse>?,

    @ColumnInfo(name = FOLLOWING_TABLE)
    var following: List<FollowerResponse>?
) : Parcelable

