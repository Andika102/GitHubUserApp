package com.dicoding.githubuser.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.githubuser.R
import com.dicoding.githubuser.utils.Constants.Companion.APP_PREFERENCE
import com.dicoding.githubuser.utils.Constants.Companion.IS_COLLAPSED_DETAIL
import com.dicoding.githubuser.utils.Constants.Companion.IS_COLLAPSED_HOME
import com.dicoding.githubuser.utils.Constants.Companion.PROFILE_EMAIL
import com.dicoding.githubuser.utils.Constants.Companion.PROFILE_IMG
import com.dicoding.githubuser.utils.Constants.Companion.PROFILE_NAME
import com.dicoding.githubuser.utils.Constants.Companion.UI_THEME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_PREFERENCE)

class GithubUserPreference(private var context: Context) {
    //theme state
    suspend fun saveTheme(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UI_THEME] = isDarkMode
        }
    }

    val uiTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val uiTheme = preferences[UI_THEME] ?: false
            uiTheme
        }

    //home view state
    suspend fun saveCollapseHomeState(isCollapsed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_COLLAPSED_HOME] = isCollapsed
        }
    }

    val homeViewState: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val viewState = preferences[IS_COLLAPSED_HOME] ?: false
            viewState
        }

    //detail view state
    suspend fun saveCollapseDetailState(isCollapsed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_COLLAPSED_DETAIL] = isCollapsed
        }
    }

    val detailViewState: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val viewState = preferences[IS_COLLAPSED_DETAIL] ?: false
            viewState
        }

    //profile
    suspend fun saveProfileImg(img: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMG] = img
        }
    }

    val profileImg: Flow<String> = context.dataStore.data
        .map { preferences ->
            val profileImg = preferences[PROFILE_IMG] ?: R.drawable.github_prototype_icon.toString()
            profileImg
        }

    suspend fun saveProfileName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_NAME] = name
        }
    }

    val profileName: Flow<String> = context.dataStore.data
        .map { preferences ->
            val name = preferences[PROFILE_NAME] ?: context.getString(R.string.name)
            name
        }

    suspend fun saveProfileEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_EMAIL] = email
        }
    }

    val profileEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            val email = preferences[PROFILE_EMAIL] ?: context.getString(R.string.email)
            email
        }


}