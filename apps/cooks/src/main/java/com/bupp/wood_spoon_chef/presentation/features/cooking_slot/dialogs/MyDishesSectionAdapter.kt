package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ListItemCategoryBinding
import com.bupp.wood_spoon_chef.databinding.ListItemDishSelectionBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterDish
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import java.util.*

class MyDishesSectionAdapter(
    private val listener: MyDishesSectionAdapterListener
) : SectionedListAdapter<MyDishesPickerAdapterModel>() {

    interface MyDishesSectionAdapterListener {
        fun onDishSelected(isChecked: Boolean, dishId: Long)
    }

    override fun sectionsToSectionedListItem(sections: List<MyDishesPickerAdapterModel>): List<SectionedListItem> {
        val items = mutableListOf<SectionedListItem>()
        for (section in sections) {
            items.add(SectionedListItem.SectionHeader(section.section?.title!!,
                section.section.title
            ))
            section.dishes?.map { SectionedListItem.SectionItem(it, it.dish?.id.toString()) }?.let { items.addAll(it) }
        }
        return items
    }

    override fun createHeaderViewHolder(view: View): BaseViewHolder {
        val binding = ListItemCategoryBinding.bind(view)
        return CategoryViewHolder(binding)
    }

    override fun createItemViewHolder(view: View): BaseViewHolder {
        val binding = ListItemDishSelectionBinding.bind(view)
        return DishItemSelectionViewHolder(binding)
    }

    override fun submitSections(sections: List<MyDishesPickerAdapterModel>) {
        submitList(sectionsToSectionedListItem(sections))
    }


    override fun getItemLayout(): Int {
        return R.layout.list_item_dish_selection
    }

    override fun getHeaderItemLayout(): Int {
        return R.layout.list_item_category
    }

    class DishItemSelectionViewHolder(val binding: ListItemDishSelectionBinding) :
        BaseViewHolder(binding.root) {
        val selectedCb: CheckBox = binding.listItemDishSelectionCb
        val dishItem = binding.root
        override fun bind(item: SectionedListItem) {
            if (item is SectionedListItem.SectionItem) {
                val myDishesPickerAdapterDish = item.item as MyDishesPickerAdapterDish
                binding.apply {
                    listItemDishSelectionDishName.text = myDishesPickerAdapterDish.dish?.name
                    listItemDishSelectionDishPrice.text =
                        myDishesPickerAdapterDish.dish?.price?.formattedValue
                    Glide.with(binding.root.context)
                        .load(myDishesPickerAdapterDish.dish?.imageGallery?.first())
                        .into(listItemDishSelectionImage)
                }
            }
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

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item is SectionedListItem.SectionItem) {
            val myDishesPickerAdapterDish = item.item as MyDishesPickerAdapterDish
            holder as DishItemSelectionViewHolder
            holder.selectedCb.setOnCheckedChangeListener(null)
            holder.selectedCb.isChecked = myDishesPickerAdapterDish.isSelected
            var isChecked: Boolean
            holder.dishItem.setOnClickListener {
                myDishesPickerAdapterDish.dish?.id?.let {
                    isChecked= !holder.selectedCb.isChecked
                    listener.onDishSelected(isChecked, it)
                }
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
}
