package com.bupp.wood_spoon_eaters.features.locations_and_address.address_list_chooser

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.address_list_item.view.*

class AddressChooserAdapter(private val listener: AddressChooserAdapterListener?) :
    ListAdapter<AddressChooserViewModel.AdapterAddress, RecyclerView.ViewHolder>(DiffCallback()) {

    var selectedAddress: Address? = null

    interface AddressChooserAdapterListener {
        fun onAddressClick(selected: Address)
        fun onMenuClick(selected: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.address_list_item,
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
        private val addressText: TextView = view.addressItemText
        private val addressMenu: ImageButton = view.addressItemMenu

        fun bindItem(listener: AddressChooserAdapterListener?, adapterAddress: AddressChooserViewModel.AdapterAddress) {

            addressText.text = adapterAddress.address.streetLine1
            addressText.isSelected = adapterAddress.isSelected

            if(adapterAddress.isSelected){
                addressText.typeface = Typeface.DEFAULT_BOLD
            }else{
                addressText.typeface = Typeface.DEFAULT
            }

            addressText.setOnClickListener {
                listener?.onAddressClick(adapterAddress.address)
            }

            addressMenu.setOnClickListener {
                listener?.onMenuClick(adapterAddress.address)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AddressChooserViewModel.AdapterAddress>() {

        override fun areItemsTheSame(oldItem: AddressChooserViewModel.AdapterAddress, newItem: AddressChooserViewModel.AdapterAddress): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AddressChooserViewModel.AdapterAddress, newItem: AddressChooserViewModel.AdapterAddress): Boolean {
            return oldItem == newItem
        }
    }
}