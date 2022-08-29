package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrderHistoryItemSkeletonBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryActiveOrderItemBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryOrderItemBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryTitleItemBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.OrderProgressBar
import android.app.Activity
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.MapSyncUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.WSSimpleBtn
import java.util.*


class OrdersHistoryAdapter(val context: Context, val listener: OrdersHistoryAdapterListener) :
    ListAdapter<OrderHistoryBaseItem, RecyclerView.ViewHolder>(DiffCallback()) {

    interface OrdersHistoryAdapterListener {
        fun onOrderClick(orderId: Long)
        fun onViewActiveOrderClicked(order: Order, transitionBundle: ActivityOptionsCompat, mapPreview: String)
        fun onOrderAgainClick(order: Order)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OrderHistoryViewType.ORDER.ordinal -> {
                val binding = OrdersHistoryOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderItemViewHolder(binding)
            }
            OrderHistoryViewType.ACTIVE_ORDER.ordinal -> {
                val binding = OrdersHistoryActiveOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ActiveOrderItemViewHolder(binding)
            }
            OrderHistoryViewType.SKELETON.ordinal -> {
                val binding = OrderHistoryItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SkeletonItemViewHolder(binding)
            }
            else -> { //OrderHistoryViewType.TITLE
                val binding = OrdersHistoryTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderHistoryTitleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is OrderAdapterItemSkeleton -> {
                holder as SkeletonItemViewHolder
            }
            is OrderAdapterItemTitle -> {
                holder as OrderHistoryTitleViewHolder
                holder.bindItem(item)
            }
            is OrderAdapterItemOrder -> {
                holder as OrderItemViewHolder
                holder.bindItem(item)
            }
            is OrderAdapterItemActiveOrder -> {
                holder as ActiveOrderItemViewHolder
                holder.bindItem(item)

//                val containerId: Int = holder.mapContainer.id // Get container id
//
//                val oldFragment: Fragment? = fm.findFragmentById(containerId)
//                if (oldFragment != null) {
//                    fm.beginTransaction().remove(oldFragment).commit()
//                }
//
//                val newContainerId = View.generateViewId() // Generate unique container id
//                holder.mapContainer.id = newContainerId // Set container id
//
//                val bundle = Bundle()
//                bundle.putFloat("zoom_level", 17f)
//                bundle.putBoolean("show_btns", false)
//                bundle.putBoolean("isCheckout", true)
//                bundle.putParcelable("order", item.order)
//                val fragment: AddressVerificationMapFragment = AddressVerificationMapFragment.newInstance(bundle)
//                fm.beginTransaction().replace(newContainerId, fragment).commit()
            }
        }
    }

    inner class SkeletonItemViewHolder(val binding: OrderHistoryItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root)

    inner class OrderItemViewHolder(val binding: OrdersHistoryOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.orderHistoryItemChef
        private val orderAgain: WSSimpleBtn = binding.orderHistoryOrderAgainBtn
        private val date: TextView = binding.orderHistoryItemDate
        private val mainLayout = binding.orderHistoryArchiveMainLayout

        fun bindItem(data: OrderAdapterItemOrder) {
            val order = data.order
            title.text = "${context.getString(R.string.order_history_item_by_cook)} ${order.restaurant?.firstName} ${order.restaurant?.lastName}"
            if (order.estDeliveryTime != null) {
                date.text = "${DateUtils.parseDateToDateAndTime(order.estDeliveryTime)
                    .uppercase(Locale.ROOT)} \u2022 ${order.total?.formatedValue}"
            } else {
                date.text = "${order.estDeliveryTimeText} • ${order.total?.formatedValue}"
            }

            orderAgain.setOnClickListener {
                listener.onOrderAgainClick(order)
            }

            mainLayout.setOnClickListener {
                order.id?.let { it1 -> listener.onOrderClick(it1) }
            }
        }
    }

    inner class ActiveOrderItemViewHolder(val binding: OrdersHistoryActiveOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val mainLayout: LinearLayout = binding.orderHistoryActiveMainLayout
        private val restaurantName: TextView = binding.activeOrderRestaurantName
        private val title: TextView = binding.activeOrderTitle
        private val orderNumber: TextView = binding.activeOrderNumber
        private val subtitle: TextView = binding.activeOrderSubtitle
        private val orderPb: OrderProgressBar = binding.activeOrderPb
        private val mapContainer: ImageView = binding.activeOrderFragContainer
        private val sep: View = binding.activeOrderSep


        fun bindItem(data: OrderAdapterItemActiveOrder) {
            val order = data.order

            mapContainer.post {
                val url = MapSyncUtil().getMapImage(order, mapContainer.measuredWidth, Utils.toPx(152))
                val bigUrl = MapSyncUtil().getMapImage(order, 600, 1800)
                Log.d("wowSTtaicMap", "url $url")
                Log.d("wowSTtaicMap", "bigUrl $bigUrl")
                Glide.with(context).load(url).into(mapContainer)

                restaurantName.text = order.restaurant?.restaurantName ?: ""
                val orderState = order.status
                Log.d("wowStatus", "bindItem: ${order.id}. orderState: $orderState")
                orderPb.reset()
                orderPb.setState(orderState)

                title.text = order.extendedStatus?.title
                subtitle.text = order.extendedStatus?.subtitle
                orderNumber.text = order.orderNumber

                mainLayout.setOnClickListener {
                    order.let { order ->
                        val pairMap: Pair<View, String> = Pair.create(mapContainer as View, mapContainer.transitionName)
                        val pairName: Pair<View, String> = Pair.create(restaurantName as View, restaurantName.transitionName)
                        val pairStatusTitle: Pair<View, String> = Pair.create(title as View, title.transitionName)
                        val pairStatusSubTitle: Pair<View, String> = Pair.create(subtitle as View, subtitle.transitionName)
                        val pairPb: Pair<View, String> = Pair.create(orderPb as View, orderPb.transitionName)


                        val transitionBundle: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity, pairMap, pairName, pairStatusTitle, pairStatusSubTitle, pairPb
                        )
                        listener.onViewActiveOrderClicked(order, transitionBundle, bigUrl)
                    }
                }
                sep.isVisible = data.isLast
            }
        }
    }

    inner class OrderHistoryTitleViewHolder(val binding: OrdersHistoryTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.orderHistoryTitle
        fun bindItem(titleItem: OrderAdapterItemTitle) {
            title.text = titleItem.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderHistoryBaseItem>() {

        override fun areItemsTheSame(oldItem: OrderHistoryBaseItem, newItem: OrderHistoryBaseItem): Boolean {
            var isSame = oldItem == newItem
            if (oldItem is OrderAdapterItemActiveOrder && newItem is OrderAdapterItemActiveOrder) {
                isSame = oldItem.order.extendedStatus?.subtitle == newItem.order.extendedStatus?.subtitle
                Log.d("wowStatus", "isSame: $isSame ${oldItem.order.id}")
            }
            Log.d("wowStatus", "adapter - areItemsTheSame $isSame")
            return isSame
        }

        override fun areContentsTheSame(oldItem: OrderHistoryBaseItem, newItem: OrderHistoryBaseItem): Boolean {
            var isSame = oldItem == newItem
            if (oldItem is OrderAdapterItemActiveOrder && newItem is OrderAdapterItemActiveOrder) {
                isSame = oldItem.order.extendedStatus?.subtitle == newItem.order.extendedStatus?.subtitle
                Log.d("wowStatus", "isSame: $isSame ${oldItem.order.id}")
            }
            Log.d("wowStatus", "adapter - areContentsTheSame $isSame")
            return isSame

        }
    }

}