package com.dicoding.githubuser.data

import com.dicoding.githubuser.data.api.GithubApi
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.model.UserResponse
import com.dicoding.githubuser.model.Users
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val githubApi: GithubApi) {

    suspend fun getUsers(queries: Map<String, String>): Response<Users> {
        return githubApi.getUsers(queries)
    }

    suspend fun getUserDetails(path: String): Response<UserResponse> {
        return githubApi.getUserDetails(path)
    }

    suspend fun getUserFollowers(path: String): Response<List<FollowerResponse>> {
        return githubApi.getUserFollowers(path)
    }

    suspend fun getUserFollowing(path: String): Response<List<FollowerResponse>>  {
        return githubApi.getUserFollowing(path)
    }
}