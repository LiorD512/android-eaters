package com.bupp.wood_spoon_chef.presentation.features.main.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.OrdersListItemBinding
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.utils.DateUtils


class OrdersAdapter(
    val context: Context,
    private val cookingSlotList: MutableList<CookingSlot>?,
    val listener: CalendarCookingSlotAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CalendarCookingSlotAdapterListener {
        fun onCookingSlotMenuClick(cookingSlot: CookingSlot)
        fun onCookingSlotShareClick(cookingSlot: CookingSlot)
        fun onCookingSlotClicked(cookingSlot: CookingSlot)
        fun onCookingSlotNationwideClick()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curCookingSlot: CookingSlot? = cookingSlotList?.get(position)
        (holder as ItemViewHolder).date.text =
            DateUtils.parseDateToDayMonthAndYear(curCookingSlot?.startsAt!!)
        holder.hours.text = DateUtils.parseTwoSimpleDates(
            curCookingSlot.startsAt,
            curCookingSlot.endsAt
        )
        holder.cookingSlotList.init(curCookingSlot.menuItems)

        if (curCookingSlot.freeDelivery) {
            holder.freeDelivery.visibility = View.VISIBLE
        } else {
            holder.freeDelivery.visibility = View.GONE
        }

        holder.menuItem.setOnClickListener { listener.onCookingSlotMenuClick(curCookingSlot) }
        holder.shareBtn.setOnClickListener { listener.onCookingSlotShareClick(curCookingSlot) }
        holder.itemView.setOnClickListener { listener.onCookingSlotClicked(curCookingSlot) }

        if (curCookingSlot.isNationwide) {
            holder.nationwide.visibility = View.VISIBLE
            holder.nationwideRect.visibility = View.VISIBLE
            holder.nationwide.setOnClickListener {
                listener.onCookingSlotNationwideClick()
            }
        } else {
            holder.nationwide.visibility = View.GONE
            holder.nationwideRect.visibility = View.GONE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            OrdersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }


    override fun getItemCount(): Int {
        if (cookingSlotList == null)
            return 0
        return cookingSlotList.size
    }

    fun removeCookingSlot(hideCookingSlot: CookingSlot?) {
        val tempList: ArrayList<CookingSlot> = arrayListOf()
        for (cookingSlot in cookingSlotList!!) {
            if (cookingSlot.id != hideCookingSlot!!.id) {
                tempList.add(cookingSlot)
            }
        }
        cookingSlotList.clear()
        cookingSlotList.addAll(tempList)
        notifyDataSetChanged()
    }

    fun clear() {
        cookingSlotList?.clear()
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: OrdersListItemBinding) : RecyclerView.ViewHolder(view.root) {
    val menuItem = view.ordersItemMenu
    val date = view.ordersItemDate
    val hours = view.ordersItemHours
    val shareBtn = view.ordersItemShare
    val cookingSlotList = view.ordersItemMenuItemListView
    val freeDelivery = view.orderItemFreeDelivery
    val nationwide = view.ordersItemNationwideLayout
    val nationwideRect = view.ordersItemNationwideLayoutRect
}






