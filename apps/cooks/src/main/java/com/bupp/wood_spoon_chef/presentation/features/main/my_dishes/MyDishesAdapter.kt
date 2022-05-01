package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.databinding.MyDishesItemBinding
import com.bupp.wood_spoon_chef.data.remote.model.Dish

class MyDishesAdapter(val context: Context, val listener: MyDishesAdapterListener) :
    ListAdapter<Dish, RecyclerView.ViewHolder>(DiffCallback()) {

    interface MyDishesAdapterListener {
        fun onDishClicked(dish: Dish)
        fun onDraftClicked(dish: Dish)
        fun onItemMenuClick(selected: Dish?)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            MyDishesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curDish = getItem(position)
        (holder as ViewHolder).bind(context, curDish, listener)
    }

    class ViewHolder(view: MyDishesItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val mainLayout: CardView = view.myDishesMainLayout
        private val draftLayout: FrameLayout = view.myDishesDraft
        private val unPublishedLayout: FrameLayout = view.myDishesUnPublish
        private val menu: ImageButton = view.myDishesItemMenu
        private val img: ImageView = view.myDishesItemImg
        private val title: TextView = view.myDishesItemTitle
        private val count: TextView = view.myDishesItemCount
        private val profit: TextView = view.myDishesItemProfit

        fun bind(context: Context, curDish: Dish, listener: MyDishesAdapterListener) {
            img.visibility = View.INVISIBLE
            if (curDish.imageGallery?.isNotEmpty() == true) {
                img.visibility = View.VISIBLE
                Glide.with(context).load(curDish.imageGallery?.first()).into(img)
            }
            title.text = curDish.name

            val countSold = curDish.unitSold
            count.text = "$countSold Dishes sold"
            val profitVal = curDish.totalProfit?.formattedValue
            profit.text = "$profitVal"

            menu.setOnClickListener {
                listener.onItemMenuClick(curDish)
            }

            mainLayout.setOnClickListener {
                onDishClick(listener, curDish, curDish.isDraft())
            }

            if (curDish.isDraft()) {
                draftLayout.visibility = View.VISIBLE
            } else {
                draftLayout.visibility = View.GONE
            }

            if (curDish.isUnpublished()) {
                unPublishedLayout.visibility = View.VISIBLE
            } else {
                unPublishedLayout.visibility = View.GONE
            }

        }

        private fun onDishClick(listener: MyDishesAdapterListener, dish: Dish, isDraft: Boolean) {
            if (isDraft) {
                listener.onDraftClicked(dish)
            } else {
                listener.onDishClicked(dish)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Dish>() {

        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem.id == newItem.id
        }
    }
}