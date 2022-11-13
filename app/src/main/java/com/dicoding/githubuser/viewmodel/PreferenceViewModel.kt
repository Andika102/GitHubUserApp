package com.dicoding.githubuser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuser.utils.GithubUserPreference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceDataStore = GithubUserPreference(application)

    //theme
    val getUITheme = preferenceDataStore.uiTheme

    fun saveTheme(isDarkMode: Boolean) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveTheme(isDarkMode)
        }
    }

    //state
    val getHomeViewState = preferenceDataStore.homeViewState

    fun saveHomeCollapseState(isCollapsed: Boolean) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveCollapseHomeState(isCollapsed)
        }
    }

    val getDetailViewState = preferenceDataStore.detailViewState

    fun saveDetailCollapseState(isCollapsed: Boolean) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveCollapseDetailState(isCollapsed)
        }
    }

    //profile
    val getProfileImg = preferenceDataStore.profileImg.asLiveData()
    val getProfileName = preferenceDataStore.profileName
    val getProfileEmail = preferenceDataStore.profileEmail

    fun saveProfileImg(img: String) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveProfileImg(img)
        }
    }

    fun saveProfileName(name: String) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveProfileName(name)
        }
    }

    fun saveProfileEmail(email: String) {
        viewModelScope.launch(IO) {
            preferenceDataStore.saveProfileEmail(email)
        }
    }
}