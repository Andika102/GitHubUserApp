package com.dicoding.githubuser.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.utils.Constants
import com.dicoding.githubuser.utils.Constants.Companion.DETAIL_FRAGMENT
import com.dicoding.githubuser.utils.loadImageFromInternalStorage
import com.dicoding.githubuser.utils.setUITheme
import com.dicoding.githubuser.viewmodel.MainViewModel
import com.dicoding.githubuser.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private val prefViewModel: PreferenceViewModel by viewModels()
    private val args: MainActivityArgs? by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.main_nav)

        if (intent.extras != null && args?.origin == DETAIL_FRAGMENT) {
            navGraph.setStartDestination(R.id.favoriteFragment)
            binding.bottomNav.selectedItemId = R.id.favoriteFragment
        } else {
            navGraph.setStartDestination(R.id.homeFragment)
        }

        navController.graph = navGraph

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.favoriteFragment
            ), binding.drawerLayout
        )

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.apply {
            mainToolbar.setupWithNavController(navController, appBarConfiguration)

            setupActionBarWithNavController(navController, appBarConfiguration)

            navView.apply {
                setupWithNavController(navController)

                val themeItem = menu.findItem(R.id.themeSwitch)
                val switchCompat = menu.findItem(R.id.themeSwitch).actionView as SwitchCompat
                val profileItem = menu.findItem(R.id.profileFragment)

                themeItem.isCheckable = false

                lifecycleScope.launch {
                    val isChecked = prefViewModel.getUITheme.first()
                    switchCompat.isChecked = isChecked
                    setUITheme(themeItem, isChecked, prefViewModel)
                }

                switchCompat.setOnClickListener {
                    setUITheme(themeItem, switchCompat.isChecked, prefViewModel)
                }

                setNavigationItemSelectedListener {
                    if (it.itemId == R.id.profileFragment) {

                        it.isCheckable = false

                        if (navController.currentDestination?.id == R.id.homeFragment) {
                            navController.navigate(R.id.action_homeFragment_to_profileActivity)
                        } else {
                            navController.navigate(R.id.action_favoriteFragment_to_profileActivity)
                        }
                    }
                    true
                }

                val drawerHeader = navView.inflateHeaderView(R.layout.drawer_layout)

                prefViewModel.getProfileImg.observe(this@MainActivity) {

                    if (drawerHeader != null) {
                        val drawerImg = drawerHeader.findViewById<ImageView>(R.id.userImg)
                        val drawerName = drawerHeader.findViewById<TextView>(R.id.userName)
                        val drawerEmail = drawerHeader.findViewById<TextView>(R.id.userEmail)

                        lifecycleScope.launch {
                            drawerName.text = prefViewModel.getProfileName.first()
                            drawerEmail.text = prefViewModel.getProfileEmail.first()

                            if (it != R.drawable.github_prototype_icon.toString()) {
                                val imgList =
                                    loadImageFromInternalStorage(this@MainActivity)
                                for (img in imgList) {
                                    if (img.name.contains(Constants.PROFILE)) {
                                        Glide.with(this@MainActivity)
                                            .load(img.bitmap)
                                            .circleCrop()
                                            .into(drawerImg)
                                    }
                                }
                            } else {
                                Glide.with(this@MainActivity)
                                    .load(AppCompatResources.getDrawable(this@MainActivity,
                                        it.toInt()))
                                    .circleCrop()
                                    .into(drawerImg)
                            }

                            if (it != R.drawable.github_prototype_icon.toString()
                                || prefViewModel.getProfileName.first() != getString(R.string.name)
                                || prefViewModel.getProfileEmail.first() != getString(R.string.email)
                            ) {
                                profileItem.title = getString(R.string.edit_profile)
                            }
                        }
                    }
                }
            }

            bottomNav.setupWithNavController(navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAllItem) {
            deleteAllDialog()
        } else if (item.itemId == R.id.mainSearchItem) {
            navController.navigate(R.id.action_homeFragment_to_searchActivity)
        }
        return true
    }

    private fun deleteAllFavorites() {

        lifecycleScope.launch {

            mainViewModel.deleteAllFavorites()
            Toast.makeText(
                this@MainActivity,
                getString(R.string.delete_all_favorite),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteAllDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_dialog))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteAllFavorites()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
        val builderDialog = builder.create()
        builderDialog.show()

        builderDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this,
                android.R.color.holo_red_light))

        builderDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.greySoft))
    }
}