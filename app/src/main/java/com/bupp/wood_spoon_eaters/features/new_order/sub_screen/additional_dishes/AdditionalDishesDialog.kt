package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.additional_dishes

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.AdditionalDishesDialogBinding
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdditionalDishesDialog : DialogFragment(R.layout.additional_dishes_dialog),
    ClearCartDialog.ClearCartDialogListener, AdditionalDishesAdapter.AdditionalDishesAdapterListener {

    //    private var mainAdapter: AdditionalDishMainAdapter? = null
    private var mainAdapter: AdditionalDishesAdapter? = null
    private lateinit var binding: AdditionalDishesDialogBinding
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    var isDismissed = true // if true - redirects to cook's profile and lock header when dismissed.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
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
        mainViewModel.additionalDishesEvent.observe(viewLifecycleOwner, { data ->
            handleData(data)
        })
        mainViewModel.clearCartEvent.observe(viewLifecycleOwner, { emptyCartEvent ->
            if (emptyCartEvent) {
                ClearCartDialog(this@AdditionalDishesDialog).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
            }
        })
        mainViewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                binding.additionalDishDialogPb.show()
            }else{
                binding.additionalDishDialogPb.hide()
            }
        })
    }

    private fun handleData(data: NewOrderMainViewModel.AdditionalDishesEvent) {
        Log.d(TAG, "handleData")
        val adapterData = mutableListOf<AdditionalDishData<Any>>()
        data.orderItems?.let {
            it.forEach {
                adapterData.add(AdditionalDishData(AdditionalDishesAdapter.VIEW_TYPE_ORDER_ITEM, it))
            }
        }

        data.additionalDishes?.let {
            if (it.isNotEmpty()) {
                adapterData.add(AdditionalDishData(AdditionalDishesAdapter.VIEW_TYPE_ADDITIONAL_HEADER, it[0]))
                it.forEach {
                    adapterData.add(AdditionalDishData(AdditionalDishesAdapter.VIEW_TYPE_ADDITIONAL, it))
                }

            }
        }
        mainAdapter?.submitList(adapterData)
    }

    private fun initUi() {
        Log.d(TAG, "initUi")
        mainAdapter = AdditionalDishesAdapter(requireContext(), this)
        with(binding) {
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
        mainViewModel.addNewDishToCart(dish, 1)
    }

    override fun onDishClick(dish: Dish) {
//        listener?.onDishClick(dish)
        mainViewModel.refreshFullDish(dish.menuItem?.id)
        mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.SINGLE_DISH_INFO)
        isDismissed = false
        dismiss()
    }

    override fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {
        Log.d(TAG, "onDishCountChange")
        if (isOrderItemsEmpty) {
            mainViewModel.showClearCartDialog()
        } else {
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
        Log.d(TAG, "onDismiss")
        if (isDismissed) {
            mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.LOCK_SINGLE_DISH_COOK)
        }
    }

    companion object {
        const val TAG = "wowAdditionalDishDialog"
    }


}