package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.databinding.CookingSlotItemBinding
import com.bupp.wood_spoon_chef.databinding.CookingSlotListViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem

class CookingSlotsListView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    private val mainBinding: CookingSlotListViewBinding = CookingSlotListViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var items: MutableList<MenuItem> = mutableListOf()

    fun init(items: List<MenuItem>?) {
        items?.let{
            this.items.clear()
            this.items.addAll(it)
            populateList()
        }
    }

    private fun populateList() {
        mainBinding.cookingSlotLayout.removeAllViews()
            for (i in 0 until items.size) {
                val item: MenuItem = items[i]
                val binding: CookingSlotItemBinding = CookingSlotItemBinding.inflate(LayoutInflater.from(context), this, false)
                Glide.with(context).load(item.dish.imageGallery?.get(0)).apply(RequestOptions.circleCropTransform()).into(binding.cookingSlotImg)
                binding.cookingSlotTitle.text = item.dish.name
                val sold = item.unitsSold
                val total = item.quantity
                binding.cookingSlotOrders.text = "$sold/$total Orders"

                if (item.id == items[items.size - 1].id) {
                    binding.cookingSlotSep.visibility = View.GONE
                }
                mainBinding.cookingSlotLayout.addView(binding.root)
            }

    }

}