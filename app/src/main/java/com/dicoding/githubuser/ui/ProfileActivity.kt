package com.dicoding.githubuser.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.databinding.ActivityProfileBinding
import com.dicoding.githubuser.model.InternalStoragePhoto
import com.dicoding.githubuser.utils.Constants.Companion.FILE_TYPE
import com.dicoding.githubuser.utils.Constants.Companion.IMG
import com.dicoding.githubuser.utils.Constants.Companion.PROFILE
import com.dicoding.githubuser.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val prefViewModel: PreferenceViewModel by viewModels()
    private lateinit var filePath: Uri
    private lateinit var bitmap: Bitmap

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if (result != null) {

                filePath = result
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(this.contentResolver, filePath)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }
                saveImageToInternalStorage(bitmap)

                lifecycleScope.launch {
                    val imageList = loadImageFromInternalStorage()
                    for (img in imageList) {
                        if (img.name.contains(PROFILE)) {
                            Glide.with(this@ProfileActivity)
                                .load(img.bitmap)
                                .circleCrop()
                                .into(binding.imgProfile)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.profileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {

            prefViewModel.getProfileImg.observe(this@ProfileActivity) {
                if (it != R.drawable.github_prototype_icon.toString()) {
                    loadData()
                } else {
                    Glide.with(this@ProfileActivity)
                        .load(AppCompatResources.getDrawable(this@ProfileActivity,
                            it.toInt()))
                        .circleCrop()
                        .into(imgProfile)
                }
            }

            saveBtn.setOnClickListener {
                if (!profileName.text.isNullOrEmpty() && !profileEmail.text.isNullOrEmpty()) {
                    saveData()
                    finish()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        getString(R.string.validation),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            imgBtn.setOnClickListener {
                resultLauncher.launch(IMG)
            }

            profileName.doOnTextChanged { text, _, _, _ ->
                if (text?.length == 0) {
                    textInputLayoutName.error = getString(R.string.text_input_error)
                } else {
                    textInputLayoutName.error = null
                }
            }

            profileEmail.doOnTextChanged { text, _, _, _ ->
                if (text?.length == 0) {
                    textInputLayoutEmail.error = getString(R.string.text_input_error)
                } else {
                    textInputLayoutEmail.error = null
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Boolean {
        return try {
            this.openFileOutput("$PROFILE$FILE_TYPE", MODE_PRIVATE).use { outputStream ->
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    throw IOException(getString(R.string.profile_error))
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun saveData() {
        binding.apply {

            prefViewModel.saveProfileName(profileName.text.toString())
            prefViewModel.saveProfileEmail(profileEmail.text.toString())
            if (this@ProfileActivity::filePath.isInitialized) {
                prefViewModel.saveProfileImg(filePath.toString())
            }
        }
        Toast.makeText(
            this,
            getString(R.string.data_saved),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadData() {

        binding.apply {

            prefViewModel.getProfileImg.observe(this@ProfileActivity) {
                if (!it.isNullOrEmpty()) {

                    lifecycleScope.launch {

                        if (prefViewModel.getProfileName.first() != getString(R.string.name)
                            && prefViewModel.getProfileEmail.first() != getString(R.string.email)
                        ) {
                            profileName.setText(prefViewModel.getProfileName.first())
                            profileEmail.setText(prefViewModel.getProfileEmail.first())

                            imgBtn.setImageDrawable(AppCompatResources.getDrawable(this@ProfileActivity,
                                R.drawable.image_btn2))

                            val imageList = loadImageFromInternalStorage()
                            for (img in imageList) {
                                if (img.name.contains(PROFILE)) {
                                    Glide.with(this@ProfileActivity)
                                        .load(img.bitmap)
                                        .circleCrop()
                                        .into(imgProfile)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadImageFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = this@ProfileActivity.filesDir.listFiles()
            files?.filter {
                it.canRead() && it.isFile && it.name.endsWith(FILE_TYPE)
            }?.map {
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bitmap)
            } ?: emptyList()
        }
    }
}