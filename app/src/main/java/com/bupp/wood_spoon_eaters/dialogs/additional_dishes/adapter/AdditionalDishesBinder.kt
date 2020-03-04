package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import kotlinx.android.synthetic.main.additional_dishes_dialog.*
import kotlinx.android.synthetic.main.additional_dishes_recycler_item.view.*
import mva2.adapter.ItemBinder


class AdditionalDishesBinder(val listener: AdditionalDishesAdapter.AdditionalDishesListener) : ItemBinder<AdditionalDishes, AdditionalDishesBinder.AdditionalDishesViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): AdditionalDishesViewHolder {
        return AdditionalDishesViewHolder(inflate(parent, R.layout.additional_dishes_recycler_item))
    }

    override fun canBindData(item: Any): Boolean {
        return item is AdditionalDishes
    }

    override fun bindViewHolder(holder: AdditionalDishesViewHolder, items: AdditionalDishes) {
        holder.bindItems(items)
    }

    inner class AdditionalDishesViewHolder(itemView: View) : ItemViewHolder<AdditionalDishes>(itemView) {
        fun bindItems(item: AdditionalDishes) {
            val adapter = AdditionalDishesAdapter(itemView.context, listener)
            itemView.additionalDishItemList.layoutManager = LinearLayoutManager(itemView.context)
            itemView.additionalDishItemList.adapter = adapter

            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
            itemView.additionalDishItemList.addItemDecoration(divider)

            adapter.submitList(item.dishes)
        }
    }

}