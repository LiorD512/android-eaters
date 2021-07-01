//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code
//
//import android.content.Context.INPUT_METHOD_SERVICE
//import android.content.DialogInterface.OnShowListener
//import android.os.Bundle
//import android.view.Gravity
//import android.view.View
//import android.view.inputmethod.InputMethodManager
//import android.widget.FrameLayout
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.androidadvance.topsnackbar.TSnackbar
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.HeaderView
//import com.bupp.wood_spoon_eaters.databinding.PromoCodeFragmentBinding
//import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
//import com.bupp.wood_spoon_eaters.views.WSEditText
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import org.koin.androidx.viewmodel.ext.android.sharedViewModel
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//
//class PromoCodeFragment : Fragment(R.layout.promo_code_fragment), HeaderView.HeaderViewListener, WSEditText.WSEditTextListener {
//
//    lateinit var binding: PromoCodeFragmentBinding
//    private lateinit var snackbar: TSnackbar
//    val viewModel by viewModel<PromoCodeViewModel>()
//    val mainViewModel by sharedViewModel<NewOrderMainViewModel>()
//
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = PromoCodeFragmentBinding.bind(view)
//
//        initUi()
//        initObservers()
//    }
//
//    private fun initObservers() {
//        with(binding) {
//            viewModel.promoCodeEvent.observe(viewLifecycleOwner, { event ->
//                if (event.isSuccess) {
//                    mainViewModel.onPromoCodeSuccess()
//                    onHeaderBackClick()
//                }
//            })
//            viewModel.errorEvent.observe(viewLifecycleOwner, {
//                it?.let {
//                    var errorStr = ""
//                    it.forEach {
//                        errorStr += "${it.msg} \n"
//                    }
//                    showWrongPromoCodeNotification(errorStr)
//                }
//            })
//            viewModel.progressData.observe(viewLifecycleOwner, {
//                if (it) {
//                    promoCodeFragPb.show()
//
//                } else {
//                    promoCodeFragPb.hide()
//                }
//            })
//        }
//
//    }
//
//    private fun initUi() {
//        with(binding) {
//            promoCodeFragHeaderView.setHeaderViewListener(this@PromoCodeFragment)
//            promoCodeFragHeaderView.setSaveButtonClickable(false)
//            promoCodeFragCodeInput.setWSEditTextListener(this@PromoCodeFragment)
//
////            promoCodeFragCodeInput.setIsEditable(true, this@PromoCodeFragment)
////            object : SimpleTextWatcher() {
////                override fun afterTextChanged(s: Editable) {
////                    if (!s.isNullOrBlank()) {
////                        promoCodeFragHeaderView.setSaveButtonClickable(true)
////                    } else {
////                        promoCodeFragHeaderView.setSaveButtonClickable(false)
////                    }
////
////                }
////            })
//
//            openKeyboard(promoCodeFragCodeInput)
//
//            promoCodeFragSubmit.setOnClickListener { submitPromoCode() }
//        }
//    }
//
//    private fun showWrongPromoCodeNotification(msg: String?) {
//        snackbar = TSnackbar.make(
//            binding.promoCodeFragmentLayout,
//            msg ?: "The promo code seems to be invalid. \nplease check again",
//            TSnackbar.LENGTH_LONG
//        )
//        val snackBarView = snackbar.view
//        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_blue))
//        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
//        textView.setTextAppearance(R.style.SemiBold13Dark)
//        textView.gravity = Gravity.CENTER_HORIZONTAL
//        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//        snackbar.show()
//    }
//
//    private fun openKeyboard(view: View) {
//        if (view.requestFocus()) {
//            val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//        }
//    }
//
//    private fun submitPromoCode() {
//        viewModel.savePromoCode(binding.promoCodeFragCodeInput.getText() ?: "")
//    }
//
//    override fun onHeaderBackClick() {
//        activity?.onBackPressed()
//    }
//
//    override fun onWSEditTextActionDone() {
//        submitPromoCode()
//    }
//}
