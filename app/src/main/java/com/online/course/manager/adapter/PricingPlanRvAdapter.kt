package com.online.course.manager.adapter

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemPricingPlanBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.PricingPlan

class PricingPlanRvAdapter(plans: List<PricingPlan>, private val rv: RecyclerView) :
    BaseArrayAdapter<PricingPlan, PricingPlanRvAdapter.ViewHolder>(plans) {

    private var mSelectedCallback: ItemCallback<PricingPlan>? = null
    private var mSelectedPosition: Int? = null

    fun getSelectedItem(): PricingPlan? {
        return if (mSelectedPosition == null) null else items[mSelectedPosition!!]
    }

    fun setOnItemSelectedListener(callback: ItemCallback<PricingPlan>) {
        mSelectedCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPricingPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = items[position]
        val context = holder.itemView.context

        holder.init(plan)
        holder.binding.itemPricingPlanTitleTv.text =
            ("${plan.title} (${plan.discount}%) ${context.getString(R.string.off)}")
        holder.binding.itemPricingPlanDescTv.text = plan.description
        holder.binding.itemPricingPlanPriceTv.text =
            Utils.formatPrice(holder.itemView.context, plan.price)

        if (mSelectedPosition == position) {
            val transition = holder.itemView.background as TransitionDrawable
            transition.startTransition(300)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.bg_transition_border_gray81_to_accent
            ) as TransitionDrawable
        }
    }

    inner class ViewHolder(val binding: ItemPricingPlanBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var mIsInit = false

        fun init(plan: PricingPlan) {
            if (mIsInit) return

            mIsInit = true

            if (plan.isValid) {
                val transitionDrawable = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.bg_transition_border_gray81_to_accent
                ) as TransitionDrawable

                itemView.background = transitionDrawable

                itemView.setOnClickListener(this)
            } else {
                binding.itemPricingPlanImg.setBackgroundResource(R.drawable.circle_gray81)
            }
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            val plan = items[position]

            if (mSelectedPosition != null) {
                if (mSelectedPosition == position) {
                    return
                }

                try {
                    val viewHolder =
                        rv.findViewHolderForAdapterPosition(mSelectedPosition!!) as ViewHolder
                    (viewHolder.itemView.background as TransitionDrawable).reverseTransition(
                        300
                    )
                } catch (ex: Exception) {
                }
            }

            val transition = itemView.background as TransitionDrawable
            transition.startTransition(300)

            mSelectedPosition = position
            mSelectedCallback?.onItem(plan)
        }
    }
}