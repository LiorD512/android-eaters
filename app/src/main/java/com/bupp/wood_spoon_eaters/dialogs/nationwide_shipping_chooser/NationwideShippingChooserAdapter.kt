package com.bupp.wood_spoon_eaters.dialogs.order_date_chooser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.nationwide_shipping_chooser_item.view.*
import kotlinx.android.synthetic.main.order_date_chooser_item.view.*

class NationwideShippingChooserAdapter(val context: Context, val listener: NationwideShippingAdapterListener) : ListAdapter<ShippingMethod, RecyclerView.ViewHolder>(NationwideShippingChooserDiffCallback()) {


    private var selectedmenuItem: ShippingMethod? = null

    interface NationwideShippingAdapterListener {
        fun onShippingMethodClick(selected: ShippingMethod)
    }

    class NationwideShippingChooserDiffCallback: DiffUtil.ItemCallback<ShippingMethod>(){
        override fun areItemsTheSame(oldItem: ShippingMethod, newItem: ShippingMethod): Boolean {
            return oldItem.code.equals(newItem.code)
        }

        override fun areContentsTheSame(oldItem: ShippingMethod, newItem: ShippingMethod): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bkg = view.nationwideShippingBkg
        var cb = view.nationwideShippingCb
        var title = view.nationwideShippingTitle
        var eta = view.nationwideShippingEta
        var price = view.nationwideShippingPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.nationwide_shipping_chooser_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val shippingMethod = getItem(position)

        holder as ViewHolder
        holder.title.text = shippingMethod.name
        holder.eta.text = "${shippingMethod.days_in_transit} business days (deliver by ${shippingMethod.deliver_by})"
        holder.price.text = shippingMethod.fee.formatedValue
        holder.bkg.setOnClickListener {
            selectedmenuItem = shippingMethod
            listener.onShippingMethodClick(shippingMethod)
        }

        holder.cb.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    selectedmenuItem = shippingMethod
                    listener.onShippingMethodClick(shippingMethod)
                }
            }

        })


        if(selectedmenuItem?.code.equals(shippingMethod.code)){
            holder.cb.isSelected = true
        }else{
            holder.cb.isSelected = false
        }
    }

    fun setSelected(shippingMethod: ShippingMethod){
        selectedmenuItem = shippingMethod
    }



}