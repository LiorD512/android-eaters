package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.RemovedIngredientViewBinding
import com.bupp.wood_spoon_eaters.model.Ingredient

class IngredientsCheckoutAdapter(val context: Context, private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsCheckoutAdapter.RemovedIngredientViewHolder>() {

    class RemovedIngredientViewHolder(view: RemovedIngredientViewBinding) : RecyclerView.ViewHolder(view.root) {
        val name: TextView = view.removedIngredientName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemovedIngredientViewHolder {
        val binding = RemovedIngredientViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RemovedIngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onBindViewHolder(holder: RemovedIngredientViewHolder, position: Int) {
        val ingredient: Ingredient = ingredients[position]

        holder.name.text = "Without ${ingredient.name}"
    }
}