package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.IngredientsCheckoutAdapter
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.track_order_item_details_view.view.*

class TrackOrderItemDetailsAdapter(val context: Context) :
    ListAdapter<OrderItem, RecyclerView.ViewHolder>(TrackOrderItemDetailsDiffCallback()){

    class TrackOrderItemDetailsDiffCallback: DiffUtil.ItemCallback<OrderItem>(){
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.trackOrderItemImage
        val title = view.trackOrderItemName
        val price = view.trackOrderItemPrice
        val ingredientsList = view.trackOrderItemIngredientsList
        val note = view.trackOrderItemNote
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.track_order_item_details_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as ViewHolder
        Glide.with(context).load(item.dish.thumbnail).circleCrop().into(holder.img)
        holder.title.text = item.dish.name
        holder.price.text = item.price.formatedValue
        if(item.notes.isNullOrEmpty()){
            holder.note.visibility = View.GONE
        }else{
            holder.note.visibility = View.VISIBLE
            holder.note.text  = item.notes
        }

        holder.ingredientsList.layoutManager = LinearLayoutManager(context)
        val adapter = IngredientsCheckoutAdapter(context, item.removedIndredients)
        holder.ingredientsList.adapter = adapter
    }


}