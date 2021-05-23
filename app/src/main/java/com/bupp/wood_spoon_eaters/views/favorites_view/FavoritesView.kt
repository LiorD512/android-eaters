package com.bupp.wood_spoon_eaters.views.favorites_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.databinding.FavoriteViewBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersBottomBarBinding
import com.bupp.wood_spoon_eaters.model.Dish


class FavoritesView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), SingleFeedListView.SingleFeedListViewListener {

    private var binding: FavoriteViewBinding = FavoriteViewBinding.inflate(LayoutInflater.from(context), this, true)

//    val viewModel = FavoritesViewViewModel()

    var listener: FavoritesViewListener? = null
    interface FavoritesViewListener{
        fun onDishClick(dish: Dish)
        fun onWorldwideInfoClick()
    }

    fun setFavoritesViewData(favorites: List<Dish>, listener: FavoritesViewListener){
        this.listener = listener
        handleList(favorites)
    }

    private fun handleList(favorites: List<Dish>?) {
        with(binding){
            if(!favorites.isNullOrEmpty()){
                favoritesViewList.initWithDishList(favorites, this@FavoritesView)//, viewModel.getDeliveryFeeString())

                favoritesViewEmptyLayout.visibility = View.GONE
                favoritesViewList.visibility = View.VISIBLE
            }else{
                favoritesViewEmptyLayout.visibility = View.VISIBLE
                favoritesViewList.visibility = View.GONE
            }
        }
    }

    override fun onDishClick(dish: Dish) {
        listener?.onDishClick(dish)
    }

    override fun onWorldwideInfoClick() {
        listener?.onWorldwideInfoClick()
    }

}