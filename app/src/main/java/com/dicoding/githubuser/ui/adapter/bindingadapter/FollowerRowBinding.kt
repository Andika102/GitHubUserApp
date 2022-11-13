package com.dicoding.githubuser.ui.adapter.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object FollowerRowBinding {

    @BindingAdapter("loadFollowerFromUrl")
    @JvmStatic
    fun loadFollowerFromUrl(img: ImageView, imgUrl: String) {
        Glide.with(img.context)
            .load(imgUrl)
            .circleCrop()
            .into(img)
    }

    @BindingAdapter("setFollowerId")
    @JvmStatic
    fun setFollowerId(textView: TextView, id: String) {
        "ID: $id".let { textView.text = it }
    }

    @BindingAdapter("setFollowerUsername")
    @JvmStatic
    fun setFollowerUsername(textView: TextView, username: String) {
        textView.text = username
    }
}