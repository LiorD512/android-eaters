package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.WsTimePickerCookingSlotItemBinding
import com.bupp.wood_spoon_eaters.databinding.WsTimePickerSingleItemBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils

class WSTimePickerCustomAdapter :
    ListAdapter<WSBaseTimePicker, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            WSBaseTimePickerType.SINGLE_TIME_PICKER.ordinal -> {
                val binding = WsTimePickerSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SingleItemViewHolder(binding)
            }
            else -> {
                val binding = WsTimePickerCookingSlotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CookingSlotItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(item){
            is WSSingleTimePicker -> {
                val itemViewHolder = holder as SingleItemViewHolder
                itemViewHolder.bindItem(item)
            }
            is WSCookingSlotTimePicker -> {
                val itemViewHolder = holder as CookingSlotItemViewHolder
                itemViewHolder.bindItem(item)
            }
            is WSRangeTimePickerModel -> {

            }
        }
    }

    class CookingSlotItemViewHolder(view: WsTimePickerCookingSlotItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val cookingSlotName: TextView = view.wsTimePickerItemName
        private val cookingSlotDate: TextView = view.wsTimePickerItemDates

        @SuppressLint("SetTextI18n")
        fun bindItem(data: WSCookingSlotTimePicker) {
            if(DateUtils.isNowInRange(data.cookingSlot.orderFrom, data.cookingSlot.endsAt)){
                cookingSlotName.text = "Now"
            }else{
                cookingSlotName.text = data.title
            }
            cookingSlotDate.text = "${DateUtils.parseDateToUsTime(data.cookingSlot.orderFrom)} - ${DateUtils.parseDateToUsTime(data.cookingSlot.endsAt)}"
        }
    }

    class SingleItemViewHolder(view: WsTimePickerSingleItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val itemText: TextView = view.wsTimePickerItem

        fun bindItem(data: WSBaseTimePicker) {
            data.date?.let{
                if(DateUtils.isToday(it)){
                    itemText.text = "Today"
                }else{
                    itemText.text = DateUtils.parseDateToFullDayDate(it)
                }
            }

            data.title?.let{
                itemText.text = it
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WSBaseTimePicker>() {

        override fun areItemsTheSame(oldItem: WSBaseTimePicker, newItem: WSBaseTimePicker): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WSBaseTimePicker, newItem: WSBaseTimePicker): Boolean {
            return oldItem == newItem
        }
    }
}