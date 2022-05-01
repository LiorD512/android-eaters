package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishNameBinding
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewDishNameFragment : BaseFragment(R.layout.fragment_new_dish_name) {

    var binding: FragmentNewDishNameBinding? = null
    val viewModel by sharedViewModel<NewDishViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishNameBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            newDishNameNext.setOnClickListener {
                if (allFieldsValid()) {
                    val name = newDishNameInput.getTextOrNull()!!
                    val description = newDishNameDescription.getText()!!
                    viewModel.saveDishName(name, description)
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            val validName = newDishNameInput.checkIfValidAndSHowError()
            val validDescription = newDishNameDescription.checkIfValidAndSHowError()
            return validName && validDescription
        }
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        with(binding!!) {
            dishRequest?.let { it ->
                it.name.let {
                    newDishNameInput.setText(it)
                }
                it.description.let {
                    newDishNameDescription.setText(it)
                }
            }
        }

    }

    override fun clearClassVariables() {
        binding = null
    }


}