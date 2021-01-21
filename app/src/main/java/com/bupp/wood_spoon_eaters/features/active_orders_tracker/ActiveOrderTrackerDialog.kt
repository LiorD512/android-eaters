package com.bupp.wood_spoon_eaters.features.active_orders_tracker


import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.TrackOrderFragment
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.bupp.wood_spoon_eaters.utils.Utils
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.fragment_active_order_tracker.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActiveOrderTrackerDialog : DialogFragment(),
    TrackOrderFragment.TrackOrderDialogListener {

    interface ActiveOrderTrackerDialogListener{
        fun onContactUsClick()
    }

    var orders: ArrayList<Order>? = null
    private var listener: ActiveOrderTrackerDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
        arguments?.let {
            orders = it.getParcelableArrayList(DIALOG_ARGS)
        }

    }

    companion object {
        const val DIALOG_ARGS = "trackables_data"
        @JvmStatic
        fun newInstance(trackablesOrder: ArrayList<Order>) =
            ActiveOrderTrackerDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(DIALOG_ARGS, trackablesOrder)
                }
            }
    }

    private lateinit var adapter: OrdersPagerAdapter
    val viewModel by sharedViewModel<ActiveOrderTrackerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_active_order_tracker, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        orders?.let{
            adapter = OrdersPagerAdapter(this, it, this)
            ordersTrackerPager.adapter = adapter

            if(it?.size > 1){
                ordersTrackerIndicator.visibility = View.VISIBLE
                ordersTrackerIndicator.setViewPager(ordersTrackerPager)
            }else{
                ordersTrackerIndicator.visibility = View.GONE
            }
        }

        viewModel.startSilentUpdate()

    }

    class OrdersPagerAdapter(fragment: DialogFragment, private val orders: ArrayList<Order>,
                             private val listener: TrackOrderFragment.TrackOrderDialogListener) : FragmentStateAdapter(fragment){


        override fun getItemCount(): Int {
            return orders?.size
        }

        override fun createFragment(position: Int): Fragment {
            val orderId = orders[position].id
            Log.d("wowTrackOrderFragment", "init newTrackFrag id: $orderId")
            return TrackOrderFragment.newInstance(orderId)
        }
    }

    //single order interfaces
    override fun onContactUsClick(order: Order) {
        listener?.onContactUsClick()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActiveOrderTrackerDialogListener) {
            listener = context
        }
        else if (parentFragment is ActiveOrderTrackerDialogListener){
            this.listener = parentFragment as ActiveOrderTrackerDialogListener
        }
        else {
            throw RuntimeException(context.toString() + " must implement ActiveOrderTrackerDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }





}
