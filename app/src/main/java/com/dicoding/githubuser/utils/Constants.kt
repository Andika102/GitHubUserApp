package com.dicoding.githubuser.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class Constants {

    companion object {
        const val BASE_URL = "https://api.github.com"
        const val GITHUB_URL = "https://github.com/"

        // API queries
        const val QUERY_Q = "q"
        const val QUERY_REF = "ref"
        const val QUERY_S = "s"
        const val QUERY_TYPE = "type"

        // Preferences
        const val DEFAULT_Q = "followers:>=20000"
        const val DEFAULT_REF= "searchresults"
        const val DEFAULT_S = "followers"
        const val DEFAULT_TYPE = "Users"

        // Database
        const val DB_NAME = "GithubUserDB"
        const val SUGGESTION_TABLE = "suggestion"
        const val FAVORITE_TABLE = "favorite"
        const val FOLLOWER_TABLE = "follower"
        const val FOLLOWING_TABLE = "following"

        // Other
        const val BLANK = ""
        const val DETAIL_FRAGMENT = "detailFragment"
        const val FAVORITE_BUNDLE = "favoriteBundle"
        const val SEARCH_TEXT = "searchText"
        const val IS_SEARCHED = "isSearched"
        const val HAS_FOCUS = "hasFocus"
        const val DARK_MODE = "Dark Mode"
        const val LIGHT_MODE = "Light Mode"
        const val HTML_TYPE = "text/html"
        const val SHARE_TEXT = "Share Using ..."
        const val TIMEOUT = "timeout"
        const val APP_PREFERENCE = "githubUserPreference"
        val IS_COLLAPSED_HOME = booleanPreferencesKey("isCollapsedHome")
        val IS_COLLAPSED_DETAIL = booleanPreferencesKey("isCollapsedDetail")
        val UI_THEME = booleanPreferencesKey("uiTheme")
        val PROFILE_IMG = stringPreferencesKey("profileImg")
        val PROFILE_NAME = stringPreferencesKey("profileName")
        val PROFILE_EMAIL = stringPreferencesKey("profileEmail")
        const val PROFILE = "profile"
        const val FILE_TYPE = ".jpg"
        const val IMG = "image/*"
        const val UI_TEST_QUERY = "sidiqpermana"
        const val WAIT = "wait for"
        const val MILLISECONDS = "milliseconds"
    }
}