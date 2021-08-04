package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleAnimatorListener
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleBottomSheetCallback
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpSaleNCartBottomSheet : BottomSheetDialogFragment(){

    private val binding: UpSaleNCartBottomSheetBinding by viewBinding()
    private val viewModel by sharedViewModel<UpSaleNCartViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.up_sale_n_cart_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = false
            behavior.isDraggable = true
            behavior.state = STATE_HALF_EXPANDED
//            behavior.expandedOffset = Utils.toPx(130)
            behavior.addBottomSheetCallback(object : SimpleBottomSheetCallback() {
                override fun onSlide(view: View, v: Float) {
                    Log.d("wowBottomShit","height: $height")
                    Log.d("wowBottomShit","view y: ${view.y}")
                    val yPos = height - (binding.floatingCartBtnLayout.height).toFloat() - view.y
                    binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
                    Log.d("wowBottomShit","yPos: $yPos")
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)

        initUI()
        initObservers()
    }

    private fun initUI() {
        with(binding){

//            upsaleCartMainLayout.waitForLayout {
//                val displayMetrics = DisplayMetrics()
//                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
//                var height = displayMetrics.heightPixels
//
//                val yPos = height - (floatingCartBtnLayout.height).toFloat() - upsaleCartMainLayout.y
//                binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
//                Log.d("wowBottomShit","yPos: $yPos")
//                Log.d("wowBottomShit","floatingCartBtnLayout.height: ${floatingCartBtnLayout.height}")
//                Log.d("wowBottomShit","upsaleCartMainLayout.y: ${upsaleCartMainLayout.y}")
//            }

//            upSaleCartViewPager.isUserInputEnabled = false
//            upSaleCartViewPager.adapter = UpSaleNCartAdapter(this@UpSaleNCartBottomSheet)
            upSaleBtn.setOnClickListener {
                viewModel.onCartBtnClick()
            }
            upsaleCartBackBtn.setOnClickListener {
                navToCart()
//                findNavController(binding.upsaleNCartContainer).navigate(R.id.action_upSaleFragment_to_cartFragment)
            }
//            navToUpSale()
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            when(it){
                UpSaleNCartViewModel.NavigationEvent.GO_TO_CHECKOUT -> {
                    navToCart()
                }
                UpSaleNCartViewModel.NavigationEvent.GO_TO_UP_SALE -> {
                    navToUpSale()
                }
            }
        })
    }

//    override fun onBackPressed() {
//        if (binding.upSaleCartViewPager.currentItem == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed()
//        } else {
//            // Otherwise, select the previous step.
//            binding.upSaleCartViewPager.currentItem = viewPager.currentItem - 1
//        }
//    }

    private fun navToUpSale() {
        animateTitle("Any thing else?")
        animateBtn("No thanks")
        AnimationUtil().alphaIn(binding.upsaleCartBackBtn)
        findNavController(binding.upsaleNCartContainer).navigate(R.id.action_cartFragment_to_upSaleFragment)
    }

    private fun navToCart() {
        animateTitle("Restaurant name")
        animateBtn("Go to checkout")
        AnimationUtil().alphaOut(binding.upsaleCartBackBtn)
        findNavController(binding.upsaleNCartContainer).navigate(R.id.action_upSaleFragment_to_cartFragment)
    }

    private fun animateTitle(text: String){
        AnimationUtil().alphaOut(binding.upsaleCartTitle, listener = object: SimpleAnimatorListener(){
            override fun onAnimationEnd(p0: Animator?) {
                binding.upsaleCartTitle.text = text
                AnimationUtil().alphaIn(binding.upsaleCartTitle)
            }
        })
    }
    private fun animateBtn(text: String){
        AnimationUtil().alphaOut(binding.floatingCartBtnLayout, listener = object: SimpleAnimatorListener(){
            override fun onAnimationEnd(p0: Animator?) {
                binding.upSaleBtn.setBtnText(text)
                AnimationUtil().alphaIn(binding.floatingCartBtnLayout)
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController(binding.upsaleNCartContainer).popBackStack()
    }

}