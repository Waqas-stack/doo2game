package com.online.course.ui.widget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.course.R
import com.online.course.databinding.DialogPricingPlansBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.adapter.PricingPlanRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.course.model.AddToCart
import com.online.course.model.BaseResponse
import com.online.course.model.Course
import com.online.course.model.PricingPlan
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.ui.MainActivity

class PricingPlansDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    ItemCallback<PricingPlan> {

    private lateinit var mBinding: DialogPricingPlansBinding
    private lateinit var mCourse: Course

    private val mResCallback = object : ItemCallback<BaseResponse> {
        override fun onItem(res: BaseResponse, vararg args: Any) {
            if (context == null) return

            if (res.isSuccessful) {
                (activity as MainActivity).updateCart()
                dismiss()
            } else {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.error),
                    res.message,
                    ToastMaker.Type.ERROR
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogPricingPlansBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        val adapter = PricingPlanRvAdapter(mCourse.pricingPlans, mBinding.pricingPlanRv)
        adapter.setOnItemSelectedListener(this)
        mBinding.pricingPlanRv.adapter = adapter

        mBinding.pricingPlanCancelBtn.setOnClickListener(this)
        mBinding.pricingPlanAddToCartBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pricing_plan_cancel_btn -> {
                dismiss()
            }

            R.id.pricing_plan_add_to_cart_btn -> {
                val plan =
                    (mBinding.pricingPlanRv.adapter as PricingPlanRvAdapter).getSelectedItem()

                val addToCart = AddToCart()
                addToCart.pricingPlanId = plan!!.id
                addToCart.webinarId = mCourse.id

                val presenter = CommonApiPresenterImpl.getInstance()
                presenter.addToCart(addToCart, mResCallback)
            }
        }
    }

    override fun onItem(plan: PricingPlan, vararg args: Any) {
        mBinding.pricingPlanAddToCartBtn.isEnabled = true
    }

}