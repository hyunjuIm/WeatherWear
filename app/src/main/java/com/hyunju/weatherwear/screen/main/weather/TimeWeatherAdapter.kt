package com.hyunju.weatherwear.screen.main.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.databinding.ItemTimeWeatherBinding
import com.hyunju.weatherwear.extension.clear
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.model.TimeWeatherModel

class TimeWeatherAdapter : ListAdapter<TimeWeatherModel, TimeWeatherAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemTimeWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TimeWeatherModel) = with(binding) {
            timeTextView.text = item.time

            weatherImageView.clear()
            weatherImageView.load(item.icon)

            temperatureTextView.text = "${item.temperature}°"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeWeatherAdapter.ViewHolder = ViewHolder(
        ItemTimeWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TimeWeatherAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TimeWeatherModel>() {
            override fun areItemsTheSame(
                oldItem: TimeWeatherModel,
                newItem: TimeWeatherModel
            ): Boolean {
                return (oldItem.date + oldItem.time) == (newItem.date + newItem.time)
            }

            override fun areContentsTheSame(
                oldItem: TimeWeatherModel,
                newItem: TimeWeatherModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}