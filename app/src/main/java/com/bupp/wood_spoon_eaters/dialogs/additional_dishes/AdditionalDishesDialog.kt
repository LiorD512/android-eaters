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
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.*
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderSharedViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.additional_dishes_dialog.*
import kotlinx.android.synthetic.main.checkout_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdditionalDishesDialog(val listener: AdditionalDishesDialogListener) : DialogFragment(), AdditionalDishesAdapter.AdditionalDishesListener, OrderItemsAdapter.OrderItemsListener,
    ClearCartDialog.ClearCartDialogListener {

    interface AdditionalDishesDialogListener{
        fun onProceedToCheckout()
        fun onDishClick(dish: Dish)
        fun onAdditionalDialogDismiss()
    }

    //    private val dishSharedViewModel by sharedViewModel<SingleDishViewModel>()
    private val sharedViewModel by sharedViewModel<NewOrderSharedViewModel>()
//    private val viewModel by viewModel<AdditionalDishesViewModel>()

    private var mainAdapter: AdditionalDishMainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.additional_dishes_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)));
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
    }

    private fun handleOrderItems(order: Order?) {
        order?.let {
            it.orderItems.let {
                mainAdapter?.refreshOrderItems(it)
                if (it.size == 0) {
                    ClearCartDialog(this).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
                }
            }

            dishAddonPrice.text = "$${sharedViewModel.calcTotalDishesPrice().toString()}"
        }
    }

    override fun onClearCart() {
        sharedViewModel.clearCart()
        dismiss()
    }

    private fun handleAdditionalDishes(additionalDishes: ArrayList<Dish>?) {
        additionalDishes?.let {
            mainAdapter?.refreshAdditionalDishes(it)
        }
    }


    private fun initUi() {
        mainAdapter = AdditionalDishMainAdapter(context!!, this, this)
        additionalDishDialogList.layoutManager = LinearLayoutManager(context)
        additionalDishDialogList.adapter = mainAdapter

        additionalDishDialogClose.setOnClickListener { dismiss() }
        dishAddonProceedBtn.setOnClickListener {
            dismiss()
            listener.onProceedToCheckout() }
    }

    override fun onAddBtnClick(dish: Dish) {
        sharedViewModel.addNewDishToCart(dish.id, 1)
    }

    override fun onDishClick(dish: Dish) {
        listener.onDishClick(dish)
    }

    override fun onDishCountChange(orderItemsCount: Int, curOrderItem: OrderItem) {
        sharedViewModel.updateOrder(curOrderItem)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
//        listener.onAdditionalDialogDismiss()
    }
}



