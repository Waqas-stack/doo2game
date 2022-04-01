package com.online.course.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemDashboardGeneralBinding
import com.online.course.databinding.ItemSlideMenuBinding
import com.online.course.model.MenuItem

class DasboardGeneralSliderAdapter(items: List<MenuItem>) :
    BaseArrayAdapter<MenuItem, DasboardGeneralSliderAdapter.ViewHolder>(items) {

    enum class Type(val value: Int) {
        BALANCE(1),
        BADGE(2);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDashboardGeneralBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.itemDashboardGeneralTitleTv.text = item.title
        binding.itemDashboardGeneralDescTv.text = item.desc

        when (item.type) {
            Type.BALANCE.value -> {
                binding.itemDashboardGeneralImg.setBackgroundResource(R.drawable.round_view_accent_corner20)
                binding.itemDashboardGeneralImg.setImageResource(R.drawable.ic_wallet_white)
            }

            Type.BADGE.value -> {
                binding.itemDashboardGeneralImg.setBackgroundResource(R.drawable.round_view_gold_corner20)
                binding.itemDashboardGeneralProgressIndicator.progress = item.progress
                binding.itemDashboardGeneralProgressIndicatorTv.text = ("${item.progress}%")

                binding.itemDashboardGeneralProgressIndicator.visibility = View.VISIBLE
                binding.itemDashboardGeneralProgressIndicatorTv.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(val binding: ItemDashboardGeneralBinding) :
        RecyclerView.ViewHolder(binding.root)
}