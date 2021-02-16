package com.bupp.wood_spoon_eaters.bottom_sheets.additional_dishes

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.additional_dishes_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdditionalDishesBottomSheet : BottomSheetDialogFragment(), OrderItemsAdapter.OrderItemsListener, AdditionalDishesAdapter.AdditionalDishesListener,
    ClearCartDialog.ClearCartDialogListener {

    private var mainAdapter: AdditionalDishMainAdapter? = null
    private lateinit var binding: AdditionalDishesDialogBinding
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    var listener: AdditionalDishesDialogListener? = null

    interface AdditionalDishesDialogListener{
        fun onProceedToCheckout()
        fun onDishClick(dish: Dish)
        fun onAdditionalDialogDismiss()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.additional_dishes_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AdditionalDishesDialogBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
        initObservers()
        
        mainViewModel.initAdditionalDishes()
    }


    private fun initObservers() {
        with(binding){
            mainViewModel.additionalDishesEvent.observe(viewLifecycleOwner, Observer { data ->
                handleData(data)
                additionalDishDialogList.scrollToPosition(0)
            })
//            mainViewModel.additionalDishes.observe(viewLifecycleOwner, Observer { additionalDishes ->
//                handleAdditionalDishes(additionalDishes)
//            })
//            mainViewModel.progressData.observe(viewLifecycleOwner, Observer { progress ->
//                when (progress) {
//                    true -> dishAddonPb.show()
//                    false -> dishAddonPb.hide()
//                }
//            })
            mainViewModel.emptyCartEvent.observe(viewLifecycleOwner, Observer { emptyCartEvent ->
                if(emptyCartEvent.shouldShow) {
                    ClearCartDialog(this@AdditionalDishesBottomSheet).show(childFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
                }
            })
        }

    }

    private fun handleData(data: NewOrderMainViewModel.AdditionalDishesEvent) {
        data.orderItems?.let {
            mainAdapter?.refreshOrderItems(it)
        }
        data.additionalDishes?.let{
            mainAdapter?.refreshAdditionalDishes(it)
        }
    }

    override fun onClearCart() {
//        mainViewModel.clearCart()
        dismiss()
    }

    override fun onCancelClearCart() {
//        binding.dishAddonPlusMinus.updateCounterUiOnly(1)
    }

//    private fun handleAdditionalDishes(additionalDishes: ArrayList<Dish>?) {
//        additionalDishes?.let {
//            mainAdapter?.refreshAdditionalDishes(it)
//        }
//    }


    private fun initUi() {
        mainAdapter = AdditionalDishMainAdapter(requireContext(), this, this)
        with(binding){
            additionalDishDialogList.layoutManager = LinearLayoutManager(context)
            additionalDishDialogList.adapter = mainAdapter

            additionalDishDialogClose.setOnClickListener { dismiss() }
            dishAddonProceedBtn.setOnClickListener {
                dismiss()
                listener?.onProceedToCheckout() }
        }
    }

    override fun onAddBtnClick(dish: Dish) {
//        mainViewModel.addNewDishToCart(dish.id, 1)

        mainAdapter?.removeDish(dish)
    }

    override fun onDishClick(dish: Dish) {
        listener?.onDishClick(dish)
    }

    override fun onDishCountChange(orderItemsCount: Int, curOrderItem: OrderItem) {
//        if(orderItemsCount == 0){
//            mainViewModel.pulItemBackToAdditionalList(curOrderItem)
//        }
//        mainViewModel.updateOrder(curOrderItem)
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onAdditionalDialogDismiss()
        super.onDismiss(dialog)
    }

}