package com.dicoding.githubuser.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.Repository
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.model.UserResponse
import com.dicoding.githubuser.model.Users
import com.dicoding.githubuser.utils.Constants.Companion.TIMEOUT
import com.dicoding.githubuser.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** Room */
    val readSuggestions = repository.local.readSuggestions().asLiveData()
    val readFavorites = repository.local.readFavorites().asLiveData()

    fun insertSuggestion(suggestionEntity: SuggestionEntity) {
        viewModelScope.launch(IO) {
            repository.local.insertQuery(suggestionEntity)
        }
    }

    fun searchSuggestions(query: String): LiveData<List<SuggestionEntity>> {
        return repository.local.searchSuggestions(query).asLiveData()
    }

    fun deleteAllSuggestions() {
        viewModelScope.launch(IO) {
            repository.local.deleteAllSuggestions()
        }
    }

    fun insertFavorite(favoriteEntity: FavoriteEntity) {
        viewModelScope.launch(IO) {
            repository.local.insertFavorite(favoriteEntity)
        }
    }

    fun getFavorite(id: Int?): LiveData<FavoriteEntity>? {
        return repository.local.getFavorite(id)?.asLiveData()
    }

    fun deleteFavorite(id: Int) {
        viewModelScope.launch(IO) {
            repository.local.deleteFavorite(id)
        }
    }

    fun deleteAllFavorites() {
        viewModelScope.launch(IO) {
            repository.local.deleteAllFavorites()
        }
    }

    /** Retrofit */
    var usersResponse: MutableLiveData<NetworkResult<Users>> = MutableLiveData()
    var searchResponse: MutableLiveData<NetworkResult<Users>> = MutableLiveData()
    var userDetailsResponse: MutableLiveData<NetworkResult<UserResponse>> = MutableLiveData()
    var userFollowersResponse: MutableLiveData<NetworkResult<List<FollowerResponse>>> = MutableLiveData()
    var userFollowingResponse: MutableLiveData<NetworkResult<List<FollowerResponse>>> = MutableLiveData()

    fun getUsers(queries: Map<String, String>) = viewModelScope.launch {
        getUsersSafeCall(queries)
    }

    fun searchUsers(searchQueries: Map<String, String>) = viewModelScope.launch {
        searchUsersSafeCall(searchQueries)
    }

    fun getUserDetails(path: String) = viewModelScope.launch {
        getUserDetailsSafeCall(path)
    }

    fun getUserFollowers(path: String) = viewModelScope.launch {
        getUserFollowersSafeCall(path)
    }

    fun getUserFollowing(path: String) = viewModelScope.launch {
        getUserFollowingSafeCall(path)
    }

    private suspend fun getUsersSafeCall(queries: Map<String, String>) {
        usersResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getUsers(queries)
                usersResponse.value = handleSearchResponse(response)
            } catch (e: Exception) {
                usersResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_not_found))
            }
        } else {
            usersResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.no_internet_connection))
        }
    }

    private suspend fun searchUsersSafeCall(searchQueries: Map<String, String>) {
        searchResponse.value = NetworkResult.Loading()

        searchResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getUsers(searchQueries)
                searchResponse.value = handleSearchResponse(response)
            } catch (e: Exception) {
                searchResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_not_found))
            }
        } else {
            searchResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.no_internet_connection))
        }
    }

    private suspend fun getUserDetailsSafeCall(path: String) {
        userDetailsResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getUserDetails(path)
                userDetailsResponse.value = handleUserDetailsResponse(response)
            } catch (e: Exception) {
                userDetailsResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_details_not_found))
            }
        } else {
            userDetailsResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.no_internet_connection))
        }
    }

    private suspend fun getUserFollowersSafeCall(path: String) {
        userFollowersResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getUserFollowers(path)
                userFollowersResponse.value = handleUserFollowersResponse(response)
            } catch (e: Exception) {
                userFollowersResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_follower_not_found))
            }
        } else {
            userFollowersResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.no_internet_connection))
        }
    }

    private suspend fun getUserFollowingSafeCall(path: String) {
        userFollowingResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getUserFollowing(path)
                userFollowingResponse.value = handleUserFollowingResponse(response)
            } catch (e: Exception) {
                userFollowingResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_following_not_found))
            }
        } else {
            userFollowingResponse.value = NetworkResult.Error(getApplication<Application>().resources.getString(R.string.no_internet_connection))
        }
    }

    private fun handleSearchResponse(response: Response<Users>): NetworkResult<Users> {
        when {
            response.message().toString().contains(TIMEOUT) -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.access_limited))
            }
            response.body()?.items.isNullOrEmpty() -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_not_found))
            }
            response.isSuccessful -> {
                val users = response.body()
                return NetworkResult.Success(users)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleUserDetailsResponse(response: Response<UserResponse>): NetworkResult<UserResponse> {
        return when {
            response.message().toString().contains(TIMEOUT) -> {
                NetworkResult.Error(getApplication<Application>().resources.getString(R.string.timeout))
            }
            response.code() == 402 -> {
                NetworkResult.Error(getApplication<Application>().resources.getString(R.string.access_limited))
            }
            response.isSuccessful -> {
                val userDetail = response.body()
                NetworkResult.Success(userDetail)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleUserFollowersResponse(response: Response<List<FollowerResponse>>): NetworkResult<List<FollowerResponse>> {
        when {
            response.message().toString().contains(TIMEOUT) -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.access_limited))
            }
            response.body().isNullOrEmpty() -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_follower_not_found))
            }
            response.isSuccessful -> {
                val users = response.body()
                return NetworkResult.Success(users)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleUserFollowingResponse(response: Response<List<FollowerResponse>>): NetworkResult<List<FollowerResponse>> {
        when {
            response.message().toString().contains(TIMEOUT) -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.access_limited))
            }
            response.body().isNullOrEmpty() -> {
                return NetworkResult.Error(getApplication<Application>().resources.getString(R.string.user_following_not_found))
            }
            response.isSuccessful -> {
                val users = response.body()
                return NetworkResult.Success(users)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}