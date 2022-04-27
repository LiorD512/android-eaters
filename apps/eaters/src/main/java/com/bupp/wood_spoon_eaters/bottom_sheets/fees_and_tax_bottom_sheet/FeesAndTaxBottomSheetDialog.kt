package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeesAndTaxBottomSheetBinding
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeesAndTaxBottomSheetDialog : BottomSheetDialogFragment() {

    private var binding: FeesAndTaxBottomSheetBinding? = null
    val viewModel by viewModel<FeesAndTaxViewModel>()

    companion object {
        private const val VALUE_ARGS_FEE = "args_fee"
        private const val VALUE_ARGS_TAX = "args_tax"
        private const val VALUE_ARGS_MIN_FEE = "args_min_fee"
        fun newInstance(fee: String?, tax: String?, minFee: String?): FeesAndTaxBottomSheetDialog {
            return FeesAndTaxBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putString(VALUE_ARGS_FEE, fee)
                    putString(VALUE_ARGS_TAX, tax)
                    putString(VALUE_ARGS_MIN_FEE, minFee)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fees_and_tax_bottom_sheet, container, false)
        binding = FeesAndTaxBottomSheetBinding.bind(view)
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
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fee = arguments?.getString(VALUE_ARGS_FEE)
        val tax = arguments?.getString(VALUE_ARGS_TAX)
        val minOrderFee = arguments?.getString(VALUE_ARGS_MIN_FEE)

        binding = FeesAndTaxBottomSheetBinding.bind(view).also { binding ->
            binding.feesTaxBSFeeTitle.text = "Service fee: $fee"
            binding.feesTaxBSTaxTitle.text = "Estimated tax: $tax"
            minOrderFee?.let {
                binding.feesTaxBSMinFeeSubTitle.visibility = View.VISIBLE
                binding.feesTaxBSMinFeeTitle.visibility = View.VISIBLE
                binding.feesTaxBSMinFeeTitle.text = "Minimum order fee: $minOrderFee"
            }
        }

        val globalMinimumOrderFee = viewModel.getGlobalMinimumOrderFee()
        binding!!.feesTaxBSMinFeeSubTitle.text = "To reduce this fee your order value should be bigger than $globalMinimumOrderFee"

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()

        viewModel.pricingExperimentParamsLiveData.observe(viewLifecycleOwner) {
            handlePricingExperiment(it)
        }
    }

    private fun initUI() {
        with(binding!!) {
            feesTaxBSTaxBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun handlePricingExperiment(experimentParams: PricingExperimentParams) {
        binding?.apply {
            feesTaxBSFeeAndTaxesTitle.text = experimentParams.feeAndTaxTitle
            feesTaxBSFeeTitle.isVisible = !experimentParams.shouldHideFee
            feesTaxBSFeeSubTitle.isVisible = !experimentParams.shouldHideFee
        }
    }
}
