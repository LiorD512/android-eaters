package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.DateUtils
import kotlinx.android.synthetic.main.ws_range_time_picker_item.view.*
import java.util.*

class WSRangeTimePickerHoursAdapter :
    ListAdapter<Date, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ws_range_time_picker_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder

        itemViewHolder.bindItem(item, position, itemCount)
        Log.d("wowWSRangeAdapter","onBind $position, $itemCount")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemText: TextView = view.wsRangeTimePickerItem

        fun bindItem(date: Date, position: Int, itemCount: Int) {
            itemText.text = DateUtils.parseDateHalfHourInterval(date)
//            if(itemCount > 1){
//                if(position+1 == itemCount || position == 0){
//                    itemText.alpha = 0.3f
//                }else{
//                    itemText.alpha = 1f
//                }
//            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Date>() {

        override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
            return oldItem == newItem
        }
    }
}