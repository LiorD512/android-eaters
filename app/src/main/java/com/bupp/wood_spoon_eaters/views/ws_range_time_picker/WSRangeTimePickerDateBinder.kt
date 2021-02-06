//package com.bupp.wood_spoon_eaters.views.ws_range_time_picker
//
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.LinearSnapHelper
//import androidx.recyclerview.widget.SnapHelper
//import com.bupp.wood_spoon_eaters.R
//import kotlinx.android.synthetic.main.ws_range_time_picker_recycler_item.view.*
//import mva2.adapter.ItemBinder
//import mva2.adapter.ItemViewHolder
//
//
//class WSRangeTimePickerDateBinder : ItemBinder<DateSectionModel, WSRangeTimePickerDateBinder.CategoryViewHolder>() {
//
//
//    override fun createViewHolder(parent: ViewGroup): CategoryViewHolder {
//        return CategoryViewHolder(inflate(parent, R.layout.ws_range_time_picker_recycler_item))
//    }
//
//    override fun canBindData(item: Any): Boolean {
//        return item is DateSectionModel
//    }
//
//    override fun bindViewHolder(holder: CategoryViewHolder, items: DateSectionModel) {
//        holder.bindItems(items)
//    }
//
//    inner class CategoryViewHolder(itemView: View) : ItemViewHolder<DateSectionModel>(itemView) {
//        fun bindItems(item: DateSectionModel) {
//            val adapter = WSRangeTimePickerListAdapter()
//            itemView.wsRangeTimePickerListItem.layoutManager = LinearLayoutManager(itemView.context)
//            itemView.wsRangeTimePickerListItem.adapter = adapter
//
////            itemView.wsRangeTimePickerListItem.isNestedScrollingEnabled = true
//
//            val snapHelper: SnapHelper = LinearSnapHelper()
//            snapHelper.attachToRecyclerView(itemView.wsRangeTimePickerListItem)
//
//            adapter.submitList(item.dates)
//        }
//    }
//
//}