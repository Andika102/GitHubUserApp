package com.dicoding.githubuser.ui.adapter.bindingadapter

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.model.SearchResponse
import com.dicoding.githubuser.ui.fragment.HomeFragmentDirections
import com.dicoding.githubuser.ui.fragment.SearchFragmentDirections

object UsersRowBinding {

    @BindingAdapter("onUserClickListener")
    @JvmStatic
    fun onUserClickListener(userRowCard: CardView, user: SearchResponse) {
        userRowCard.setOnClickListener {
            Log.d("UsersRowBinding", "onUserClickListener")
            try {
                when (userRowCard.findNavController().graph.id) {

                    R.id.mainNav -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToDetailActivity(user)
                        userRowCard.findNavController().navigate(action)
                    }

                    R.id.searchNav -> {
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToDetailActivity(user)
                        userRowCard.findNavController().navigate(action)
                    }
                }
            } catch (e: Exception) {
                Log.d("UsersRowBinding", "onUserClickListener $e")
            }
        }
    }

    @BindingAdapter("loadFromUrl")
    @JvmStatic
    fun loadFromUrl(img: ImageView, imgUrl: String) {
        Glide.with(img.context)
            .load(imgUrl)
            .circleCrop()
            .into(img)
    }

    @BindingAdapter("setUserId")
    @JvmStatic
    fun setUserId(textView: TextView, userId: String) {
        "ID: $userId".let { textView.text = it }
    }

    @BindingAdapter("setUsername")
    @JvmStatic
    fun setUsername(textView: TextView, username: String?) {
        textView.text = username ?: textView.context.getString(R.string.no_data)
    }
}