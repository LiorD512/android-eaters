package com.bupp.wood_spoon_eaters.features.locations_and_address.select_address

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.select_address_item.view.*

class SelectAddressAdapter(private val listener: SelectAddressAdapterListener?) :
    ListAdapter<Address, RecyclerView.ViewHolder>(DiffCallback()) {

    var selectedAddress: Address? = null

    interface SelectAddressAdapterListener {
        fun onAddressClick(selected: Address)
        fun onMenuClick(selected: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.select_address_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as ViewHolder
        itemViewHolder.bindItem(listener, item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val layout: LinearLayout = view.selectAddressItemLayout
        private val streetText: TextView = view.selectAddressItemStreet
        private val stateText: TextView = view.selectAddressItemState
        private val dropOff: TextView = view.selectAddressItemDropOff
        private val menuBtn: ImageView = view.selectAddressItemMenu
        private val selected: ImageView = view.selectAddressItemCheck

        @SuppressLint("SetTextI18n")
        fun bindItem(listener: SelectAddressAdapterListener?, address: Address) {
            streetText.text = address.streetLine1
            stateText.text = "${address.city?.name ?: ""} ${address.state?.name ?: ""}"
            dropOff.text = address.getDropoffLocationStr()

            menuBtn.setOnClickListener { listener?.onMenuClick(address) }
            layout.setOnClickListener { listener?.onAddressClick(address) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Address>() {

        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
}