package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.DishIngredient
import kotlinx.android.synthetic.main.dish_ingredient_item.view.*

class DishIngredientsAdapter(val context: Context, val ingredient: ArrayList<DishIngredient>) : RecyclerView.Adapter<DishIngredientsAdapter.ViewHolder>() {

    val ingredientsRemoved: ArrayList<Long> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.dishIngredientItemTitle
        val remove = view.dishIngredientItemRemove
        val layout = view.dishIngredientItemLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.dish_ingredient_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ingredient.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dishIngredient = ingredient[position]
        val ingredient = dishIngredient.ingredient
        val name = ingredient?.name
        val quantity = "${dishIngredient.quantity} ${dishIngredient.unit?.name}"
        holder.title.text = "$name ($quantity)"
        if(dishIngredient.isAdjustable!!){
            holder.remove.visibility = View.VISIBLE

            if(ingredientsRemoved.contains(ingredient?.id)){
                holder.remove.text = "Add"
                holder.title.alpha = 0.5f
            }else{
                holder.remove.text = "Remove"
                holder.title.alpha = 1f
            }

            holder.remove.setOnClickListener {
                if(ingredientsRemoved.contains(ingredient?.id)){
                    ingredientsRemoved.remove(ingredient?.id)
                }else{
                    ingredientsRemoved.add(ingredient?.id!!)
                }
                notifyDataSetChanged()
            }
        }else{
            holder.remove.visibility = View.GONE
        }

    }


}