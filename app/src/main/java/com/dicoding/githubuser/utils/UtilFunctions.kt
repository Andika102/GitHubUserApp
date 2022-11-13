package com.dicoding.githubuser.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.dicoding.githubuser.R
import com.dicoding.githubuser.model.InternalStoragePhoto
import com.dicoding.githubuser.viewmodel.PreferenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun setUITheme(item: MenuItem, checked: Boolean, viewModel: PreferenceViewModel) {
    if (checked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        viewModel.saveTheme(true)
        item.setIcon(R.drawable.dark)
        item.title = Constants.DARK_MODE
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        viewModel.saveTheme(false)
        item.setIcon(R.drawable.light)
        item.title = Constants.LIGHT_MODE
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object: Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

suspend fun loadImageFromInternalStorage(context: Context): List<InternalStoragePhoto> {
    return withContext(Dispatchers.IO) {
        val files = context.filesDir.listFiles()
        files?.filter {
            it.canRead() && it.isFile && it.name.endsWith(Constants.FILE_TYPE)
        }?.map {
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            InternalStoragePhoto(it.name, bitmap)
        } ?: emptyList()
    }
}