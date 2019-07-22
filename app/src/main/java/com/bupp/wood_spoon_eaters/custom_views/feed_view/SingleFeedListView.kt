package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Feed
import kotlinx.android.synthetic.main.single_feed_list_view.view.*
import androidx.recyclerview.widget.LinearSnapHelper
import com.bupp.wood_spoon_eaters.features.main.search.SingleFeedItemDecoration


class SingleFeedListView : FrameLayout, SingleFeedAdapter.SearchAdapterListener {


    lateinit var listener: SingleFeedListViewListener
    interface SingleFeedListViewListener {
        fun onDishClick(dish: Dish)
        fun onFavClick(dishId: Long, isFavorite: Boolean)
    }

    private lateinit var adapter: SingleFeedAdapter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.single_feed_list_view, this, true)
    }


    fun initSingleFeed(feedObj: Feed, listener: SingleFeedListViewListener) {
        this.listener = listener
        singleFeedListViewTitle.text = feedObj.title

        singleFeedListView.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(singleFeedListView)

        adapter = SingleFeedAdapter(context!!, feedObj.search!!.results as ArrayList<Dish>, this)
        singleFeedListView.adapter = adapter
    }

    override fun onDishClick(dish: Dish) {
        if(::listener.isInitialized){
            listener.onDishClick(dish)
        }
    }

    override fun onFavClick(dishId: Long, favSelected: Boolean) {
        listener.onFavClick(dishId, favSelected)
    }


}