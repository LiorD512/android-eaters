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
import kotlinx.android.synthetic.main.fragment_create_account.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CreateAccountFragment : Fragment(R.layout.fragment_create_account), EmptyIconsGridView.OnItemSelectedListener,
    CuisinesChooserDialog.CuisinesChooserListener {


    val viewModel by sharedViewModel<LoginViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()

    }

    private fun initUi() {
        createAccountFragmentNext.setOnClickListener {updateEater()}

        createAccountFragmentCookingGridView.setListener(this)
        createAccountFragmentDietaryGridView.initIconsGrid(viewModel.getDietaryList(), Constants.MULTI_SELECTION)
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
        val fullName = createAccountFragmentFullName.getText()
        val email = createAccountFragmentEmail.getText()
        if(fullName.isEmpty()){
            createAccountFragmentFullName.showError()
        }
        if(email.isEmpty()){
            createAccountFragmentEmail.showError()
        }
        if(fullName.isNotEmpty() && email.isNotEmpty()){
            viewModel.updateClientAccount(requireContext(), fullName, email, createAccountFragmentCookingGridView.getSelectedCuisines(),
                createAccountFragmentDietaryGridView.getSelectedItems())
        }
    }

    override fun OnEmptyItemSelected() {
        var cuisineFragment = CuisinesChooserDialog(this, viewModel.getCuisineList(), Constants.MULTI_SELECTION)
        cuisineFragment.setSelectedCuisine(createAccountFragmentCookingGridView.getSelectedCuisines())
        cuisineFragment.show(childFragmentManager, "CookingCuisine")
    }

    override fun onCuisineChoose(selectedCuisines: ArrayList<SelectableIcon>) {
        createAccountFragmentCookingGridView.updateItems(selectedCuisines)
    }

}