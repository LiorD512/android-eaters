package com.bupp.wood_spoon_eaters.features.order_checkout.promo_code

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.androidadvance.topsnackbar.TSnackbar
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.CheckoutHeaderView
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.databinding.PromoCodeFragmentBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutActivity
import com.bupp.wood_spoon_eaters.utils.closeKeyboard
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.bupp.wood_spoon_eaters.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PromoCodeFragment : Fragment(R.layout.promo_code_fragment),
    HeaderView.HeaderViewListener, WSEditText.WSEditTextListener, CheckoutHeaderView.CheckoutHeaderListener {

    private lateinit var snackbar: TSnackbar
    val viewModel by viewModel<PromoCodeViewModel>()
    private var binding: PromoCodeFragmentBinding? =  null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PromoCodeFragmentBinding.bind(view)

        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding!!) {
            checkoutFragHeader.setCheckoutHeaderListener(this@PromoCodeFragment)
            promoCodeFragCodeInput.setWSEditTextListener(this@PromoCodeFragment)

            openKeyboard(promoCodeFragCodeInput)

            promoCodeFragSubmit.setOnClickListener { submitPromoCode() }
        }

    }

    private fun initObservers() {
        viewModel.promoCodeEvent.observe(viewLifecycleOwner, { event ->
            if (event.isSuccess) {
                closeKeyboard()
                activity?.onBackPressed()
            }
        })
        viewModel.errorEvent.observe(viewLifecycleOwner, {
            it?.let {
                var errorStr = ""
                it.forEach {
                    errorStr += "${it.msg} \n"
                }
                showWrongPromoCodeNotification(errorStr)
            }
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding!!.promoCodeFragPb.show()
            } else {
                binding!!.promoCodeFragPb.hide()
            }
        })
    }

    private fun showWrongPromoCodeNotification(msg: String?) {
        showErrorToast(msg!!, binding!!.root, Toast.LENGTH_LONG)
//        snackbar = TSnackbar.make(
//            binding!!.promoCodeFragmentLayout,
//            msg ?: "The promo code seems to be invalid. \nplease check again",
//            TSnackbar.LENGTH_LONG
//        ).apply {
//            view.elevation = 1000F
//        }
//        val snackBarView = snackbar.view
//        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_blue))
//        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
//        textView.setTextAppearance(R.style.LatoBlack13Dark)
//        textView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
//        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//        snackbar.show()
    }

    private fun openKeyboard(view: View) {
        if (view.requestFocus()) {
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    private fun submitPromoCode() {
        with(binding!!) {
            if (promoCodeFragCodeInput.validateIsNotEmpty()) {
                viewModel.savePromoCode(promoCodeFragCodeInput.getText() ?: "")
            }
        }
    }

    override fun onWSEditTextActionDone() {
        submitPromoCode()
    }

    override fun onHeaderBackClick() {
        closeKeyboard()
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onBackBtnClick() {
        closeKeyboard()
        activity?.onBackPressed()
    }

}