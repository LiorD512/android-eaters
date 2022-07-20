package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ListItemCategoryBinding
import com.bupp.wood_spoon_chef.databinding.ListItemMenuDishBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterDish
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.*
import java.util.*

class DishMenuAdapter(private val listener: DishesMenuAdapterListener) :
    SectionedListAdapter<MyDishesPickerAdapterModel>() {

    interface DishesMenuAdapterListener {
        fun onDeleteClick(dish: MyDishesPickerAdapterDish, position: Int)
    }

    override fun submitSections(sections: List<MyDishesPickerAdapterModel>) {
        submitList(sectionsToSectionedListItem(sections))
    }

    override fun sectionsToSectionedListItem(sections: List<MyDishesPickerAdapterModel>): List<SectionedListItem> {
        val items = mutableListOf<SectionedListItem>()
        for (section in sections) {
            section.section?.title?.let { title ->
                items.add(SectionedListItem.SectionHeader(title, title))
                section.dishes?.map { SectionedListItem.SectionItem(it, it.dish?.id.toString()) }?.let { items.addAll(it) }
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
        if (item is SectionedListItem.SectionItem){
            val myDishesPickerAdapterDish = item.item as MyDishesPickerAdapterDish
            holder as DishMenuItemViewHolder
            holder.deleteBtn.setOnClickListener {
                listener.onDeleteClick(myDishesPickerAdapterDish, position)
            }
            holder.bind(item)
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position) is SectionedListItem.SectionHeader){
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

    class DishMenuItemViewHolder(val binding: ListItemMenuDishBinding) :
        BaseViewHolder(binding.root) {
        val deleteBtn = binding.listItemDishMenuDeleteDish
        override fun bind(item: SectionedListItem) {
            if (item is SectionedListItem.SectionItem){
                val myDishesAdapterModel = item.item as MyDishesPickerAdapterDish
                binding.apply {
                    listItemDishMenuDishName.text = myDishesAdapterModel.dish?.name
                    listItemDishMenuDishPrice.text = myDishesAdapterModel.dish?.price?.formattedValue
                    Glide.with(binding.root.context).load(myDishesAdapterModel.dish?.imageGallery?.first())
                        .into(listItemDishMenuImage)
                }
            }
        }
    }
}