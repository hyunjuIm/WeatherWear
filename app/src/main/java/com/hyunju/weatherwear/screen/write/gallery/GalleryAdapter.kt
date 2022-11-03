package com.hyunju.weatherwear.screen.write.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ItemGalleryPhotoBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.model.GalleryModel

class GalleryAdapter(val clickItem: (GalleryModel) -> Unit) :
    ListAdapter<GalleryModel, GalleryAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGalleryPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryModel) = with(binding) {
            photoImageView.load(item.uri.toString())
            checkButton.setBackgroundResource(
                if (item.isSelected) R.drawable.bg_rounded_blue else R.drawable.bg_rounded_gray
            )
            root.setOnClickListener { clickItem(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder =
        ViewHolder(
            ItemGalleryPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GalleryModel>() {
            override fun areItemsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
                return oldItem == newItem
            }
        }
    }

}