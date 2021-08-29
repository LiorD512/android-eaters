//package com.bupp.wood_spoon_eaters.custom_views.feed_view
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Log
//import android.view.LayoutInflater
//import android.widget.FrameLayout
//import com.bupp.wood_spoon_eaters.views.favorites_view.FavoritesView
//import com.bupp.wood_spoon_eaters.custom_views.many_cooks_view.ManyCooksView
//import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.bupp.wood_spoon_eaters.databinding.MultiSectionFeedViewBinding
//import com.bupp.wood_spoon_eaters.model.*
//import com.bupp.wood_spoon_eaters.views.ShareBanner
//
//
//class MultiSectionFeedView @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    FrameLayout(context, attrs, defStyleAttr), SearchAdapter.SearchAdapterListener, ManyCooksView.ManyCooksViewListener,
//    SingleFeedListView.SingleFeedListViewListener, FavoritesView.FavoritesViewListener, ShareBanner.WSCustomBannerListener {
//
//    private var binding: MultiSectionFeedViewBinding = MultiSectionFeedViewBinding.inflate(LayoutInflater.from(context), this, true)
//
//    lateinit var listener: MultiSectionFeedViewListener
//    interface MultiSectionFeedViewListener {
//        fun onDishClick(dish: Dish)
//        fun onCookClick(cook: Cook)
//        fun refreshList(){}
//        fun onEmptyhDishList(){}
//        fun onWorldwideInfoClick()
//        fun onShareClick(campaign: Campaign)
//    }
//
//    private lateinit var adapter: SearchAdapter
//
//    init {
//        binding.multiSectionViewRefreshLayout.setOnRefreshListener { refresh() }
//    }
//
//    private fun refresh() {
//        Log.d("wowMultiSection","refresh !")
//        listener.refreshList()
////        multiSectionViewRefreshLayout.isRefreshing = false
//    }
//
//
//    fun setMultiSectionFeedViewListener(listener: MultiSectionFeedViewListener){
//        this.listener = listener
//    }
//
//    fun initFeed(feedArr: List<Feed>, isWithFavorites: Boolean = true, stubView: Int, isPullToRefreshEnabled: Boolean = true, isEvent: Boolean = false) {
//        with(binding){
//            clearFeed()
//            multiSectionViewRefreshLayout.isRefreshing = false
//            val dishArr: ArrayList<Feed> = arrayListOf()
//            val cooksArr: ArrayList<Cook> = arrayListOf()
//            var cooksTitle: String = ""
//
//            for(item in feedArr){
//                if(isADishResource(item)){
//                    dishArr.add(item)
//                }
//                else if(isACookResource(item)){
//                    item.title?.let{
//                        cooksTitle = it
//                    }
//                    cooksArr.addAll(item.search?.results as ArrayList<Cook>)
//                }
//            }
//
//            if(dishArr.size > 0){
//                initDishesList(dishArr, isEvent)
//            }else{
//                listener.onEmptyhDishList()
//            }
//            if(cooksArr?.size > 0){
//                initCooksList(cooksArr, cooksTitle)
//            }
//
//
////            when(stubView){
////                Constants.FEED_VIEW_STUB_SHARE -> {
////                    multiSectionViewShareViewLayout.visibility = View.VISIBLE
////                    multiSectionViewLogoLayout.visibility = View.GONE
////                    multiSectionViewShareViewLayout.setOnClickListener { listener.onShareClick() }
////                }
////                Constants.FEED_VIEW_STUB_PROMO -> {
////                    multiSectionViewShareViewLayout.visibility = View.GONE
////                    multiSectionViewLogoLayout.visibility = View.VISIBLE
////                }
////            }
//
//
//            if(!isPullToRefreshEnabled){
//                multiSectionViewRefreshLayout.isEnabled = false
//            }
//        }
//    }
//
//    fun initFavorites(favorites: List<Dish>){
//        binding.multiSectionViewFavorites.setFavoritesViewData(favorites, this)
//    }
//
//    private fun clearFeed() {
//        binding.multiSectionViewDishList.removeAllViews()
//    }
//
//    private fun initDishesList(dishArr: ArrayList<Feed>, isEvent: Boolean) {
//        dishArr.forEachIndexed { index, feed ->
//            val singleFeedListView = SingleFeedListView(context)
//            singleFeedListView.initSingleFeed(feed, this, isEvent, index == 0)
//            binding.multiSectionViewDishList.addView(singleFeedListView)
//
//        }
//
//    }
//
//    private fun initCooksList(cooksArr: ArrayList<Cook>, cooksTitle: String) {
//        binding.multiSectionViewTopCooks.setTitle(cooksTitle)
//        binding.multiSectionViewTopCooks.initCooksView(cooksArr, this)
//    }
//
//    private fun isADishResource(item: Feed): Boolean {
//        return item.search?.resource == Constants.RESOURCE_TYPE_DISH// && item.hasItems()
//    }
//
//    private fun isACookResource(item: Feed): Boolean {
//        return item.search?.resource == Constants.RESOURCE_TYPE_COOK
//    }
//
//    override fun onDishClick(dish: Dish) {
//        if(::listener.isInitialized){
//            listener.onDishClick(dish)
//        }
//    }
//
//    override fun onCookClicked(clicked: Cook) {
//        if(::listener.isInitialized){
//            listener.onCookClick(clicked)
//        }
//    }
//
//
//    override fun onWorldwideInfoClick() {
//        if(::listener.isInitialized){
//            listener.onWorldwideInfoClick()
//        }
//    }
//
//    fun initShareCampaign(campaign: Campaign){
//        binding.multiSectionViewShareBanner.initCustomBannerByCampaign(campaign, this)
//    }
//
//    override fun onShareBannerClick(campaign: Campaign?) {
//        campaign?.let{
//            listener.onShareClick(it)
//        }
//    }
//
//}