package com.online.course.model

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.online.course.R
import com.online.course.databinding.DialogSelectionBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.CountryUtils
import com.online.course.manager.adapter.BaseArrayAdapter
import com.online.course.manager.adapter.SelectionDialogRvAdapter
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverDialog
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class SelectionDialog<T> : NetworkObserverDialog(), OnItemClickListener, View.OnClickListener {

    private lateinit var mCountries: ArrayList<Country>
    private lateinit var mLngs: ArrayList<Language>

    private lateinit var mOnItemSelection: ItemSelection<T>
    private lateinit var mBinding: DialogSelectionBinding

    enum class Selection : Serializable {
        Country, Language
    }

    private val mSearchTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            searchInCountries(s.toString())
        }
    }

    companion object {
        fun <T> getInstance(): SelectionDialog<T> {
            return SelectionDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogSelectionBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.selectionHeaderCancelBtn.setOnClickListener(this)

        if (arguments?.getSerializable(App.SELECTION_TYPE) == Selection.Country) {
            setCountries()

        } else if (arguments?.getSerializable(App.SELECTION_TYPE) == Selection.Language) {
            mBinding.selectionSearchEdtx.visibility = View.GONE
            setLngs()
        }
    }

    private fun setLngs() {
        mBinding.selectionHeaderTv.text = getString(R.string.select_lang)
        mBinding.selectionSearchEdtx.visibility = View.GONE
        mBinding.selectionProgressBar.visibility = View.GONE
        mBinding.selectionRv.visibility = View.VISIBLE
        val supportedLangs = App.appConfig.supportedLangs

        mLngs = ArrayList()
        for (lang in supportedLangs) {
            val language = Language()
            language.code = lang.key
            language.name = lang.value
            language.title = lang.value
            language.img = BuildVars.LNG_FLAG[lang.value.toUpperCase(Locale.ENGLISH)]
            mLngs.add(language)
        }

        val adpt = SelectionDialogRvAdapter(mLngs)
        with(mBinding.selectionRv) {
            adapter = adpt
            addOnItemTouchListener(ItemClickListener(this, this@SelectionDialog))
        }
    }

    private fun setCountries() {
        mBinding.selectionProgressBar.visibility = View.GONE
        mBinding.selectionRv.visibility = View.VISIBLE
        mCountries = ArrayList(CountryUtils.getAllCountries(requireContext()))
        for (country in mCountries) {
            country.title = "${country.name} (${country.callingCode})"
            country.img = CountryUtils.getFlagDrawableResId(country)
        }
        val adpt = SelectionDialogRvAdapter(CountryUtils.getAllCountries(requireContext()))
        with(mBinding.selectionRv) {
            adapter = adpt
            addOnItemTouchListener(ItemClickListener(this, this@SelectionDialog))
        }
        mBinding.selectionSearchEdtx.addTextChangedListener(mSearchTextWatcher)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
        }
    }

    private fun searchInCountries(text: String) {
        val adapter: SelectionDialogRvAdapter =
            mBinding.selectionRv.adapter as SelectionDialogRvAdapter
        @Suppress("UNCHECKED_CAST") val items = adapter.items as MutableList<SelectionItem>
        items.clear()

        if (this::mCountries.isInitialized) {
            if (text.isEmpty()) {
                items.addAll(mCountries)
            } else {
                for (country in mCountries) {
                    if (country.name?.toLowerCase(Locale.getDefault())
                            ?.contains(text.toLowerCase(Locale.getDefault()))!! || country.callingCode?.toLowerCase(
                            Locale.getDefault()
                        )
                            ?.contains(text.toLowerCase(Locale.getDefault()))!!
                    ) {
                        items.add(country)
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    @Suppress("UNCHECKED_CAST")
    override fun onClick(view: View?, position: Int, id: Int) {
        mOnItemSelection.onItemSelected((mBinding.selectionRv.adapter as BaseArrayAdapter<T, *>).items[position])
        dismiss()
    }

    override fun onClick(v: View?) {
        dismiss()
    }

    fun setOnItemSelected(itemSelection: ItemSelection<T>) {
        mOnItemSelection = itemSelection
    }

    interface ItemSelection<T> {
        fun onItemSelected(item: T)
    }
}