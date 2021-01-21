package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.segment.analytics.Analytics


class SearchFragment : Fragment(), SearchAdapter.SearchAdapterListener, NewDishSuggestionDialog.OfferDishDialogListener,
    FilterFragment.FilterFragmentListener, CookProfileDialog.CookProfileDialogListener {

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
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Search")

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.searchEvent.observe(this, Observer { event ->
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

        viewModel.nextSearchEvent.observe(this, Observer { event ->
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

        viewModel.suggestionEvent.observe(this, Observer { event ->
            if(event != null){
                if(event.isSuccess){
                    (activity as MainActivity).loadDishOfferedDialog()
                }else{
                    Toast.makeText(context,"There was a problem with our servers, please try again later",Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getCookEvent.observe(this, Observer { event ->
            searchFragPb.hide()
            if(event.isSuccess){
                CookProfileDialog(this, event.cook!!).show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
    }

    //CookProfileDialog interface
    override fun onDishClick(menuItemId: Long) {
        (activity as MainActivity).loadNewOrderActivity(menuItemId)
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    private fun initUi() {
        itemDecor = GridItemDecoration(15.dpToPx(), 2)
        searchFragList.addItemDecoration(itemDecor)
        adapter = SearchAdapter(context!!, viewModel.getCuisineLabels(), this)
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
        Log.d("wowSearch","onSearchInputChanged: " + str)
        this.query = str
        if(str.isNullOrEmpty()){
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
            (activity as MainActivity).loadNewOrderActivity(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        searchFragPb.show()
        viewModel.getCurrentCook(cook.id)
    }

    override fun onCuisineClick(cuisine: CuisineLabel) {
        Log.d("wowSearch","onCuisineClick")
        updateInput(cuisine.name)
        viewModel.getDishesByCusineId(cuisine.id)
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
