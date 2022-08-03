package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

sealed class SectionedListItem(open val id: String) {
    data class SectionHeader(val section: Any?, override val id: String) : SectionedListItem(id)
    data class SectionItem(val item: Any?, override val id: String) : SectionedListItem(id)
}

enum class SectionViewType{
    HEADER,
    ITEM
}

abstract class SectionedListAdapter<S> :
    ListAdapter<SectionedListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    abstract fun submitSections(sections: List<S>)
    abstract fun sectionsToSectionedListItem(sections: List<S>): List<SectionedListItem>
    abstract fun createHeaderViewHolder(view: View): BaseViewHolder
    abstract fun createItemViewHolder(view: View): BaseViewHolder
    abstract fun getHeaderItemLayout(): Int
    abstract fun getItemLayout(): Int
    abstract fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    abstract fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int)


    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is SectionedListItem.SectionHeader -> SectionViewType.HEADER.ordinal
            is SectionedListItem.SectionItem -> SectionViewType.ITEM.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SectionViewType.HEADER.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(getHeaderItemLayout(), parent, false)
                createHeaderViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(getItemLayout(), parent, false)
                createItemViewHolder(view)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItem(position)){
            is SectionedListItem.SectionHeader -> onBindHeaderViewHolder(holder, position)
            is SectionedListItem.SectionItem -> onBindItemViewHolder(holder, position)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SectionedListItem>() {
        override fun areItemsTheSame(oldItem: SectionedListItem, newItem: SectionedListItem): Boolean {
            return oldItem.id == newItem.id
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SectionedListItem, newItem: SectionedListItem): Boolean {
            return oldItem == newItem
        }
    }

    abstract class BaseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: SectionedListItem)
    }
}