package com.bupp.wood_spoon_eaters.custom_views.stackableTextView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.stackable_text_view_item.view.*

class StackableTextViewAdapter(val context: Context) : RecyclerView.Adapter<StackableTextViewAdapter.ViewHolder>() {

    var bundles: ArrayList<StackableTextView.StackablesBundle> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stackableViewItem: StackableViewItem = view.stackableTextViewItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.stackable_text_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return bundles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bundle: StackableTextView.StackablesBundle = bundles[position]

        holder.stackableViewItem.init(bundle.stackables)
    }

    fun addItem(iconsList: ArrayList<StackableTextView.StackablesBundle>) {
        bundles.addAll(iconsList)
       notifyDataSetChanged()

    }

    fun clear() {
        bundles.clear()
        notifyDataSetChanged()
    }
}