package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon.dialogs.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.EmptyIconsGridView
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentCreateAccountBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    var binding: FragmentCreateAccountBinding? = null
    val viewModel by sharedViewModel<LoginViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateAccountBinding.bind(view)
        initUi()
        initObservers()

    }

    private fun initUi() {
        binding!!.createAccountFragmentNext.setOnClickListener {updateEater()}

    }

    private fun initObservers() {
        /*viewModel.navigationEvent.observe(this, Observer { navigationEvent ->
            if (navigationEvent != null) {
                (activity as SignUpActivity).hidePb()
                if(navigationEvent.isSuccess){
                    (activity as SignUpActivity).startToMainActivity()
                }else{
                    Log.d("wowAccount","failed api")
                }
            }
        })*/
    }

    private fun updateEater() {
        if(validateFields()){
            with(binding!!){
                val firstName = createAccountFragmentFirstName.getText()
                val lastName = createAccountFragmentLastName.getText()
                val email = createAccountFragmentEmail.getText()

                viewModel.updateClientAccount(requireContext(), firstName, lastName, email)
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        with(binding!!){
            if(createAccountFragmentFirstName.getText().isEmpty()){
                createAccountFragmentFirstName.showError()
                isValid = false
            }
            if(createAccountFragmentLastName.getText().isEmpty()){
                createAccountFragmentLastName.showError()
                isValid = false
            }
            if(createAccountFragmentEmail.getText().isEmpty()){
                createAccountFragmentEmail.showError()
                isValid = false
            }
        }
        return isValid
    }

//    override fun OnEmptyItemSelected() {
//        var cuisineFragment = CuisinesChooserDialog(this, viewModel.getCuisineList(), Constants.MULTI_SELECTION)
//        cuisineFragment.setSelectedCuisine(createAccountFragmentCookingGridView.getSelectedCuisines())
//        cuisineFragment.show(childFragmentManager, "CookingCuisine")
//    }
//
//    override fun onCuisineChoose(selectedCuisines: ArrayList<SelectableIcon>) {
//        createAccountFragmentCookingGridView.updateItems(selectedCuisines)
//    }

}