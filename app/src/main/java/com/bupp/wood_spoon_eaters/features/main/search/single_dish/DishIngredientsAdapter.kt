package com.bupp.wood_spoon_eaters.features.main.search.single_dish

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.DishIngredient
import kotlinx.android.synthetic.main.dish_ingredient_item.view.*
import kotlinx.android.synthetic.main.stackable_text_view_item.view.*

class DishIngredientsAdapter(val context: Context, val ingredient: ArrayList<DishIngredient>) : RecyclerView.Adapter<DishIngredientsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.dishIngredientItemTitle
        val remove = view.dishIngredientItemRemove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.dish_ingredient_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ingredient.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredient[position]

        holder.title.text = ingredient.ingredient?.name
        if(ingredient.isAdjustable!!){
            holder.remove.visibility = View.VISIBLE
        }else{
            holder.remove.visibility = View.GONE
        }
        holder.remove.setOnClickListener {} //todo
    }


}