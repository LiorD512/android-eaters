package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.delete_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetDeleteAccountBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeleteAccountBottomSheet: TopCorneredBottomSheet() {

    private var binding: BottomSheetDeleteAccountBinding? = null
    private val viewModel: DeleteAccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_delete_account, container, false)
        binding = BottomSheetDeleteAccountBinding.bind(view)
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
           deleteAccountHeader.setOnIconClickListener {
               dismiss()
           }
           deleteAccountButton.setOnClickListener{
               viewModel.deleteAccount()
           }
       }
    }

    private fun initObservers() {
        viewModel.deleteSuccessEvent.observe(viewLifecycleOwner,{
            onDeleteAccountSuccess()
        })
    }

    private fun onDeleteAccountSuccess() {
        viewModel.logout(requireActivity())
    }


    override fun clearClassVariables() {
        binding = null
    }

}