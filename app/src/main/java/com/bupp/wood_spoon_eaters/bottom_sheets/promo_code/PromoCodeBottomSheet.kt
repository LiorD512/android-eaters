package com.bupp.wood_spoon_eaters.bottom_sheets.promo_code

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.androidadvance.topsnackbar.TSnackbar
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.PromoCodeFragmentBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PromoCodeBottomSheet : BottomSheetDialogFragment(), HeaderView.HeaderViewListener {

    private lateinit var snackbar: TSnackbar
    val viewModel by viewModel<PromoCodeViewModel>()
    private lateinit var binding: PromoCodeFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.promo_code_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = false
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PromoCodeFragmentBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding){

            extraSpace.minimumHeight = (Resources.getSystem().displayMetrics.heightPixels) / 2

            promoCodeFragHeaderView.setHeaderViewListener(this@PromoCodeBottomSheet)
            promoCodeFragHeaderView.setSaveButtonClickable(false)

            promoCodeFragCodeInput.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    if (!s.isNullOrBlank()) {
                        promoCodeFragHeaderView.setSaveButtonClickable(true)
                    } else {
                        promoCodeFragHeaderView.setSaveButtonClickable(false)
                    }

                }
            })
            openKeyboard(promoCodeFragCodeInput)
        }

    }

    private fun initObservers() {
        viewModel.promoCodeEvent.observe(viewLifecycleOwner, { event ->
//            promoCodeFragPb.hide()
            if(event.isSuccess){
                activity?.let { Utils.hideKeyBoard(it) }
                dismiss()
//                listener.onPromoCodeDone()
//                (activity as NewOrderActivity).onCheckout()//ny !!!
            }
        })
        viewModel.errorEvent.observe(viewLifecycleOwner, {
//            promoCodeFragPb.hide()
            it?.let{
                var errorStr = ""
                it.forEach {
                    errorStr += "${it.msg} \n"
                }
                showWrongPromoCodeNotification(errorStr)
//                ErrorDialog.newInstance(errorStr).show(childFragmentManager, Constants.ERROR_DIALOG)
//                WSErrorDialog(it.msg).show(childFragmentManager, Constants.ERROR_DIALOG)
//                showWrongPromoCodeNotification()
            }
        })
    }
    private fun showWrongPromoCodeNotification(msg: String?) {
        snackbar = TSnackbar.make(binding.promoCodeFragHeaderView,
            msg ?: "The promo code seems to be invalid. \nplease check again",
            TSnackbar.LENGTH_LONG).apply {
            view.elevation = 1000F
        }
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_blue))
        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextAppearance(R.style.SemiBold13Dark)
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar.show()
    }

    private fun openKeyboard(view: View) {
        if(view.requestFocus()){
            val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    override fun onHeaderSaveClick() {
//        promoCodeFragPb.show()
        viewModel.savePromoCode(binding.promoCodeFragCodeInput.text.toString())
    }

    override fun onHeaderBackClick() {
        dismiss()
    }


}