package com.bupp.wood_spoon_eaters.features.main.cook_profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Dish
import kotlinx.android.synthetic.main.cooks_profile_dish_item.view.*

class CooksDishesAdapter(private val listener: CooksProfileDishesListener?) :
    ListAdapter<Dish, RecyclerView.ViewHolder>(DiffCallback()) {

    interface CooksProfileDishesListener {
        fun onDishClick(dish: Dish) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cooks_profile_dish_item,
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
        private val mainLayout = view.cooksDishItemLayout
        private val title = view.cooksDishItemTitle
        private val description = view.cooksDishItemDescription
        private val price = view.cooksDishItemPrice
        private val quantityLeft = view.cooksDishItemQuantityLeft
        private val quantitySep = view.cooksDishItemQuantitySep
        private val dishImg = view.cooksDishItemImg

        @SuppressLint("SetTextI18n")
        fun bindItem(listener: CooksProfileDishesListener?, dish: Dish) {

            title.text = dish.name
            price.text = dish.getPriceObj().formatedValue
            description.text = dish.description.replace("\n", ". ")

            Glide.with(itemView.context).load(dish.thumbnail).centerCrop().into(dishImg)

            if (dish.menuItem != null) {
                val quantityCount = dish.menuItem.getQuantityCount()
                if (quantityCount > 0) {
                    quantityLeft.text = "${dish.menuItem.getQuantityLeftString()}"
//                quantitySep.visibility = View.VISIBLE
                } else {
                    quantityLeft.text = "Sold Out"
                }
                mainLayout.setOnClickListener { listener?.onDishClick(dish) }
            } else {
                quantityLeft.text = "Currently Not Available"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }
    }
}