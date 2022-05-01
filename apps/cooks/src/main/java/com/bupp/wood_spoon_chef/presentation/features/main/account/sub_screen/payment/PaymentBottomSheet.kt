package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.PaymentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class PaymentBottomSheet : TopCorneredBottomSheet() {

    private var binding: PaymentBottomSheetBinding? = null
    private val viewModel: PaymentViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.payment_bottom_sheet, container, false)
        binding = PaymentBottomSheetBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFullScreenDialog()

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!){
            aboutFragHeader.setOnIconClickListener {
                dismiss()
            }
            paymentBankAccount.setOnClickListener{
                viewModel.getOtl()
            }
        }
    }

    private fun initObservers() {
        viewModel.otlData.observe(viewLifecycleOwner,{
            it.getContentIfNotHandled()?.let{ otl ->
                loadPaymentMethodsDialog(otl.url)
            }
        })
        viewModel.errorEvent.observe(viewLifecycleOwner,{
            handleErrorEvent(it, binding?.root)
        })
    }

        private fun loadPaymentMethodsDialog(url: String) {
        PaymentMethodsBottomSheet.newInstance(url).show(childFragmentManager, Constants.PAYMENT_METHOD_DIALOG)
    }

    override fun clearClassVariables() {
       binding = null
    }

    companion object{
        fun newInstance(): BottomSheetDialogFragment{
            return PaymentBottomSheet().apply {
            }
        }
    }

}