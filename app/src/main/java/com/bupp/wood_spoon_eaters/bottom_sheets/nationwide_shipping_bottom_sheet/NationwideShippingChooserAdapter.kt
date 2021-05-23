package com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.NationwideShippingChooserItemBinding
import com.bupp.wood_spoon_eaters.model.ShippingMethod

class NationwideShippingChooserAdapter(val context: Context, val listener: NationwideShippingAdapterListener) : ListAdapter<ShippingMethod, RecyclerView.ViewHolder>(
    NationwideShippingChooserDiffCallback()
) {


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

    class ViewHolder(view: NationwideShippingChooserItemBinding) : RecyclerView.ViewHolder(view.root) {
        var bkg = view.nationwideShippingBkg
        var cb = view.nationwideShippingCb
        var title = view.nationwideShippingTitle
        var eta = view.nationwideShippingEta
        var price = view.nationwideShippingPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NationwideShippingChooserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val shippingMethod = getItem(position)

        holder as ViewHolder
        holder.title.text = shippingMethod.name
        if(shippingMethod.description != null){
            holder.eta.text = "${shippingMethod.description}"
            holder.eta.visibility = View.VISIBLE
        }else{
            holder.eta.visibility = View.GONE
        }
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