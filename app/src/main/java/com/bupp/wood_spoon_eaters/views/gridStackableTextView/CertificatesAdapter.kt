package com.bupp.wood_spoon_eaters.views.gridStackableTextView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.GridStackableTextViewItemBinding

class CertificatesAdapter :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = GridStackableTextViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
//        return ViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.grid_stackable_text_view_item,
//                parent,
//                false
//            )
//        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder
        itemViewHolder.bindItem(item)
    }

    class ViewHolder(binding: GridStackableTextViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val certificateView: TextView = binding.gridStackableTextItem

        fun bindItem(certificate: String) {
            certificateView.text = certificate
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}