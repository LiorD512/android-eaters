package com.bupp.wood_spoon_chef.presentation.dialogs.custom_string_chooser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.CustomStringChooserItemBinding

class CustomStringChooserAdapter(private val listener: CustomStringChooserListener?) :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    private var selectedString: String? = null

    interface CustomStringChooserListener {
        fun onStringSelected(selected: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CustomStringChooserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder
        itemViewHolder.bindItem(item, selectedString)

        itemViewHolder.mainLayout.setOnClickListener {
            listener?.onStringSelected(item)
//            notifyItemChanged(position)
        }
    }

    fun setSelected(selectedString: String?) {
        this.selectedString = selectedString
    }

    class ViewHolder(view: CustomStringChooserItemBinding) : RecyclerView.ViewHolder(view.root) {
        val mainLayout: FrameLayout = view.customStringLayout
        private val countryText: TextView = view.customStringItem
        private val selectedView: ImageView = view.customStringSelected

        fun bindItem(currentItem: String, selectedString: String?) {
            countryText.text = currentItem
//
            if(currentItem == selectedString){
                selectedView.visibility = View.VISIBLE
            }else{
                selectedView.visibility = View.GONE
            }
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