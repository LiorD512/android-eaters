package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheetRestaurant
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters.RPAdapterCuisine
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DeliveryDate
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.views.DeliveryDateTabLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.SimpleExoPlayer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page),
    DeliveryDateTabLayout.DeliveryTimingTabLayoutListener,
    TimePickerBottomSheetRestaurant.TimePickerListener
{

    private val binding: FragmentRestaurantPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    var adapterDishes: DishesMainAdapter? = DishesMainAdapter(getDishesAdapterListener())
    var adapterCuisines: RPAdapterCuisine? = RPAdapterCuisine()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initData(mainViewModel.currentRestaurant)

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
//            restaurantDishesList.adapter = adapterDishes
            restaurantCuisinesList.adapter = adapterCuisines

            detailsSkeleton.visibility = View.VISIBLE
            detailsLayout.visibility = View.INVISIBLE

            restaurantTimePicker.setOnClickListener{
                viewModel.currentSelectedDate?.let{ deliveryDate->
                    val timePickerBottomSheet = TimePickerBottomSheetRestaurant(this@RestaurantPageFragment)
                    timePickerBottomSheet.setDeliveryDate(deliveryDate)
                    timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
                }
            }
            restaurantDeliveryTiming.setTabListener(this@RestaurantPageFragment)
            adapterDishes?.let{ adapter->
                val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.divider_white_three)
                restaurantDishesList.initSwipeableRecycler(adapter)
                restaurantDishesList.addItemDecoration(DividerItemDecoratorDish(divider))
            }
        }
    }

    private fun onDeliveryTimingChange(date: DeliveryDate?) {
        viewModel.onDeliveryDateChanged(date)
    }

    private fun initObservers() {
        viewModel.restaurantData.observe(viewLifecycleOwner, {
            handleRestaurantData(it)
        })
        viewModel.restaurantFullData.observe(viewLifecycleOwner, {
            handleRestaurantFullData(it)
        })
        viewModel.deliveryDates.observe(viewLifecycleOwner, {
            handleDeliveryTimingData(it)
        })
        viewModel.dishesList.observe(viewLifecycleOwner, {
            handleDishesList(it)
        })
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
    private fun handleRestaurantData(cook: Cook) {
        with(binding) {
            restHeaderRestName.text = "Restaurant name"
            restHeaderChefName.text = "by ${cook.getFullName()}"
            restHeaderChefThumbnail.setImage(cook.thumbnail)
            rating.text = "${cook.rating} (${cook.reviewCount} ratings)"

            topHeaderRestaurantName.text = "Restaurant name"
            topHeaderChefName.text = "by ${cook.getFullName()}"


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

//    private fun handleReadMoreButton() {
//        with(binding.restaurantMainListLayout) {
//            restaurantDescriptionViewMore.setOnClickListener {
//                if (restaurantDescription.maxLines == Integer.MAX_VALUE) {
//                    applyLayoutTransition()
//                    restaurantDescription.maxLines = 2
//                    restaurantDescriptionViewMore.text = "View more"
//                } else {
//                    applyLayoutTransition()
//                    restaurantDescription.maxLines = Integer.MAX_VALUE
//                    restaurantDescriptionViewMore.text = "View less"
//                }
//            }
//
//            restaurantDescription.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    val l = restaurantDescription.layout
//                    if (l != null) {
//                        val lines = l.lineCount
//                        if (lines > 2)
//                            restaurantDescriptionViewMore.visibility = View.VISIBLE
//                        restaurantDescription.maxLines = 2
//                    }
//                    restaurantDescription.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            })
//        }
//    }
//
//    private fun applyLayoutTransition() {
//        val transition = LayoutTransition()
//        transition.setDuration(300)
//        transition.enableTransitionType(LayoutTransition.CHANGING)
//        binding.restaurantMainListLayout.detailsLayout.layoutTransition = transition
//    }

    /** All sections click actions **/
    private fun getDishesAdapterListener(): DishesMainAdapter.DishesMainAdapterListener =
        object : DishesMainAdapter.DishesMainAdapterListener {
            override fun onDishClick(dish: Dish) {
                mainViewModel.openDishPage(dish)
            }
        }


//            restaurantDescription.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    val l = restaurantDescription.layout
//                    if (l != null) {
//                        val lines = l.lineCount
//                        if (lines > 2)
//                            restaurantDescriptionViewMore.visibility = View.VISIBLE
//                        restaurantDescription.maxLines = 2
//                    }
//                    restaurantDescription.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            })
//        }
//    }

    //    /** All sections click actions **/
//    private fun getMainAdapterListener(): RestaurantPageMainAdapter.RestaurantPageMainAdapterListener =
//        object: RestaurantPageMainAdapter.RestaurantPageMainAdapterListener{
//
    override fun onDateSelected(date: DeliveryDate?) {
        onDeliveryTimingChange(date)
    }

    override fun onCookingSlotSelected() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        adapterDishes = null
//        adapterCuisines = null
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
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