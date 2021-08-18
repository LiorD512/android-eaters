package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.annotation.SuppressLint
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.WsTimePickerCookingSlotItemBinding
import com.bupp.wood_spoon_eaters.databinding.WsTimePickerSingleItemBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils

class WSTimePickerStringAdapter :
    ListAdapter<Pair<String?, String?>, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = WsTimePickerCookingSlotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StringPairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as StringPairViewHolder
        holder.bindItem(item)
    }

    class StringPairViewHolder(view: WsTimePickerCookingSlotItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val cookingSlotName: TextView = view.wsTimePickerItemName
        private val cookingSlotDate: TextView = view.wsTimePickerItemDates

        @SuppressLint("SetTextI18n")
        fun bindItem(data: Pair<String?, String?>) {
            data.first?.let{
                cookingSlotName.text = it
            }
            data.second?.let{
                cookingSlotDate.text = it

                if(data.first == null){
                    cookingSlotName.visibility = View.GONE
                    cookingSlotDate.gravity = CENTER
                }
            }
        }
    }
//
//    class SingleItemViewHolder(view: WsTimePickerSingleItemBinding) : RecyclerView.ViewHolder(view.root) {
//        private val itemText: TextView = view.wsTimePickerItem
//
//        fun bindItem(data: WSBaseTimePicker) {
//            data.date?.let{
//                if(DateUtils.isToday(it)){
//                    itemText.text = "Today"
//                }else{
//                    itemText.text = DateUtils.parseDateToFullDayDate(it)
//                }
//            }
//
//            data.title?.let{
//                itemText.text = it
//            }
//        }
//    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<String?, String?>>() {

        override fun areItemsTheSame(oldItem: Pair<String?, String?>, newItem: Pair<String?, String?>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Pair<String?, String?>, newItem: Pair<String?, String?>): Boolean {
            return oldItem == newItem
        }
    }
}