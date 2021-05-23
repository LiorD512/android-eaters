package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.StackableTextViewItemBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class StackableTextViewAdapter(val context: Context) :
    ListAdapter<SelectableIcon, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = StackableTextViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder

        itemViewHolder.bindItem(item.name, position+1 == itemCount)
        Log.d("wowWSRangeAdapter","onBind $position, $itemCount")
    }

    class ViewHolder(view: StackableTextViewItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val stackableViewItem: TextView = view.stackableViewItem
        private val divider: View = view.stackableViewItemDivider

        fun bindItem(string: String, isLast: Boolean) {
            stackableViewItem.text = string
            if(isLast){
                divider.visibility = View.GONE
            }else{
                divider.visibility = View.VISIBLE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SelectableIcon>() {

        override fun areItemsTheSame(oldItem: SelectableIcon, newItem: SelectableIcon): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SelectableIcon, newItem: SelectableIcon): Boolean {
            return oldItem.id == newItem.id
        }
    }
}