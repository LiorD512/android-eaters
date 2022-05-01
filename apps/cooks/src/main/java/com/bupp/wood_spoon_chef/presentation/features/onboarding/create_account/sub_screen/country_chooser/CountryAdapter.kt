package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.databinding.ItemCountryAdapterBinding
import com.bupp.wood_spoon_chef.data.remote.model.Country

class CountryAdapter(private val listener:CountryAdapterListener) :
    ListAdapter<Country, RecyclerView.ViewHolder>(DiffCallback()) {

    interface CountryAdapterListener {
        fun onCountryClick(country: Country)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCountryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        with(holder.binding){
            itemCountryName.text = item.name
            Glide.with(root).load(item.flagUrl).into(itemCountryFlagIcon)
            root.setOnClickListener{
                listener.onCountryClick(item)
            }
        }
    }

    class ViewHolder(val binding: ItemCountryAdapterBinding) : RecyclerView.ViewHolder(binding.root)
    private class DiffCallback : DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.id == oldItem.id
        }
    }
}