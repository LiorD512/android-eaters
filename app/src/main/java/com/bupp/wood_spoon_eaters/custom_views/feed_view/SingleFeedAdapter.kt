package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.search_dish_item.view.*


class SingleFeedAdapter(val context: Context, val dishes: ArrayList<Dish>, val listener: SearchAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface SearchAdapterListener {
        fun onDishClick(dish: Dish) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dish: Dish = dishes[position]
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(context, 8, 0, RoundedCornersTransformation.CornerType.TOP))
        Glide.with(context).load(dish.thumbnail).apply(requestOptions).into((holder as DishItemViewHolder).bkgImg)
        (holder as DishItemViewHolder).cookImg.setImage(dish.cook.thumbnail)
        holder.name.text = dish.name
        holder.cookName.text = "by ${dish.cook.getFullName()}"
        holder.rating.text = "${dish.rating}"

        if (dish.menuItem != null) {
            holder.dishCount.setText("${dish.menuItem?.unitsSold}/${dish.menuItem?.quantity}")
            val upcomingSlot = dish.menuItem.cookingSlot
            if (upcomingSlot != null) {
                holder.date.text = Utils.parseDateToDayDateHour(upcomingSlot?.startsAt)
            }
        }

        holder.favBtn.setOnClickListener { onFavClick(dish) }
        holder.mainLayout.setOnClickListener { listener?.onDishClick(dish) }

    }

    private fun onFavClick(dish: Dish) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.search_dish_item, parent, false)
//        val width = recyclerView.getWidth()
//        val params = view.getLayoutParams()
//        params.width = (width * 0.8).toInt()
//        view.setLayoutParams(params)
        return DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.search_dish_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dishes!!.size
    }

}


class DishItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mainLayout = view.searchDishItemLayout
    val bkgImg = view.searchDishItemBkg
    val cookImg = view.searchDishItemUserImg
    val favBtn = view.searchDishItemFavorite
    val name = view.searchDishItemDishName
    val dishCount = view.searchDishItemDishCount
    val cookName = view.searchDishItemCookName
    val rating = view.searchDishItemRating
    val date = view.searchDishItemDate
    val freeDelivery = view.searchDishItemFreeDelivery
}




