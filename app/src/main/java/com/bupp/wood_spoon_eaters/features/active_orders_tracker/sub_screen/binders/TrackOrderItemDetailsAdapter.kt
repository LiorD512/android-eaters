package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.TrackOrderItemDetailsViewBinding
import com.bupp.wood_spoon_eaters.model.OrderItem

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

    class ViewHolder(view: TrackOrderItemDetailsViewBinding) : RecyclerView.ViewHolder(view.root) {
        val img = view.trackOrderItemImage
        val title = view.trackOrderItemName
        val price = view.trackOrderItemPrice
        val note = view.trackOrderItemNote
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrackOrderItemDetailsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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
    }


}