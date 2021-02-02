package com.bupp.wood_spoon_eaters.features.bottom_sheets.country_code_chooser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.State
import kotlinx.android.synthetic.main.country_chooser_item.view.*

class CountryIsoChooserAdapter(private val listener: AddressChooserAdapterListener?) :
    ListAdapter<State, RecyclerView.ViewHolder>(DiffCallback()) {

    var selectedAddress: Address? = null

    interface AddressChooserAdapterListener {
        fun onAddressClick(selected: Address)
        fun onMenuClick(selected: Address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.country_chooser_item,
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
        private val countryText: TextView = view.countryChooserItem
        private val selectedView: ImageView = view.countryChooserSelected

        fun bindItem(listener: AddressChooserAdapterListener?, country: State) {

//            addressText.text = adapterAddress.address.streetLine1
//            addressText.isSelected = adapterAddress.isSelected
//
//            if(adapterAddress.isSelected){
//                addressText.typeface = Typeface.DEFAULT_BOLD
//            }else{
//                addressText.typeface = Typeface.DEFAULT
//            }
//
//            addressText.setOnClickListener {
//                listener?.onAddressClick(adapterAddress.address)
//            }
//
//            addressMenu.setOnClickListener {
//                listener?.onMenuClick(adapterAddress.address)
//            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<State>() {

        override fun areItemsTheSame(oldItem: State, newItem: State): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: State, newItem: State): Boolean {
            return oldItem == newItem
        }
    }
}