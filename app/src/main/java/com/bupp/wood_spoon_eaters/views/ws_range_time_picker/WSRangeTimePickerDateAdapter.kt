package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.WsRangeTimePickerDateItemBinding
import com.bupp.wood_spoon_eaters.model.WSRangeTimePickerHours
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class WSRangeTimePickerDateAdapter :
    ListAdapter<WSRangeTimePickerHours, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = WsRangeTimePickerDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder

        itemViewHolder.bindItem(item.date)
        Log.d("wowWSRangeAdapter","onBind $position, $itemCount")
    }

    class ViewHolder(view: WsRangeTimePickerDateItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val itemText: TextView = view.wsRangeTimePickerItem

        fun bindItem(date: Date) {
            if(DateUtils.isToday(date)){
                itemText.text = "Today"
            }else{
                itemText.text = DateUtils.parseDateToUsDate(date)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WSRangeTimePickerHours>() {

        override fun areItemsTheSame(oldItem: WSRangeTimePickerHours, newItem: WSRangeTimePickerHours): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WSRangeTimePickerHours, newItem: WSRangeTimePickerHours): Boolean {
            return oldItem == newItem
        }
    }
}