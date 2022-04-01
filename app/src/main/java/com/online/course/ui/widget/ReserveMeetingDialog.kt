package com.online.course.ui.widget

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.online.course.R
import com.online.course.databinding.DialogReserveMeetingBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.BaseResponse
import com.online.course.model.MeetingReserve
import com.online.course.model.ReserveTimeMeeting
import com.online.course.model.Timing
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ReserveMeetingDialogPresenterImpl
import java.text.SimpleDateFormat
import java.util.*


class ReserveMeetingDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogReserveMeetingBinding
    private lateinit var mMeetingReserve: MeetingReserve
    private lateinit var mPresenter: Presenter.ReserveMeetingDialogPresenter
    private var mSelectedChip: Chip? = null
    private var mSelectedTimeId: Int? = null
    private var mLastSelectedCalendar: Calendar? = null
    private var mUserId = 0
    val noTimingDays =
        mutableListOf(
            "saturday",
            "sunday",
            "monday",
            "tuesday",
            "wednesday",
            "thursday",
            "friday"
        )

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogReserveMeetingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mMeetingReserve = requireArguments().getParcelable(App.MEETING)!!
        mUserId = requireArguments().getInt(App.USER_ID)

        mPresenter = ReserveMeetingDialogPresenterImpl(this)

        initCalendar()
        mBinding.reserveMeetingDialogAddBtn.setOnClickListener(this)
        mBinding.reserveMeetingDialogCancelBtn.setOnClickListener(this)
    }

    private fun initCalendar() {
        var calendar = Calendar.getInstance();
        calendar.time = Date()
        calendar.add(Calendar.MONTH, 6)

        val calendarView = mBinding.reserveMeetingCalendarView
        calendarView.minDate = System.currentTimeMillis()
        calendarView.maxDate = calendar.timeInMillis

        val timings = mMeetingReserve.timings
        for (timing in timings) {
            val timingLowerCase = timing.key.toLowerCase(Locale.ENGLISH)
            if (timingLowerCase in noTimingDays && timing.value.isNotEmpty()) {
                noTimingDays.remove(timingLowerCase)
            }
        }


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val cd = Calendar.getInstance()
            cd[year, month] = dayOfMonth
            if (cd == mLastSelectedCalendar) return@setOnDateChangeListener
            val dayOfWeek = getDayOfWeek(cd)

            if (dayOfWeek in noTimingDays) {
                mBinding.reserveMeetingDialogTimeChipGroupProgressBar.visibility = View.GONE
                mBinding.reserveMeetingDialogMeetingCountTv.text = getString(R.string.no_timing_is_available)
                mBinding.reserveMeetingDialogUnavailableTv.visibility = View.VISIBLE
                getTimingsForDay(cd, false)
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.unavaiable),
                    getString(R.string.no_meeting_available),
                    ToastMaker.Type.ERROR
                )
            } else {
                getTimingsForDay(cd, true)
            }
        }

        if (noTimingDays.size != 7) {
            calendar = Calendar.getInstance()

            for (i in 0..6) {
                val day = getDayOfWeek(calendar)
                if (canReserve(day)) {
                    calendarView.date = calendar.timeInMillis
                    break
                }

                calendar.add(Calendar.DATE, 1)
            }

            getTimingsForDay(calendar, true)
        } else {
            calendarView.date = System.currentTimeMillis()
            getTimingsForDay(Calendar.getInstance(), false)
        }
    }

    private fun getTimingsForDay(cd: Calendar, request: Boolean) {
        mBinding.reserveMeetingDialogDateTv.text =
            Utils.getDateFromTimestamp(cd.timeInMillis / 1000)
        mBinding.reserveMeetingDialogAddBtn.isEnabled = false
        mBinding.reserveMeetingDialogTimeChipGroup.removeAllViews()
        mLastSelectedCalendar = cd
        if (request) {
            mBinding.reserveMeetingDialogTimeChipGroupProgressBar.visibility = View.VISIBLE
            mPresenter.getAvailableMeetingTimes(mUserId, getDate(cd))
        }
    }

    private fun canReserve(today: String): Boolean {
        for (timing in mMeetingReserve.timings) {
            if (today == timing.key) {
                for (time in timing.value) {
                    if (time.canReserve) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun onTimingsReceived(timings: List<Timing>) {
        mBinding.reserveMeetingDialogTimeChipGroupProgressBar.visibility = View.GONE

        if (timings.isNotEmpty()) {
            val timingTxt = "${timings.size} ${getString(R.string.meeting_times_are_available)}"
            mBinding.reserveMeetingDialogMeetingCountTv.text = timingTxt
            mBinding.reserveMeetingDialogUnavailableTv.visibility = View.GONE
        } else {
            mBinding.reserveMeetingDialogUnavailableTv.visibility = View.VISIBLE
            mBinding.reserveMeetingDialogMeetingCountTv.text =
                getString(R.string.no_timing_is_available)
        }

        for (timing in timings) {
            val horizontalPadding = Utils.changeDpToPx(requireContext(), 8f).toInt()
            val verticalPadding = Utils.changeDpToPx(requireContext(), 15f).toInt()
            val chip = Chip(context)

            val drawable = ChipDrawable.createFromAttributes(
                requireContext(), null, 0,
                R.style.Widget_MaterialComponents_Chip_Choice
            )

            drawable.setPadding(
                horizontalPadding,
                verticalPadding,
                horizontalPadding,
                verticalPadding
            )

            drawable.shapeAppearanceModel =
                ShapeAppearanceModel.builder()
                    .setAllCorners(CornerFamily.ROUNDED, Utils.changeDpToPx(requireContext(), 15f))
                    .build()

            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )

            val bgColors = intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.green_accent),
                ContextCompat.getColor(requireContext(), R.color.white)
            )

            drawable.chipBackgroundColor = ColorStateList(states, bgColors)
            drawable.chipStrokeColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_accent
                    )
                )
            drawable.chipStrokeWidth = Utils.changeDpToPx(requireContext(), 1f)
            drawable.shapeAppearanceModel.withCornerSize(10f)
            drawable.setTextSize(resources.getDimension(R.dimen.textsize_14d))

            val colors = intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.white),
                ContextCompat.getColor(requireContext(), R.color.green_accent)
            )

            chip.setChipDrawable(drawable)
            chip.text = timing.time
            chip.gravity = Gravity.CENTER
            chip.typeface = ResourcesCompat.getFont(requireContext(), R.font.regular)
            chip.setTextColor(ColorStateList(states, colors))
            chip.tag = timing.id

            chip.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    if (!timing.canReserve) {
                        chip.isChecked = false
                        ToastMaker.show(
                            requireContext(),
                            getString(R.string.error),
                            getString(R.string.this_time_already_reserved),
                            ToastMaker.Type.ERROR
                        )
                        return@setOnCheckedChangeListener
                    }

                    mSelectedChip?.isChecked = false
                    mSelectedTimeId = chip.tag as Int
                    mSelectedChip = chip
                    mBinding.reserveMeetingDialogAddBtn.isEnabled = true
                    mBinding.reserveMeetingDialogAddBtn.text =
                        ("${getString(R.string.add_to_cart)} (${
                            Utils.formatPrice(
                                requireContext(),
                                mMeetingReserve.price
                            )
                        })")
                } else {
                    mSelectedTimeId = null
                    mSelectedChip = null
                    mBinding.reserveMeetingDialogAddBtn.text = getString(R.string.add_to_cart)
                    mBinding.reserveMeetingDialogAddBtn.isEnabled = false
                }
            }
            mBinding.reserveMeetingDialogTimeChipGroup.addView(chip)
        }
    }

    private fun getDayOfWeek(calendar: Calendar): String {
        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        return dayFormat.format(calendar.time).toLowerCase(Locale.ENGLISH)
    }

    private fun getDate(calendar: Calendar): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateTimeFormat.format(calendar.time)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.reserveMeetingDialogCancelBtn -> {
                dismiss()
            }

            R.id.reserveMeetingDialogAddBtn -> {
                val reserveMeeting = ReserveTimeMeeting()
                reserveMeeting.date = getDate(mLastSelectedCalendar!!)
                reserveMeeting.timeId = mSelectedTimeId!!

                mPresenter.reserveMeeting(reserveMeeting)
            }
        }
    }

    fun onMeetingReserved(response: BaseResponse) {
        if (context == null) return

        val title: String
        val type: ToastMaker.Type
        if (response.isSuccessful) {
            title = getString(R.string.success)
            type = ToastMaker.Type.SUCCESS
        } else {
            title = getString(R.string.error)
            type = ToastMaker.Type.ERROR
        }
        ToastMaker.show(requireContext(), title, response.message, type)

        if (response.isSuccessful) {
            dismiss()
        }
    }
}