package com.bupp.wood_spoon_eaters.dialogs.abs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.SelectableString
import kotlinx.android.synthetic.main.auto_complete_list_item.view.*

class AutoCompleteListAdapter(
    val context: Context,
    val originalArr: ArrayList<SelectableString>,
    val listener: AutoCompleteListAdapterListener
) : RecyclerView.Adapter<AutoCompleteListAdapter.ViewHolder>() {

    var presentedList: ArrayList<SelectableString> = arrayListOf()
    var lastSelectedItem: SelectableString? = null
    var query: String = ""

    interface AutoCompleteListAdapterListener {
        fun onItemClick(selected: SelectableString?)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.autoCompleteItemTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.auto_complete_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return presentedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curObj = presentedList.get(position)

        holder.title.text = curObj.name

        holder.title.setOnClickListener {
            listener.onItemClick(curObj)
        }

        holder.title.isSelected = curObj == lastSelectedItem
    }

    fun sortList(queryStr: String) {
        presentedList.clear()
        for (selectable in originalArr) {
            if (selectable.name.toLowerCase().contains(queryStr)) {
                presentedList.add(selectable)
            }
        }
        notifyDataSetChanged()
    }

    fun clean() {
        presentedList.clear()
        presentedList.addAll(originalArr)
        notifyDataSetChanged()
    }

}