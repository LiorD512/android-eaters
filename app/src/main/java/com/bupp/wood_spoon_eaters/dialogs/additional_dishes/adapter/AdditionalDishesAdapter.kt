package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DishAddonView
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.additional_dishes_item.view.*


class AdditionalDishesAdapter(val context: Context, val listener: AdditionalDishesListener) :
    ListAdapter<Dish, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), DishAddonView.DishAddonListener {

    class AdditionalDishesDiffCallback: DiffUtil.ItemCallback<Dish>(){
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }
    }

    interface AdditionalDishesListener {
        fun onDishClick(dish: Dish) {}
        fun onAddBtnClick(dish: Dish) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("wowAdditionalAdapter","onBind")

        val dish = getItem(position)
        (holder as DishItemViewHolder).item.setDish(dish, position)
        holder.item.setDishAddonListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.additional_dishes_item, parent, false)
        return DishItemViewHolder(view)
    }

    override fun onAddBtnClick(position: Int) {
       listener.onAddBtnClick(getItem(position))
//       listener.onAddBtnClick(dish)
    }

    override fun onDishClick(position: Int) {
        listener.onDishClick(getItem(position))
    }


}


class DishItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val item = view.additionalDishItem
}




