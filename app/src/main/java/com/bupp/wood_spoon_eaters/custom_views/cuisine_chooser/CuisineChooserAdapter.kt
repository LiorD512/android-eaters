package com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.CuisineChooserItemBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class CuisineChooserAdapter(val context: Context) :
    ListAdapter<SelectableIcon, RecyclerView.ViewHolder>(DiffCallback()) {

    var selectedCuisines = mutableListOf<SelectableIcon>()
        fun setSelected(cuisines: MutableList<SelectableIcon>){
        this.selectedCuisines = cuisines
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CuisineChooserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder

        if(selectedCuisines.contains(item)){
            itemViewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.pale_grey))
            itemViewHolder.vCheck.visibility = View.VISIBLE
        }else{
            itemViewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            itemViewHolder.vCheck.visibility = View.INVISIBLE
        }

        itemViewHolder.layout.setOnClickListener {
            if(selectedCuisines.contains(item)){
                selectedCuisines.remove(item)
            }else{
                selectedCuisines.add(item)
            }
            notifyItemChanged(position)
        }

        itemViewHolder.title.text = item.name
        Log.d("wowWSRangeAdapter","onBind $position, $itemCount")
    }

    class ViewHolder(view: CuisineChooserItemBinding) : RecyclerView.ViewHolder(view.root) {
        val layout: FrameLayout = view.cuisineItemLayout
        val title: TextView = view.cuisineItemTitle
        val vCheck: ImageView = view.cuisineItemV
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