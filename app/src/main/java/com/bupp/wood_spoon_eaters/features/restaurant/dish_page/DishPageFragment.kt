package com.bupp.wood_spoon_eaters.features.restaurant.dish_page;

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheetRestaurant
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentDishPageBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
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


class DishPageFragment : Fragment(R.layout.fragment_dish_page) {

    private val binding: FragmentDishPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<DishPageViewModel>()


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
//                viewModel.dishData.value?.shareUrl?.let {
//                }
            }
        }
        with(binding.dishMainListLayout) {
//            restaurantDishesList.adapter = adapterDishes
//            restaurantCuisinesList.adapter = adapterCuisines
//
//            detailsSkeleton.visibility = View.GONE
//            detailsLayout.visibility = View.INVISIBLE
//
//            restaurantTimePicker.setOnClickListener{
//                viewModel.currentSelectedDate?.let{ deliveryDate->
//                    val timePickerBottomSheet = TimePickerBottomSheetRestaurant(this@RestaurantPageFragment)
//                    timePickerBottomSheet.setDeliveryDate(deliveryDate)
//                    timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
//                }
//            }
//            restaurantDeliveryTiming.setTabListener(this@RestaurantPageFragment)
//            adapterDishes?.let{ adapter->
//                restaurantDishesList.initSwipeableRecycler(adapter)
//            }
        }
    }

    private fun initObservers() {
        viewModel.dishData.observe(viewLifecycleOwner, {
            handleDishData(it)
        })
        viewModel.dishFullData.observe(viewLifecycleOwner, {
            handleDishFullData(it)
        })
    }

    /** Headers data **/
    private fun handleDishData(dish: Dish) {
        with(binding) {
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

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
}