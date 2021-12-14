package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_search

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentDishSearchBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.closeKeyboard
import me.ibrahimsn.lib.util.clear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DishSearchFragment : Fragment(R.layout.fragment_dish_search), FeedMainAdapter.FeedMainAdapterListener {

    private val mainViewModel by sharedViewModel<RestaurantMainViewModel>()
    private val viewModel by sharedViewModel<RestaurantPageViewModel>()
//    private val mainViewModel by sharedViewModel<MainViewModel>()
//    val viewModel by viewModel<SearchViewModel>()
    var binding: FragmentDishSearchBinding? = null
//    var searchAdapter: FeedMainAdapter? = null
    var adapterDishes: DishesMainAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDishSearchBinding.bind(view)

        initUi()
        initObservers()

        viewModel.initDishSearch()
    }

    private fun initUi() {
        with(binding!!){

            adapterDishes = DishesMainAdapter(getDishesAdapterListener())
            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.divider_white_three)
            val dishDividerDecoration = DividerItemDecoratorDish(divider)
            dishDividerDecoration.let {
                dishSearchFragList.addItemDecoration(it)
            }
            dishSearchFragList.initSwipeableRecycler(adapterDishes!!)
            dishSearchFragList.disableSwipes()

            dishSearchFragInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    if (s.isEmpty()) {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_reg)
                        binding!!.dishSearchFragInput.typeface = face
                        AnimationUtil().alphaOut(binding!!.dishSearchFragInput)
                    } else {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)
                        binding!!.dishSearchFragInput.typeface = face
                        AnimationUtil().alphaIn(binding!!.dishSearchFragInput)
                    }
                    super.afterTextChanged(s)
                }
            })

            dishSearchFragInput.addTextChangedListener(object: AutoCompleteTextWatcher(){
                override fun handleInputString(str: String) {
                    if(str.isNotEmpty()){
                        if(str.startsWith(" ")){
                            handleInputString(str.substring(1))
                        }else{
                            viewModel.filterDishSearch(str)
                        }
                    }else{
                        viewModel.initDishSearch()
                    }
                }
            })

            dishSearchFragClearInput.setOnClickListener {
                dishSearchFragInput.clear()
            }

//            dishSearchFragInput.setOnFocusChangeListener { view, b ->
//                if(b){
//                    viewModel.logEvent(Constants.EVENT_SEARCH_QUERY_CLICK)
//                }
//            }

            dishSearchFragInput.setOnEditorActionListener { _, actionId, _ ->
                when(actionId){
                    IME_ACTION_DONE -> {
                        closeKeyboard()
                        return@setOnEditorActionListener true
                    }
                    else -> {
                        return@setOnEditorActionListener false
                    }
                }
            }
        }
    }

    /** All sections click actions **/
    private fun getDishesAdapterListener(): DishesMainAdapter.DishesMainAdapterListener =
        object : DishesMainAdapter.DishesMainAdapterListener {
            override fun onDishClick(menuItem: MenuItem) {
//                val curCookingSlot = viewModel.currentCookingSlot
                val curCookingSlot = viewModel.currentCookingSlot
                mainViewModel.openDishPage(menuItem, curCookingSlot, true)
            }

            override fun onDishSwipedAdd(item: DishSectionSingleDish) {
                //swipe disabled
//                item.menuItem.dishId?.let {
//                    viewModel.addDishToCart(1, it)
//                    mainViewModel.logDishSwipeEvent(Constants.EVENT_SWIPED_ADD_DISH, item)
//                }
            }

            override fun onDishSwipedRemove(item: DishSectionSingleDish) {
                //swipe disabled
//                viewModel.removeOrderItemsByDishId(item.menuItem.dishId)
//                mainViewModel.logDishSwipeEvent(Constants.EVENT_SWIPED_REMOVE_DISH, item)
            }

        }

    private fun initObservers() {
        viewModel.dishSearchListLiveData.observe(viewLifecycleOwner, {
            handleDishesList(it)
        })
//        viewModel.searchResultData.observe(viewLifecycleOwner, {
//            it.feedData?.let { it1 -> searchAdapter?.setDataList(it1) }
//        })
//        mainViewModel.refreshSearchData.observe(viewLifecycleOwner, {
//            viewModel.getSearchTags()
//            viewModel.getRecentOrders()
//        })
//        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
//            viewModel.getSearchTags()
//            viewModel.getRecentOrders()
//        })
    }

    private fun handleDishesList(dishSections: RestaurantPageViewModel.DishListData?) {
        with(binding!!) {
            if (dishSections?.dishes.isNullOrEmpty()) {
//                if (!restaurantNoNetwork.isVisible) {
//                    detailsSkeleton.visibility = View.GONE
//                    restaurantNoNetwork.visibility = View.VISIBLE
//                } else {
//                    //Do nothing
//                }
            } else {
                restaurantNoNetwork.visibility = View.GONE
//                restaurantMainLayout.visibility = View.VISIBLE
//
//                if (dishSections?.animateList == true)
//                    restaurantDishesList.scheduleLayoutAnimation()

                adapterDishes?.submitList(dishSections?.dishes)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onRestaurantClick(restaurantInitParams: RestaurantInitParams) {
//        val query = binding!!.searchFragInput.text.toString()
//        restaurantInitParams.query = query
//        mainViewModel.startRestaurantActivity(restaurantInitParams)
//        viewModel.logRestaurantClick(restaurantInitParams)
    }

    override fun onTagClick(tag: String) {
//        binding!!.searchFragInput.setText(tag)
//        binding!!.searchFragInput.setSelection(tag.length)
//        viewModel.logTagEvent(Constants.EVENT_SEARCH_TAG_CLICK, tag)
    }

    override fun onShareBannerClick(campaign: Campaign) {
        //do nothing
    }

    override fun onChangeAddressClick() {
        //do nothing
    }

    override fun onDishSwiped() {
        //do nothing
    }

    override fun onRefreshFeedClick() {
//        val input = binding!!.searchFragInput.text.toString()
//        if(input.isNullOrBlank()){
//            viewModel.postDefaultData()
//        }else{
//            viewModel.searchInput(input)
//        }
    }

    override fun onComingSoonBtnClick(comingSoonData: FeedComingSoonSection) {
        //do nothing
    }

    override fun onResume() {
        super.onResume()
//        viewModel.showDefaultSearchData()

    }



}