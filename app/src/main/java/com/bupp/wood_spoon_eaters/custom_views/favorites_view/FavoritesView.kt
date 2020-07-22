package com.bupp.wood_spoon_eaters.custom_views.favorites_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.model.Dish
import com.example.matthias.mvvmcustomviewexample.custom.FavoritesViewViewModel
import kotlinx.android.synthetic.main.favorite_view.view.*


class FavoritesView : FrameLayout, FavoritesViewViewModel.FavoritesViewListener,
    SingleFeedListView.SingleFeedListViewListener {

    var listener: FavoritesViewListener? = null
    interface FavoritesViewListener{
        fun onDishClick(dish: Dish)
        fun onWorldwideInfoClick()
    }

    fun setFavoritesViewListener(listener: FavoritesViewListener){
        this.listener = listener
    }

    val viewModel = FavoritesViewViewModel()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.favorite_view, this, true)
//        initFavorites()
    }

    fun initFavorites() {
        viewModel.fetchFavorites()
        viewModel.setFavoritesViewListener(this)
    }


    override fun onDone(favorites: ArrayList<Dish>?) {
        if(favorites != null && favorites.size > 0){
            favoritesViewList.initWithDishList(favorites, this)//, viewModel.getDeliveryFeeString())

            favoritesViewEmptyLayout.visibility = View.GONE
            favoritesViewList.visibility = View.VISIBLE
        }else{
            favoritesViewEmptyLayout.visibility = View.VISIBLE
            favoritesViewList.visibility = View.GONE
        }
    }

    override fun onDishClick(dish: Dish) {
        listener?.onDishClick(dish)
    }

    override fun onWorldwideInfoClick() {
        listener?.onWorldwideInfoClick()
    }

}