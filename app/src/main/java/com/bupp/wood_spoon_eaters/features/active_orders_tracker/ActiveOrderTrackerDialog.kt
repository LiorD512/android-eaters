package com.bupp.wood_spoon_eaters.features.active_orders_tracker


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.TrackOrderFragment
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.fragment_active_order_tracker.*
import org.koin.android.viewmodel.ext.android.viewModel


class ActiveOrderTrackerDialog(val orders: ArrayList<Order>, val listener: ActiveOrderTrackerDialogListener) : DialogFragment(),
    TrackOrderFragment.TrackOrderDialogListener {

    interface ActiveOrderTrackerDialogListener{
        fun onContactUsClick()
    }

    private lateinit var adapter: OrdersPagerAdapter
    val viewModel: ActiveOrderTrackerViewModel by viewModel<ActiveOrderTrackerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_active_order_tracker, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        adapter = OrdersPagerAdapter(childFragmentManager, orders, this)
        ordersTrackerPager.adapter = adapter
    }

    class OrdersPagerAdapter(fragmentManager: FragmentManager, private val orders: ArrayList<Order>,
                             private val listener: TrackOrderFragment.TrackOrderDialogListener) : FragmentStatePagerAdapter(fragmentManager){

        override fun getItem(position: Int): Fragment {
            return TrackOrderFragment.newInstance(orders[position], listener)
        }

        override fun getCount(): Int {
            return orders.size
        }

        fun updateOrders(newOrders: ArrayList<Order>) {
            orders.clear()
            orders.addAll(newOrders)
            notifyDataSetChanged()
        }
    }

    //single order interfaces
    override fun onContactUsClick(order: Order) {
        listener.onContactUsClick()
    }


    override fun onMessageClick(order: Order) {

    }

    override fun onShareImageClick(order: Order) {
        val text = viewModel.getShareText()
        Utils.shareText(activity!!, text)
    }


    override fun onOrderCanceled() {
        (activity as MainActivity).checkForActiveOrder()
        dismiss()
    }

    override fun onCloseClick() {
        dismiss()
    }





}
