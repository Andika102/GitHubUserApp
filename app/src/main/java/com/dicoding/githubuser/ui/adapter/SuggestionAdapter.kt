package com.dicoding.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.data.db.entity.SuggestionEntity
import com.dicoding.githubuser.databinding.SuggestionRowBinding
import com.dicoding.githubuser.utils.GithubDiffUtil

class SuggestionAdapter : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    private var oldSuggestions = emptyList<SuggestionEntity>()

    private lateinit var suggestionCallback: OnSuggestionClickCallback

    fun setOnSuggestionClickCallback(suggestionCallback: OnSuggestionClickCallback) {
        this.suggestionCallback = suggestionCallback
    }

    interface OnSuggestionClickCallback{
        fun onSuggestionClicked(data: SuggestionEntity)
        fun onCommitIconClicked(data: SuggestionEntity)
    }

    class SuggestionViewHolder(var binding: SuggestionRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SuggestionEntity) {
            binding.suggestions = result
            binding.executePendingBindings()
        }

        companion object {
            fun holderForm(parent: ViewGroup): SuggestionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bindingLayout = SuggestionRowBinding.inflate(layoutInflater, parent, false)

                return SuggestionViewHolder(bindingLayout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder.holderForm(parent)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestionRow = oldSuggestions[position]
        holder.apply {

            bind(suggestionRow)
            binding.apply {

                suggestionName.setOnClickListener {
                    suggestionCallback.onSuggestionClicked(suggestionRow)
                }
                commitIcon.setOnClickListener {
                    suggestionCallback.onCommitIconClicked(suggestionRow)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return oldSuggestions.size
    }

    fun setData(newSuggestions: List<SuggestionEntity>) {
        val suggestionDiffUtil = GithubDiffUtil(oldSuggestions, newSuggestions)
        val suggestionResult = DiffUtil.calculateDiff(suggestionDiffUtil)
        oldSuggestions = newSuggestions
        suggestionResult.dispatchUpdatesTo(this)
    }
}