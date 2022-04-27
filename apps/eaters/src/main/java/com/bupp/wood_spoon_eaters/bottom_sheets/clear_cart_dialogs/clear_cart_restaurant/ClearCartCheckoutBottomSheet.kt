package com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.ClearCartCheckoutBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.ClearCartRestaurantBottomSheetBinding
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ClearCartCheckoutBottomSheet(val listener: ClearCartListener): BottomSheetDialogFragment(), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener {

    interface ClearCartListener{
        fun onPerformClearCart()
        fun onClearCartCanceled()
    }

    private var binding: ClearCartCheckoutBottomSheetBinding ?= null
    var notifyType: Int = NOTIFY_CANCEL_CLEAR_CART

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.clear_cart_checkout_bottom_sheet, container, false)
        binding = ClearCartCheckoutBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
            behavior.isDraggable = true
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
    }

    private fun initUI() {
        with(binding!!){
            clearCartRestClearBtn.setOnClickListener {
                notifyType = NOTIFY_CLEAR_CART
                dismiss()
            }
            clearCartRestCancelBtn.setOnClickListener {
                notifyType = NOTIFY_CANCEL_CLEAR_CART
                dismiss()
            }
        }
    }

    companion object{
        private const val CUR_DATA_ARGS = "cur_data_args"
        private const val NEW_DATA_ARGS = "new_data_args"

        private const val NOTIFY_CLEAR_CART = 0
        private const val NOTIFY_CANCEL_CLEAR_CART = 1

        fun newInstance(listener: ClearCartListener): ClearCartCheckoutBottomSheet{
            val bottomSheetFragment = ClearCartCheckoutBottomSheet(listener)
            val bundle = Bundle()
            bottomSheetFragment.arguments = bundle
            return bottomSheetFragment
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        when(notifyType){
            NOTIFY_CLEAR_CART -> {
                listener.onPerformClearCart()
            }
            NOTIFY_CANCEL_CLEAR_CART -> {
                listener.onClearCartCanceled()
            }
        }
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}