package com.bupp.wood_spoon_eaters.dialogs.order_date_chooser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.OrderDateChooserItemBinding
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils

class OrderDateChooserAdapter(val context: Context, private val menuItems: ArrayList<MenuItem>, val listener: OrderDateChooserAdapterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var selectedmenuItem: MenuItem? = null

    interface OrderDateChooserAdapterListener {
        fun onOrderDateClick(selected: MenuItem)
    }

    class ViewHolder(view: OrderDateChooserItemBinding) : RecyclerView.ViewHolder(view.root) {
        var item = view.orderDateItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderDateChooserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val menuItem = menuItems[position]
//        val datePeriod = "${Utils.parseTwoDates(menuItem.cookingSlot.startsAt, menuItem.cookingSlot.endsAt)}"
        val datePeriod = DateUtils.parseTwoDates(menuItem.cookingSlot.orderFrom, menuItem.cookingSlot.endsAt)
        (holder as ViewHolder).item.text = "$datePeriod"
        holder.item.setOnClickListener {
            listener?.onOrderDateClick(menuItem)
        }

        if(selectedmenuItem?.id == menuItem.id){
            holder.item.isSelected = true
        }else{
            holder.item.isSelected = false
        }
    }

    fun setSelected(menuItem: MenuItem){
        selectedmenuItem = menuItem
    }



}