package com.bupp.wood_spoon_eaters.custom_views.feed_view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.custom_views.AutofitTextView
import com.bupp.wood_spoon_eaters.custom_views.fav_btn.FavoriteBtn
import com.bupp.wood_spoon_eaters.databinding.FeedDishItemBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils


class SingleFeedAdapter(
    val context: Context,
    val dishes: List<Dish>,
    val listener: SearchAdapterListener,
//    val deliveryFee: String,
    val isEvent: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){//}, FavoriteBtn.FavoriteBtnListener {


    private var lastClickedPosition: Int = 0

    interface SearchAdapterListener {
        fun onDishClick(dish: Dish) {}
        fun onWorldwideInfoClick() {}
//        fun onFavClick(dishId: Long, favSelected: Boolean){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dish: Dish = dishes[position]
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(context, 8, 0, RoundedCornersTransformation.CornerType.TOP))
        Glide.with(context).load(dish.thumbnail).apply(requestOptions).into((holder as DishItemViewHolder).bkgImg)
//        (holder as DishItemViewHolder).cookImg.setImage(dish.cook.thumbnail)

        val name = dish.name
        val price = dish.getPriceObj()?.formatedValue
        holder.name.text = "$name"
        holder.price.text = "$price"
        holder.cookName.text = "By ${dish.cook?.getFullName()}"
        holder.rating.text = "${dish.rating}"

        Glide.with(context).load(dish.cook?.thumbnail).circleCrop().into(holder.cookImg)
        Glide.with(context).load(dish.cook?.country?.flagUrl).into(holder.cookFlag)

        Log.d("wowSingleFeed","isNationwide: ${dish.menuItem?.cookingSlot?.isNationwide}")
        dish.menuItem?.cookingSlot?.isNationwide?.let{
            if(it){
                holder.worldwideLayout.visibility = View.VISIBLE
                holder.worldwideLayoutRect.visibility = View.VISIBLE
                val param = holder.cardLayout.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(Utils.toPx(9), Utils.toPx(9), Utils.toPx(9),0)
                holder.cardLayout.layoutParams = param

                holder.worldwideLayout.setOnClickListener {
                    listener.onWorldwideInfoClick()
                }

//                holder.bottomLayout.alpha = 0.3f
                holder.nationwideDate.visibility = View.VISIBLE
                holder.date.visibility = View.GONE
                holder.freeDelivery.visibility = View.GONE
            }else{
                holder.worldwideLayout.visibility = View.GONE
                holder.worldwideLayoutRect.visibility = View.GONE

                val param = holder.cardLayout.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(Utils.toPx(9), 0,Utils.toPx(9),0)
                holder.cardLayout.layoutParams = param

                holder.bottomLayout.alpha = 1f
                holder.date.visibility = View.VISIBLE
                holder.nationwideDate.visibility = View.GONE
                holder.freeDelivery.visibility = View.VISIBLE
            }
        }


        if (dish.menuItem != null) {
            holder.unAvailableLayout.visibility = View.GONE
            holder.dishCount.text = dish.menuItem.getQuantityLeftString()

            if(dish.menuItem.orderAt == null){
                //Dish is offered today.
                if(dish.doorToDoorTime != null){
                    holder.date.text = "ASAP, ${dish.doorToDoorTime}"
                }
            }else{
//                if(isEvent){
////                    holder.date.text = Utils.parseDDateToUsTime(dish.menuItem.cookingSlot.startsAt)
//                    holder.date.text = DateUtils.parseDateToUsTime(dish.menuItem.cookingSlot.orderFrom)
//                }else{
                    if(DateUtils.isTodayOrTomorrow(dish.menuItem.orderAt)){
                        //Dish is offered today or tomorrow.
//                        holder.date.text = Utils.parseDateToStartToEnd(dish.menuItem.cookingSlot.startsAt, dish.menuItem.cookingSlot.endsAt)
                        dish.menuItem.cookingSlot?.let{
                            holder.date.text = DateUtils.parseDateToStartToEnd(it.orderFrom, it.endsAt)
                        }
                    }else{
                        //Dish is offered later this week and beyond
                        dish.menuItem.cookingSlot?.let{
                            holder.date.text = DateUtils.parseDateToFromStartingDate(it.orderFrom)
                        }
                    }
//                }
            }
            holder.mainLayout.setOnClickListener {
                listener.onDishClick(dish)
            }

            val upcomingSlot = dish.menuItem.cookingSlot
            val deliveryFee = upcomingSlot?.deliveryFee?.cents
            upcomingSlot?.let {
                if(it.freeDelivery){
                    holder.freeDelivery.text = "Free Delivery"
                }else{
                    if(deliveryFee != null && deliveryFee.toInt() == 0){
                        holder.freeDelivery.text = "Free Delivery"
                    }else{
                        holder.freeDelivery.text = "${upcomingSlot.deliveryFee?.formatedValue}"
                    }
                }
            }

            val quantityLeft = dish.menuItem.quantity - dish.menuItem.unitsSold
            if(quantityLeft <= 0){
                holder.soldOutLayout.visibility = View.VISIBLE
            }else{
                holder.soldOutLayout.visibility = View.GONE
            }
        }else{
            holder.unAvailableLayout.visibility = View.VISIBLE
        }

        holder.favBtn.setIsFav(dish.isFavorite)
        holder.favBtn.setDishId(dish.id)

        lastClickedPosition = position


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = FeedDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

}


class DishItemViewHolder(view: FeedDishItemBinding) : RecyclerView.ViewHolder(view.root) {
    val mainLayout: RelativeLayout = view.feedDishItemLayout
    val bottomLayout: RelativeLayout = view.feedDishItemBottomLayout
    val nationwideDate: AutofitTextView = view.feedDishItemNationwideDate
    val cardLayout: CardView = view.feedDishItemCardLayout
    val worldwideLayout: LinearLayout = view.feedDishItemWorldwideLayout
    val worldwideLayoutRect: View = view.feedDishItemWorldwideLayoutRect
    val soldOutLayout: FrameLayout = view.feedDishItemSoldOutLayout
    val unAvailableLayout: TextView = view.feedDishUnAvailable
    val bkgImg: ImageView = view.feedDishItemBkg
    val cookImg: ImageView = view.feedDishItemUserImg
    val cookFlag: ImageView = view.feedDishCookFlag
    val favBtn: FavoriteBtn = view.feedDishItemFavorite
    val name: TextView = view.feedDishItemDishName
    val price: TextView = view.feedDishItemPrice
    val dishCount: TextView = view.feedDishQuantityLeft
    val cookName: TextView = view.feedDishItemCookName
    val rating: TextView = view.feedDishItemRating
    val date: TextView = view.feedDishItemDate
    val freeDelivery: TextView = view.feedDishItemFreeDelivery
}




