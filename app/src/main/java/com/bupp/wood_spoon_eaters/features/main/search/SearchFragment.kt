package com.bupp.wood_spoon_eaters.features.main.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable



class SearchFragment : Fragment(), SearchAdapter.SearchAdapterListener {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var itemDecor: GridItemDecoration
    private val SEARCH_LIST_TYPE_CUISINE: Int = 0
    private val SEARCH_LIST_TYPE_RESULT: Int = 1

    private lateinit var adapter: SearchAdapter
    val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
                    if(event.cooks != null && event.cooks.size > 0){
                        adapter.updateCooks(event.cooks)
                        haveCooks = true
                    }
                    if(event.dishes != null && event.dishes.size > 0){
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
//                        adapter.updateSearchData(event.searchId, event.searchResponse)
                    }
                }else{

                }
            }
        })
    }

    private fun initUi() {
        itemDecor = GridItemDecoration(2, 2)
        searchFragList.addItemDecoration(itemDecor)
        adapter = SearchAdapter(context!!, viewModel.getCuisineLabels(), this)
        searchFragList.adapter = adapter
        showListLayout(SEARCH_LIST_TYPE_CUISINE)
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
//                searchFragList.addItemDecoration(SearchItemDecoration())
//                val dividerDrawable = ContextCompat.getDrawable(context!!, R.drawable.search_list_divider)
//                val itemDecor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//                itemDecor.setDrawable(dividerDrawable!!)
//                searchFragList.addItemDecoration(RecyclerView.ItemDecoration(context))
            }
        }
    }

    private fun showEmptyLayout() {
        searchFragList.visibility = View.GONE
        searchFragEmptyLayout.visibility = View.VISIBLE
    }

    fun onSearchInputChanged(str: String) {
        Log.d("wowSearch","onSearchInputChanged: " + str)
        searchFragPb.show()
        viewModel.search(str)
    }

    override fun onDishClick(dish: Dish) {
        Log.d("wowSearch","onDishClick")
    }

    override fun onCookClick(cook: Cook) {
        Log.d("wowSearch","onCookClick")
    }

    override fun onCuisineClick(cuisine: CuisineLabel) {
        Log.d("wowSearch","onCuisineClick")
        updateInput(cuisine.name)
    }

    private fun updateInput(name: String) {
        (activity as MainActivity).updateSearchInput(name)
    }

}
