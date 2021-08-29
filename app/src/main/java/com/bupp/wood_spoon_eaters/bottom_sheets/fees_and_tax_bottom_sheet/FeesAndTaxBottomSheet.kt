package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeesAndTaxBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeesAndTaxBottomSheet : BottomSheetDialogFragment(){

    private lateinit var binding: FeesAndTaxBottomSheetBinding
    val viewModel by viewModel<FeesAndTaxViewModel>()

    companion object {
        private const val VALUE_ARGS_FEE = "args_fee"
        private const val VALUE_ARGS_TAX = "args_tax"
        private const val VALUE_ARGS_MIN_FEE = "args_min_fee"
        fun newInstance(fee: String?, tax: String?, minFee: String?): FeesAndTaxBottomSheet {
            return FeesAndTaxBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(VALUE_ARGS_FEE, fee)
                    putString(VALUE_ARGS_TAX, tax)
                    putString(VALUE_ARGS_MIN_FEE, minFee)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fees_and_tax_bottom_sheet, container, false)
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
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FeesAndTaxBottomSheetBinding.bind(view)

        arguments?.let {
            val fee = it.getString(VALUE_ARGS_FEE)
            val tax = it.getString(VALUE_ARGS_TAX)
            binding.feesTaxBSFeeTitle.text = "Service fee: $fee"
            binding.feesTaxBSTaxTitle.text = "Estimated tax: $tax"
            val minOrderFee = it.getString(VALUE_ARGS_MIN_FEE)
            minOrderFee?.let{
                binding.feesTaxBSMinFeeSubTitle.visibility = View.VISIBLE
                binding.feesTaxBSMinFeeTitle.visibility = View.VISIBLE
                binding.feesTaxBSMinFeeTitle.text = "Minimum order fee: $minOrderFee"
            }
        }

        val globalMinimumOrderFee = viewModel.getGlobalMinimumOrderFee()
        binding.feesTaxBSMinFeeSubTitle.text = "To reduce this fee your order value should be bigger than $globalMinimumOrderFee"

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
    }

    private fun initUI() {
        with(binding) {
            feesTaxBSTaxBtn.setOnClickListener {
                dismiss()
            }
        }
    }


}

