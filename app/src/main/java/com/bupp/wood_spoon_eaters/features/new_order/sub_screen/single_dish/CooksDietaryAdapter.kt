package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.DietaryIcon
import kotlinx.android.synthetic.main.cooks_dietary_item.view.*

class CooksDietaryAdapter(val context: Context, private var diets: ArrayList<DietaryIcon>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dietary = diets[position]
        Glide.with(context).load(dietary.icon).into((holder as ItemViewHolder).icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cooks_dietary_item, parent, false))
    }

    override fun getItemCount(): Int {
        return diets.size
    }


}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val icon: ImageView = view.dietaryItem

}