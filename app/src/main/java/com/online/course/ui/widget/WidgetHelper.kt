package com.online.course.ui.widget

import android.app.Dialog
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

object WidgetHelper {

    fun removeBottomSheetDialogHalfExpand(dialog: Dialog?) {
        if (dialog != null) {
            val d = dialog as BottomSheetDialog

            val bottomSheetView =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val bottomSheet = BottomSheetBehavior.from<FrameLayout?>(bottomSheetView!!)
            bottomSheet.skipCollapsed = true
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}