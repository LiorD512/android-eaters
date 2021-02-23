package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.additional_dishes

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.AdditionalDishesDialogBinding
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.AdditionalDishMainAdapter
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.AdditionalDishesAdapter
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.OrderItemsAdapter
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.additional_dishes_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdditionalDishesDialog : DialogFragment(), OrderItemsAdapter.OrderItemsListener, AdditionalDishesAdapter.AdditionalDishesListener,
    ClearCartDialog.ClearCartDialogListener {

    private var mainAdapter: AdditionalDishMainAdapter? = null
    private lateinit var binding: AdditionalDishesDialogBinding
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    var isDismissed = true // if true - redirects to cook's profile and lock header when dismissed.

//    var listener: AdditionalDishesDialogListener? = null
//
//    interface AdditionalDishesDialogListener{
////        fun onProceedToCheckout()
////        fun onDishClick(dish: Dish)
////        fun onAdditionalDialogDismiss()
////        fun onNewOrderDone()
//    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.additional_dishes_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AdditionalDishesDialogBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

        initUi()
        initObservers()
        
        mainViewModel.initAdditionalDishes()
    }


    private fun initObservers() {
            mainViewModel.additionalDishesEvent.observe(viewLifecycleOwner, Observer { data ->
                handleData(data)
            })
            mainViewModel.clearCartEvent.observe(viewLifecycleOwner, Observer { emptyCartEvent ->
                if(emptyCartEvent) {
                    ClearCartDialog(this@AdditionalDishesDialog).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
                }
            })
    }

    private fun handleData(data: NewOrderMainViewModel.AdditionalDishesEvent) {
//        mainAdapter?.clearAllSelections()
        data.orderItems?.let {
            mainAdapter?.refreshOrderItems(it)
        }
        data.additionalDishes?.let{
            mainAdapter?.refreshAdditionalDishes(it)
        }
    }

    private fun initUi() {
        mainAdapter = AdditionalDishMainAdapter(requireContext(), this, this)
        with(binding){
            additionalDishDialogList.layoutManager = LinearLayoutManager(context)
            additionalDishDialogList.adapter = mainAdapter

            additionalDishDialogClose.setOnClickListener {
                dismiss()
            }
            dishAddonProceedBtn.setOnClickListener {
                mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.CHECKOUT)
                isDismissed = false
                dismiss()
             }
        }
    }

    override fun onAddBtnClick(dish: Dish) {
        mainViewModel.addNewDishToCart(dish.id, 1)
        mainAdapter?.removeDish(dish)
    }

    override fun onDishClick(dish: Dish) {
//        listener?.onDishClick(dish)
        mainViewModel.refreshFullDish(dish.menuItem?.id)
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO)
        isDismissed = false
        dismiss()
    }

    override fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {
        if(isOrderItemsEmpty){
            mainViewModel.showClearCartDialog()
        }else{
            mainViewModel.updateOrderItem(curOrderItem)
        }
    }

    override fun onClearCart() {
        mainViewModel.clearCart()
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
        isDismissed = false
        dismiss()
    }

    override fun onCancelClearCart() {
        //refresh additional dishes counter (set to 0 by user and canceled)
        mainViewModel.initAdditionalDishes()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if(isDismissed){
            mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCK_SINGLE_DISH_COOK)
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is AdditionalDishesDialogListener) {
//            listener = context
//        }
//        else if (parentFragment is AdditionalDishesDialogListener){
//            this.listener = parentFragment as AdditionalDishesDialogListener
//        }
//        else {
//            throw RuntimeException("$context must implement AdditionalDishesDialogListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }


}