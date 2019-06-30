package com.bupp.wood_spoon_eaters.features.sign_up.create_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import kotlinx.android.synthetic.main.fragment_create_account.*
import org.koin.android.viewmodel.ext.android.viewModel


class CreateAccountFragment : Fragment(), InputTitleView.InputTitleViewListener {

    val viewModel by viewModel<CreateAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createAccountFragmentNext.setOnClickListener {

            (activity as SignUpActivity).showPb()

            viewModel.updateClientAccount(
                createAccountFragmentFullName.getText(),
                createAccountFragmentEmail.getText()
            )
        }

        createAccountFragmentEmail.setInputTitleViewListener(this)
        createAccountFragmentFullName.setInputTitleViewListener(this)

        createAccountFragmentNext.setBtnEnabled(false)

        viewModel.navigationEvent.observe(this, Observer { navigationEvent ->
            if (navigationEvent != null) {
                (activity as SignUpActivity).hidePb()

                (activity as SignUpActivity).moveToMainActivity()
            }
        })
    }

    private fun checkValidation() {
        if (createAccountFragmentFullName.isValid() && createAccountFragmentEmail.isValid()) {
            createAccountFragmentNext.setBtnEnabled(true)
        } else {
            createAccountFragmentNext.setBtnEnabled(false)
        }

    }

    override fun onInputTitleChange(str: String?) {
        checkValidation()
    }

}