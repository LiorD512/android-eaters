package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.views.favorites_view.FavoritesView
import com.bupp.wood_spoon_eaters.custom_views.many_cooks_view.ManyCooksView
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.multi_section_feed_view.view.*



class MultiSectionFeedView : FrameLayout, SearchAdapter.SearchAdapterListener, ManyCooksView.ManyCooksViewListener,
    SingleFeedListView.SingleFeedListViewListener, FavoritesView.FavoritesViewListener {


    lateinit var listener: MultiSectionFeedViewListener
    interface MultiSectionFeedViewListener {
        fun onDishClick(dish: Dish)
        fun onCookClick(cook: Cook)
        fun refreshList(){}
        fun onEmptyhDishList(){}
        fun onWorldwideInfoClick()
//        fun onFavClick(dishId: Long, isFavorite: Boolean)
        fun onShareClick()
    }

    private lateinit var adapter: SearchAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.multi_section_feed_view, this, true)

        multiSectionViewRefreshLayout.setOnRefreshListener { refresh() }
    }

    private fun refresh() {
        Log.d("wowMultiSection","refresh !")
        listener.refreshList()
//        multiSectionViewRefreshLayout.isRefreshing = false
    }


    fun setMultiSectionFeedViewListener(listener: MultiSectionFeedViewListener){
        this.listener = listener
    }

    fun initFeed(feedArr: List<Feed>, isWithFavorites: Boolean = true, stubView: Int, isPullToRefreshEnabled: Boolean = true, isEvent: Boolean = false) {
        clearFeed()
        multiSectionViewRefreshLayout.setRefreshing(false)
        val dishArr: ArrayList<Feed> = arrayListOf()
        val cooksArr: ArrayList<Cook> = arrayListOf()
        var cooksTitle: String = ""

        for(item in feedArr){
            if(isADishResource(item)){
                dishArr.add(item)
            }
            else if(isACookResource(item)){
                item.title?.let{
                    cooksTitle = it
                }
                cooksArr.addAll(item.search?.results as ArrayList<Cook>)
            }
        }

        if(dishArr.size > 0){
            initDishesList(dishArr, isEvent)
        }else{
            listener.onEmptyhDishList()
        }
        if(cooksArr?.size > 0){
            initCooksList(cooksArr, cooksTitle)
        }

//        when(isWithFavorites){
//            true -> {
//                multiSectionViewFavorites.setFavoritesViewListener(this)
//                multiSectionViewFavorites.initFavorites()
//            }
//            false -> {
//                multiSectionViewFavorites.visibility = View.GONE
//            }
//        }

        when(stubView){
            Constants.FEED_VIEW_STUB_SHARE -> {
                multiSectionViewShareView.visibility = View.VISIBLE
                multiSectionViewLogo.visibility = View.GONE
                multiSectionViewShareView.setOnClickListener { listener.onShareClick() }
            }
            Constants.FEED_VIEW_STUB_PROMO -> {
                multiSectionViewShareView.visibility = View.GONE
                multiSectionViewLogo.visibility = View.VISIBLE
            }
        }

        if(!isPullToRefreshEnabled){
            multiSectionViewRefreshLayout.isEnabled = false
        }
    }

    fun initFavorites(favorites: List<Dish>){
        multiSectionViewFavorites.setFavoritesViewData(favorites, this)
    }

    private fun clearFeed() {
        multiSectionViewDishList.removeAllViews()
    }

    private fun initDishesList(dishArr: ArrayList<Feed>, isEvent: Boolean) {
        dishArr.forEachIndexed { index, feed ->
            val singleFeedListView = SingleFeedListView(context)
            singleFeedListView.initSingleFeed(feed, this, isEvent, index == 0)
            multiSectionViewDishList.addView(singleFeedListView)

        }

    }

    private fun initCooksList(cooksArr: ArrayList<Cook>, cooksTitle: String) {
        multiSectionViewTopCooks.setTitle(cooksTitle)
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


    override fun onWorldwideInfoClick() {
        if(::listener.isInitialized){
            listener.onWorldwideInfoClick()
        }
    }

}