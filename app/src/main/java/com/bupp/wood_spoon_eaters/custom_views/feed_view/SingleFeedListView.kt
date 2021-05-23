package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import androidx.recyclerview.widget.LinearSnapHelper
import com.bupp.wood_spoon_eaters.databinding.SingleFeedListViewBinding


class SingleFeedListView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), SingleFeedAdapter.SearchAdapterListener {


    private var deliveryFee: String = ""
    lateinit var listener: SingleFeedListViewListener

    private var binding: SingleFeedListViewBinding = SingleFeedListViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface SingleFeedListViewListener {
        fun onDishClick(dish: Dish)
        fun onWorldwideInfoClick()
//        fun onFavClick(dishId: Long, isFavorite: Boolean)
    }

    private lateinit var adapter: SingleFeedAdapter

    init {
        initUi()
    }


    private fun initUi() {
        with(binding){
            singleFeedListView.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(singleFeedListView)
        }
    }


    fun initSingleFeed(feedObj: Feed, listener: SingleFeedListViewListener, isEvent: Boolean = false, isFirst: Boolean = false) {
        this.deliveryFee = deliveryFee
        this.listener = listener
        with(binding){
            feedObj.title?.let {
                if (it.isNotEmpty()) {
                    singleFeedListViewTitle.text = feedObj.title
                    singleFeedListViewTitle.visibility = View.VISIBLE
                    if(!isFirst){
                        singleFeedListViewSep.visibility = View.VISIBLE
                    }
                }
            }
            feedObj.subtitle?.let {
                if (it.isNotEmpty()) {
                    singleFeedListViewSubtitle.text = feedObj.subtitle
                    singleFeedListViewSubtitle.visibility = View.VISIBLE

                }
            }

            adapter = SingleFeedAdapter(context, feedObj.search!!.results as ArrayList<Dish>, this@SingleFeedListView, isEvent)
            singleFeedListView.adapter = adapter
        }
    }

    fun initWithDishList(dishes: List<Dish>, listener: SingleFeedListViewListener) {
        this.listener = listener
        with(binding){
            singleFeedListViewTitle.visibility = View.GONE
            adapter = SingleFeedAdapter(context, dishes, this@SingleFeedListView)
            singleFeedListView.adapter = adapter
        }
    }

    override fun onDishClick(dish: Dish) {
        if (::listener.isInitialized) {
            listener.onDishClick(dish)
        }
    }

    override fun onWorldwideInfoClick() {
        if (::listener.isInitialized) {
            listener.onWorldwideInfoClick()
        }
    }

}