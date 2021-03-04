package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.NewDishSuggestionDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.features.main.filter.FilterFragment
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SearchFragment : Fragment(), SearchAdapter.SearchAdapterListener, NewDishSuggestionDialog.OfferDishDialogListener,
    FilterFragment.FilterFragmentListener, CookProfileDialog.CookProfileDialogListener {

    private val mainViewModel by sharedViewModel<MainViewModel>()

    companion object {
        fun newInstance() = SearchFragment()
    }

    private var query: String = ""
    private lateinit var itemDecor: GridItemDecoration
    private val SEARCH_LIST_TYPE_CUISINE: Int = 0
    private val SEARCH_LIST_TYPE_RESULT: Int = 1

    private lateinit var adapter: SearchAdapter
    val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Search")

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.searchEvent.observe(viewLifecycleOwner, { event ->
            if(event != null){
                searchFragPb.hide()
                if(event.isSuccess){
                    var haveCooks = false
                    var haveDishes = false
                    if(event.cooks != null && event.cooks?.size > 0){
                        adapter.updateCooks(event.cooks)
                        haveCooks = true
                    }
                    if(event.dishes != null && event.dishes?.size > 0){
                        adapter.updateDishes(event.dishes)
                        haveDishes = true
                    }
                    showListLayout(SEARCH_LIST_TYPE_RESULT)
                    if(!haveCooks && !haveDishes){
                        showEmptyLayout()
                    }
                }else{
                    showEmptyLayout()
                }
            }
        })

        viewModel.nextSearchEvent.observe(viewLifecycleOwner, { event ->
            if(event != null){
                searchFragPb.hide()
                if(event.isSuccess){
                    if(event.searchResponse != null){
//                        ingredientsAdapter.updateSearchData(event.searchId, event.searchResponse)
                    }
                }else{

                }
            }
        })

        viewModel.suggestionEvent.observe(viewLifecycleOwner, { event ->
            if(event != null){
                if(event.isSuccess){
                    (activity as MainActivity).loadDishOfferedDialog()
                }else{
                    Toast.makeText(context,"There was a problem with our servers, please try again later",Toast.LENGTH_SHORT).show()
                }
            }
        })

//        viewModel.getCookEvent.observe(viewLifecycleOwner, { event ->
//            searchFragPb.hide()
//            if(event != null){
//                CookProfileDialog(this, event).show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
//            }
//        })
    }

    //CookProfileDialog interface
    override fun onDishClick(menuItemId: Long) {
        mainViewModel.onDishClick(menuItemId)
//        (activity as MainActivity).loadNewOrderActivity(menuItemId)
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    private fun initUi() {
        itemDecor = GridItemDecoration(15.dpToPx(), 2)
        searchFragList.addItemDecoration(itemDecor)
        adapter = SearchAdapter(requireContext(), viewModel.getCuisineLabels(), this)
        searchFragList.adapter = adapter
        showListLayout(SEARCH_LIST_TYPE_CUISINE)

        searchFragSoundsGood.setOnClickListener {
            NewDishSuggestionDialog(this, query).show(childFragmentManager, Constants.OFFER_DISH_TAG)
        }
    }

    override fun onNewDishSuggestion(dishName: String, dishDetails: String) {
        viewModel.suggestDish(dishName,dishDetails)
    }

    private fun showListLayout(type: Int) {
        searchFragList.visibility = View.VISIBLE
        searchFragEmptyLayout.visibility = View.GONE
        when(type){
            SEARCH_LIST_TYPE_CUISINE -> {
                searchFragList.layoutManager = GridLayoutManager(context,2)
                itemDecor.setDecorType(0)
                searchFragList.requestLayout()
            }
            SEARCH_LIST_TYPE_RESULT -> {
                searchFragList.layoutManager = LinearLayoutManager(context)
                itemDecor.setDecorType(1)
                searchFragList.requestLayout()
            }
        }
    }

    private fun showEmptyLayout() {
        searchFragList.visibility = View.GONE
        searchFragEmptyLayout.visibility = View.VISIBLE
    }

    fun onSearchInputChanged(str: String) {
        Log.d("wowSearch", "onSearchInputChanged: $str")
        this.query = str
        if(str.isEmpty()){
            adapter.clearData()
            showListLayout(SEARCH_LIST_TYPE_CUISINE)
            viewModel.clearSearchQuery()
        }else{
            searchFragPb.show()
            viewModel.search(query)
        }
    }

    override fun onDishClick(dish: Dish) {
        Log.d("wowSearch","onDishClick")
        dish.menuItem?.let{
            mainViewModel.onDishClick(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        val args = Bundle()
        args.putLong(Constants.ARG_COOK_ID, cook.id)
        val cookDialog = CookProfileDialog(this)
        cookDialog.arguments = args
        cookDialog.show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
    }

    override fun onCuisineClick(cuisine: CuisineLabel) {
        Log.d("wowSearch","onCuisineClick")
        updateInput(cuisine.name)
        viewModel.getDishesByCuisineId(cuisine)
    }

    private fun updateInput(name: String) {
        (activity as MainActivity).updateSearchBarTitle(name)
    }

    fun openFilterDialog() {
        FilterFragment(this).show(childFragmentManager, Constants.PICK_FILTERS_TAG)
    }

    override fun onFilterDone(isFiltered: Boolean) {
        searchFragPb.show()
        (activity as MainActivity).updateFilterUi(isFiltered)
        viewModel.search(query)
    }

    override fun onFavClick(dishId: Long, favSelected: Boolean) {
        if(favSelected){
            viewModel.likeDish(dishId)
        }else{
            viewModel.unlikeDish(dishId)
        }
    }

}
