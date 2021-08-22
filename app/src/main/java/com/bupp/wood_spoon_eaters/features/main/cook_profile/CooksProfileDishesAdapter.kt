//package com.bupp.wood_spoon_eaters.features.main.cook_profile
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bupp.wood_spoon_eaters.databinding.CooksProfileDishItemBinding
//import com.bupp.wood_spoon_eaters.model.Dish
//
//
//class CooksProfileDishesAdapter(
//    val context: Context,
//    val dishes: MutableList<Dish>,
//    val listener: CooksProfileDishesListener
//) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {//}, FavoriteBtn.FavoriteBtnListener {
//
//
//    private var lastClickedPosition: Int = 0
//
//    interface CooksProfileDishesListener {
//        fun onDishClick(dish: Dish) {}
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//        val dish: Dish = dishes[position]
//        holder as DishItemViewHolder
//
//        val title = dish.name
//        val price = dish.getPriceObj()?.formatedValue
//        val description = dish.description.replace("\n", ". ")
//        holder.title.text = "$title"
//        holder.price.text = "$price"
//        holder.description.text = "${description}"
//
//        Glide.with(context).load(dish.thumbnail).centerCrop().into(holder.dishImg)
//
//        if (dish.menuItem != null) {
//            val quantityCount = dish.menuItem.getQuantityCount()
//            if (quantityCount > 0) {
//                holder.quantityLeft.text = "${dish.menuItem.getQuantityLeftString()}"
////                holder.quantitySep.visibility = View.VISIBLE
//            } else {
//                holder.quantityLeft.text = "Sold Out"
//            }
//            holder.mainLayout.setOnClickListener {
//                listener.onDishClick(dish)
//            }
//        } else {
//            holder.quantityLeft.text = "Currently Not Available"
//        }
//
//        lastClickedPosition = position
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding = CooksProfileDishItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DishItemViewHolder(binding)
////        return DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cooks_profile_dish_item, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return dishes?.size
//    }
//}
//
//class DishItemViewHolder(view: CooksProfileDishItemBinding) : RecyclerView.ViewHolder(view.root) {
//    val mainLayout = view.cooksDishItemLayout
//    val title = view.cooksDishItemTitle
//    val description = view.cooksDishItemDescription
//    val price = view.cooksDishItemPrice
//    val quantityLeft = view.cooksDishItemQuantityLeft
//    val quantitySep = view.cooksDishItemQuantitySep
//    val dishImg = view.cooksDishItemImg
//}
//
//
//
//
