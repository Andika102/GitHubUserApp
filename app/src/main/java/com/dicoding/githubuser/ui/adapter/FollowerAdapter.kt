package com.dicoding.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.databinding.FollowerRowBinding
import com.dicoding.githubuser.model.FollowerResponse
import com.dicoding.githubuser.utils.GithubDiffUtil

class FollowerAdapter : RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder>() {

    private var users = emptyList<FollowerResponse>()

    class FollowerViewHolder(private var binding: FollowerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: FollowerResponse) {
            binding.follower = result
            binding.executePendingBindings()
        }

        companion object {
            fun holderFrom(parent: ViewGroup): FollowerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bindingLayout = FollowerRowBinding.inflate(layoutInflater, parent, false)

                return FollowerViewHolder(bindingLayout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        return FollowerViewHolder.holderFrom(parent)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        val currentUser = users[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setData(newData: List<FollowerResponse>) {
        val usersDiffUtil = GithubDiffUtil(users, newData)
        users = newData
        usersDiffUtil.let { DiffUtil.calculateDiff(it) }.dispatchUpdatesTo(this)
    }
}