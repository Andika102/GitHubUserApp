package com.dicoding.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.data.db.entity.FavoriteEntity
import com.dicoding.githubuser.databinding.FavoriteRowBinding
import com.dicoding.githubuser.utils.GithubDiffUtil

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favorite = emptyList<FavoriteEntity>()

    private lateinit var favoriteCallback: OnFavoriteClickCallback

    fun setOnFavoriteClickCallback(favoriteCallback: OnFavoriteClickCallback) {
        this.favoriteCallback = favoriteCallback
    }

    interface OnFavoriteClickCallback {
        fun onDeleteBtnClicked(id: Int)
        fun onCardClicked(data: FavoriteEntity)
    }

    class FavoriteViewHolder(var binding: FavoriteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteEntity) {
            binding.favorite = favorite
            binding.executePendingBindings()
        }

        companion object {
            fun holderFrom(parent: ViewGroup): FavoriteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val bindingLayout = FavoriteRowBinding.inflate(layoutInflater, parent, false)

                return FavoriteViewHolder(bindingLayout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.holderFrom(parent)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteRow = favorite[position]
        holder.apply {

            bind(favoriteRow)
            binding.apply {

                card.setOnClickListener {
                    favoriteCallback.onCardClicked(favoriteRow)
                }

                deleteItemBtn.setOnClickListener {
                    favoriteRow.id?.let { id -> favoriteCallback.onDeleteBtnClicked(id) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favorite.size
    }

    fun setData(newData: List<FavoriteEntity>) {
        val usersDiffUtil = GithubDiffUtil(favorite, newData)
        favorite = newData
        usersDiffUtil.let { DiffUtil.calculateDiff(it) }.dispatchUpdatesTo(this)
    }
}