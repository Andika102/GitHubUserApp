package com.dicoding.githubuser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.databinding.FragmentDetailBinding
import com.dicoding.githubuser.model.SearchResponse
import com.dicoding.githubuser.model.UserResponse
import com.dicoding.githubuser.ui.adapter.PagerAdapter
import com.dicoding.githubuser.utils.Constants
import com.dicoding.githubuser.utils.Constants.Companion.DETAIL_FRAGMENT
import com.dicoding.githubuser.utils.Constants.Companion.FAVORITE_BUNDLE
import com.dicoding.githubuser.utils.Constants.Companion.SHARE_TEXT
import com.dicoding.githubuser.utils.NetworkResult
import com.dicoding.githubuser.utils.observeOnce
import com.dicoding.githubuser.viewmodel.MainViewModel
import com.dicoding.githubuser.viewmodel.PreferenceViewModel
import com.google.android.material.tabs.TabLayoutMediator
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private val prefViewModel: PreferenceViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()
    private var detailBundle: SearchResponse? = null
    private var favoriteBundle: FavoriteEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        if (args.detail != null) {
            detailBundle = args.detail
        } else {
            favoriteBundle = args.favoriteDetail
        }

        val appCompatActivity = activity as AppCompatActivity?

        appCompatActivity?.apply {

            setSupportActionBar(binding.detailToolbar)
            supportActionBar?.title = detailBundle?.login ?: favoriteBundle?.favorite?.login
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(AppCompatResources.getDrawable(this,
                R.drawable.back))
        }

        val userBundle = Bundle()
        favoriteBundle?.let {
            userBundle.putParcelable(FAVORITE_BUNDLE, it)
        }

        binding.apply {

            lifecycleScope.launch {
                detailAppBarLayout.setExpanded(!prefViewModel.getDetailViewState.first())

                detailAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

                    val isCollapsed = abs(verticalOffset) == appBarLayout.totalScrollRange
                    prefViewModel.saveDetailCollapseState(isCollapsed)
                }
            }
        }

        if (mainViewModel.userDetailsResponse.value?.data == null) {
            requestData()
        } else {
            if (detailBundle == null && favoriteBundle != null) {
                lifecycleScope.launch {
                    setViewDataLocal()
                }
            } else {
                mainViewModel.userDetailsResponse.observeOnce(this) {
                    setViewDataRemote(it)
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().finish()
                    }
                    R.id.detailShareItem -> {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = Constants.HTML_TYPE
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${Constants.GITHUB_URL}${args.detail?.login ?: args.favoriteDetail?.favorite?.login}"
                            )
                        }
                        startActivity(Intent.createChooser(intent, SHARE_TEXT))
                    }
                    R.id.favoriteFragment -> {
                        val action = DetailFragmentDirections.actionDetailFragmentToMainActivity(
                            DETAIL_FRAGMENT)
                        findNavController().navigate(action)
                    }
                }

                return true
            }

        })

        Glide.with(requireActivity())
            .load(detailBundle?.avatarUrl ?: favoriteBundle?.favorite?.avatarUrl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(binding.bannerImg)

        Glide.with(requireActivity())
            .load(detailBundle?.avatarUrl ?: favoriteBundle?.favorite?.avatarUrl)
            .circleCrop()
            .into(binding.detailImg)

        val fragments = ArrayList<Fragment>()
        fragments.apply {
            add(FollowerFragment())
            add(FollowingFragment())
        }

        val titles = ArrayList<String>()
        titles.apply {
            add(getString(R.string.followers))
            add(getString(R.string.following))
        }

        val pagerAdapter = PagerAdapter(
            userBundle,
            fragments,
            requireActivity()
        )

        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = titles[pos]
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestData() {

        if (detailBundle == null && favoriteBundle != null) {

            lifecycleScope.launch {
                setViewDataLocal()
            }
        } else {

            mainViewModel.userDetailsResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        setViewDataRemote(response)
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }
    }

    private fun setViewDataRemote(response: NetworkResult<UserResponse>?) {
        if (response != null) {

            binding.apply {
                nameText.text = response.data?.name ?: getString(R.string.no_data)
                companyText.text = response.data?.company ?: getString(R.string.no_data)
                followersNum.text = response.data?.followers.toString()
                repositoryNum.text = response.data?.publicRepos.toString()
                followingNum.text = response.data?.following.toString()
            }
        }
    }

    private fun setViewDataLocal() {
        if (favoriteBundle != null) {
            binding.apply {
                nameText.text = favoriteBundle?.favorite?.name ?: getString(R.string.no_data)
                companyText.text =
                    favoriteBundle?.favorite?.company ?: getString(R.string.no_data)
                followersNum.text = favoriteBundle?.favorite?.followers.toString()
                repositoryNum.text = favoriteBundle?.favorite?.publicRepos.toString()
                followingNum.text = favoriteBundle?.favorite?.following.toString()
            }
        }
    }

}