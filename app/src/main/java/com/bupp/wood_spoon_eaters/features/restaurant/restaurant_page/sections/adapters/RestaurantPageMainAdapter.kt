package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.sections.adapters;

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
//import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.sections.dishs_section.adapter.RPAdapterDish
//import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.sections.view_holders.RPViewHolderDishes
//import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.sections.view_holders.RPViewHolderMainInfo
//
//class RestaurantPageMainAdapter(private val listener: RestaurantPageMainAdapterListener) :
//    ListAdapter<RestaurantPageSections, RecyclerView.ViewHolder>(DiffCallback()) {
//
//    interface RestaurantPageMainAdapterListener: RPAdapterDish.RPAdapterDishListener {}
//
//    override fun getItemViewType(position: Int): Int = getItem(position).viewType
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): BaseItemViewHolder {
//        when (viewType) {
//            RPSectionDeliveryTiming.viewType -> {
//                return RPViewHolderDeliveryTiming(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.restaurant_item_delivery_timing, parent, false)
//                )
//            }
//            RPSectionMainInfo.viewType -> {
//                return RPViewHolderMainInfo(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.restaurant_item_main_info, parent, false)
//                )
//            }
//            RPSectionDishList.viewType -> {
//                return RPViewHolderDishes(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.restaurant_item_main_info, parent, false)
//                )
//            }
//
//        }
//        return RPViewHolderMainInfo(
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.restaurant_item_main_info, parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val section = getItem(position)) {
//            is RPSectionDeliveryTiming -> {
//                holder as RPViewHolderDeliveryTiming
//                holder.bind(section, listener)
//            }
//            is RPSectionMainInfo -> {
//                holder as RPViewHolderMainInfo
//                holder.bind(section, listener)
//            }
//            is RPSectionSearch -> {
//
//            }
//            is RPSectionDishList -> {
//
//            }
//        }
//    }
//
//
//    abstract class BaseItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        abstract fun bind(
//            section: RestaurantPageSections,
//            listener: RestaurantPageMainAdapterListener
//        )
//    }
//
//    private class DiffCallback : DiffUtil.ItemCallback<RestaurantPageSections>() {
//
//        override fun areItemsTheSame(
//            oldItem: RestaurantPageSections,
//            newItem: RestaurantPageSections
//        ): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(
//            oldItem: RestaurantPageSections,
//            newItem: RestaurantPageSections
//        ): Boolean {
//            return oldItem.viewType == oldItem.viewType
//        }
//    }
//}