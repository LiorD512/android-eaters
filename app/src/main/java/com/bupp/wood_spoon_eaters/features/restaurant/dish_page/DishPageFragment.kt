package com.bupp.wood_spoon_eaters.features.restaurant.dish_page;

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DietariesAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DishAvailabilityAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.ModificationsListAdapter
import com.bupp.wood_spoon_eaters.model.DietaryIcon
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DishPageFragment : Fragment(R.layout.fragment_dish_page), DishAvailabilityAdapter.DishAvailabilityAdapterListener,
    ModificationsListAdapter.ModificationsListAdapterListener {

    private val binding: FragmentDishPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<DishPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navArgs: DishPageFragmentArgs by navArgs()
        viewModel.initData(navArgs.extras)

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
            handleDishQuantityButtons()
        }
    }

    private fun handleDishQuantityButtons() {
        with(binding.dishMainListLayout) {
            dishAddQuantity.setOnClickListener {
                val quantity = (dishQuantity.text).toString().toInt() + 1
                viewModel.updateDishQuantity(quantity)
                dishQuantity.text = quantity.toString()
            }
            dishRemoveQuantity.setOnClickListener {
                val quantity = (dishQuantity.text).toString().toInt() - 1
                viewModel.updateDishQuantity(quantity)
                dishQuantity.text = quantity.toString()
            }
        }
    }

    private fun initObservers() {
        viewModel.menuItemData.observe(viewLifecycleOwner, {
            handleDishData(it)
        })
        viewModel.dishFullData.observe(viewLifecycleOwner, {
            handleDishFullData(it)
        })
        viewModel.dishQuantityState.observe(viewLifecycleOwner, {
            handleDishQuantityState(it)
        })
        viewModel.userRequestData.observe(viewLifecycleOwner, {
            handleUserRequestData(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleSkeleton(it)
        })
    }

    private fun handleSkeleton(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                dishMainListLayout.root.isVisible = false
                dishMainListLayoutSkeleton.root.isVisible = true
            } else {
                dishMainListLayoutSkeleton.root.isVisible = false
                dishMainListLayout.root.isVisible = true
            }
        }
    }

    /** Headers data **/
    private fun handleDishData(menuItem: MenuItem) {
        with(binding) {
            val dish = menuItem.dish
            dishHeaderName.text = dish?.name
            dishHeaderPrice.text = dish?.price?.formatedValue ?: ""
            topHeaderDishName.text = dish?.name
            Glide.with(requireContext()).load(dish?.thumbnail).into(coverPhoto)
            if(menuItem.availableLater == null) {
                dishTags.setTags(menuItem.tags)
            } else{
                dishTags.setTags(listOf(menuItem.availableLater?.getStartEndAtTag()?:""))
            }

        }
    }

    private fun handleDishFullData(dish: FullDish) {
        with(binding.dishMainListLayout) {
            handleDietaryList(dish.dietaries)
            //Description
            dishDescription.isVisible = !dish.description.isNullOrEmpty()
            dishDescription.text = dish.description
            //Portion size
            dishPortionSize.isVisible = !dish.portionSize.isNullOrEmpty()
            dishPortionSize.text = "Portion size : ${dish.portionSize} Servings "
            //Ingredients
            dishIngredientsLayout.isVisible = !dish.ingredients.isNullOrEmpty()
            dishIngredients.text = dish.ingredients ?: ""
            //Accommodations
            dishAccommodationsLayout.isVisible = !dish.accommodations.isNullOrEmpty()
            dishAccommodations.text = dish.accommodations ?: ""
            //Additional Details
            dishAdditionalDetailsLayout.isVisible = false
//            dishAdditionalDetails.text = dish.  //todo : what goes here?
        }
    }

    private fun handleDietaryList(dietaries: List<DietaryIcon>?) {
        val adapter = DietariesAdapter()
        with(binding.dishMainListLayout) {
            dishDietaryList.adapter = adapter
            adapter.submitList(dietaries)
            dishDietaryList.isVisible = !dietaries.isNullOrEmpty()
        }
    }

    private fun handleUserRequestData(userRequest: DishPageViewModel.UserRequest?) {
        with(binding.dishMainListLayout) {
            userRequest?.let {
                dishChefName.text = userRequest.cook.getFullName()
                userRequest.cook.thumbnail.url?.let{
                    dishChefThumbnail.setImage(it)
                }
                dishUserRequestLine.text = "Hey ${userRequest.eaterName}, any special requests?"
            }
        }
    }

    private fun handleDishQuantityState(quantityState: DishPageViewModel.DishQuantityState?) {
        quantityState?.let { state ->
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

    override fun onModificationClick() {
        //
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
}