package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartCookingSlotBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartRestaurantBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CustomCartItem
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleNCartBottomSheet
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters.RPAdapterCuisine
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.SortedCookingSlots
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.views.DeliveryDateTabLayout
import com.bupp.wood_spoon_eaters.views.floating_buttons.WSFloatingButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page),
    DeliveryDateTabLayout.DeliveryTimingTabLayoutListener, WSErrorDialog.WSErrorListener, ClearCartRestaurantBottomSheet.ClearCartListener,
    ClearCartCookingSlotBottomSheet.ClearCartListener, SingleColumnTimePickerBottomSheet.TimePickerListener,
    WSFloatingButton.WSFloatingButtonListener,
    UpSaleNCartBottomSheet.UpsaleNCartBSListener {

    private val binding: FragmentRestaurantPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()
    private var isAdapterInitialized = false

    var adapterDishes: DishesMainAdapter? = null
    var adapterCuisines: RPAdapterCuisine? = RPAdapterCuisine()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("orderFlow - rest", "onViewCreated")

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding) {
            backButton.setOnClickListener {
                activity?.onBackPressed()
            }
            shareButton.setOnClickListener {
                viewModel.restaurantFullData.value?.shareUrl?.let {
                }
            }
            restaurantFragFloatingCartBtn.setWSFloatingBtnListener(this@RestaurantPageFragment)
            restaurantFragFloatingCartBtn.setOnClickListener { openCartNUpsaleDialog() }
        }
        with(binding.restaurantMainListLayout) {
            restaurantCuisinesList.adapter = adapterCuisines

            detailsSkeleton.visibility = View.VISIBLE
            detailsLayout.visibility = View.INVISIBLE

            handleTimerPickerUi()
            restaurantDeliveryDates.setTabListener(this@RestaurantPageFragment)


            adapterDishes = DishesMainAdapter(getDishesAdapterListener())
            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.divider_white_three)
            restaurantDishesList.addItemDecoration(DividerItemDecoratorDish(divider))
            restaurantDishesList.initSwipeableRecycler(adapterDishes!!)
//            if(!isAdapterInitialized){
//                adapterDishes?.let { adapter ->
//                    Log.d("orderFlow - rest","init restaurant adapter")
//                }
//                isAdapterInitialized = true
//            }else{
//                Log.d("orderFlow - rest","init restaurant adapter 2")
//                restaurantDishesList.adapter = adapterDishes
//                restaurantDishesList.initTouchHelpers(adapterDishes!!)
//            }
        }
    }


    private fun handleTimerPickerUi() {
        with(binding.restaurantMainListLayout) {
            restaurantTimePickerView.setOnClickListener {
                restaurantDeliveryDates.getCurrentSelection()?.let { date ->
                    if (date.cookingSlots.size > 1) {
                        viewModel.onTimePickerClicked()
                    }
                }
            }
        }
    }

    private fun openCartNUpsaleDialog() {
        UpSaleNCartBottomSheet(this).show(childFragmentManager, Constants.UPSALE_AND_CART_BOTTOM_SHEET)
    }

    override fun onCartDishCLick(customCartItem: CustomCartItem) {
        mainViewModel.openDishPageWithOrderItem(customCartItem)
    }

    override fun onGoToCheckoutClicked() {
        mainViewModel.handleNavigation(RestaurantMainViewModel.NavigationType.START_ORDER_CHECKOUT_ACTIVITY)
    }

    private fun onDeliveryTimingChange(date: SortedCookingSlots?) {

    }

    private fun initObservers() {
        mainViewModel.restaurantInitParamsLiveData.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { params ->
                viewModel.handleInitialParamData(params)
            }
        })
        viewModel.restaurantFullData.observe(viewLifecycleOwner, {
            handleRestaurantFullData(it)
        })
        viewModel.deliveryDatesData.observe(viewLifecycleOwner, {
            initDeliveryDatesTabLayout(it)
        })
        viewModel.onCookingSlotUiChange.observe(viewLifecycleOwner, {
            handleCookingSlotUiChange(it)
        })
        viewModel.dishListLiveData.observe(viewLifecycleOwner, {
            handleDishesList(it)
        })
        viewModel.initialParamData.observe(viewLifecycleOwner, {
            handleInitialParamData(it)
        })
        viewModel.orderLiveData.observe(viewLifecycleOwner, {
            viewModel.handleCartData(it)
        })
        viewModel.clearCartEvent.observe(viewLifecycleOwner, {
            handleClearCartEvent(it)
        })
        viewModel.wsErrorEvent.observe(viewLifecycleOwner, {
            handleWSError(it.getContentIfNotHandled())
        })
        viewModel.timePickerEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { it1 -> handleTimePickerClick(it1) }
        })
        viewModel.onCookingSlotForceChange.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { viewModel.forceCookingSlotUiChange(it) }
        })
        viewModel.floatingBtnEvent.observe(viewLifecycleOwner, {
            handleFloatingBtnEvent(it)
        })
    }

    private fun handleCookingSlotUiChange(uiChange: RestaurantPageViewModel.CookingSlotUi?) {
        uiChange?.let {
            with(binding.restaurantMainListLayout) {
                //delivery dates tabLayout
                restaurantDeliveryDates.selectTabByCookingSlotId(uiChange.cookingSlotId)

                //timer picker ui
                restaurantDeliveryDates.getCurrentSelection()?.let { date ->
                    if (date.cookingSlots.size > 1) {
//                        restaurantTimePickerView.setCompoundDrawables
                    } else {

                    }
                }
                handleTimePickerUi(uiChange.timePickerString)
            }
        }
    }

    private fun handleFloatingBtnEvent(event: CartManager.FloatingCartEvent?) {
        event?.let {
            binding.restaurantFragFloatingCartBtn.updateFloatingCartButton(it.restaurantName, it.allOrderItemsQuantity)
        }
    }

    private fun handleTimePickerClick(selectedCookingSlot: CookingSlot) {
        binding.restaurantMainListLayout.restaurantDeliveryDates.getCurrentSelection()?.let { deliveryDate ->
            val timePickerBottomSheet = SingleColumnTimePickerBottomSheet(this@RestaurantPageFragment)
            timePickerBottomSheet.setCookingSlots(selectedCookingSlot, deliveryDate.cookingSlots)
            timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
        }
    }

    private fun handleClearCartEvent(event: LiveEvent<CartManager.ClearCartEvent>) {
        event.getContentIfNotHandled()?.let {
            when (it.dialogType) {
                CartManager.ClearCartDialogType.CLEAR_CART_DIFFERENT_RESTAURANT -> {
                    ClearCartRestaurantBottomSheet.newInstance(it.curData, it.newData, this)
                        .show(childFragmentManager, Constants.CLEAR_CART_RESTAURANT_DIALOG_TAG)
                }
                CartManager.ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT -> {
                    ClearCartCookingSlotBottomSheet.newInstance(it.curData, it.newData, this)
                        .show(childFragmentManager, Constants.CLEAR_CART_COOKING_SLOT_DIALOG_TAG)
                }
            }
        }
    }

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            WSErrorDialog(it, this).show(childFragmentManager, Constants.ERROR_DIALOG)
        }
    }

    private fun handleTimePickerUi(timePickerStr: String?) {
        timePickerStr?.let {
            with(binding.restaurantMainListLayout) {
                restaurantTimePickerView.text = it
            }
        }
    }

    private fun handleDishesList(dishSections: RestaurantPageViewModel.DishListData?) {
        if (dishSections?.animateList == true)
            binding.restaurantMainListLayout.restaurantDishesList.scheduleLayoutAnimation()
        Log.d("orderFlow - rest", "handleDishesList ${dishSections?.dishes?.size}")
//        adapterDishes?.submitList(emptyList())
        adapterDishes?.submitList(dishSections?.dishes)
        binding.restaurantMainListLayout.restaurantDishesList.smoothScrollToPosition(0)
    }

    private fun initDeliveryDatesTabLayout(datesList: List<SortedCookingSlots>?) {
        datesList?.let {
            with(binding.restaurantMainListLayout) {
                restaurantDeliveryDates.initDates(it)
            }
        }
    }

    /** Headers data **/
    @SuppressLint("SetTextI18n")
    private fun handleInitialParamData(params: RestaurantInitParams) {
        with(binding) {
            Glide.with(requireContext()).load(params.coverPhoto?.url).into(coverPhoto)
            restHeaderRestName.text = params.restaurantName
            restHeaderChefName.text = "by ${params.chefName}"
            params.chefThumbnail?.url?.let { restHeaderChefThumbnail.setImage(it) }
            rating.text = "${params.rating}"// (${cook.reviewCount} ratings)"

            topHeaderRestaurantName.text = params.restaurantName
            topHeaderChefName.text = "by ${params.chefName}"
        }
    }

    private fun handleRestaurantFullData(restaurant: Restaurant) {
        with(binding) {
            //Cover photo/video
            if (restaurant.video.isNullOrEmpty()) {
                Glide.with(requireContext()).load(restaurant.cover?.url).into(coverPhoto)
            } else {
                //show video icon
            }
        }
        with(binding.restaurantMainListLayout) {

            //Description
            restaurantDescription.text = restaurant.about

            //Cuisines
            adapterCuisines?.submitList(restaurant.cuisines)
            restaurantCuisinesList.isVisible = !restaurant.cuisines.isNullOrEmpty()

            detailsLayout.visibility = View.VISIBLE
            detailsSkeletonLayout.root.visibility = View.GONE
        }
    }

//    private fun handleCookingSlotChange(cookingSlot: CookingSlot?) {
//        cookingSlot?.let {
//            with(binding.restaurantMainListLayout) {
//                //todo : not sure about this shit
//                restaurantDeliveryDates.selectTabByCookingSlot(cookingSlot)
//            }
//        }
//    }

    /** All sections click actions **/
    private fun getDishesAdapterListener(): DishesMainAdapter.DishesMainAdapterListener =
        object : DishesMainAdapter.DishesMainAdapterListener {
            override fun onDishClick(menuItem: MenuItem) {
                val curCookingSlot = viewModel.currentCookingSlot
                mainViewModel.openDishPage(menuItem, curCookingSlot)
            }

            override fun onDishSwipedAdd(item: DishSectionSingleDish) {
                item.menuItem.dishId?.let {
                    viewModel.addDishToCart(1, it)
                }
            }

            override fun onDishSwipedRemove(item: DishSectionSingleDish) {
                viewModel.removeOrderItemsByDishId(item.menuItem.dishId)
            }

        }

    override fun onTimerPickerCookingSlotChange(cookingSlot: CookingSlot) {
        //callback from TimePickerDialog - for changing cooking slot
        viewModel.onCookingSlotSelected(cookingSlot)
    }

    override fun onDateSelected(date: SortedCookingSlots?) {
        //callback from time tab layout
        date?.let {
            viewModel.onDeliveryDateChanged(date)
        }
    }
//    fun setFadeInOnScrollRecycler(
//        fadingView: View,
//        recyclerView: RecyclerView,
//        startFadeAtChild: Int = 0,
//        fadeDuration: Int = 500
//    ) {
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            var startFadeAt: Float? = null
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val currentChildIndex =
//                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//                if (startFadeAt == null && currentChildIndex == startFadeAtChild - 1) {
//                    startFadeAt = recyclerView.computeVerticalScrollOffset().toFloat()
//                }
//                startFadeAt?.let { itemHeight ->
//                    // The length that is currently scrolled
//                    val scrolledLength = recyclerView.computeVerticalScrollOffset() - itemHeight
//                    // The distance you need to scroll to end the animation
//                    val totalScrollableLength = fadeDuration
//                    if (abs(scrolledLength) > 0) {
//                        val alpha = scrolledLength.div(totalScrollableLength)
//                        fadingView.alpha = alpha
//                    }
//                }
//            }
//        })
//    }

    override fun onWSErrorDone() {
        viewModel.refreshRestaurantUi()
    }

    override fun onPerformClearCart() {
        viewModel.onPerformClearCart()
    }

    override fun onClearCartCanceled() {
        viewModel.refreshRestaurantUi()
    }

    override fun refreshParentOnCartCleared() {
        viewModel.refreshRestaurantUi()
    }

    override fun onFloatingCartStateChanged(isShowing: Boolean) {
        binding.restaurantFragHeightCorrection.isVisible = isShowing
    }

    private var hasMotionScrolled = false

    override fun onResume() {
        super.onResume()
        if (hasMotionScrolled) {
            binding.motionLayout.progress = 1F
        }

    }

    override fun onPause() {
        super.onPause()
        hasMotionScrolled = binding.motionLayout.progress > MOTION_TRANSITION_INITIAL
    }

    override fun onDestroyView() {
        adapterDishes = null
        adapterCuisines = null
        super.onDestroyView()
    }


    companion object {
        private const val MOTION_TRANSITION_COMPLETED = 1F
        private const val MOTION_TRANSITION_INITIAL = 0F
        private const val TAG = "RestaurantPageFragment"
    }


}