package com.bupp.wood_spoon_eaters.features.restaurant.dish_page;

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.DishMainListLayoutBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DishAvailabilityAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.ModificationsListAdapter
import com.bupp.wood_spoon_eaters.model.Dish
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DishPageFragment : Fragment(R.layout.fragment_dish_page), DishAvailabilityAdapter.DishAvailabilityAdapterListener,
    ModificationsListAdapter.ModificationsListAdapterListener {

    private val binding: FragmentDishPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<DishPageViewModel>()

    val availabilityAdapter = DishAvailabilityAdapter(this)
    val modificationsAdapter = ModificationsListAdapter(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navArgs: DishPageFragmentArgs by navArgs()
        viewModel.initData(navArgs.dish)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding) {
            backButton.setOnClickListener {
                activity?.onBackPressed()
            }
            shareButton.setOnClickListener {

            }
        }
        with(binding.dishMainListLayout) {
            dishAvailabilityList.adapter = availabilityAdapter
            dishModificationList.adapter = modificationsAdapter
            handleDishQuantityButtons()
        }
    }

    private fun handleDishQuantityButtons() {
        with(binding.dishMainListLayout) {
            dishAddQuantity.setOnClickListener{
                val quantity = (dishQuantity.text).toString().toInt() + 1
                viewModel.updateDishQuantity(quantity)
                dishQuantity.text = quantity.toString()
            }
            dishRemoveQuantity.setOnClickListener{
                val quantity = (dishQuantity.text).toString().toInt() - 1
                viewModel.updateDishQuantity(quantity)
                dishQuantity.text = quantity.toString()
            }
        }
    }

    private fun initObservers() {
        viewModel.dishData.observe(viewLifecycleOwner, {
            handleDishData(it)
        })
        viewModel.dishFullData.observe(viewLifecycleOwner, {
            handleDishFullData(it)
        })
        viewModel.modificationsData.observe(viewLifecycleOwner, {
            handleModificationsData(it)
        })
        viewModel.dishAvailabilityData.observe(viewLifecycleOwner, {
            handleAvailabilityData(it)
        })
        viewModel.dishQuantityState.observe(viewLifecycleOwner,{
            handleDishQuantityState(it)
        })
    }

    /** Headers data **/
    private fun handleDishData(dish: Dish) {
        with(binding) {
            dishHeaderName.text = dish.name
            dishHeaderPrice.text = dish.price?.formatedValue?:""
            topHeaderDishName.text = dish.name
//            Glide.with(requireContext()).load(dish.thumbnail).transform(CircleCrop()).into()
        }
    }

    private fun handleDishFullData(dish: Dish) {
        with(binding) {
            //Cover photo/video
            if (dish.video.isNullOrEmpty()) {
                Glide.with(requireContext()).load(dish.thumbnail).into(coverPhoto)
            } else {

            }
        }
    }

    private fun handleModificationsData(modifications: List<String>?) {
        modifications?.let{
            modificationsAdapter.submitList(it)
        }
    }

    private fun handleDishQuantityState(quantityState: DishPageViewModel.DishQuantityState?) {
       quantityState?.let{ state->
           with(binding.dishMainListLayout) {
               when (state) {
                   DishPageViewModel.DishQuantityState.ZERO_QUANTITY -> {
                        dishRemoveQuantity.isEnabled = false
                        dishAddQuantity.isEnabled = true
                   }
                   DishPageViewModel.DishQuantityState.REGULAR -> {
                       dishRemoveQuantity.isEnabled = true
                       dishAddQuantity.isEnabled = true
                   }
                   DishPageViewModel.DishQuantityState.MAX_QUANTITY -> {
                       dishRemoveQuantity.isEnabled = true
                       dishAddQuantity.isEnabled = false
                   }
               }
           }
       }
    }

    private fun handleAvailabilityData(availability: List<String>?) {
        availability?.let{
            availabilityAdapter.submitList(it)
        }
    }

    override fun onModificationClick() {
      //
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
}