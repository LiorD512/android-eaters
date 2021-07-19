package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters.RPAdapterCuisine
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Restaurant
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page) {

    private val binding: FragmentRestaurantPageBinding by viewBinding()

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    val adapter = DishesMainAdapter(getDishesAdapterListener())

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
                //todo: Implement Click Listener
            }
            menuButton.setOnClickListener {
                //todo: Implement Click Listener
            }
        }

        binding.restaurantMainListLayout.restaurantDishesList.adapter = adapter
    }

    private fun initObservers() {
        viewModel.restaurantData.observe(viewLifecycleOwner, {
            handleRestaurantData(it)
        })
        viewModel.restaurantFullData.observe(viewLifecycleOwner,{
            handleRestaurantFullData(it)
        })
        viewModel.deliveryTiming.observe(viewLifecycleOwner,{
            handleDeliveryTimingData(it)
        })
        viewModel.dishesList.observe(viewLifecycleOwner,{
            handleDishesList(it)
        })
    }

    private fun handleDishesList(dishSections: List<DishSections>?) {
        adapter.submitList(dishSections)
    }

    private fun getDishesAdapterListener(): DishesMainAdapter.RestaurantPageMainAdapterListener =
        object: DishesMainAdapter.RestaurantPageMainAdapterListener{

        }

    private fun handleDeliveryTimingData(datesList: List<RestaurantPageViewModel.DeliveryDate>?) {
        datesList?.let{
            with(binding.restaurantMainListLayout){
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
            Glide.with(requireContext()).load(cook.thumbnail).transform(CircleCrop()).into(restHeaderChefThumbnail)
            rating.text = "${cook.rating} (${cook.reviewCount} ratings)"

            topHeaderRestaurantName.text = "Restaurant name"
            topHeaderChefName.text = "by ${cook.getFullName()}"
        }
    }

    private fun handleRestaurantFullData(restaurant: Restaurant){
        with(binding) {
            Glide.with(requireContext()).load(restaurant.cover).into(coverPhoto)
        }
        with(binding.restaurantMainListLayout){
            restaurantDescription.text = restaurant.about
            val adapter= RPAdapterCuisine()
            adapter.submitList(restaurant.cuisines)
            restaurantCuisinesList.adapter = adapter
            restaurantCuisinesList.isVisible = !restaurant.cuisines.isNullOrEmpty()

            restaurantDescriptionViewMore.setOnClickListener {
                if (restaurantDescription.maxLines == Integer.MAX_VALUE) {
                    restaurantDescription.maxLines = 2
                    restaurantDescriptionViewMore.text = "View more"
                } else {
                    restaurantDescription.maxLines = Integer.MAX_VALUE
                    restaurantDescriptionViewMore.text = "View less"
                }
            }

            restaurantDescription.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val l = restaurantDescription.layout
                    if (l != null) {
                        val lines = l.lineCount
                        if (lines > 2)
                            restaurantDescriptionViewMore.visibility = View.VISIBLE
                            restaurantDescription.maxLines = 2
                    }
                    restaurantDescription.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }


    }

//    /** All sections click actions **/
//    private fun getMainAdapterListener(): RestaurantPageMainAdapter.RestaurantPageMainAdapterListener =
//        object: RestaurantPageMainAdapter.RestaurantPageMainAdapterListener{
//
//        }

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