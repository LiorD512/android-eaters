package com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.MenuItem

abstract class SwipeableAdapter<T: SwipeableAdapterItem>(diff :DiffUtil.ItemCallback<T>) :
    ListAdapter<T, RecyclerView.ViewHolder>(diff) {

    fun isInCart(position: Int): Boolean {
        return getItem(position).isInCart()
    }

    fun isSwipeable(position: Int): Boolean {
        return getItem(position).isSwipeable
    }

    fun updateItemQuantityAdd(position: Int) {
        getItem(position).quantity++
    }

    fun updateItemQuantityRemoved(position: Int) {
        if (getItem(position).quantity > 0) {
            getItem(position).quantity--
        }
    }

    companion object {
        const val TAG = "wowUpsaleAdapter"
    }
}

abstract class SwipeableAdapterItem {
    abstract var quantity: Int
    abstract val menuItem: MenuItem?
    abstract val isSwipeable: Boolean
    fun isInCart(): Boolean {
        return quantity > 0
    }
}