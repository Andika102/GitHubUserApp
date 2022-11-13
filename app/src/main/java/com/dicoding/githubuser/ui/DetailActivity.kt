package com.dicoding.githubuser.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.databinding.ActivityDetailBinding
import com.dicoding.githubuser.utils.NetworkResult
import com.dicoding.githubuser.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs? by navArgs()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.detailNavHost) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(R.navigation.detail_nav, intent.extras)

        if (mainViewModel.userDetailsResponse.value?.data == null && args?.detail != null) {
            requestData()
        }

        checkData()
    }

    private fun requestData() {

        /** Detail */
        args?.detail?.login?.let { mainViewModel.getUserDetails(it) }

        /** Follower */
        args?.detail?.login?.let { mainViewModel.getUserFollowers(it) }

        /** Following */
        args?.detail?.login?.let { mainViewModel.getUserFollowing(it) }
    }

    private fun checkData() {

        mainViewModel.getFavorite(args?.detail?.id ?: args?.favoriteDetail?.id)
            ?.observe(this) { data ->

                binding.fab.apply {

                    setOnClickListener {
                        if (data != null) {
                            data.id?.let { id -> deleteFromFavorite(id) }
                            setColorFilter(getColor(android.R.color.black))
                        } else {
                            addToFavorite()
                            setColorFilter(getColor(android.R.color.holo_red_light))
                        }
                    }

                    if (data != null) {
                        setColorFilter(getColor(android.R.color.holo_red_light))
                    } else {
                        setColorFilter(getColor(android.R.color.black))
                    }
                }
            }

        mainViewModel.userFollowersResponse.observe(this) { response1 ->
            mainViewModel.userFollowingResponse.observe(this) { response2 ->
                if (response1 !is NetworkResult.Loading && response2 !is NetworkResult.Loading) {
                    binding.fab.visibility = View.VISIBLE
                }
            }
        }

        if (args?.detail == null && args?.favoriteDetail != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.fab.visibility = View.VISIBLE
            }, 1)
        }
    }

    private fun addToFavorite() {

        lifecycleScope.launch {

            mainViewModel.apply {
                userDetailsResponse.observe(this@DetailActivity) { detailsResponse ->
                    userFollowersResponse.observe(this@DetailActivity) { followersResponse ->
                        userFollowingResponse.observe(this@DetailActivity) { followingResponse ->
                            val userId = args?.detail?.id ?: args?.favoriteDetail?.id
                            val userData = detailsResponse?.data ?: args?.favoriteDetail?.favorite
                            val userFollower =
                                followersResponse?.data ?: args?.favoriteDetail?.follower
                            val userFollowing =
                                followingResponse?.data ?: args?.favoriteDetail?.following

                            val data = FavoriteEntity(userId, userData, userFollower, userFollowing)

                            if (userData != null) {
                                mainViewModel.insertFavorite(data)
                            }
                        }
                    }
                }
            }

            Toast.makeText(
                this@DetailActivity,
                getString(R.string.add_favorite),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteFromFavorite(id: Int) {

        lifecycleScope.launch {

            mainViewModel.deleteFavorite(id)
            Toast.makeText(
                this@DetailActivity,
                getString(R.string.delete_favorite),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}