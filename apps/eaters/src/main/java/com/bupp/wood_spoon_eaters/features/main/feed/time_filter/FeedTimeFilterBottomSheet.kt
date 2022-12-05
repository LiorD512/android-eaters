package com.bupp.wood_spoon_eaters.features.main.feed.time_filter

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.databinding.BottomSheetFeedTimeFilterBinding
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.eatwoodspoon.android_utils.views.setLongerSafeOnClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date


class FeedTimeFilterBottomSheet() : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomSheetFeedTimeFilterBinding::bind)
    private var currentTimeFilter: SingleColumnTimePickerBottomSheet.DeliveryTimeParam? = null
    private val viewModel by viewModel<FeedTimeFilterViewModel>()
    private var timeFilterBtnList = listOf<TextView>()

    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_feed_time_filter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet ?: return@setOnShowListener)
            behavior.isDraggable = false
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resources = resources
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val parent = view.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // LEFT
                0,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // RIGHT
                0
            )
            parent.layoutParams = layoutParams
            parent.setBackgroundResource(R.drawable.floating_bottom_sheet_bkg)
        }

        currentTimeFilter = requireArguments().getParcelable(CURRENT_FILTER_ARGS_KEY)
        viewModel.onItemClicked(currentTimeFilter?.deliveryTimeType)

        observeViewModelState()
        initUi()
    }


    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        handleCurrentTimeFilter(state.selectedTimeFilterBtn)
                    }
                }
            }
        }
    }

    private fun initUi() {
        binding.apply {

            timeFilterBtnList = listOf(
                feedTimeFilterAnytimeBtn,
                feedTimeTodayBtn,
                feedTimeFilterScheduleBtn
            )

            feedTimeFilterTimePicker.initSimpleDatesDataFromTomorrow(
                14,
                viewModel.getSelectedData()
            )

            feedTimePickerCloseBtn.setOnClickListener {
                dismiss()
            }

            feedTimeTodayBtn.setLongerSafeOnClickListener {
                it.isHapticFeedbackEnabled = true
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                viewModel.onItemClicked(SingleColumnTimePickerBottomSheet.DeliveryType.TODAY)
                setResultAndDismissWithDelay( SingleColumnTimePickerBottomSheet.DeliveryType.TODAY)
            }

            feedTimeFilterAnytimeBtn.setLongerSafeOnClickListener {
                it.isHapticFeedbackEnabled = true
                it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                viewModel.onItemClicked(SingleColumnTimePickerBottomSheet.DeliveryType.ANYTIME)
                setResultAndDismissWithDelay( SingleColumnTimePickerBottomSheet.DeliveryType.ANYTIME)
            }


            feedTimeFilterScheduleBtn.setOnClickListener {
                viewModel.onItemClicked(SingleColumnTimePickerBottomSheet.DeliveryType.FUTURE)
            }

            feedTimeFilterApplyBtn.setLongerSafeOnClickListener {
                setResultAndDismissWithDelay(SingleColumnTimePickerBottomSheet.DeliveryType.FUTURE,
                    feedTimeFilterTimePicker.getChosenDate())
            }
        }
    }

    private fun handleCurrentTimeFilter(deliveryTimeType: SingleColumnTimePickerBottomSheet.DeliveryType?) {
        binding.apply {
            timeFilterBtnList.forEach {
                it.apply {
                    setCurrentUi(tag.equals(deliveryTimeType?.name), it)
                }
            }

            if (deliveryTimeType == SingleColumnTimePickerBottomSheet.DeliveryType.FUTURE) {
                if (!isExpanded) {
                    expandTimePicker()
                }
            } else {
                if (isExpanded) {
                    collapseTimePicker()
                }
            }
        }
    }

    private fun setCurrentUi(isActive: Boolean, textView: TextView) {
        if (isActive) {
            textView.setTextAppearance(R.style.TimeFilterActiveUi)
            textView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.time_filter_btn_active, null)
            val compoundDrawables = textView.compoundDrawables
            for (drawable in compoundDrawables) {
                if (drawable == null) {
                    continue
                }else{
                    drawable.setTint(resources.getColor(R.color.teal_blue, null))
                }
            }
        } else {
            textView.setTextAppearance(R.style.TimeFilterInactiveUi)
            textView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.time_filter_btn_inactive, null)
            val compoundDrawables = textView.compoundDrawables
            for (drawable in compoundDrawables) {
                if (drawable == null) {
                    continue
                }else{
                    drawable.setTint(resources.getColor(R.color.medium_grey, null))
                }
            }
        }
    }

    private fun setResultAndDismissWithDelay(deliveryTimeType: SingleColumnTimePickerBottomSheet.DeliveryType, chosenDate: Date? = null) {
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss()

            setFragmentResult(
                FILTER_TIME_KEY,
                bundleOf(
                    FILTER_TIME_VALUE to SingleColumnTimePickerBottomSheet.DeliveryTimeParam(
                        deliveryTimeType,
                        chosenDate
                    )
                )
            )

        }, 1000)
    }

    private fun expandTimePicker() {
        val timePickerView = binding.feedTimeFilterBottomLayout

        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((timePickerView.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        timePickerView.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = timePickerView.measuredHeight

        timePickerView.layoutParams.height = 1
        timePickerView.visibility = View.VISIBLE
        val anim = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                timePickerView.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                timePickerView.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        anim.duration = 500
        timePickerView.startAnimation(anim)

        val compoundDrawables = binding.feedTimeFilterScheduleBtn.compoundDrawables
        for (drawable in compoundDrawables) {
            if (drawable == null) {
                continue
            }else{
                val rotationAnim = ObjectAnimator.ofInt(drawable, "level", 0, 10000)
                rotationAnim.duration = 500
                rotationAnim.interpolator = LinearInterpolator()
                rotationAnim.start()
            }
        }

        isExpanded = true
    }

    private fun collapseTimePicker() {

        val timePickerView = binding.feedTimeFilterBottomLayout

        val initialHeight = timePickerView.measuredHeight
        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    timePickerView.visibility = View.GONE
                } else {
                    timePickerView.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    timePickerView.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        anim.duration = 500
        timePickerView.startAnimation(anim)

        val compoundDrawables = binding.feedTimeFilterScheduleBtn.compoundDrawables
        for (drawable in compoundDrawables) {
            if (drawable == null) {
                continue
            }else{
                val rotationAnim = ObjectAnimator.ofInt(drawable, "level", 10000, 0)
                rotationAnim.duration = 500
                rotationAnim.interpolator = LinearInterpolator()
                rotationAnim.start()
            }
        }

        isExpanded = false
    }


    companion object {
        const val FILTER_TIME_KEY = "filterTimeKey"
        const val FILTER_TIME_VALUE = "filterTimeValue"
        const val CURRENT_FILTER_ARGS_KEY = "currentFilterArgsKey"

        fun show(
            fragment: Fragment,
            currentTimeFilter: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?,
            listener: ((SingleColumnTimePickerBottomSheet.DeliveryTimeParam) -> Unit)
        ) {
            FeedTimeFilterBottomSheet().apply {
                arguments = bundleOf(
                    CURRENT_FILTER_ARGS_KEY to currentTimeFilter
                )
            }.show(
                fragment.childFragmentManager,
                FeedTimeFilterBottomSheet::class.simpleName
            )
            fragment.setTimeFilterResultListener(listener)
        }
    }
}

private fun Fragment.setTimeFilterResultListener(listener: ((SingleColumnTimePickerBottomSheet.DeliveryTimeParam) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        FeedTimeFilterBottomSheet.FILTER_TIME_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getParcelable<SingleColumnTimePickerBottomSheet.DeliveryTimeParam>(
            FeedTimeFilterBottomSheet.FILTER_TIME_VALUE
        )
        result?.let {
            listener.invoke(it)
        }
    }
}