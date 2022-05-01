package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CreateAccountHelpBottomSheetBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CreateAccountHelpBottomSheet : BaseBottomSheetDialogFragment(){


    private var binding: CreateAccountHelpBottomSheetBinding? = null
    val viewModel by sharedViewModel<CreateAccountViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_account_help_bottom_sheet, container, false)
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
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.isFitToContents = true
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CreateAccountHelpBottomSheetBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
        initObservers()
    }


    private fun initUi() {
        binding?.apply{
            createAccountHelpSend.setOnClickListener {
                if(validateFields()){
                    val first = createAccountHelpFirstName.getTextOrNull()!!
                    val last = createAccountHelpLastName.getTextOrNull()!!
                    val email = createAccountHelpEmail.getTextOrNull()!!
                    val message = createAccountHelpInput.text.toString()
                    viewModel.sendHelpMessage(first, last, email, message)

                }
            }
            createAccountHelpClose.setOnClickListener { dismiss() }
        }

    }

    private fun validateFields(): Boolean {
        binding?.apply{
            val validFirst = createAccountHelpFirstName.checkIfValidAndSHowError()
            val validLast = createAccountHelpLastName.checkIfValidAndSHowError()
            val validEmail = createAccountHelpEmail.checkIfValidEmailAndSHowError()
            val isTextValid = createAccountHelpInput.text.isNotEmpty()
            if(!isTextValid){
                AnimationUtil().shakeView(createAccountHelpInput, requireContext())
                return false
            }
            return  validFirst && validLast && validEmail && isTextValid
        }
        return false
    }

    private fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            if(it == CreateAccountViewModel.NavigationEventType.HELP_TICKET_SEND){
                dismiss()
            }
        })
    }

    private fun loadUnSavedData(cookRequest: CookRequest?) {
        binding?.apply{
            cookRequest?.let{ it ->
                it.firstName?.let{
                    createAccountHelpFirstName.setText(it)
                }
                it.lastName?.let{
                    createAccountHelpLastName.setText(it)
                }
                it.email?.let{
                    createAccountHelpEmail.setText(it)
                }
            }

        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}