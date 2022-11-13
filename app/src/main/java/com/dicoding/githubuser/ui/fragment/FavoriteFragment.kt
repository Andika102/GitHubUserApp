package com.dicoding.githubuser.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.databinding.FragmentFavoriteBinding
import com.dicoding.githubuser.ui.adapter.FavoriteAdapter
import com.dicoding.githubuser.utils.MarginDecoration
import com.dicoding.githubuser.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private val favoriteAdapter by lazy { FavoriteAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setupRecyclerView()
        requestFavoriteData()

        val appCompatActivity = (activity as AppCompatActivity)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menu.clear()
                menuInflater.inflate(R.menu.favorite_menu, menu)

                if (Navigation.findNavController(appCompatActivity.findViewById(R.id.navHostFragment)).currentDestination?.id == R.id.favoriteFragment) {

                    val deleteAllItem = menu.findItem(R.id.deleteAllItem)

                    lifecycleScope.launch {
                        showDeleteAllDialog(deleteAllItem)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        })

        favoriteAdapter.setOnFavoriteClickCallback(object :
            FavoriteAdapter.OnFavoriteClickCallback {

            override fun onDeleteBtnClicked(id: Int) {
                deleteFromFavorite(id)
            }

            override fun onCardClicked(data: FavoriteEntity) {
                val action =
                    FavoriteFragmentDirections.actionFavoriteFragmentToDetailActivity(null, data)
                findNavController().navigate(action)
            }

        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {

        binding.favoriteRecyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                MarginDecoration(resources.getDimension(R.dimen.small_margin).toInt())
            )
        }
    }

    private fun requestFavoriteData() {

        mainViewModel.readFavorites.observe(viewLifecycleOwner) {
            favoriteAdapter.setData(it)
            binding.noUsersLayout.isVisible = it.isNullOrEmpty()
        }
    }

    private fun deleteFromFavorite(id: Int) {

        lifecycleScope.launch {

            mainViewModel.deleteFavorite(id)
            Toast.makeText(
                requireContext(),
                getString(R.string.delete_favorite),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteAllDialog(item: MenuItem?) {

        if (view != null) {
            mainViewModel.readFavorites.observe(viewLifecycleOwner) {
                item?.isVisible = !it.isNullOrEmpty()
            }
        }
    }
}