package com.bupp.wood_spoon_eaters.features.main.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.many_cooks_view.ManyCooksView
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.search_cuisine_item.view.*
import kotlinx.android.synthetic.main.search_dish_cooks_item.view.*
import kotlinx.android.synthetic.main.search_dish_item.view.*
import kotlin.collections.ArrayList


class SearchAdapter(val context: Context, val cuisineLabels: ArrayList<CuisineLabel>, val listener: SearchAdapterListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(),ManyCooksView.ManyCooksViewListener {

    private var hasCooks: Boolean = false
    private var hasDishes: Boolean = false
    var cooks: ArrayList<Cook> = arrayListOf()
    var dishes: ArrayList<Dish> = arrayListOf()

    interface SearchAdapterListener{
        fun onDishClick(dish: Dish){}
        fun onCookClick(cook: Cook){}
        fun onCuisineClick(cuisine: CuisineLabel){}
        fun onFavClick(dishId: Long, favSelected: Boolean) {}
    }

    private object ViewType {
        val CUISINE = 0
        val COOKS_VIEW = 1
        val DISH = 2
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.CUISINE -> {
                val currentCuisine = cuisineLabels[position]
                Glide.with(context).load(currentCuisine.cover).into((holder as CuisineItemViewHolder).cuisineBkg)
                holder.cuisineName.text = currentCuisine.name
                holder.cuisineBkg.setOnClickListener { listener?.onCuisineClick(currentCuisine) }
            }
            ViewType.COOKS_VIEW -> {
                (holder as CooksViewItemViewHolder).manyCooksView.initCooksView(cooks!!, this)
            }
            else -> {
                var curPosition = position
                if(hasCooks){
                    curPosition--
                }
                val dish: Dish = dishes[curPosition]
                Glide.with(context).load(dish.thumbnail).into((holder as DishItemViewHolder).bkgImg)
                (holder as DishItemViewHolder).cookImg.setImage(dish.cook.thumbnail)
                holder.name.text = dish.name
                holder.cookName.text = "by ${dish.cook.getFullName()}"
                holder.rating.text = "${dish.rating}"

                if(dish.menuItem != null){
                    holder.dishCount.setText("${dish.menuItem?.unitsSold}/${dish.menuItem?.quantity}")
                    val upcomingSlot = dish.menuItem.cookingSlot
                    if(upcomingSlot != null){
                        holder.date.text = Utils.parseDateToDayDateHour(upcomingSlot?.startsAt)
                    }
                }

                holder.favBtn.setIsFav(dish.isFavorite)
                holder.favBtn.setOnClickListener { onFavClick(dish.id, holder.favBtn.isFavSelected) }
                holder.mainLayout.setOnClickListener { listener?.onDishClick(dish) }
            }}

    }

    private fun onFavClick(dishId: Long, favSelected: Boolean) {
        listener?.onFavClick(dishId, favSelected)
    }

    override fun onCookClicked(clicked: Cook) {
        listener?.onCookClick(clicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ViewType.CUISINE) {
            return CuisineItemViewHolder(LayoutInflater.from(context).inflate(R.layout.search_cuisine_item, parent, false))
        }else if (viewType == ViewType.COOKS_VIEW) {
            return CooksViewItemViewHolder(LayoutInflater.from(context).inflate(R.layout.search_dish_cooks_item, parent, false))
        }else{
            return DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.search_dish_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        if(hasData()){
            var count = 0
            if(hasCooks){
                count =+ 1
            }
            if(hasDishes){
                count += dishes!!.size
            }
            return count
        }else{
            return cuisineLabels.size
        }
    }

    private fun hasData(): Boolean {
        return hasCooks || hasDishes
    }

    override fun getItemViewType(position: Int): Int {
        if(hasData()){
            if(hasCooks && position == 0){
                return ViewType.COOKS_VIEW
            }
            return ViewType.DISH
        }else{
            return ViewType.CUISINE
        }
    }

    fun updateCooks(cooks: ArrayList<Cook>) {
        hasCooks = true;
        this.cooks.clear()
        this.cooks.addAll(cooks)
        notifyDataSetChanged()
    }

    fun updateDishes(dishes: ArrayList<Dish>) {
        hasDishes = true;
        this.dishes.clear()
        this.dishes.addAll(dishes)
        notifyDataSetChanged()
    }

    fun clearData(){
        this.cooks.clear()
        this.dishes.clear()
        hasCooks = false
        hasDishes = false
        notifyDataSetChanged()
    }

}

class CuisineItemViewHolder(view: View): RecyclerView.ViewHolder(view){
    val cuisineBkg = view.searchCuisineItemBkg
    val cuisineName = view.searchCuisineItemName
}

class CooksViewItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val manyCooksView: ManyCooksView = view.searchDishCooksItem
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




