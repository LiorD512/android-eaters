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
import kotlinx.android.synthetic.main.feed_dish_item.view.*


class SingleFeedAdapter(
    val context: Context,
    val dishes: ArrayList<Dish>,
    val listener: SearchAdapterListener,
    val deliveryFee: String,
    val isEvent: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){//}, FavoriteBtn.FavoriteBtnListener {


    private var lastClickedPosition: Int = 0

    interface SearchAdapterListener {
        fun onDishClick(dish: Dish) {}
//        fun onFavClick(dishId: Long, favSelected: Boolean){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dish: Dish = dishes[position]
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(context, 8, 0, RoundedCornersTransformation.CornerType.TOP))
        Glide.with(context).load(dish.thumbnail).apply(requestOptions).into((holder as DishItemViewHolder).bkgImg)
        (holder as DishItemViewHolder).cookImg.setImage(dish.cook.thumbnail)

        val name = dish.name
        val price = dish.getPriceObj().formatedValue
        holder.name.text = "$name $price"
        holder.cookName.text = "By ${dish.cook.getFullName()}"
        holder.rating.text = "${dish.rating}"

        if (dish.menuItem != null) {
            holder.unAvailableLayout.visibility = View.GONE
            holder.dishCount.initQuantityView(dish.menuItem)

            if(dish.menuItem.orderAt == null){
                //Dish is offered today.
                if(dish.doorToDoorTime != null){
                    holder.date.text = "ASAP, ${dish.doorToDoorTime}"
                }
            }else{
                if(isEvent){
                    holder.date.text = Utils.parseDDateToUsTime(dish.menuItem.cookingSlot.startsAt)
                }else{
                    if(Utils.isTodayOrTomorrow(dish.menuItem.orderAt)){
                        //Dish is offered today or tomorrow.
                        holder.date.text = Utils.parseDateToStartToEnd(dish.menuItem.cookingSlot.startsAt, dish.menuItem.cookingSlot.endsAt)
                    }else{
                        //Dish is offered later this week and beyond
                        holder.date.text = Utils.parseDateToFromStartingDate(dish.menuItem.orderAt)
                    }
                }
            }
            holder.mainLayout.setOnClickListener { listener?.onDishClick(dish) }

            val upcomingSlot = dish.menuItem.cookingSlot
            upcomingSlot?.let {
                if(it.freeDelivery){
                    holder.freeDelivery.text = "Free Delivery"
                }else{
                    holder.freeDelivery.text = "$deliveryFee Delivery"
                }
            }

            val quantityLeft = dish.menuItem.quantity - dish.menuItem.unitsSold
            if(quantityLeft <= 0){
                holder.soldOutLayout.visibility = View.VISIBLE
            }
        }else{
            holder.unAvailableLayout.visibility = View.VISIBLE
        }

        holder.favBtn.setIsFav(dish.isFavorite)
        holder.favBtn.setDishId(dish.id)

        lastClickedPosition = position


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.feed_dish_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

}


class DishItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mainLayout = view.feedDishItemLayout
    val soldOutLayout = view.feedDishItemSoldOutLayout
    val unAvailableLayout = view.feedDishUnAvailable
    val bkgImg = view.feedDishItemBkg
    val cookImg = view.feedDishItemUserImg
    val favBtn = view.feedDishItemFavorite
    val name = view.feedDishItemDishName
    val dishCount = view.feedDishQuantityView
    val cookName = view.feedDishItemCookName
    val rating = view.feedDishItemRating
    val date = view.feedDishItemDate
    val freeDelivery = view.feedDishItemFreeDelivery
}




