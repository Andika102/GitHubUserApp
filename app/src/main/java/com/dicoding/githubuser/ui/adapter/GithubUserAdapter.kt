package com.dicoding.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.databinding.UserRowBinding
import com.dicoding.githubuser.model.SearchResponse
import com.dicoding.githubuser.model.Users
import com.dicoding.githubuser.utils.GithubDiffUtil

class GithubUserAdapter : RecyclerView.Adapter<GithubUserAdapter.GithubUserViewHolder>() {

    private var users = emptyList<SearchResponse>()

    class GithubUserViewHolder(private var binding: UserRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SearchResponse) {
            binding.user = result
            binding.executePendingBindings()
        }

        companion object {
            fun holderFrom(parent: ViewGroup): GithubUserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bindingLayout = UserRowBinding.inflate(layoutInflater, parent, false)

                return GithubUserViewHolder(bindingLayout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubUserViewHolder {
        return GithubUserViewHolder.holderFrom(parent)
    }

    override fun onBindViewHolder(holder: GithubUserViewHolder, position: Int) {
        val currentUser = users[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setData(newData: Users) {
        val usersDiffUtil = newData.items?.let { GithubDiffUtil(users, it) }
        users = newData.items!!
        usersDiffUtil?.let { DiffUtil.calculateDiff(it) }?.dispatchUpdatesTo(this)
    }
}