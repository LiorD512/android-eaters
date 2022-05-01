package com.bupp.wood_spoon_chef.presentation.custom_views.orderSlotListView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.OrderSlotItemBinding
import com.bupp.wood_spoon_chef.data.remote.model.OrderItem

class OrderSlotListAdapter(val context: Context, private val items: List<OrderItem>) : RecyclerView.Adapter<OrderSlotListAdapter.ViewHolder>() {

    class ViewHolder(view: OrderSlotItemBinding) : RecyclerView.ViewHolder(view.root) {
        val title = view.orderSlotTitle
        val unitSold = view.orderSlotUnits
        val exceptionsLayout = view.orderSlotExceptionsLayout
        val exceptionText = view.orderSlotExceptions
        val sep = view.orderSlotSep
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderSlotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.dish.name
        val quantity = item.quantity
        val exceptions = item.notes
        if(quantity == 0){
            holder.exceptionsLayout.visibility = View.GONE
        }else{
            holder.unitSold.text = "x$quantity"
            holder.unitSold.visibility = View.VISIBLE
        }

        if(exceptions != null){
            if(exceptions.isEmpty()){
                holder.exceptionsLayout.visibility = View.GONE
            }else{
                holder.exceptionText.text = exceptions
                holder.exceptionsLayout.visibility = View.VISIBLE
            }
        }

        if(position+1 == items.size){
            holder.sep.visibility = View.GONE
        }
    }


}