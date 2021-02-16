package com.bupp.wood_spoon_eaters.dialogs.additional_dishes

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.*
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.additional_dishes_dialog.*
import kotlinx.android.synthetic.main.dish_addon_view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AdditionalDishesDialog(val listener: AdditionalDishesDialogListener) : DialogFragment(), AdditionalDishesAdapter.AdditionalDishesListener, OrderItemsAdapter.OrderItemsListener,
    ClearCartDialog.ClearCartDialogListener {

    interface AdditionalDishesDialogListener{
        fun onProceedToCheckout()
        fun onDishClick(dish: Dish)
        fun onAdditionalDialogDismiss()
    }

    //    private val dishSharedViewModel by sharedViewModel<SingleDishViewModel>()
//    private val viewModel by viewModel<AdditionalDishesViewModel>()

    private val sharedViewModel by sharedViewModel<NewOrderMainViewModel>()
    private var mainAdapter: AdditionalDishMainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.additional_dishes_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)));
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        sharedViewModel.orderData.observe(this, Observer { orderItems ->
            handleOrderItems(orderItems)
            additionalDishDialogList.scrollToPosition(0)
        })
        sharedViewModel.additionalDishes.observe(this, Observer { additionalDishes ->
            handleAdditionalDishes(additionalDishes)
        })
        sharedViewModel.progressData.observe(this, Observer { progress ->
            when (progress) {
                true -> dishAddonPb.show()
                false -> dishAddonPb.hide()
            }
        })
        sharedViewModel.emptyCartEvent.observe(this, Observer { emptyCartEvent ->
            if(emptyCartEvent.shouldShow) {
                ClearCartDialog(this).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
            }
        })
    }

    private fun handleOrderItems(order: Order?) {
        order?.let {
            it.orderItems.let {
                mainAdapter?.refreshOrderItems(it)
            }

//            val priceStr = DecimalFormat("##.##").format(sharedViewModel.calcTotalDishesPrice())
//            dishAddonPrice.text = "$$priceStr"
        }
    }

    override fun onClearCart() {
//        sharedViewModel.clearCart()
        dismiss()
    }

    override fun onCancelClearCart() {
        dishAddonPlusMinus.updateCounterUiOnly(1)
    }

    private fun handleAdditionalDishes(additionalDishes: ArrayList<Dish>?) {
        additionalDishes?.let {
            mainAdapter?.refreshAdditionalDishes(it)
        }
    }


    private fun initUi() {
        mainAdapter = AdditionalDishMainAdapter(requireContext(), this, this)
        additionalDishDialogList.layoutManager = LinearLayoutManager(context)
        additionalDishDialogList.adapter = mainAdapter

        additionalDishDialogClose.setOnClickListener { dismiss() }
        dishAddonProceedBtn.setOnClickListener {
            dismiss()
            listener.onProceedToCheckout() }
    }

    override fun onAddBtnClick(dish: Dish) {
//        sharedViewModel.addNewDishToCart(dish.id, 1)

        mainAdapter?.removeDish(dish)
    }

    override fun onDishClick(dish: Dish) {
        listener.onDishClick(dish)
    }

    override fun onDishCountChange(orderItemsCount: Int, curOrderItem: OrderItem) {
//        if(orderItemsCount == 0){
//            sharedViewModel.pulItemBackToAdditionalList(curOrderItem)
//        }
//        sharedViewModel.updateOrder(curOrderItem)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onAdditionalDialogDismiss()
    }
}



