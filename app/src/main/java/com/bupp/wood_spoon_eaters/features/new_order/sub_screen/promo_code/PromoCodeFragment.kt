package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.androidadvance.topsnackbar.TSnackbar
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.ErrorDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.promo_code_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PromoCodeFragment : Fragment(), HeaderView.HeaderViewListener {

    private lateinit var snackbar: TSnackbar
    val viewModel by viewModel<PromoCodeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.promo_code_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        promoCodeFragHeaderView.setHeaderViewListener(this)
        promoCodeFragHeaderView.setSaveButtonClickable(false)

        promoCodeFragCodeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    promoCodeFragHeaderView.setSaveButtonClickable(true)
                } else {
                    promoCodeFragHeaderView.setSaveButtonClickable(false)
                }
            }
        })

        viewModel.promoCodeEvent.observe(this, Observer{ event ->
            promoCodeFragPb.hide()
            if(event.isSuccess){
//                listener.onPromoCodeDone()
//                (activity as NewOrderActivity).onCheckout()//ny !!!
            }
        })
        viewModel.errorEvent.observe(this, Observer {
            promoCodeFragPb.hide()
            it?.let{
                var errorStr = ""
                it.forEach {
                    errorStr += "${it.msg} \n"
                }
                ErrorDialog.newInstance(errorStr).show(childFragmentManager, Constants.ERROR_DIALOG)
//                WSErrorDialog(it.msg).show(childFragmentManager, Constants.ERROR_DIALOG)
//                showWrongPromoCodeNotification()
            }
        })

        openKeyboard(promoCodeFragCodeInput)
    }

    private fun showWrongPromoCodeNotification() {
        snackbar = TSnackbar.make(promoCodeFragmentLayout,
            "The promo code seems to be invalid. \nplease check again",
            TSnackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_blue))
        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.SemiBold13Dark)
        }
        textView.setGravity(Gravity.CENTER_HORIZONTAL)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar.show()
    }

    private fun openKeyboard(view: View) {
        if(view.requestFocus()){
            val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    override fun onHeaderSaveClick() {
        promoCodeFragPb.show()
        viewModel.savePromoCode(promoCodeFragCodeInput.text.toString())
    }

    override fun onHeaderBackClick() {
//        (activity as NewOrderActivity).onCheckout() // nyyy
    }
}
