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
import com.bupp.wood_spoon_eaters.common.DepthPageTransformer
import com.bupp.wood_spoon_eaters.databinding.FragmentActiveOrderTrackerBinding
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.TrackOrderFragment
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.fragment_active_order_tracker.*
import kotlinx.android.synthetic.main.sharing_campaign_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActiveOrderTrackerDialog : DialogFragment(),
    TrackOrderFragment.TrackOrderDialogListener {

    interface ActiveOrderTrackerDialogListener {
        fun onContactUsClick()
    }

    //    var orders: ArrayList<Order>? = null
    private var listener: ActiveOrderTrackerDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }
//
//    companion object {
//        const val DIALOG_ARGS = "trackables_data"
//        @JvmStatic
//        fun newInstance(trackablesOrder: ArrayList<Order>) =
//            ActiveOrderTrackerDialog().apply {
//                arguments = Bundle().apply {
//                    putParcelableArrayList(DIALOG_ARGS, trackablesOrder)
//                }
//            }
//    }

    var binding: FragmentActiveOrderTrackerBinding? = null
    private lateinit var adapter: OrdersPagerAdapter
    val viewModel by sharedViewModel<ActiveOrderTrackerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_order_tracker, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentActiveOrderTrackerBinding.bind(view)

        initObservers()
    }

    private fun initObservers() {
        viewModel.traceableOrdersLiveData.observe(viewLifecycleOwner, {
            handleOrders(it)
        })
    }

    private fun handleOrders(orders: List<Order>?) {
        with(binding!!){
            orders?.let {
                if(!this@ActiveOrderTrackerDialog::adapter.isInitialized){
                    //check if isInitialized to do this only once. when dialog opens for the first time.
                    adapter = OrdersPagerAdapter(this@ActiveOrderTrackerDialog, it)
                    ordersTrackerPager.adapter = adapter
                    ordersTrackerPager.setPageTransformer(DepthPageTransformer())
                    ordersTrackerPager.offscreenPageLimit = it.size

                    if (it.size > 1) {
                        ordersTrackerIndicator.visibility = View.VISIBLE
                        ordersTrackerIndicator.setViewPager(ordersTrackerPager)
    //                    adapter.registerAdapterDataObserver(ordersTrackerIndicator.adapterDataObserver);
                    } else {
                        ordersTrackerIndicator.visibility = View.GONE
                    }
                }
            }
        }
    }

    class OrdersPagerAdapter(
        fragment: DialogFragment, private val orders: List<Order>,
    ) : FragmentStateAdapter(fragment) {


        override fun getItemCount(): Int {
            return orders.size
        }

        override fun createFragment(position: Int): Fragment {
            val orderId = orders[position].id
            Log.d(TAG, "init newTrackFrag id: $orderId")
            return TrackOrderFragment.newInstance(orderId!!)
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
        Utils.shareText(requireActivity(), text)
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
        } else if (parentFragment is ActiveOrderTrackerDialogListener) {
            this.listener = parentFragment as ActiveOrderTrackerDialogListener
        } else {
            throw RuntimeException("$context must implement ActiveOrderTrackerDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object{
        const val TAG = "wowTrackOrderFragment"
    }


}
