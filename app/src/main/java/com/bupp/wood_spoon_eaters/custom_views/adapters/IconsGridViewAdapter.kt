package com.bupp.wood_spoon_eaters.custom_views.adapters

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

class IconsGridViewAdapter(val context: Context, val icons: ArrayList<SelectableIcon>, val listener: IconGridViewAdapterListener, val choiceCount: Int) : RecyclerView.Adapter<IconsGridViewAdapter.ViewHolder>() {

    var selectedList: ArrayList<SelectableIcon> = arrayListOf()
//    var lastSelectedItem: String = ""
//    var query: String = ""

    interface IconGridViewAdapterListener {
        fun onItemClick(selected: SelectableIcon)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val background = view.iconsGridBackground
        val iconView = view.iconsGridItem
        val name = view.iconsGridName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        presentedList.addAll(orders)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.icons_grid_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon: SelectableIcon? = icons.get(position)

        Glide.with(context).load(icon!!.icon).into(holder.iconView)
        holder.name.text = icon.name

        holder?.background.setOnClickListener {
            if (selectedList.contains(icon)) {
                selectedList.remove(icon)
            } else if (selectedList.size < choiceCount) {
                selectedList.add(icon)

            }
            notifyItemChanged(position)
            listener?.onItemClick(icon)
        }

        if (selectedList.contains(icon)) {
            holder.iconView.setColorFilter(ContextCompat.getColor(context, R.color.teal_blue), android.graphics.PorterDuff.Mode.MULTIPLY)
            holder.name.isSelected = true
        } else {
            holder.iconView.setColorFilter(0)
            holder.name.isSelected = false
        }

    }

    fun getSelectedIcons(): ArrayList<SelectableIcon> {
        return selectedList
    }

    fun setSelectedIcons(selectedIcons : ArrayList<SelectableIcon>) {
        selectedList = selectedIcons
        notifyDataSetChanged()
    }

    fun loadSelectedIcons(icons: ArrayList<SelectableIcon>) {
        selectedList = icons
        notifyDataSetChanged()
    }

}