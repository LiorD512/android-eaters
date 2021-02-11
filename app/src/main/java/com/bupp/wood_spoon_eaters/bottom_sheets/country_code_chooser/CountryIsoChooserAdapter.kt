package com.bupp.wood_spoon_eaters.bottom_sheets.country_code_chooser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.CountriesISO
import kotlinx.android.synthetic.main.country_chooser_item.view.*

class CountryIsoChooserAdapter(private val listener: AddressChooserAdapterListener?) :
    ListAdapter<CountriesISO, RecyclerView.ViewHolder>(DiffCallback()) {

    var selectedCountry: CountriesISO? = null

    interface AddressChooserAdapterListener {
        fun onCountryCodeSelected(selected: CountriesISO)
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
        itemViewHolder.bindItem(listener, item, selectedCountry)
    }

    fun setSelected(selectedCountry: CountriesISO?) {
        this.selectedCountry = selectedCountry
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mainLayout: FrameLayout = view.countryChooserLayout
        private val countryText: TextView = view.countryChooserItem
        private val selectedView: ImageView = view.countryChooserSelected

        fun bindItem(listener: AddressChooserAdapterListener?, countryData: CountriesISO, selectedCountry: CountriesISO?) {
            countryText.text = "${countryData.flag} ${countryData.name} (+${countryData.country_code})"
            mainLayout.setOnClickListener {
                listener?.onCountryCodeSelected(countryData)
            }

            if(countryData.country_code == selectedCountry?.country_code){
                selectedView.visibility = View.VISIBLE
            }else{
                selectedView.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CountriesISO>() {

        override fun areItemsTheSame(oldItem: CountriesISO, newItem: CountriesISO): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CountriesISO, newItem: CountriesISO): Boolean {
            return oldItem == newItem
        }
    }
}