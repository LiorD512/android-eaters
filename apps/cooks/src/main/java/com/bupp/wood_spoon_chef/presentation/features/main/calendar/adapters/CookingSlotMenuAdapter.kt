package com.bupp.wood_spoon_chef.presentation.features.main.calendar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.databinding.ItemCalendarCookingSlotMenuBinding
import com.bupp.wood_spoon_chef.databinding.ItemCookingslotMenuFooterBinding

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.bupp.wood_spoon_chef.utils.Utils
import kotlin.collections.ArrayList

class CookingSlotMenuAdapter(
    val context: Context,
    private val cookingSlotList: MutableList<CookingSlot>?,
    val listener: CalendarCookingSlotAdapterListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CalendarCookingSlotAdapterListener{
        fun onCookingSlotMenuClick(curCookingSlot: CookingSlot)
        fun onCookingSlotAddClick()
        fun onWorldwideInfoClick()
        fun onCookingSlotShareClick(cookingSlot: CookingSlot)
    }

    private object ViewType {
        val ITEM = 1
        val FOOTER = 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.FOOTER -> {
                (holder as MenuFooterViewHolder).addCookingSlot.setOnClickListener { listener?.onCookingSlotAddClick() }
            }
            else -> {
                val curCookingSlot: CookingSlot? = cookingSlotList?.get(position)
                curCookingSlot?.let{
                    (holder as MenuItemViewHolder).menuItem.setOnClickListener {
                        listener?.onCookingSlotMenuClick(curCookingSlot)
                    }
                    holder.shareBtn.setOnClickListener { listener?.onCookingSlotShareClick(curCookingSlot) }

                    holder.cookingSlotList.init(curCookingSlot.menuItems)

                    curCookingSlot.isNationwide.let{
                        if(it){
                            holder.nationwide.visibility = View.VISIBLE
                            holder.nationwideRect.visibility = View.VISIBLE

                            val param = holder.cardLayout.layoutParams as ViewGroup.MarginLayoutParams
                            param.setMargins(Utils.toPx(9), Utils.toPx(9), Utils.toPx(9),0)
                            holder.cardLayout.layoutParams = param

                            holder.nationwide.setOnClickListener {
                                listener?.onWorldwideInfoClick()
                            }
                        }else{
                            holder.nationwide.visibility = View.GONE
                            holder.nationwideRect.visibility = View.GONE
                        }
                    }

                    if(curCookingSlot.event != null){
                        holder.eventLayout.visibility = View.VISIBLE
                        holder.eventTitle.text = curCookingSlot.event.title
                        Glide.with(context).load(curCookingSlot.event.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.eventIcon)
                        val date = DateUtils.parseDateToDayMonthDay(curCookingSlot.event.startsAt)
                        val hours = DateUtils.parseTwoDatesToHours(curCookingSlot.event.startsAt, curCookingSlot.event.endsAt)
                        val pickup = "(pickup at ${DateUtils.parseDateToHour(curCookingSlot.event.pickupAt)})"
                        holder.title.text = "$date \n$hours $pickup"
                    }else{
                        holder.eventLayout.visibility = View.GONE
                        val date = DateUtils.parseDateToDayMonthDay(curCookingSlot.startsAt)
                        val hours = DateUtils.parseTwoDatesToHours(curCookingSlot.startsAt, curCookingSlot.endsAt)
                        holder.title.text = "$date \n$hours"
                    }
                }
            }}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.FOOTER) {
            val binding = ItemCookingslotMenuFooterBinding.inflate(LayoutInflater.from(context), parent, false)
            MenuFooterViewHolder(binding)
        }
        else{
            val binding = ItemCalendarCookingSlotMenuBinding.inflate(LayoutInflater.from(context), parent, false)
            MenuItemViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        if(cookingSlotList == null)
            return 1
        return cookingSlotList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount-1){
            ViewType.FOOTER
        }else {
            ViewType.ITEM
        }
    }


    fun updateList(cookingSlots: List<CookingSlot>) {
        cookingSlotList?.clear()
        cookingSlotList?.addAll(cookingSlots)
        notifyDataSetChanged()
    }

    fun removeCookingSlot(hideCookingSlot: CookingSlot?) {
        val tempList: ArrayList<CookingSlot> = arrayListOf()
        for(cookingSlot in cookingSlotList!!){
            if(cookingSlot.id != hideCookingSlot!!.id){
                tempList.add(cookingSlot)
            }
        }
        cookingSlotList.clear()
        cookingSlotList.addAll(tempList)
        notifyDataSetChanged()
    }
}

class MenuItemViewHolder(view: ItemCalendarCookingSlotMenuBinding): RecyclerView.ViewHolder(view.root){
    val cardLayout = view.feedDishItemCardLayout
    val eventLayout = view.cookingSlotItemEventLayout
    val eventTitle = view.cookingSlotItemEventTitle
    val eventIcon = view.cookingSlotItemEventIcon
    val menuItem = view.cookSlotItemMenu
    val title = view.cookingSlotItemDate
    val shareBtn = view.cookingSlotItemShare
    val cookingSlotList = view.cookingSlotItemMenuItemListView
    val nationwide = view.feedDishItemWorldwideLayout
    val nationwideRect = view.feedDishItemWorldwideLayoutRect
}

class MenuFooterViewHolder(view: ItemCookingslotMenuFooterBinding) : RecyclerView.ViewHolder(view.root) {
    val addCookingSlot = view.calendarCookFooterAddBtn
}




