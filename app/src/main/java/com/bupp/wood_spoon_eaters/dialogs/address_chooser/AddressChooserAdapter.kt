package com.bupp.wood_spoon_eaters.dialogs.address_chooser

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.address_list_item.view.*

class AddressChooserAdapter(
    val context: Context,
    private val addresses: ArrayList<Address>?,
    val listener: AddressChooserAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedAddress: Address? = null

    interface AddressChooserAdapterListener {
        fun onAddressClick(selected: Address)
        fun onMenuClick(selected: Address)
        fun onAddAddressClick()
    }

    private object ViewType {
        const val ITEM = 1
        const val FOOTER = 2
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressText: TextView = view.addressItemText
        val addressMenu: ImageButton = view.addressItemMenu

        fun initAddress(address: Address, isSelected: Boolean) {
            addressText.text = address.streetLine1

            addressText.isSelected = isSelected

            if(isSelected){
                addressText.typeface = Typeface.DEFAULT_BOLD
            }else{
                addressText.typeface = Typeface.DEFAULT
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.ITEM -> {
                var address = addresses!![position]
                (holder as ItemViewHolder).initAddress(address, address == selectedAddress)

                holder.addressText.setOnClickListener {
                    listener.onAddressClick(address)
                }

                holder.addressMenu.setOnClickListener {
                    listener.onMenuClick(address)
                }

            }
            ViewType.FOOTER -> {
                holder?.itemView.setOnClickListener {
                    listener.onAddAddressClick()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.ITEM -> ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.address_list_item, parent, false)
            )
            else -> FooterViewHolder(
                LayoutInflater.from(context).inflate(R.layout.address_list_footer, parent, false)
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> ViewType.FOOTER
            else -> ViewType.ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (addresses != null)
            addresses!!.size + 1
        else 1
    }

    fun addAddress(newAddress: Address) {
        addresses?.add(newAddress)
        notifyDataSetChanged()
    }

    fun setSelected(selectedAddress: Address?) {
        this.selectedAddress = selectedAddress
        notifyDataSetChanged()
    }

}