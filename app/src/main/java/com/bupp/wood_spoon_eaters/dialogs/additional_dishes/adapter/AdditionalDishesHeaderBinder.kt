package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import android.widget.TextView
import com.bupp.wood_spoon_eaters.R
import mva2.adapter.ItemBinder


class AdditionalDishesHeaderBinder : ItemBinder<AdditionalDishHeader, AdditionalDishesHeaderBinder.AdditionalHeader>() {

    override fun createViewHolder(parent: ViewGroup): AdditionalHeader {
        return AdditionalHeader(inflate(parent, R.layout.additional_dish_item_header))
    }

    override fun canBindData(item: Any): Boolean {
        return item is AdditionalDishHeader
    }

    override fun bindViewHolder(holder: AdditionalHeader, item: AdditionalDishHeader) {
        holder.body.text = "People who order from ${item.cooksName} usually added the following dishes"
    }

    class AdditionalHeader(itemView: View) : ItemViewHolder<AdditionalDishHeader>(itemView) {
        var body: TextView

        init {
            body = itemView.findViewById(R.id.additionalHeaderBody)
        }
    }
}