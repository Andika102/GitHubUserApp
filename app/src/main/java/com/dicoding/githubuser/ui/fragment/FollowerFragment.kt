package com.dicoding.githubuser.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.databinding.FragmentFollowerBinding
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.ui.adapter.FollowerAdapter
import com.dicoding.githubuser.utils.Constants.Companion.FAVORITE_BUNDLE
import com.dicoding.githubuser.utils.MarginDecoration
import com.dicoding.githubuser.utils.NetworkResult
import com.dicoding.githubuser.utils.observeOnce
import com.dicoding.githubuser.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class FollowerFragment : Fragment() {

    private var _binding: FragmentFollowerBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var favoriteBundle: FavoriteEntity? = null
    private val followerAdapter by lazy { FollowerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFollowerBinding.inflate(inflater, container, false)

        val args = arguments
        if (args?.getParcelable<Parcelable>(FAVORITE_BUNDLE) != null) {
            favoriteBundle = args.getParcelable(FAVORITE_BUNDLE)
        }

        setupRecyclerView()

        if (mainViewModel.userFollowersResponse.value?.data == null) {
            requestData()
        } else {
            if (favoriteBundle != null) {
                favoriteBundle?.follower?.let { setAdapterData(it) }
            } else {
                mainViewModel.userFollowingResponse.observeOnce(viewLifecycleOwner) { response ->
                    if (!response.data.isNullOrEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        followerAdapter.setData(response.data)
                    } else {
                        binding.noUsersLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestData() {

        if (favoriteBundle != null) {

            if (favoriteBundle?.follower != null) {
                binding.progressBar.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    favoriteBundle?.follower?.let { setAdapterData(it) }
                }, 1000)
            } else {
                binding.noUsersLayout.visibility = View.VISIBLE
            }
        } else {

            mainViewModel.userFollowersResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.followerRecyclerView.visibility = View.VISIBLE

                        if (!response.data.isNullOrEmpty()) {
                            followerAdapter.setData(response.data)
                        } else {
                            binding.noUsersLayout.visibility = View.VISIBLE
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.noUsersLayout.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {

        binding.followerRecyclerView.adapter = followerAdapter
        binding.followerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.followerRecyclerView.addItemDecoration(MarginDecoration(resources.getDimension(R.dimen.small_margin)
            .toInt()))
    }

    private fun setAdapterData(list: List<FollowerResponse>) {

        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
            binding.followerRecyclerView.visibility = View.VISIBLE
            followerAdapter.setData(list)
        }
    }
}