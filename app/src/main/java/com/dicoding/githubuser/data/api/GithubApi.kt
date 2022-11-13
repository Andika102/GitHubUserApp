package com.dicoding.githubuser.data.api

import com.dicoding.githubuser.BuildConfig
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.model.UserResponse
import com.dicoding.githubuser.model.Users
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface GithubApi {

    @GET("/search/users")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getUsers(
        @QueryMap queries: Map<String, String>
    ) : Response<Users>

    @GET("users/{username}")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getUserDetails(
        @Path("username") path: String
    ) : Response<UserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getUserFollowers(
        @Path("username") path: String
    ) : Response<List<FollowerResponse>>

    @GET("users/{username}/following")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getUserFollowing(
        @Path("username") path: String
    ) : Response<List<FollowerResponse>>
}
