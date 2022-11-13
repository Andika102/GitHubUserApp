package com.dicoding.githubuser.ui.adapter.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object SuggestionsRowBinding {

    @BindingAdapter("setSuggestion")
    @JvmStatic
    fun setSuggestion(textView: TextView, suggestion: String) {
        textView.text = suggestion
    }
}