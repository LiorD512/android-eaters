package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.DishIngredient
import com.bupp.wood_spoon_eaters.model.Ingredient
import kotlinx.android.synthetic.main.order_item_view.view.*
import kotlinx.android.synthetic.main.removed_ingredient_view.view.*

class IngredientsCheckoutAdapter(val context: Context, private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsCheckoutAdapter.RemovedIngredientViewHolder>() {

    class RemovedIngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.removedIngredientName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemovedIngredientViewHolder {
        return RemovedIngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.removed_ingredient_view,parent,false))
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: RemovedIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients[position]

        holder.name.text = "Without ${ingredient.name}"
    }
}