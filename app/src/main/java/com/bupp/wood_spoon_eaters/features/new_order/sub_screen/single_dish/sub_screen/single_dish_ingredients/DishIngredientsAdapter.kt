package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.DishIngredient
import kotlinx.android.synthetic.main.dish_ingredient_item.view.*

class DishIngredientsAdapter(private val listener: DishIngredientsAdapterListener?) :
    ListAdapter<DishIngredient, RecyclerView.ViewHolder>(DiffCallback()) {

    private val ingredientsRemoved: MutableList<Long> = arrayListOf()

    interface DishIngredientsAdapterListener {
        fun onIngredientChange(ingredientsRemoved: List<Long>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DishIngredientViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.dish_ingredient_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dishIngredient = getItem(position) as DishIngredient
        val itemViewHolder = holder as DishIngredientViewHolder

        val ingredient = dishIngredient.ingredient
        val name = ingredient?.name
        val quantity = "${dishIngredient.quantity} ${dishIngredient.unit?.name}"
        itemViewHolder.title.text = "$name ($quantity)"
        if(dishIngredient.isAdjustable!!){
            itemViewHolder.remove.visibility = View.VISIBLE

            if(ingredientsRemoved.contains(ingredient?.id)){
                itemViewHolder.remove.text = "Add"
                itemViewHolder.title.alpha = 0.5f
            }else{
                itemViewHolder.remove.text = "Remove"
                itemViewHolder.title.alpha = 1f
            }

            itemViewHolder.remove.setOnClickListener {
                ingredient?.id?.let{
                    if(ingredientsRemoved.contains(it)){
                        ingredientsRemoved.remove(it)
                    }else{
                        ingredientsRemoved.add(it)
                    }
                listener?.onIngredientChange(ingredientsRemoved)
                    notifyItemChanged(position)
                }
            }
        }else{
            itemViewHolder.remove.visibility = View.GONE
        }
    }

    class DishIngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.dishIngredientItemTitle
        val remove: TextView = view.dishIngredientItemRemove
        val layout: LinearLayout = view.dishIngredientItemLayout
    }

    class DiffCallback : DiffUtil.ItemCallback<DishIngredient>() {

        override fun areItemsTheSame(oldItem: DishIngredient, newItem: DishIngredient): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DishIngredient, newItem: DishIngredient): Boolean {
            return oldItem == newItem
        }
    }

    fun getIngredientsRemovedList(): List<Long>{
        return ingredientsRemoved
    }
}


