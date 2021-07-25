package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.os.Bundle
import android.view.View
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.SimpleExoPlayer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page) {

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
                    restaurantMainListLayout.shimmerViewContainer.hideShimmer()
                    restaurantMainListLayout.test.isVisible = true
                    restaurantMainListLayout.test1.root.isVisible = false
                }
            }
        }
        with(binding.restaurantMainListLayout) {
            restaurantDishesList.adapter = adapterDishes
            restaurantCuisinesList.adapter = adapterCuisines
        }
    }

    private fun initObservers() {
        viewModel.restaurantData.observe(viewLifecycleOwner, {
            handleRestaurantData(it)
        })
        viewModel.restaurantFullData.observe(viewLifecycleOwner, {
            handleRestaurantFullData(it)
        })
        viewModel.deliveryTiming.observe(viewLifecycleOwner, {
            handleDeliveryTimingData(it)
        })
        viewModel.dishesList.observe(viewLifecycleOwner, {
            handleDishesList(it)
        })
    }

    private fun handleDishesList(dishSections: List<DishSections>?) {
        adapterDishes?.submitList(dishSections)
    }

    private fun getDishesAdapterListener(): DishesMainAdapter.RestaurantPageMainAdapterListener =
        object : DishesMainAdapter.RestaurantPageMainAdapterListener {
        }

    private fun handleDeliveryTimingData(datesList: List<RestaurantPageViewModel.DeliveryDate>?) {
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
            Glide.with(requireContext()).load(cook.thumbnail).transform(CircleCrop()).into(restHeaderChefThumbnail)
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
                handleVideoCover(restaurant.video)
            }
        }
        with(binding.restaurantMainListLayout) {
            //Description
            restaurantDescription.text = restaurant.about
            handleReadMoreButton()

            //Cuisines
            adapterCuisines?.submitList(restaurant.cuisines)
            restaurantCuisinesList.isVisible = !restaurant.cuisines.isNullOrEmpty()
        }
    }

    private fun handleReadMoreButton() {
        with(binding.restaurantMainListLayout) {
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

    private var player: ExoPlayer? = null
    private fun handleVideoCover(video: String) {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        binding.coverVideo.isVisible = true
        binding.coverVideo.player = player
        binding.coverVideo.hideController()
        val mediaItem = MediaItem.fromUri(video)
        player?.let { player ->
            player.setMediaItem(mediaItem)
            player.playWhenReady = true
            player.repeatMode = REPEAT_MODE_ALL
            player.volume = 0f
            player.prepare()
        }


        //todo : ask if plater should play sound?
//        binding.coverVideo.setOnClickListener {
//            if (player?.audioComponent?.volume == 0f) {
//                (player as SimpleExoPlayer).volume = 1f
//            } else {
//                (player as SimpleExoPlayer).volume = 0f
//            }
//        }
    }

//    /** All sections click actions **/
//    private fun getMainAdapterListener(): RestaurantPageMainAdapter.RestaurantPageMainAdapterListener =
//        object: RestaurantPageMainAdapter.RestaurantPageMainAdapterListener{
//
//        }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterDishes = null
        adapterCuisines = null
        player?.release()
        player = null
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