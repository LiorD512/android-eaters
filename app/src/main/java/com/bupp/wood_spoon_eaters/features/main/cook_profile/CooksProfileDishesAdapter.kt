package com.bupp.wood_spoon_eaters.features.main.cook_profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.RoundedCornersTransformation
import com.bupp.wood_spoon_eaters.dialogs.WorldwideShippmentDialog
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.cooks_profile_dish_item.view.*
import kotlinx.android.synthetic.main.feed_dish_item.view.*


class CooksProfileDishesAdapter(
    val context: Context,
    val dishes: ArrayList<Dish>,
    val listener: CooksProfileDishesListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {//}, FavoriteBtn.FavoriteBtnListener {


    private var lastClickedPosition: Int = 0

    interface CooksProfileDishesListener {
        fun onDishClick(dish: Dish) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dish: Dish = dishes[position]
        holder as DishItemViewHolder

        val title = dish.name
        val price = dish.getPriceObj().formatedValue
        val description = dish.description.replace("\n", ". ")
        holder.title.text = "$title"
        holder.price.text = "$price"
        holder.description.text = "${description}"

        Glide.with(context).load(dish.thumbnail).centerCrop().into(holder.dishImg)

        if (dish.menuItem != null) {
            val quantityCount = dish.menuItem.getQuantityCount()
            if (quantityCount > 0) {
                holder.quantityLeft.text = "${dish.menuItem.getQuantityLeft()}"
//                holder.quantitySep.visibility = View.VISIBLE
            } else {
                holder.quantityLeft.text = "Sold Out"
            }
            holder.mainLayout.setOnClickListener { listener.onDishClick(dish) }
        } else {
            holder.quantityLeft.text = "Currently Not Available"
        }

        lastClickedPosition = position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cooks_profile_dish_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dishes.size
    }
}

class DishItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mainLayout = view.cooksDishItemLayout
    val title = view.cooksDishItemTitle
    val description = view.cooksDishItemDescription
    val price = view.cooksDishItemPrice
    val quantityLeft = view.cooksDishItemQuantityLeft
    val quantitySep = view.cooksDishItemQuantitySep
    val dishImg = view.cooksDishItemImg
}




