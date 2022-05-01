package com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.presentation.custom_views.ExpandableMenuItem
import com.bupp.wood_spoon_chef.databinding.CalendarMenuListHeaderBinding
import com.bupp.wood_spoon_chef.databinding.CalendarMenuListItemBinding
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.CookingSlotHelper
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.MenuItemRequest
import com.bupp.wood_spoon_chef.utils.DateUtils
import java.util.*

class CookingSlotDetailsAdapter(
    val listener: CalendarAdapterListener
) : ListAdapter<MenuAdapterModel, RecyclerView.ViewHolder>(DiffCallback()),
    ExpandableMenuItem.ExpandableMenuItemListener {

    private val cookingSlotHelper = CookingSlotHelper()
    private var isEditMode: Boolean = false

    companion object {
        const val TAG = "wowMenuAdapter"
        const val START_TIME = 0
        const val FINISH_TIME = 1
        const val LAST_CALL = 2
    }

    interface CalendarAdapterListener {
        fun onAddDishClick()
        fun onTimeDialogClick(cookingSlotHelper: CookingSlotHelper)
        fun onDateTimeDialogClick(cookingSlotHelper: CookingSlotHelper)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MenuAdapterViewType.HEADER.ordinal -> {
                val binding = CalendarMenuListHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MenuHeaderViewHolder(binding)
            }
            else -> { //ViewType.ITEM
                val binding = CalendarMenuListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MenuItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (getItemViewType(position)) {
            MenuAdapterViewType.HEADER.ordinal -> {
                holder as MenuHeaderViewHolder
                holder.startTimeLayout.setOnClickListener {
                    cookingSlotHelper.lastClickedView = START_TIME
                    listener.onTimeDialogClick(cookingSlotHelper)
                }
                holder.finishTimeLayout.setOnClickListener {
                    cookingSlotHelper.lastClickedView = FINISH_TIME
                    listener.onTimeDialogClick(cookingSlotHelper)
                }
                holder.lastCallTLayout.setOnClickListener {
                    listener.onDateTimeDialogClick(cookingSlotHelper)
                    cookingSlotHelper.lastClickedView = LAST_CALL
                }
                holder.freeDelivery.setOnCheckedChangeListener { buttonView, isChecked ->
                    cookingSlotHelper.isFreeDelivery = isChecked
                }
                holder.worldwideSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    cookingSlotHelper.isWorldWide = isChecked
                }

                holder.startTimeView.text = DateUtils.parseDateToTime(cookingSlotHelper.startTime)
                holder.finishTimeView.text = DateUtils.parseDateToTime(cookingSlotHelper.finishTime)
                cookingSlotHelper.lastCallTime?.let {
                    holder.lastCallTimeView.text = DateUtils.parseDateToDayDateHour(it)
                }

                //editMode
                if (isEditMode) {
                    holder.freeDelivery.isChecked = cookingSlotHelper.isFreeDelivery
                    holder.worldwideSwitch.isChecked = cookingSlotHelper.isWorldWide
                }

                holder.addDishBtn.setOnClickListener { listener.onAddDishClick() }

            }
            else -> { //MenuAdapterViewType.ITEM.ordinal.
                val isExpanded = cookingSlotHelper.isExpanded(item.dishId)
                val cachedItemQuantity = cookingSlotHelper.getCachedItemQuantity(item.dishId)

                cachedItemQuantity?.let {
                    item.quantity = it
                }

                (holder as MenuItemViewHolder).menuItem.initDish(
                    item,
                    this,
                    isExpanded
                )
            }
        }

    }

    fun updateTime(date: Date) {
        cookingSlotHelper.updateTime(date)
        notifyItemChanged(0)
    }

    fun enableEditMode(cookingSlot: CookingSlot) {
        isEditMode = true
        cookingSlotHelper.startTime = cookingSlot.startsAt
        cookingSlotHelper.finishTime = cookingSlot.endsAt
        cookingSlotHelper.lastCallTime = cookingSlot.lastCallAt
        cookingSlotHelper.isWorldWide = cookingSlot.isNationwide
        cookingSlotHelper.isFreeDelivery = cookingSlot.freeDelivery

        cookingSlotHelper.parseMenuItemToRequestItems(cookingSlot.menuItems)

        notifyDataSetChanged()
    }

    fun clearData() {
        cookingSlotHelper.clearData()
    }

    private class DiffCallback : DiffUtil.ItemCallback<MenuAdapterModel>() {
        override fun areItemsTheSame(oldItem: MenuAdapterModel, newItem: MenuAdapterModel) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MenuAdapterModel, newItem: MenuAdapterModel) =
            oldItem == newItem
    }

    class MenuHeaderViewHolder(view: CalendarMenuListHeaderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val startTimeView: TextView = view.calendarHeaderStartTime
        val startTimeLayout: LinearLayout = view.calendarHeaderStartLayout
        val finishTimeView: TextView = view.calendarHeaderFinishTime
        val finishTimeLayout: LinearLayout = view.calendarHeaderFinishLayout
        val lastCallTimeView: TextView = view.calendarHeaderLastCallTime
        val lastCallTLayout: LinearLayout = view.calendarHeaderLastCallLayout
        val addDishBtn: TextView = view.calendarHeaderAddDish
        val freeDelivery: SwitchCompat = view.calendarHeaderDeliverySwitch
        val worldwideSwitch: SwitchCompat = view.calendarHeaderWorldwideSwitch
    }


    class MenuItemViewHolder(view: CalendarMenuListItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        val menuItem = view.calendarMenuItem
    }

    override fun onExpandableMenuItemChange(viewId: Long, isExpanded: Boolean) {
        if (isEditMode) {
            if (isExpanded) {
                cookingSlotHelper.unDestroyItem(viewId)
            } else {
                cookingSlotHelper.destroyItem(viewId)
            }
        } else {
            cookingSlotHelper.updateExpandedViews(viewId, isExpanded)
        }
    }

    override fun onExpandableMenuQuantityChange(id: Long, quantity: Int) {
        cookingSlotHelper.updateViewQuantity(id, quantity)
    }

    fun allDataValid(): Boolean {
        return cookingSlotHelper.isAllDataValid()
    }

    fun getStartTime(): Date? {
        return cookingSlotHelper.startTime
    }

    fun getFinishTime(): Date? {
        return cookingSlotHelper.finishTime
    }

    fun getLastCallTime(): Date? {
        return cookingSlotHelper.lastCallTime
    }

    fun getFreeDelivery(): Boolean {
        return cookingSlotHelper.isFreeDelivery
    }

    fun getWorldWide(): Boolean {
        return cookingSlotHelper.isWorldWide
    }

    fun getMenuItems(): List<MenuItemRequest> {
        return cookingSlotHelper.getAllValidItems()
    }


}

