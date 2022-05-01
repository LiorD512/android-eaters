package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen

import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.databinding.FragmentSetupAddressBinding
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.presentation.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SetupAddressFragment : BaseFragment(R.layout.fragment_setup_address), WSEditText.WSEditTextListener {

    var binding: FragmentSetupAddressBinding? = null
    val viewModel by sharedViewModel<CreateAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSetupAddressBinding.bind(view)

        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding!!) {
            setupAddressStreet.setIsEditable(false, this@SetupAddressFragment)
            setupAddressNext.setOnClickListener {
                if (allFieldsValid()) {
                    with(binding!!) {
                        val note = setupAddressInfo.getTextOrNull()
                        val apt = setupAddressApt.getTextOrNull()!!
                        viewModel.saveAddress(apt, note)
                    }
                }
            }
            setupAddressBack.setOnClickListener { activity?.onBackPressed() }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            if(!setupAddressStreet.checkIfValidAndSHowError()){
                return false
            }
            if(!setupAddressApt.checkIfValidAndSHowError()){
                return false
            }
        }
        return true
    }


    private fun initObservers() {
        viewModel.addressFoundEvent.observe(viewLifecycleOwner, {
            it?.let {
                binding!!.setupAddressStreet.setText(it.getUserLocationStr())
            }
        })
        viewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
    }


    private fun loadUnSavedData(cookRequest: CookRequest?) {
        with(binding!!) {
            cookRequest?.let { it ->
                it.pickupAddress?.let {  pickupAddress->
                    setupAddressStreet.setText(pickupAddress.getUserLocationStr())
                    pickupAddress.notes?.let {
                        setupAddressInfo.setText(it)
                    }
                    pickupAddress.streetLine2.let {
                        setupAddressApt.setText(it)
                    }
                }
            }
        }
    }

    override fun onWSEditUnEditableClick() {
        viewModel.onSearchAddressAutoCompleteClick()
    }

    override fun clearClassVariables() {
        binding = null
    }

}