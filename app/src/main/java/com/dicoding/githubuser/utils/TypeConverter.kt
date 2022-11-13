package com.dicoding.githubuser.utils

import androidx.room.TypeConverter
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.model.UserResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun userResponseToString(userResponse: UserResponse?): String {
        return gson.toJson(userResponse)
    }

    @TypeConverter
    fun stringToUserResponse(data: String): UserResponse? {
        val listType = object : TypeToken<UserResponse>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun followerResponseToString(followerResponse: List<FollowerResponse>?): String {
        return gson.toJson(followerResponse)
    }

    @TypeConverter
    fun stringToFollowerResponse(data: String): List<FollowerResponse>? {
        val listType = object : TypeToken<List<FollowerResponse>>() {}.type
        return gson.fromJson(data, listType)
    }
}