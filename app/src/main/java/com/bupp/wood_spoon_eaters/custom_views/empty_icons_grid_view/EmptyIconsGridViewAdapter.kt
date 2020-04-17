package com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import kotlinx.android.synthetic.main.icons_grid_view_item.view.*

class EmptyIconsGridViewAdapter(val context: Context, val icons: ArrayList<SelectableIcon>, val listener: EmptyIconGridViewAdapterListener): RecyclerView.Adapter<EmptyIconsGridViewAdapter.ViewHolder>() {

    val DEFAULT_EMPTY_ITEMS_COUNT = 5
    var iconsList = icons

    private object ViewType {
        const val ITEM = 1
        const val EMPTY_ITEM = 2
    }

    interface EmptyIconGridViewAdapterListener{
        fun onItemSelected(itemPosition : Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val background = view.iconsGridBackground
        val iconView = view.iconsGridItem
        val name = view.iconsGridName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyIconsGridViewAdapter.ViewHolder {
        return EmptyIconsGridViewAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.icons_grid_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return DEFAULT_EMPTY_ITEMS_COUNT
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.ITEM -> {
                val icon : SelectableIcon? = iconsList[position]
                Glide.with(holder.itemView.context).load(icon!!.icon).into(holder.iconView)
                holder.name.text = icon.name
                holder.name.visibility = View.VISIBLE

                holder.iconView.setColorFilter(ContextCompat.getColor(context, R.color.teal_blue), android.graphics.PorterDuff.Mode.MULTIPLY)
                holder.name.isSelected = true
            }
            ViewType.EMPTY_ITEM -> {
                holder.iconView.setImageResource(R.drawable.cooking_cuisine_empty)
                holder.name.visibility = View.INVISIBLE
                holder.iconView.setColorFilter(0)
                holder.name.isSelected = false
            }
        }
        holder.background.setOnClickListener {
            listener.onItemSelected(holder.adapterPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position > iconsList.size-1){
            return ViewType.EMPTY_ITEM
        }else {
            return ViewType.ITEM
        }
    }


    public fun getItems() : ArrayList<SelectableIcon>? {
        val  list  = iconsList.filterNotNull() as ArrayList<SelectableIcon>
        return  if(list.size == 0) null else list
    }

    fun updateItems(selectedCuisines: ArrayList<SelectableIcon>) {
        iconsList.clear()
        iconsList.addAll(selectedCuisines)
        notifyDataSetChanged()
    }

}