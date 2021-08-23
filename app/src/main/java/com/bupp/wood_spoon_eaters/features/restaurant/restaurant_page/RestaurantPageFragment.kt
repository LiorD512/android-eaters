package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartCookingSlotBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartRestaurantBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.fav_btn.FavoriteBtn
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.dialogs.WSErrorDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
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
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.bupp.wood_spoon_eaters.views.DeliveryDateTabLayout
import com.bupp.wood_spoon_eaters.views.floating_buttons.WSFloatingButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page),
    DeliveryDateTabLayout.DeliveryTimingTabLayoutListener,
    ClearCartRestaurantBottomSheet.ClearCartListener,
    ClearCartCookingSlotBottomSheet.ClearCartListener,
    SingleColumnTimePickerBottomSheet.TimePickerListener,
    WSFloatingButton.WSFloatingButtonListener,
    FavoriteBtn.FavoriteBtnListener,
    UpSaleNCartBottomSheet.UpsaleNCartBSListener {

    private val binding: FragmentRestaurantPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    var adapterDishes: DishesMainAdapter? = null

    var adapterCuisines: RPAdapterCuisine? = RPAdapterCuisine()

    var dishDividerDecoration: RecyclerView.ItemDecoration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navArgs: RestaurantPageFragmentArgs by navArgs()
        viewModel.handleInitialParamData(navArgs.extras)
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
                    Utils.shareText(requireActivity(), it)
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
            dishDividerDecoration = DividerItemDecoratorDish(divider)
            dishDividerDecoration?.let {
                restaurantDishesList.addItemDecoration(it)
            }
            restaurantDishesList.initSwipeableRecycler(adapterDishes!!)
        }
    }


    private fun handleTimerPickerUi() {
        with(binding.restaurantMainListLayout) {
            restaurantTimePickerViewLayout.setOnClickListener {
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

    private fun initObservers() {
        viewModel.initialParamData.observe(viewLifecycleOwner, {
            handleInitialParamData(it)
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
        viewModel.favoriteEvent.observe(viewLifecycleOwner, {
            handleFavoriteEvent(it)
        })
    }

    /** Headers data **/
    @SuppressLint("SetTextI18n")
    private fun handleInitialParamData(params: RestaurantInitParams) {
        with(binding) {
            Glide.with(requireContext()).load(params.coverPhoto?.url).into(coverPhoto)
            restHeaderRestName.text = params.restaurantName
            restHeaderChefName.text = "by ${params.chefName}"
            params.chefThumbnail?.url?.let { restHeaderChefThumbnail.setImage(it) }
            rating.text = "${params.rating}"
            ratingLayout.isVisible = params.rating ?: 0.0 > 0

            topHeaderRestaurantName.text = params.restaurantName
            topHeaderChefName.text = "by ${params.chefName}"
        }
    }

    private fun handleRestaurantFullData(restaurant: Restaurant) {
        with(binding) {
            //Cover photo + video
            Glide.with(requireContext()).load(restaurant.cover?.url).into(coverPhoto)
            restFragVideoBtn.isVisible = !restaurant.video.isNullOrEmpty()
            restFragVideoBtn.setOnClickListener {
                restaurant.video?.let { video ->
                    VideoViewDialog(restaurant.getFullName(), video).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
                }
            }

            //ratings
            ratingCount.isVisible = restaurant.reviewCount > 0
            ratingCount.text = "(${restaurant.reviewCount} ratings)"

            //favorite
            restHeaderFavorite.setIsFavorite(restaurant.isFavorite)
            restHeaderFavorite.setClickListener(this@RestaurantPageFragment)
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

    private fun handleCookingSlotUiChange(uiChange: RestaurantPageViewModel.CookingSlotUi?) {
        uiChange?.let {
            with(binding.restaurantMainListLayout) {
                //delivery dates tabLayout
                if (uiChange.forceTabChnage) {
                    restaurantDeliveryDates.selectTabByCookingSlotId(uiChange.cookingSlotId)
                }

                //timer picker ui
                restaurantDeliveryDates.getCurrentSelection()?.let { date ->
                    if (date.cookingSlots.size > 1) {
                        restaurantTimePickerViewIcon.visibility = View.VISIBLE
                    } else {
                        restaurantTimePickerViewIcon.visibility = View.GONE
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
            showErrorToast(errorEvent, binding.root)
            viewModel.refreshRestaurantUi()
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
        adapterDishes?.submitList(dishSections?.dishes)
    }

    private fun initDeliveryDatesTabLayout(datesList: List<SortedCookingSlots>?) {
        datesList?.let {
            with(binding.restaurantMainListLayout) {
                restaurantDeliveryDates.initDates(it)
            }
        }
    }

    private fun handleFavoriteEvent(event: LiveEvent<Boolean>?) {
        event?.getContentIfNotHandled()?.let { isSuccess ->
            if (!isSuccess)
                binding.restHeaderFavorite.onFail()
        }
    }

    /** All sections click actions **/
    private fun getDishesAdapterListener(): DishesMainAdapter.DishesMainAdapterListener =
        object : DishesMainAdapter.DishesMainAdapterListener {
            override fun onDishClick(menuItem: MenuItem) {
//                val curCookingSlot = viewModel.currentCookingSlot
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
        viewModel.onCookingSlotSelected(cookingSlot, false)
    }

    override fun onDateSelected(date: SortedCookingSlots?) {
        //callback from time tab layout
        date?.let {
            viewModel.onDeliveryDateChanged(date)
        }
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

    override fun onAddToFavoriteClick() {
        viewModel.addToFavorite()
    }

    override fun onRemoveFromFavoriteClick() {
        viewModel.removeFromFavoriteClick()
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


///    fun setFadeInOnScrollRecycler(
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
