package com.bupp.wood_spoon_eaters.features.restaurant.dish_page;

import android.graphics.Paint
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
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DietariesAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.DishAvailabilityAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters.ModificationsListAdapter
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.floating_buttons.WSFloatingButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class DishPageFragment : Fragment(R.layout.fragment_dish_page),
    ModificationsListAdapter.ModificationsListAdapterListener,
    ClearCartCookingSlotBottomSheet.ClearCartListener,
    PlusMinusView.PlusMinusInterface,
    WSFloatingButton.WSFloatingButtonListener,
    WSErrorDialog.WSErrorListener {

    private val binding: FragmentDishPageBinding by viewBinding()

//    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<DishPageViewModel>()

    var availableTimesAdapter: DishAvailabilityAdapter? = null

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
            dishFragAddToCartBtn.setOnClickListener {
                onAddToCartClick()
            }
            dishFragAddToCartBtn.setWSFloatingBtnListener(this@DishPageFragment)

            availableTimesAdapter = DishAvailabilityAdapter()
        }
    }

    private fun onAddToCartClick() {
        val note = binding.dishFragMainListLayout.dishFragUserRequestInput.getText() ?: ""
        viewModel.onDishPageCartClick(note.toString())
    }

    private fun initDishQuantityButtons(initialCounter: Int? = 1, maxQuantity: Int) {
        with(binding.dishFragMainListLayout) {
            dishFragPlusMinus.initSimplePlusMinus(this@DishPageFragment, initialCounter!!, maxQuantity)
        }
    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        viewModel.updateDishQuantity(counter)
    }

    private fun initObservers() {
        viewModel.menuItemData.observe(viewLifecycleOwner, {
            handleDishData(it)
        })
        viewModel.orderItemData.observe(viewLifecycleOwner, {
            handleOrderItemData(it)
        })
        viewModel.dishFullData.observe(viewLifecycleOwner, {
            handleDishFullData(it)
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
            handleFinishPage(it)
        })
        viewModel.shakeAddToCartBtn.observe(viewLifecycleOwner, {
            AnimationUtil().shakeView(binding.dishFragAddToCartBtn)
        })
        viewModel.dishQuantityChange.observe(viewLifecycleOwner, {
            handleAddToCartBtn(it)
        })
        viewModel.wsErrorEvent.observe(viewLifecycleOwner, {
            handleWSError(it.getContentIfNotHandled())
        })
    }

    private fun handleFinishPage(type: DishPageViewModel.FinishNavigation) {
        when(type){
            DishPageViewModel.FinishNavigation.FINISH_AND_BACK -> {
                activity?.onBackPressed()
            }
            DishPageViewModel.FinishNavigation.FINISH_ACTIVITY -> {
                activity?.finish()
            }
        }
    }

    private fun handleAddToCartBtn(event: DishPageViewModel.DishQuantityData?) {
        event?.let{
            binding.dishFragAddToCartBtn.updateFloatingBtnPrice(it.overallPrice)
        }
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
            //update counter -> must be after menuItemLiveData is posted and can be read.
            viewModel.updateDishQuantity(1)

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
            initDishQuantityButtons(maxQuantity = menuItem.quantity)
        // todo - add dish initial counter when in edit
        }
    }

    private fun handleOrderItemData(orderItem: OrderItem) {
        with(binding) {
            //update counter -> must be after menuItemLiveData is posted and can be read.
            viewModel.updateDishQuantity(orderItem.quantity)

            dishFragAddToCartBtn.updateFloatingBtnTitle("Update cart")
            val dish = orderItem.dish
            dishFragHeaderName.text = dish.name
            dishFragHeaderPrice.text = dish.price?.formatedValue ?: ""
            dishFragTopHeaderDishName.text = dish.name
            Glide.with(requireContext()).load(dish.thumbnail?.url).into(dishFragCoverPhoto)
                dishFragTags.setTags(orderItem.menuItem?.tags)

            dishFragMainListLayout.dishFragRemoveBtn.visibility = View.VISIBLE
            dishFragMainListLayout.dishFragRemoveBtn.setOnClickListener {
                viewModel.onDishRemove(orderItem.dish.id)
            }

            initDishQuantityButtons(initialCounter = orderItem.quantity, maxQuantity = orderItem.menuItem?.quantity ?: -1)
        }
    }

    private fun handleDishFullData(dish: FullDish) {
        with(binding.dishFragMainListLayout) {
            handleDietaryList(dish.dietaries)
            handleAvailableTimes(dish.availableTimes)
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

    private fun handleAvailableTimes(availableTimes: List<AvailabilityDate>) {
        availableTimes.let{ list->
            with(binding.dishFragMainListLayout){
                dishFragAvailabilityList.adapter = availableTimesAdapter
                if(list.size > 3){
                    dishFragAvailabilityViewMore.isVisible = true
                    availableTimesAdapter?.submitList(list.subList(0,3))
                    dishFragAvailabilityViewMore.setOnClickListener {
                        dishFragAvailabilityList.scheduleLayoutAnimation()
                        if((availableTimesAdapter?.itemCount ?: 0)!! > 3){
                            dishFragAvailabilityViewMore.text = "View More"
                            availableTimesAdapter?.submitList(list.subList(0,3))
                        } else {
                            dishFragAvailabilityViewMore.text = "View Less"
                            availableTimesAdapter?.submitList(list)
                        }
                    }
                } else{
                    availableTimesAdapter?.submitList(list)
                }
            }
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

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            WSErrorDialog(it, this).show(childFragmentManager, Constants.ERROR_DIALOG)
        }
    }

    override fun onWSErrorDone() {

    }

    /**
     * Clear Cart Dialog callbacks.
     */
    override fun onPerformClearCart() {
        viewModel.onPerformClearCart()
    }

    override fun onFloatingCartStateChanged(isShowing: Boolean) {
        binding.dishFragHeightCorrection.isVisible = isShowing
    }


    override fun onClearCartCanceled() {
        //todo
    }

    override fun onModificationClick() {
        //
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }

    override fun onDestroyView() {
        availableTimesAdapter = null
        super.onDestroyView()
    }



}