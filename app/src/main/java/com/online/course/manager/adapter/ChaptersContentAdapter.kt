package com.online.course.manager.adapter

import android.app.Activity
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemCourseCommon2Binding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterSessionItem
import com.online.course.model.ChapterTextItem
import com.online.course.model.Course
import com.online.course.model.view.ChapterView
import com.online.course.ui.MainActivity
import com.online.course.ui.SplashScreenActivity
import com.online.course.ui.frag.CourseChapterItemFrag
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlin.math.roundToInt


class ChaptersContentAdapter(
    groups: List<ExpandableGroup<*>>?,
    private val course: Course,
    private val activity: Activity,
    private val mOfflineMode: Boolean
) : ExpandableRecyclerViewAdapter<ChaptersContentAdapter.ChapterViewHolder, ChaptersContentAdapter.ChapterItemViewHolder>(
    groups
) {

    class ChapterViewHolder(val binding: ItemCourseCommon2Binding) :
        GroupViewHolder(binding.root) {
        init {
            initCard(false)
            binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_arrow_bottom_gull_gray)
            val padding = Utils.changeDpToPx(itemView.context, 23f).toInt()
            binding.itemCourseCommon2Img.setPadding(padding, padding, padding, padding)
        }

        private fun initCard(expand: Boolean) {
            val params =
                binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
            params.topMargin = Utils.changeDpToPx(itemView.context, 8f).toInt()
            if (expand) {
                params.bottomMargin = 0
                binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_top_corner15)
            } else {
                params.bottomMargin = Utils.changeDpToPx(itemView.context, 8f).toInt()
                binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_corner15)
            }
            binding.itemCourseCommonCard2.requestLayout()
        }

        override fun expand() {
            initCard(true)
            binding.itemCourseCommon2CheckImg.visibility = View.INVISIBLE
            binding.itemCourseCommon2BottomDash.visibility = View.VISIBLE
        }

        override fun collapse() {
            initCard(false)
            binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_arrow_bottom_gull_gray)
            binding.itemCourseCommon2BottomDash.visibility = View.INVISIBLE
        }
    }

    class ChapterItemViewHolder(val binding: ItemCourseCommon2Binding) :
        ChildViewHolder(binding.root) {

        init {
            val params =
                binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
            params.topMargin = 0
            params.bottomMargin = 0
            binding.itemCourseCommonCard2.requestLayout()
        }
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(
            ItemCourseCommon2Binding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChapterItemViewHolder {
        return ChapterItemViewHolder(
            ItemCourseCommon2Binding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onBindChildViewHolder(
        holder: ChapterItemViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val context = holder.itemView.context
        val item = (group as ChapterView).items[childIndex]

        holder.binding.itemCourseCommon2TitleTv.text = item.title(context)
        holder.binding.itemCourseCommon2DescTv.text = item.desc(context)
        holder.binding.itemCourseCommon2Img.setBackgroundResource(item.imgBgResource(context))
        holder.binding.itemCourseCommon2Img.setImageResource(item.imgResource(context))
        if (item.passed()) {
            holder.binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_check_green)
        } else {
            holder.binding.itemCourseCommon2CheckImg.visibility = View.GONE
        }

        if (childIndex == group.itemCount - 1) {
            holder.binding.itemCourseCommon2TopDash.visibility = View.VISIBLE
            holder.binding.itemCourseCommon2BottomDash.visibility = View.INVISIBLE

        } else {
            holder.binding.itemCourseCommon2BottomDash.visibility = View.VISIBLE
            holder.binding.itemCourseCommon2TopDash.visibility = View.VISIBLE
        }

        val params = holder.binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
        if (childIndex == group.itemCount - 1) {
            params.bottomMargin = Utils.changeDpToPx(context, 8f).toInt()
            holder.binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_bottom_corner15)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.binding.itemCourseCommonCard2.foreground =
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect_bottom_corner_15)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.binding.itemCourseCommonCard2.foreground =
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect)
            }
            holder.binding.itemCourseCommonCard2.setBackgroundResource(R.color.white)
            params.bottomMargin = 0
        }
        holder.binding.itemCourseCommonCard2.requestLayout()

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(App.COURSE, course)
            //flat position starts with 1 not 0
            bundle.putInt(App.CHAPTER_POSITION, flatPosition - 1)
            bundle.putInt(App.CHAPTER_ITEM_POSITION, childIndex)
            bundle.putBoolean(App.OFFLINE, mOfflineMode)

            when (item) {
                is ChapterSessionItem -> {
                    bundle.putParcelable(App.ITEM, item)
                }

                is ChapterFileItem -> {
                    bundle.putParcelable(App.ITEM, item)
                }

                is ChapterTextItem -> {
                    bundle.putParcelable(App.ITEM, item)
                }
            }

            val courseChapterPage = CourseChapterItemFrag()
            courseChapterPage.arguments = bundle
            if (activity is MainActivity) {
                activity.transact(courseChapterPage)
            } else if (activity is SplashScreenActivity) {
                activity.transact(courseChapterPage)
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: ChapterViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val chapterView = group as ChapterView

        holder.binding.itemCourseCommon2Img.setImageResource(R.drawable.ic_grid_white)
        holder.binding.itemCourseCommon2Img.setBackgroundResource(R.drawable.round_view_accent_corner10)
        holder.binding.itemCourseCommon2TitleTv.text = chapterView.title
        holder.binding.itemCourseCommon2DescTv.text = chapterView.description
    }
}