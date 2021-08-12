package com.bupp.wood_spoon_eaters.features.restaurant.dish_page;

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartCookingSlotBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DietariesAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DishAvailabilityAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.ModificationsListAdapter
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.DietaryIcon
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DishPageFragment : Fragment(R.layout.fragment_dish_page), DishAvailabilityAdapter.DishAvailabilityAdapterListener,
    ModificationsListAdapter.ModificationsListAdapterListener, ClearCartCookingSlotBottomSheet.ClearCartListener {

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
            dishFragBackButton.setOnClickListener {
                activity?.onBackPressed()
            }
            dishFragShareButton.setOnClickListener {

            }
            dishFragAddBtn.setOnClickListener {
                onAddToCartClick()
            }
        }
        with(binding.dishFragMainListLayout) {
            handleDishQuantityButtons()
        }
    }

    private fun onAddToCartClick() {
        viewModel.addDishToCart()
    }

    private fun handleDishQuantityButtons() {
        with(binding.dishFragMainListLayout) {
            dishFragAddQuantity.setOnClickListener {
                val quantity = (dishFragQuantity.text).toString().toInt() + 1
                viewModel.updateDishQuantity(quantity)
                dishFragQuantity.text = quantity.toString()
            }
            dishFragRemoveQuantity.setOnClickListener {
                val quantity = (dishFragQuantity.text).toString().toInt() - 1
                viewModel.updateDishQuantity(quantity)
                dishFragQuantity.text = quantity.toString()
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
        viewModel.clearCartEvent.observe(viewLifecycleOwner, {
            handleClearCartEvent(it)
        })
        viewModel.onFinishDishPage.observe(viewLifecycleOwner, {
            activity?.onBackPressed()
        })
    }

    private fun handleSkeleton(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                dishFragMainListLayout.root.isVisible = false
                dishFragMainListLayoutSkeleton.root.isVisible = true
            } else {
                dishFragMainListLayoutSkeleton.root.isVisible = false
                dishFragMainListLayout.root.isVisible = true
            }
        }
    }

    private fun handleClearCartEvent(event: LiveEvent<CartManager.ClearCartEvent>) {
        event.getContentIfNotHandled()?.let{
            when(it.dialogType){
                CartManager.ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT -> {
                    ClearCartCookingSlotBottomSheet.newInstance(it.curData, it.newData, this).show(childFragmentManager, Constants.CLEAR_CART_COOKING_SLOT_DIALOG_TAG)
                }
                else -> {}
            }
        }
    }

    /** Headers data **/
    private fun handleDishData(menuItem: MenuItem) {
        with(binding) {
            val dish = menuItem.dish
            dishFragHeaderName.text = dish?.name
            dishFragHeaderPrice.text = dish?.price?.formatedValue ?: ""
            dishFragTopHeaderDishName.text = dish?.name
            Glide.with(requireContext()).load(dish?.thumbnail?.url).into(dishFragCoverPhoto)
            if(menuItem.availableLater == null) {
                dishFragTags.setTags(menuItem.tags)
            } else{
                dishFragTags.setTags(listOf(menuItem.availableLater?.getStartEndAtTag()?:""))
            }

        }
    }

    private fun handleDishFullData(dish: FullDish) {
        with(binding.dishFragMainListLayout) {
            handleDietaryList(dish.dietaries)
            //Description
            dishFragDescription.isVisible = !dish.description.isNullOrEmpty()
            dishFragDescription.text = dish.description
            //Portion size
            dishFragPortionSize.isVisible = !dish.portionSize.isNullOrEmpty()
            dishFragPortionSize.text = "Portion size : ${dish.portionSize} Servings "
            //Ingredients
            dishFragIngredientsLayout.isVisible = !dish.ingredients.isNullOrEmpty()
            dishFragIngredients.text = dish.ingredients ?: ""
            //Accommodations
            dishFragAccommodationsLayout.isVisible = !dish.accommodations.isNullOrEmpty()
            dishFragAccommodations.text = dish.accommodations ?: ""
            //Additional Details
            dishFragAdditionalDetailsLayout.isVisible = false
//            dishAdditionalDetails.text = dish.  //todo : what goes here?
        }
    }

    private fun handleDietaryList(dietaries: List<DietaryIcon>?) {
        val adapter = DietariesAdapter()
        with(binding.dishFragMainListLayout) {
            dishFragDietaryList.adapter = adapter
            adapter.submitList(dietaries)
            dishFragDietaryList.isVisible = !dietaries.isNullOrEmpty()
        }
    }

    private fun handleUserRequestData(userRequest: DishPageViewModel.UserRequest?) {
        with(binding.dishFragMainListLayout) {
            userRequest?.let {
                dishFragChefName.text = userRequest.cook.getFullName()
                userRequest.cook.thumbnail?.url?.let{
                    dishFragChefThumbnail.setImage(it)
                }
                dishFragUserRequestLine.text = "Hey ${userRequest.eaterName}, any special requests?"
            }
        }
    }

    private fun handleDishQuantityState(quantityState: DishPageViewModel.DishQuantityState?) {
        quantityState?.let { state ->
            with(binding.dishFragMainListLayout) {
                when (state) {
                    DishPageViewModel.DishQuantityState.ZERO_QUANTITY -> {
                        dishFragRemoveQuantity.isEnabled = false
                        dishFragAddQuantity.isEnabled = true
                    }
                    DishPageViewModel.DishQuantityState.REGULAR -> {
                        dishFragRemoveQuantity.isEnabled = true
                        dishFragAddQuantity.isEnabled = true
                    }
                    DishPageViewModel.DishQuantityState.MAX_QUANTITY -> {
                        dishFragRemoveQuantity.isEnabled = true
                        dishFragAddQuantity.isEnabled = false
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

    override fun onPerformClearCart() {
        viewModel.onPerformClearCart()
    }
}