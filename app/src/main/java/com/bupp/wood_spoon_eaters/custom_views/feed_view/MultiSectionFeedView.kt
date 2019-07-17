package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.many_cooks_view.ManyCooksView
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.multi_section_feed_view.view.*
import kotlinx.android.synthetic.main.single_feed_list_view.view.*

class MultiSectionFeedView : FrameLayout, SearchAdapter.SearchAdapterListener, ManyCooksView.ManyCooksViewListener,
    SingleFeedListView.SingleFeedListViewListener {


    lateinit var listener: MultiSectionFeedViewListener
    interface MultiSectionFeedViewListener {
        fun onDishClick(dish: Dish)
        fun onCookClick(cook: Cook)
        fun onShareClick(){}
    }

    private lateinit var adapter: SearchAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.multi_section_feed_view, this, true)
    }

    fun setMultiSectionFeedViewListener(listener: MultiSectionFeedViewListener){
        this.listener = listener
    }

    fun initFeed(hasFavorites: Boolean, feedArr: ArrayList<Feed>) {
        val dishArr: ArrayList<Feed> = arrayListOf()
        val cooksArr: ArrayList<Cook> = arrayListOf()

        for(item in feedArr){
            if(isADishResource(item)){
                dishArr.add(item)
            }
            else if(isACookResource(item)){
                cooksArr.addAll(item.search?.results as ArrayList<Cook>)
            }
        }

        if(dishArr.size > 0){
            initDishesList(dishArr)
        }
        if(cooksArr.size > 0){
            initCooksList(cooksArr)
        }

        if(!hasFavorites){
            multiSectionViewFavEmptyLayout.visibility = View.VISIBLE
        }else{
            multiSectionViewFavEmptyLayout.visibility = View.GONE
        }
    }

    private fun initDishesList(dishArr: ArrayList<Feed>) {
        for(item in dishArr){
            val singleFeedListView = SingleFeedListView(context)
            singleFeedListView.initSingleFeed(item, this)
            multiSectionViewDishList.addView(singleFeedListView)
        }
    }

    private fun initCooksList(cooksArr: ArrayList<Cook>) {
        multiSectionViewTopCooks.initCooksView(cooksArr, this)
    }

    private fun isADishResource(item: Feed): Boolean {
        return item.search?.resource == Constants.RESOURCE_TYPE_DISH// && item.hasItems()
    }

    private fun isACookResource(item: Feed): Boolean {
        return item.search?.resource == Constants.RESOURCE_TYPE_COOK
    }



    override fun onDishClick(dish: Dish) {
        if(::listener.isInitialized){
            listener.onDishClick(dish)
        }
    }

    override fun onCookClicked(clicked: Cook) {
        if(::listener.isInitialized){
            listener.onCookClick(clicked)
        }
    }



}