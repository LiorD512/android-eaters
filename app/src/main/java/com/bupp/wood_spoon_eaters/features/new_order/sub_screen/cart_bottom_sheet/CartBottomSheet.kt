package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.cart_bottom_sheet

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleAnimatorListener
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleBottomSheetCallback
import com.bupp.wood_spoon_eaters.databinding.CartBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartBottomSheet : BottomSheetDialogFragment(), CartAdapter.CartAdapterListener {

    private val binding: CartBottomSheetBinding by viewBinding()
    private val viewModel by viewModel<CartViewModel>()
    lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cart_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
            behavior.isDraggable = true
            behavior.state = STATE_HALF_EXPANDED
//            behavior.expandedOffset = Utils.toPx(130)
            behavior.addBottomSheetCallback(object : SimpleBottomSheetCallback() {
                override fun onSlide(view: View, v: Float) {
                    val yPos = height - (binding.floatingCartBtnLayout.height).toFloat() - view.y
                    binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)

        initUi()
        initObservers()
    }

    fun initUi() {
        with(binding){
            cartAdapter = CartAdapter()

            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            CartFragList.initSwipeableRecycler(cartAdapter)
            CartFragList.addItemDecoration(DividerItemDecorator(divider))
//            cartFragList.setHasFixedSize(true)
        }
        viewModel.initData()
    }

    private fun initObservers() {
        viewModel.cartLiveData.observe(viewLifecycleOwner, {
            handleCartData(it)
        })
    }

    private fun handleCartData(data: CartViewModel.CartData) {
        with(binding){
            cartAdapter.submitList(data.items)
        }
    }

    override fun onCartBtnClicked() {
//        viewModel.onCartBtnClick()
    }

}