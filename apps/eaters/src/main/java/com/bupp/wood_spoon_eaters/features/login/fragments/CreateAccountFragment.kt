package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.FragmentCreateAccountBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.WSEditText
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CreateAccountFragment : Fragment(R.layout.fragment_create_account), WSEditText.WSEditTextListener {

    var binding: FragmentCreateAccountBinding? = null
    val viewModel by sharedViewModel<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateAccountBinding.bind(view)

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_CREATE_ACCOUNT)

        initUi()
    }

    private fun initUi() {
        with(binding!!){
            createAccountFragNext.setOnClickListener {updateEater()}
            createAccountFragCloseBtn.setOnClickListener { activity?.onBackPressed() }
            createAccountFragEmail.setWSEditTextListener(this@CreateAccountFragment)
        }
    }

    private fun updateEater() {
        if(validateFields()){
            with(binding!!){
                val firstName = createAccountFragFirstName.getText()!!
                val lastName = createAccountFragLastName.getText()!!
                val email = createAccountFragEmail.getText()!!

                viewModel.updateClientAccount(requireContext(), firstName, lastName, email)
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        with(binding!!){
            if(createAccountFragFirstName.getText().isNullOrEmpty()){
                createAccountFragFirstName.showError()
                isValid = false
            }
            if(createAccountFragLastName.getText().isNullOrEmpty()){
                createAccountFragLastName.showError()
                isValid = false
            }
            val email = createAccountFragEmail.getText().toString()
            if(email.isEmpty() || !Utils.isValidEmailAddress(email)){
                createAccountFragEmail.showError()
                isValid = false
            }
        }
        return isValid
    }

    override fun onWSEditTextActionDone() {
        binding!!.createAccountFragNext.performClick()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}