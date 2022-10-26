package com.hyunju.weatherwear.screen.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.databinding.ItemPickClothesBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.util.clothes.Clothes

class PickClothesAdapter : ListAdapter<Clothes, PickClothesAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemPickClothesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Clothes) {
            binding.clothesIcon.load(item.image)
            binding.clothesTextView.text = item.text
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PickClothesAdapter.ViewHolder = ViewHolder(
        ItemPickClothesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: PickClothesAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Clothes>() {

            override fun areItemsTheSame(oldItem: Clothes, newItem: Clothes): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(oldItem: Clothes, newItem: Clothes): Boolean {
                return oldItem == newItem
            }
        }
    }
}