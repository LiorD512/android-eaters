package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ListItemCategoryBinding
import com.bupp.wood_spoon_chef.databinding.ListItemMenuDishBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.DishesMenuAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.*
import com.bupp.wood_spoon_chef.utils.extensions.setTextIfNeeded
import java.util.*

class DishMenuAdapter(
    private val listener: DishesMenuAdapterListener
) : SectionedListAdapter<DishesMenuAdapterModel>() {

    interface DishesMenuAdapterListener {
        fun onDeleteClick(dishId: Long?)
        fun onQuantityChange(dishId: Long?, quantity: Int)
    }

    override fun submitSections(sections: List<DishesMenuAdapterModel>) {
        submitList(sectionsToSectionedListItem(sections))
    }

    override fun sectionsToSectionedListItem(sections: List<DishesMenuAdapterModel>): List<SectionedListItem> {
        val items = mutableListOf<SectionedListItem>()
        for (section in sections) {
            section.section?.title?.let { title ->
                items.add(SectionedListItem.SectionHeader(title, title))
                section.dishes.map { SectionedListItem.SectionItem(it, it.dish?.id.toString()) }
                    .let { items.addAll(it) }
            }
        }
        return items
    }

    override fun createHeaderViewHolder(view: View): BaseViewHolder {
        val binding = ListItemCategoryBinding.bind(view)
        return CategoryViewHolder(binding)
    }

    override fun createItemViewHolder(view: View): BaseViewHolder {
        val binding = ListItemMenuDishBinding.bind(view)
        return DishMenuItemViewHolder(binding)
    }

    override fun getHeaderItemLayout(): Int {
        return R.layout.list_item_category
    }

    override fun getItemLayout(): Int {
        return R.layout.list_item_menu_dish
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item is SectionedListItem.SectionItem) {
            val menuDishItem = item.item as MenuDishItem
            holder as DishMenuItemViewHolder
            holder.deleteBtn.setOnClickListener {
                listener.onDeleteClick(menuDishItem.dish?.id)
            }
            holder.reduceQuantityBtn.setOnClickListener {
                listener.onQuantityChange(menuDishItem.dish?.id, menuDishItem.quantity.minus(1))
            }
            holder.raiseQuantityBtn.setOnClickListener {
                listener.onQuantityChange(menuDishItem.dish?.id, menuDishItem.quantity.plus(1))
            }
            holder.bind(item)
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position) is SectionedListItem.SectionHeader) {
            holder as CategoryViewHolder
            holder.bind(getItem(position))
        }
    }

    class CategoryViewHolder(val binding: ListItemCategoryBinding) :
        BaseViewHolder(binding.root) {
        override fun bind(item: SectionedListItem) {
            if (item is SectionedListItem.SectionHeader) {
                val title = item.section as String
                binding.apply {
                    listItemCategoryTitle.text = title.replaceFirstChar {
                        it.titlecase(
                            Locale.ROOT
                        )
                    }
                }
            }
        }

    }

    inner class DishMenuItemViewHolder(
        val binding: ListItemMenuDishBinding
    ) : BaseViewHolder(binding.root) {

        val deleteBtn = binding.listItemDishMenuDeleteDish
        val reduceQuantityBtn = binding.listItemMenuDishQuantityMinus
        val raiseQuantityBtn = binding.listItemMenuDishQuantityPlus
        val dishQuantityNumber = binding.listItemMenuDishQuantityNumber

        init {
            dishQuantityNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    val cleanedInput = s.filter { it.isDigit() }
                    if (!s.contentEquals(cleanedInput)) {
                        dishQuantityNumber.setTextIfNeeded(cleanedInput.toString())
                    } else {
                        ((getItem(absoluteAdapterPosition) as SectionedListItem.SectionItem).item as MenuDishItem).dish?.id?.let {
                            listener.onQuantityChange(it, s.toString().toIntOrNull() ?: 0)
                        }
                    }
                }

            })
        }

        override fun bind(item: SectionedListItem) {
            if (item is SectionedListItem.SectionItem) {
                val menuDishItem = item.item as MenuDishItem
                binding.apply {
                    listItemDishMenuDishName.text = menuDishItem.dish?.name
                    listItemDishMenuDishPrice.text = menuDishItem.dish?.price?.formattedValue
                    listItemMenuDishQuantityNumber.setTextIfNeeded(menuDishItem.quantity.toString())
                    Glide.with(binding.root.context).load(menuDishItem.dish?.imageGallery?.firstOrNull())
                        .into(listItemDishMenuImage)
                }
            }
        }
    }
}
