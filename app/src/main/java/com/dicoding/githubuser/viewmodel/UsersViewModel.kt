package com.dicoding.githubuser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dicoding.githubuser.utils.Constants.Companion.DEFAULT_Q
import com.dicoding.githubuser.utils.Constants.Companion.DEFAULT_REF
import com.dicoding.githubuser.utils.Constants.Companion.DEFAULT_S
import com.dicoding.githubuser.utils.Constants.Companion.DEFAULT_TYPE
import com.dicoding.githubuser.utils.Constants.Companion.QUERY_Q
import com.dicoding.githubuser.utils.Constants.Companion.QUERY_REF
import com.dicoding.githubuser.utils.Constants.Companion.QUERY_S
import com.dicoding.githubuser.utils.Constants.Companion.QUERY_TYPE

class UsersViewModel(application: Application) : AndroidViewModel(application) {

    fun applyQueries(): HashMap<String, String> {

        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_Q] = DEFAULT_Q
        queries[QUERY_REF] = DEFAULT_REF
        queries[QUERY_S] = DEFAULT_S
        queries[QUERY_TYPE] = DEFAULT_TYPE

        return queries

    }

    fun applySearchQueries(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_Q] = searchQuery

        return queries
    }
}
