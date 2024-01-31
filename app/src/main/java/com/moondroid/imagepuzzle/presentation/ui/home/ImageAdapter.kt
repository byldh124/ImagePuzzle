package com.moondroid.imagepuzzle.presentation.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moondroid.imagepuzzle.R
import com.moondroid.imagepuzzle.common.PuzzleItem
import com.moondroid.imagepuzzle.databinding.RecyclerItemBinding

class ImageAdapter(val changeImage: (Int) -> Unit) :
    ListAdapter<PuzzleItem, ImageAdapter.ViewHolder>(ImageDiffUtil) {

    @SuppressLint("NotifyDataSetChanged")
    override fun submitList(list: MutableList<PuzzleItem>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)
            item.bitmap?.let {
                Glide.with(binding.iv2).load(it).into(binding.iv2)
                binding.root.setOnClickListener { changeImage(position) }
            } ?: run {
                Glide.with(binding.iv2).load(R.drawable.bg_white).into(binding.iv2)
            }
        }
    }

    object ImageDiffUtil : DiffUtil.ItemCallback<PuzzleItem>() {
        override fun areItemsTheSame(oldItem: PuzzleItem, newItem: PuzzleItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PuzzleItem, newItem: PuzzleItem): Boolean {
            return oldItem.index == newItem.index
        }
    }
}