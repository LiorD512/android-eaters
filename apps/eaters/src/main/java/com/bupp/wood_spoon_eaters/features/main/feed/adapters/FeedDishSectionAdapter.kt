package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.PagerItemDishBinding
import com.bupp.wood_spoon_eaters.model.FeedDishItemSection

class FeedDishSectionAdapter(
    val listener: FeedDishSectionListener
) : ListAdapter<FeedDishItemSection, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedDishSectionListener {
        fun onDishClick(dish: FeedDishItemSection?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PagerItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedDishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as FeedDishViewHolder
        itemViewHolder.bindItem(holder.itemView.context, item, listener)
    }

    class FeedDishViewHolder(
        val binding: PagerItemDishBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(
            context: Context,
            dish: FeedDishItemSection,
            listener: FeedDishSectionListener
        ) {
            dish.cookingSlot?.let {
                if (it.canOrder) {
                    Glide
                        .with(context)
                        .load(dish.thumbnail?.url)
                        .into(binding.ivDishPhoto)
                } else {
                    Glide
                        .with(context)
                        .load(dish.cook?.cover?.url)
                        .into(binding.ivDishPhoto)
                }
            }
            binding.apply {
                tvDishName.text = dish.name
                tvDishPrice.text = dish.price
                tvSubtitle.text = dish.cook?.restaurantName
                tvRating.text = dish.cook?.rating.toString()
                tvItemUnavailable.isVisible = (dish.cookingSlot?.canOrder)?.not() ?: false
                tagView.apply {
                    isVisible = dish.cookingSlot?.canOrder ?: false
                    setTags(dish.tags)
                }

                root.setOnClickListener {
                    listener.onDishClick(dish)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedDishItemSection>() {

        override fun areItemsTheSame(oldItem: FeedDishItemSection, newItem: FeedDishItemSection) =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: FeedDishItemSection,
            newItem: FeedDishItemSection
        ) = oldItem == newItem
    }
}
