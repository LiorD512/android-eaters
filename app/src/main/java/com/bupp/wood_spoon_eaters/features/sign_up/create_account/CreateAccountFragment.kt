package com.bupp.wood_spoon_eaters.features.sign_up.create_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon.dialogs.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.EmptyIconsGridView
import com.bupp.wood_spoon_eaters.features.sign_up.SignUpActivity
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.fragment_create_account.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateAccountFragment : Fragment(), InputTitleView.InputTitleViewListener, EmptyIconsGridView.OnItemSelectedListener,
    CuisinesChooserDialog.CuisinesChooserListener {


    val viewModel by viewModel<CreateAccountViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAccountFragmentNext.setOnClickListener {updateEater()}

        createAccountFragmentEmail.setInputTitleViewListener(this)
        createAccountFragmentFullName.setInputTitleViewListener(this)

        createAccountFragmentCookingGridView.setListener(this)
        createAccountFragmentDietaryGridView.initIconsGrid(viewModel.getDietaryList(), Constants.MULTI_SELECTION)

        createAccountFragmentNext.setBtnEnabled(false)

        viewModel.navigationEvent.observe(this, Observer { navigationEvent ->
            if (navigationEvent != null) {
                (activity as SignUpActivity).hidePb()
                if(navigationEvent.isSuccess){
                    (activity as SignUpActivity).startToMainActivity()
                }else{
                    Log.d("wowAccount","failed api")
                }
            }
        })
    }

    private fun updateEater() {
        (activity as SignUpActivity).showPb()
        viewModel.updateClientAccount(createAccountFragmentFullName.getText(), createAccountFragmentEmail.getText(),
            createAccountFragmentCookingGridView.getSelectedCuisines(), createAccountFragmentDietaryGridView.getSelectedItems())
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

    override fun OnEmptyItemSelected() {
        var cuisineFragment = CuisinesChooserDialog(this, viewModel.getCuisineList(), Constants.MULTI_SELECTION)
        cuisineFragment.setSelectedCuisine(createAccountFragmentCookingGridView.getSelectedCuisines())
        cuisineFragment.show(childFragmentManager, "CookingCuisine")
    }

    override fun onCuisineChoose(selectedCuisines: ArrayList<SelectableIcon>) {
        createAccountFragmentCookingGridView.updateItems(selectedCuisines)
    }

}