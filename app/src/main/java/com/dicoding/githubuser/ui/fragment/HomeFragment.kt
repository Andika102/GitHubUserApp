package com.dicoding.githubuser.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.databinding.FragmentHomeBinding
import com.dicoding.githubuser.ui.adapter.GithubUserAdapter
import com.dicoding.githubuser.utils.MarginDecoration
import com.dicoding.githubuser.utils.NetworkResult
import com.dicoding.githubuser.utils.observeOnce
import com.dicoding.githubuser.viewmodel.MainViewModel
import com.dicoding.githubuser.viewmodel.PreferenceViewModel
import com.dicoding.githubuser.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var usersViewModel: UsersViewModel
    private val userAdapter by lazy { GithubUserAdapter() }
    private val prefViewModel: PreferenceViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        usersViewModel = ViewModelProvider(requireActivity())[UsersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.main_menu, menu)

                val searchItem = menu.findItem(R.id.mainSearchItem)

                lifecycleScope.launch {
                    searchItem.isVisible = prefViewModel.getHomeViewState.first()

                    binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

                        val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
                        prefViewModel.saveHomeCollapseState(isCollapsed)
                        searchItem.isVisible = isCollapsed
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.mainSearchItem -> {
                        findNavController().navigate(R.id.action_homeFragment_to_searchActivity)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.apply {

            customSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchActivity)
            }
            lifecycleScope.launch {
                appBarLayout.setExpanded(!prefViewModel.getHomeViewState.first())
            }
        }

        setupRecyclerView()

        if (mainViewModel.usersResponse.value?.data == null) {
            requestApiData()
        } else {
            mainViewModel.usersResponse.observeOnce(viewLifecycleOwner) { response ->
                response.data?.let { userAdapter.setData(it) }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {

        binding.mainRecyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                MarginDecoration(resources.getDimension(R.dimen.small_margin).toInt())
            )
        }
    }

    private fun requestApiData() {

        mainViewModel.getUsers(usersViewModel.applyQueries())

        mainViewModel.usersResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerAffect()
                    response.data?.let { userAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerAffect()
                    binding.noUsersLayout.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerAffect()
                }
            }
        }
    }

    private fun showShimmerAffect() {

        binding.shimmer.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
    }

    private fun hideShimmerAffect() {

        binding.shimmer.apply {
            stopShimmer()
            visibility = View.GONE
        }
    }
}