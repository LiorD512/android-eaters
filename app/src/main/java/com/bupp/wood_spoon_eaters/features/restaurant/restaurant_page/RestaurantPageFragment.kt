package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.databinding.RestaurantMainListLayoutBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters.RPAdapterCuisine
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DeliveryDate
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.DeliveryDateTabLayout
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page),
    DeliveryDateTabLayout.DeliveryTimingTabLayoutListener {

    private val binding: FragmentRestaurantPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    var adapterDishes: DishesMainAdapter? = DishesMainAdapter(getDishesAdapterListener())
    var adapterCuisines: RPAdapterCuisine? = RPAdapterCuisine()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }
        with(binding.restaurantMainListLayout) {
            restaurantCuisinesList.adapter = adapterCuisines

            detailsSkeleton.visibility = View.VISIBLE
            detailsLayout.visibility = View.INVISIBLE

            restaurantTimePickerView.setOnClickListener {
                Log.d(TAG, "restaurantTimePicker clicker")
                viewModel.currentSelectedDate?.let { deliveryDate ->
//                    val timePickerBottomSheet = TimePickerBottomSheetRestaurant(this@RestaurantPageFragment)
//                    timePickerBottomSheet.setDeliveryDate(deliveryDate)
//                    timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
                }
            }
            restaurantDeliveryTiming.setTabListener(this@RestaurantPageFragment)
            adapterDishes?.let { adapter ->
                val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.divider_white_three)
                restaurantDishesList.addItemDecoration(DividerItemDecoratorDish(divider))
                restaurantDishesList.initSwipeableRecycler(adapter)
            }
        }
    }

    private fun onDeliveryTimingChange(date: DeliveryDate?) {
        viewModel.onDeliveryDateChanged(date)
    }

    private fun initObservers() {
        mainViewModel.restaurantInitParamsLiveData.observe(viewLifecycleOwner, {
            handleRestaurantInitialParamData(it)
        })
        viewModel.restaurantFullData.observe(viewLifecycleOwner, {
            handleRestaurantFullData(it)
        })
        viewModel.deliveryDatesData.observe(viewLifecycleOwner, {
            handleDeliveryTimingData(it)
        })
        viewModel.timePickerUi.observe(viewLifecycleOwner, {
            handleTimePickerUi(it)
        })
        viewModel.dishesList.observe(viewLifecycleOwner, {
            handleDishesList(it)
        })
    }

    private fun handleTimePickerUi(timePickerStr: String?) {
        timePickerStr?.let {
            with(binding.restaurantMainListLayout) {
                restaurantTimePickerView.text = it
            }
        }
    }

    private fun handleDishesList(dishSections: List<DishSections>?) {
        binding.restaurantMainListLayout.restaurantDishesList.scheduleLayoutAnimation()
        adapterDishes?.submitList(dishSections)
    }

    private fun handleDeliveryTimingData(datesList: List<DeliveryDate>?) {
        datesList?.let {
            with(binding.restaurantMainListLayout) {
                restaurantDeliveryTiming.initDates(it)
                restaurantDeliveryTiming.onDateChangeListener()
            }
        }
    }

    /** Headers data **/
    @SuppressLint("SetTextI18n")
    private fun handleRestaurantInitialParamData(restaurantInitParams: RestaurantInitParams) {
        with(binding) {
            viewModel.getRestaurantFullData(restaurantInitParams.restaurantId)
            Glide.with(requireContext()).load(restaurantInitParams.coverPhoto).into(coverPhoto)
            restHeaderRestName.text = restaurantInitParams.restaurantName
            restHeaderChefName.text = "by ${restaurantInitParams.chefName}"
            restaurantInitParams.chefThumbnail?.url?.let { restHeaderChefThumbnail.setImage(it) }
            rating.text = "${restaurantInitParams.rating}"// (${cook.reviewCount} ratings)"

            topHeaderRestaurantName.text = restaurantInitParams.restaurantName
            topHeaderChefName.text = "by ${restaurantInitParams.chefName}"


        }
    }

    private fun handleRestaurantFullData(restaurant: Restaurant) {
        with(binding) {
            //Cover photo/video
            if (restaurant.video.isNullOrEmpty()) {
                Glide.with(requireContext()).load(restaurant.cover).into(coverPhoto)
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


    /** All sections click actions **/
    private fun getDishesAdapterListener(): DishesMainAdapter.DishesMainAdapterListener =
        object : DishesMainAdapter.DishesMainAdapterListener {
            override fun onDishClick(menuItem: MenuItem) {
                mainViewModel.openDishPage(menuItem)
            }
        }

    override fun onDateSelected(date: DeliveryDate?) {
        onDeliveryTimingChange(date)
    }

//    override fun onCookingSlotSelected() {
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
//        adapterDishes = null
//        adapterCuisines = null
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }

    fun setFadeInOnScrollRecycler(
        fadingView: View,
        recyclerView: RecyclerView,
        startFadeAtChild: Int = 0,
        fadeDuration: Int = 500
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var startFadeAt: Float? = null

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val currentChildIndex =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (startFadeAt == null && currentChildIndex == startFadeAtChild - 1) {
                    startFadeAt = recyclerView.computeVerticalScrollOffset().toFloat()
                }
                startFadeAt?.let { itemHeight ->
                    // The length that is currently scrolled
                    val scrolledLength = recyclerView.computeVerticalScrollOffset() - itemHeight
                    // The distance you need to scroll to end the animation
                    val totalScrollableLength = fadeDuration
                    if (abs(scrolledLength) > 0) {
                        val alpha = scrolledLength.div(totalScrollableLength)
                        fadingView.alpha = alpha
                    }
                }
            }
        })
    }
}