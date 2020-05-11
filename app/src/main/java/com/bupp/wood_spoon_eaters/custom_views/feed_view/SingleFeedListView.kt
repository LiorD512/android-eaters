package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import kotlinx.android.synthetic.main.single_feed_list_view.view.*
import androidx.recyclerview.widget.LinearSnapHelper


class SingleFeedListView : FrameLayout, SingleFeedAdapter.SearchAdapterListener {


    private var deliveryFee: String = ""
    lateinit var listener: SingleFeedListViewListener

    interface SingleFeedListViewListener {
        fun onDishClick(dish: Dish)
//        fun onFavClick(dishId: Long, isFavorite: Boolean)
    }

    private lateinit var adapter: SingleFeedAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.single_feed_list_view, this, true)

        initUi()
    }

    private fun initUi() {
        singleFeedListView.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(singleFeedListView)
    }


    fun initSingleFeed(feedObj: Feed, listener: SingleFeedListViewListener, isEvent: Boolean = false, isFirst: Boolean = false) {
        this.deliveryFee = deliveryFee
        this.listener = listener
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

        adapter = SingleFeedAdapter(context!!, feedObj.search!!.results as ArrayList<Dish>, this, isEvent)
        singleFeedListView.adapter = adapter
    }

    fun initWithDishList(dishes: ArrayList<Dish>, listener: SingleFeedListViewListener) {
        this.listener = listener
        singleFeedListViewTitle.visibility = View.GONE
        adapter = SingleFeedAdapter(context!!, dishes, this)
        singleFeedListView.adapter = adapter
    }

    override fun onDishClick(dish: Dish) {
        if (::listener.isInitialized) {
            listener.onDishClick(dish)
        }
    }

//    override fun onFavClick(dishId: Long, favSelected: Boolean) {
//        listener.onFavClick(dishId, favSelected)
//    }


}