package com.bupp.wood_spoon_eaters.features.main.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.custom_views.many_cooks_view.ManyCooksView
import com.bupp.wood_spoon_eaters.databinding.SearchCuisineItemBinding
import com.bupp.wood_spoon_eaters.databinding.SearchDishCooksItemBinding
import com.bupp.wood_spoon_eaters.databinding.SearchDishItemBinding
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.DateUtils


class SearchAdapter(val context: Context, val cuisineLabels: List<CuisineLabel>, val listener: SearchAdapterListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(),ManyCooksView.ManyCooksViewListener {

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
                holder.cookName.text = "By ${dish.cook.getFullName()}"
                holder.rating.text = "${dish.rating}"

                if(dish.menuItem != null){
                    holder.dishCount.initQuantityView(dish.menuItem)
                    val upcomingSlot = dish.menuItem.cookingSlot
                    if (dish.menuItem.orderAt != null) {
                        holder.date.text = DateUtils.parseDateToDayDateHour(dish.menuItem.orderAt)
                    }else if(dish.doorToDoorTime != null){
                        holder.date.text = "ASAP, ${dish.doorToDoorTime}"
                    }
                    upcomingSlot?.let {
                        if (it.freeDelivery) {
                            holder.freeDelivery.visibility = View.VISIBLE
                        } else {
                            holder.freeDelivery.visibility = View.GONE
                        }
                    }
                    holder.mainLayout.setOnClickListener { listener?.onDishClick(dish) }
                }else{
                    holder.unAvailableLayout.visibility = View.VISIBLE
                }

                holder.favBtn.setIsFav(dish.isFavorite)
                holder.favBtn.setOnClickListener { onFavClick(dish.id, holder.favBtn.isFavSelected) }
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
            val binding = SearchCuisineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CuisineItemViewHolder(binding)
        }else if (viewType == ViewType.COOKS_VIEW) {
            val binding = SearchDishCooksItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CooksViewItemViewHolder(binding)
        }else{
            val binding = SearchDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DishItemViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        if(hasData()){
            var count = 0
            if(hasCooks){
                count =+ 1
            }
            if(hasDishes){
                count += dishes?.size
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

    fun updateCooks(cooks: List<Cook>) {
        hasCooks = true;
        this.cooks.clear()
        this.cooks.addAll(cooks)
        notifyDataSetChanged()
    }

    fun updateDishes(dishes: List<Dish>) {
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

class CuisineItemViewHolder(view: SearchCuisineItemBinding): RecyclerView.ViewHolder(view.root){
    val cuisineBkg = view.searchCuisineItemBkg
    val cuisineName = view.searchCuisineItemName
}

class CooksViewItemViewHolder(view: SearchDishCooksItemBinding) : RecyclerView.ViewHolder(view.root) {
    val manyCooksView: ManyCooksView = view.searchDishCooksItem
}

class DishItemViewHolder(view: SearchDishItemBinding) : RecyclerView.ViewHolder(view.root) {
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
    val unAvailableLayout = view.searchDishUnAvailable

}




