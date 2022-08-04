package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartCookingSlotBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.clear_cart_dialogs.clear_cart_restaurant.ClearCartRestaurantBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewsBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.UpSaleNCartBottomSheet
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel.UnavailableUiType.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters.RPAdapterCuisine
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.bupp.wood_spoon_eaters.views.DeliveryDateTabLayout
import com.bupp.wood_spoon_eaters.views.FavoriteBtn
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

    private var binding: FragmentRestaurantPageBinding? = null

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    var adapterDishes: DishesMainAdapter? = null

    var adapterCuisines: RPAdapterCuisine? = RPAdapterCuisine()

    var dishDividerDecoration: RecyclerView.ItemDecoration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navArgs: RestaurantPageFragmentArgs by navArgs()
        binding = FragmentRestaurantPageBinding.bind(view)
        viewModel.handleInitialParamData(navArgs.extras)

        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_HOME_CHEF)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            backButton.setOnClickListener {
                activity?.onBackPressed()
            }
            shareButton.setOnClickListener {
                viewModel.restaurantFullData.value?.getShareTextStr()?.let {
                    Utils.shareText(requireActivity(), it)
                    viewModel.logEvent(Constants.EVENT_SHARE_RESTAURANT)
                }
            }
            ratingLayout.setOnClickListener {
                openReviews()
            }
            restaurantFragFloatingCartBtn.setWSFloatingBtnListener(this@RestaurantPageFragment)
            restaurantFragFloatingCartBtn.setOnClickListener { openCartNUpsaleDialog() }
        }

        with(binding!!.restaurantMainListLayout) {
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

            restaurantNoNetworkLayout.noNetworkSectionBtn.setOnClickListener {
                detailsSkeleton.visibility = View.VISIBLE
                restaurantNoNetwork.visibility = View.GONE
                viewModel.reloadPage(false)
            }
            tipFragToolTip.setOnClickListener{
                DeliveryInfoBottomSheet().show(
                    childFragmentManager,
                    DeliveryInfoBottomSheet::class.java.simpleName
                )
            }
        }
    }


    private fun handleTimerPickerUi() {
        with(binding!!.restaurantMainListLayout) {
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
        binding!!.restaurantFragFloatingCartBtn.isEnabled = false
        UpSaleNCartBottomSheet().show(childFragmentManager, Constants.UPSALE_AND_CART_BOTTOM_SHEET)
        binding!!.restaurantFragFloatingCartBtn.isEnabled = true
    }

    override fun onCartDishCLick(customOrderItem: CustomOrderItem) {
        mainViewModel.openDishPageWithOrderItem(customOrderItem)
    }

    override fun onGoToCheckoutClicked() {
        mainViewModel.handleNavigation(RestaurantMainViewModel.NavigationType.START_ORDER_CHECKOUT_ACTIVITY)
    }

    private fun initObservers() {
        viewModel.initialParamData.observe(viewLifecycleOwner) {
            handleInitialParamData(it)
        }
        viewModel.restaurantFullData.observe(viewLifecycleOwner) {
            handleRestaurantFullData(it)
        }
        viewModel.deliveryDatesData.observe(viewLifecycleOwner) {
            initDeliveryDatesTabLayout(it)
        }
        viewModel.onCookingSlotUiChange.observe(viewLifecycleOwner) {
            handleCookingSlotUiChange(it)
        }
        viewModel.dishListLiveData.observe(viewLifecycleOwner) {
            handleDishesList(it)
        }
        viewModel.orderLiveData.observe(viewLifecycleOwner) {
            viewModel.handleCartData()
        }
        viewModel.clearCartEvent.observe(viewLifecycleOwner) {
            handleClearCartEvent(it)
        }
        viewModel.wsErrorEvent.observe(viewLifecycleOwner) {
            handleWSError(it.getContentIfNotHandled())
        }
        viewModel.timePickerEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { it1 -> handleTimePickerClick(it1) }
        }
        viewModel.onCookingSlotForceChange.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { viewModel.forceCookingSlotUiChange(it) }
        }
        viewModel.floatingBtnEvent.observe(viewLifecycleOwner) {
            handleFloatingBtnEvent(it)
        }
        viewModel.favoriteEvent.observe(viewLifecycleOwner) {
            handleFavoriteEvent(it)
        }
        viewModel.unavailableEventData.observe(viewLifecycleOwner) {
            handleUnAvailableEvent(it)
        }
        mainViewModel.reOpenCartEvent.observe(viewLifecycleOwner) {
            reOpenCart()
        }
        viewModel.networkErrorEvent.observe(viewLifecycleOwner) {
            handleNetworkErrorEvent(it)
        }
        viewModel.selectedDishNavigationLifeData.observe(viewLifecycleOwner) { menuItem ->
            val curCookingSlot = viewModel.currentCookingSlot
            mainViewModel.openDishPage(menuItem, curCookingSlot)
        }
    }

    private fun handleUnAvailableEvent(event: RestaurantPageViewModel.UnavailableUiData?) {
        event?.let {
            if (event.type != AVAILABLE) {
                showErrorToast(event.text!!, binding!!.root, Toast.LENGTH_LONG)
                with(binding!!) {
                    restFragUnavailableGradient.visibility = View.VISIBLE
                    restaurantMainListLayout.restaurantDishesList.initSwipeableRecycler(adapterDishes!!, false)
                    restaurantMainListLayout.restaurantCookingSlotLayout.visibility = View.GONE
                }
            } else {
                binding!!.restaurantMainListLayout.restaurantDishesList.initSwipeableRecycler(adapterDishes!!, true)
            }
        }
    }

    private fun reOpenCart() {
        UpSaleNCartBottomSheet().show(childFragmentManager, Constants.UPSALE_AND_CART_BOTTOM_SHEET)
    }

    /** Headers data **/
    @SuppressLint("SetTextI18n")
    private fun handleInitialParamData(params: RestaurantInitParams) {
        with(binding!!) {
            rating.text = "${params.rating}"
            topHeaderRestaurantName.text = params.restaurantName
            topHeaderChefName.text = "By ${params.chefName}"
        }
    }

    private fun handleRestaurantFullData(restaurant: Restaurant) {
        binding?.apply {
            restHeaderRestName.text = restaurant.restaurantName
            restHeaderChefName.text = "By ${restaurant.getFullName()}"
            topHeaderRestaurantName.text = restaurant.restaurantName
            topHeaderChefName.text = "By ${restaurant.firstName}"

            //Cover photo + video
            Glide.with(requireContext()).load(restaurant.cover?.url).into(coverPhoto)
            restaurant.thumbnail?.url?.let { restHeaderChefThumbnail.setImage(it) }
            restaurant.flagUrl?.let { restHeaderChefThumbnail.setFlag(it) }

            restFragVideoBtn.isVisible = !restaurant.video.isNullOrEmpty()
            restFragVideoBtn.setOnClickListener {
                restaurant.video?.let { video ->
                    VideoViewDialog(video).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
                    mainViewModel.logClickVideo(restaurant.getFullName(), restaurant.id)
                }
            }

            //ratings
            ratingLayout.isVisible = restaurant.reviewCount > 0
            ratingCount.text = "(${restaurant.reviewCount} ratings)"

            //favorite
            restHeaderFavorite.setIsFavorite(restaurant.isFavorite)
            restHeaderFavorite.setClickListener(this@RestaurantPageFragment)
        }

        binding?.restaurantMainListLayout?.apply {
            restaurantDescription.text = restaurant.about

            adapterCuisines?.submitList(restaurant.tags)
            restaurantCuisinesList.isVisible = !restaurant.tags.isNullOrEmpty()

            detailsLayout.visibility = View.VISIBLE
            detailsSkeletonLayout.root.visibility = View.GONE
        }
    }

    private fun handleCookingSlotUiChange(uiChange: RestaurantPageViewModel.CookingSlotUi?) {
        uiChange?.let {
            with(binding!!.restaurantMainListLayout) {
                //delivery dates tabLayout
                val selectedDate: SortedCookingSlots? = if (uiChange.forceTabChange) {
                    restaurantDeliveryDates.selectTabByCookingSlotId(uiChange.cookingSlotId)
                } else {
                    //timer picker ui
                    restaurantDeliveryDates.getCurrentSelection()
                }
                //timer picker ui
                selectedDate?.let { date ->
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

    private fun handleFloatingBtnEvent(event: FloatingCartEvent?) {
        event?.let {
            binding!!.restaurantFragFloatingCartBtn.updateFloatingCartButton(it.restaurantName, it.allOrderItemsQuantity)
        }
    }

    private fun handleTimePickerClick(selectedCookingSlot: CookingSlot) {
        binding!!.restaurantMainListLayout.restaurantDeliveryDates.getCurrentSelection()?.let { deliveryDate ->
            val timePickerBottomSheet = SingleColumnTimePickerBottomSheet(this@RestaurantPageFragment)
            timePickerBottomSheet.setCookingSlots(selectedCookingSlot, deliveryDate.cookingSlots)
            timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
        }
    }

    private fun handleClearCartEvent(event: LiveEvent<ClearCartEvent>) {
        event.getContentIfNotHandled()?.let {
            when (it.dialogType) {
                ClearCartDialogType.CLEAR_CART_DIFFERENT_RESTAURANT -> {
                    ClearCartRestaurantBottomSheet.newInstance(it.curData, it.newData, this)
                        .show(childFragmentManager, Constants.CLEAR_CART_RESTAURANT_DIALOG_TAG)
                }
                ClearCartDialogType.CLEAR_CART_DIFFERENT_COOKING_SLOT -> {
                    ClearCartCookingSlotBottomSheet.newInstance(it.curData, it.newData, this)
                        .show(childFragmentManager, Constants.CLEAR_CART_COOKING_SLOT_DIALOG_TAG)
                }
            }
        }
    }

    private fun handleWSError(errorEvent: String?) {
        errorEvent?.let {
            showErrorToast(errorEvent, binding!!.root)
            viewModel.refreshRestaurantUi()
        }
    }

    private fun handleTimePickerUi(timePickerStr: String?) {
        timePickerStr?.let {
            with(binding!!.restaurantMainListLayout) {
                restaurantTimePickerView.text = it
            }
        }
    }

    private fun handleDishesList(dishSections: RestaurantPageViewModel.DishListData?) {
        with(binding!!) {
            restaurantMainListLayout.restaurantNoNetwork.visibility = View.GONE
            restaurantMainListLayout.restaurantMainLayout.visibility = View.VISIBLE

            if (dishSections?.animateList == true)
                restaurantMainListLayout.restaurantDishesList.scheduleLayoutAnimation()

            adapterDishes?.submitList(dishSections?.dishes)
        }
    }


    private fun handleNetworkErrorEvent(it: LiveEvent<Boolean>?) {
        with(binding!!) {
            it?.getContentIfNotHandled()?.let {
                if (!restaurantMainListLayout.restaurantNoNetwork.isVisible) {
                    restaurantMainListLayout.detailsSkeleton.visibility = View.GONE
                    restaurantMainListLayout.restaurantNoNetwork.visibility = View.VISIBLE
                } else {
                    //Do nothing
                }
            }
        }
    }


    private fun openReviews() {
        val restaurant = viewModel.restaurantFullData.value
        restaurant?.let { restaurant ->
            val header = "${restaurant.getAvgRating()} (${restaurant?.reviewCount ?: ""} reviews)"
            ReviewsBottomSheet.newInstance(restaurant.id, restaurant.restaurantName ?: "", header).show(childFragmentManager, Constants.RATINGS_DIALOG_TAG)
        }
    }

    private fun initDeliveryDatesTabLayout(datesList: List<SortedCookingSlots>?) {
        datesList?.let {
            with(binding!!.restaurantMainListLayout) {
                restaurantDeliveryDates.initDates(it)
            }
        }
    }

    private fun handleFavoriteEvent(event: LiveEvent<Boolean>?) {
        event?.getContentIfNotHandled()?.let { isSuccess ->
            if (isSuccess) {
                mainViewModel.forceFeedRefresh()
            } else {
                binding!!.restHeaderFavorite.onFail()
            }
        }
    }

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
                    mainViewModel.logDishSwipeEvent(Constants.EVENT_SWIPED_ADD_DISH, item)
                }
            }

            override fun onDishSwipedRemove(item: DishSectionSingleDish) {
                viewModel.removeOrderItemsByDishId(item.menuItem.dishId)
                mainViewModel.logDishSwipeEvent(Constants.EVENT_SWIPED_REMOVE_DISH, item)
            }

        }

    override fun onTimerPickerCookingSlotChange(cookingSlot: CookingSlot) {
        //callback from TimePickerDialog - for changing cooking slot
        viewModel.onCookingSlotSelected(cookingSlot, false)
        viewModel.logEvent(Constants.EVENT_CHANGE_COOKING_SLOT)
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
        binding!!.restaurantFragHeightCorrection.isVisible = isShowing
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
            binding!!.motionLayout.progress = 1F
        }
    }

    override fun onPause() {
        super.onPause()
        hasMotionScrolled = binding!!.motionLayout.progress > MOTION_TRANSITION_INITIAL
    }

    override fun onDestroyView() {
        adapterDishes = null
        adapterCuisines = null
        binding = null
        super.onDestroyView()
    }


    companion object {
        private const val MOTION_TRANSITION_INITIAL = 0F
        private const val TAG = "RestaurantPageFragment"
    }

}

