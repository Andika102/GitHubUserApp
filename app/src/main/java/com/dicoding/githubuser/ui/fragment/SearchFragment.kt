package com.dicoding.githubuser.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import com.dicoding.githubuser.databinding.FragmentSearchBinding
import com.dicoding.githubuser.ui.adapter.GithubUserAdapter
import com.dicoding.githubuser.ui.adapter.SuggestionAdapter
import com.dicoding.githubuser.utils.Constants.Companion.BLANK
import com.dicoding.githubuser.utils.Constants.Companion.HAS_FOCUS
import com.dicoding.githubuser.utils.Constants.Companion.IS_SEARCHED
import com.dicoding.githubuser.utils.Constants.Companion.SEARCH_TEXT
import com.dicoding.githubuser.utils.MarginDecoration
import com.dicoding.githubuser.utils.NetworkResult
import com.dicoding.githubuser.viewmodel.MainViewModel
import com.dicoding.githubuser.viewmodel.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var searchItem: MenuItem
    private var searchText: String? = null
    private var isSearched: Boolean = false
    private var hasFocus: Boolean = true
    private val searchAdapter by lazy { GithubUserAdapter() }
    private val suggestionAdapter by lazy { SuggestionAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        usersViewModel = ViewModelProvider(requireActivity())[UsersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val appCompatActivity = activity as AppCompatActivity?

        appCompatActivity?.apply {

            setSupportActionBar(binding.toolbar)
            supportActionBar?.title = BLANK
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT)
            isSearched = savedInstanceState.getBoolean(IS_SEARCHED)
            hasFocus = savedInstanceState.getBoolean(HAS_FOCUS)
            when (hasFocus) {
                true -> {
                    binding.searchRecyclerView.visibility = View.GONE
                    if (!mainViewModel.readSuggestions.value.isNullOrEmpty()) showSuggestions()
                }
                false -> {
                    hideSuggestions()
                    binding.searchRecyclerView.visibility = View.VISIBLE
                }
            }
            mainViewModel.searchResponse.observe(viewLifecycleOwner) { response ->
                response.data?.let { searchAdapter.setData(it) }
            }
        }

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                searchItem = menu.findItem(R.id.searchItem)

                searchItem.apply {

                    val searchView = (this.actionView as SearchView)

                    searchView.apply {

                        maxWidth = Integer.MAX_VALUE

                        if (hasFocus) requestFocus()

                        if (searchText != null) setQuery(searchText, false)

                        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {

                                isSearched = true

                                hideSuggestions()

                                clearFocus()

                                if (query != null) {

                                    searchApiData(query)

                                    val suggestionEntity = SuggestionEntity(query)
                                    mainViewModel.insertSuggestion(suggestionEntity)
                                }

                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {

                                if (newText != null) {
                                    searchText = newText

                                    val searchQuery = "%$newText%"

                                    mainViewModel.searchSuggestions(searchQuery)
                                        .observe(viewLifecycleOwner) {
                                            if (it.isNotEmpty() && searchView.hasFocus()) {
                                                showSuggestions()
                                            } else if (it.isEmpty()) {
                                                hideSuggestions()
                                            }
                                            suggestionAdapter.setData(it)
                                        }
                                }

                                return true
                            }

                        })

                        setOnQueryTextFocusChangeListener { _, isFocused ->

                            if (isFocused) {

                                hasFocus = true
                                binding.searchRecyclerView.visibility = View.GONE
                                binding.noUsersLayout.visibility = View.GONE
                                if (!mainViewModel.readSuggestions.value.isNullOrEmpty()) showSuggestions()

                            } else {
                                hasFocus = false
                            }
                        }
                    }

                    suggestionAdapter.setOnSuggestionClickCallback(

                        object : SuggestionAdapter.OnSuggestionClickCallback {
                            override fun onSuggestionClicked(data: SuggestionEntity) {
                                searchView.setQuery(data.suggestion, true)
                            }

                            override fun onCommitIconClicked(data: SuggestionEntity) {
                                searchView.setQuery(data.suggestion, false)
                            }
                        }
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {

                    android.R.id.home -> {

                        val searchView = searchItem.actionView as SearchView

                        if (searchView.hasFocus() && isSearched) {
                            searchView.clearFocus()
                            hideSuggestions()
                            binding.searchRecyclerView.visibility = View.VISIBLE
                        } else if (searchView.hasFocus() && !isSearched) {
                            requireActivity().finish()
                        } else {
                            requireActivity().finish()
                        }
                    }

                    R.id.searchItem -> searchItem.expandActionView()
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupSuggestions()
        readSuggestions()
        setupRecyclerView()

        binding.clearBtn.setOnClickListener {
            mainViewModel.deleteAllSuggestions()
            hideSuggestions()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putString(SEARCH_TEXT, searchText)
            putBoolean(IS_SEARCHED, isSearched)
            putBoolean(HAS_FOCUS, hasFocus)
        }
        super.onSaveInstanceState(outState)
    }

    private fun searchApiData(searchQuery: String) {

        mainViewModel.searchUsers(usersViewModel.applySearchQueries(searchQuery))
        mainViewModel.searchResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerAffect()
                    val users = response.data
                    users?.let { searchAdapter.setData(it) }
                    binding.searchRecyclerView.visibility = View.VISIBLE

                }
                is NetworkResult.Error -> {
                    hideShimmerAffect()
                    binding.noUsersLayout.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
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

    private fun setupRecyclerView() {

        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                MarginDecoration(resources.getDimension(R.dimen.small_margin).toInt())
            )
        }
    }

    private fun setupSuggestions() {

        binding.suggestionsList.apply {
            adapter = suggestionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun readSuggestions() {

        var count = suggestionAdapter.itemCount
        mainViewModel.readSuggestions.observe(viewLifecycleOwner) {

            suggestionAdapter.setData(it)

            if (count == 0 && it.isNotEmpty() && !isSearched) {
                showSuggestions()
            }

            count = suggestionAdapter.itemCount
        }
    }

    private fun showSuggestions() {

        binding.apply {
            clearSuggestionLayout.visibility = View.VISIBLE
            suggestionsList.visibility = View.VISIBLE
        }
    }

    private fun hideSuggestions() {

        binding.apply {
            clearSuggestionLayout.visibility = View.GONE
            suggestionsList.visibility = View.GONE
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
