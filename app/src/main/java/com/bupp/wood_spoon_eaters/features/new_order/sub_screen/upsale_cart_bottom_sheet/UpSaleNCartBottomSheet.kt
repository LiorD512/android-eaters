package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleAnimatorListener
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleBottomSheetCallback
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class UpSaleNCartBottomSheet : BottomSheetDialogFragment() {

    private val defaultPeekHeight = Utils.toPx(400)

    private val binding: UpSaleNCartBottomSheetBinding by viewBinding()
    private val viewModel by sharedViewModel<UpSaleNCartViewModel>()
    private var currentParentHeight: Int = defaultPeekHeight
    private var behavior: BottomSheetBehavior<View>? = null

    private lateinit var cartAdapter: UpSaleNCartAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.up_sale_n_cart_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        var height = displayMetrics.heightPixels

        dialog.setOnShowListener {
            Log.d(TAG, "setOnShowListener")
            val d = it as BottomSheetDialog
            d.setCancelable(false)
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior!!.peekHeight = defaultPeekHeight
            behavior!!.addBottomSheetCallback(object : SimpleBottomSheetCallback() {
                override fun onSlide(view: View, v: Float) {
                    val yPos = height - (binding.floatingCartBtnLayout.height).toFloat() - view.y
                    if (yPos > binding.floatingCartBtnLayout.height) {
                        binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
                        Log.d(TAG, "yPos: $yPos")
                        Log.d(TAG,"view y: ${view.y}")
                    }
                    currentParentHeight = height - view.y.toInt()
                }
            })

            refreshButtonPosition()

//            currentParentHeight = behavior!!.peekHeight
        }

        return dialog
    }

    private fun refreshButtonPosition() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        var height = displayMetrics.heightPixels
        val yPos = height - (height - defaultPeekHeight).toFloat() - binding.floatingCartBtnLayout.height
        binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)

        initUI()
        initObservers()

    }

    private fun initUI() {
        with(binding) {
            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))

            upSaleCartBtn.setOnClickListener {
                viewModel.onCartBtnClick()
            }

            upsaleCartCloseBtn.setOnClickListener {
                onCloseBtnClick()
            }
            viewModel.initData()
        }
    }

    private fun onCloseBtnClick() {
        dismiss()
    }


    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            when (it) {
                UpSaleNCartViewModel.NavigationEvent.GO_TO_CHECKOUT -> {
                    dismiss()
                }
                UpSaleNCartViewModel.NavigationEvent.GO_TO_UP_SALE -> {
                    navToUpSale()
                }
            }
        })
        viewModel.upsaleNCartLiveData.observe(viewLifecycleOwner, {
            handleCartData(it)
        })
    }

    private fun handleCartData(data: UpSaleNCartViewModel.CartData) {
        Log.d(TAG, "handleCartData data: $data")
        cartAdapter = UpSaleNCartAdapter()
        binding.cartFragList.initSwipeableRecycler(cartAdapter)
        cartAdapter.submitList(data.items)
    }

    private fun navToUpSale() {
        Log.d(TAG, "navToUpSale")
        setUpsaleUi()
        animateCollapse(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                super.onAnimationEnd(p0)
                Log.d(TAG, "navToUpSale: onAnimationEnd")
                viewModel.initData()
                animateExpand()
            }
        })
    }

    private fun animateCollapse(listener: SimpleAnimatorListener) {
        val currentHeight = currentParentHeight
        Log.d(TAG, "animateCollapse: currentHeight $currentHeight")
        behavior!!.peekHeight = currentHeight
        behavior!!.state = STATE_COLLAPSED
        ObjectAnimator.ofInt(
            behavior!!, "peekHeight",
            currentHeight, 50
        ).apply {
                duration = 500
                interpolator = FastOutSlowInInterpolator()
                addListener(listener)
                start()
            }
    }

    private fun animateExpand() {
        Log.d(TAG, "animateExpand")
        val targetHeight = defaultPeekHeight
        ObjectAnimator.ofInt(
            behavior!!, "peekHeight",
            50, 25, targetHeight
        ).apply {
                duration = 600
                startDelay = 150
                interpolator = FastOutSlowInInterpolator()
                start()
            }
        refreshButtonPosition()

    }

    private fun setUpsaleUi() {
        Log.d(TAG, "setUpsaleUi")
        animateTitle("Any thing else?")
        animateBtn("No Thanks")
        AnimationUtil().alphaIn(binding.upsaleCartCloseBtn)
    }

    private fun setCartUi() {
        Log.d(TAG, "setCartUi")
        animateTitle("Restaurant name")
        animateBtn("Go to checkout")
        AnimationUtil().alphaOut(binding.upsaleCartCloseBtn)
    }

    private fun animateTitle(text: String) {
        AnimationUtil().alphaOut(binding.upsaleCartTitle, listener = object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                binding.upsaleCartTitle.text = text
                AnimationUtil().alphaIn(binding.upsaleCartTitle)
            }
        })
    }

    private fun animateBtn(text: String) {
        AnimationUtil().alphaOut(binding.floatingCartBtnLayout, listener = object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                binding.upSaleCartBtn.setBtnText(text)
                AnimationUtil().alphaIn(binding.floatingCartBtnLayout)
            }
        })
    }

    companion object {
        const val TAG = "wowUpSaleNCartBS"

    }

}