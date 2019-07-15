package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.adapters.RateLastOrderAdapter
import kotlinx.android.synthetic.main.rate_last_order_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel

class RateLastOrderDialog(val listener: RateLastOrderDialogListener) : DialogFragment(),
    InputTitleView.InputTitleViewListener, RateLastOrderAdapter.RateLastOrderAdapterListener {


    private var accuracyWasGood: Boolean? = null
    private var deliveryWasGood: Boolean? = null
    private var isRated: Boolean = false
    private lateinit var adapter: RateLastOrderAdapter
    val viewModel by viewModel<RateLastOrderViewModel>()


    interface RateLastOrderDialogListener {
        fun onDoneRateClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.rate_last_order_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        rateLastOrderDialogNotes.setInputTitleViewListener(this)
        rateLastOrderDialogCloseBtn.setOnClickListener { dismiss() }
        rateLastOrderDialogAccuracyPositive.setOnClickListener {
            rateLastOrderDialogAccuracyPositive.isSelected = true
            rateLastOrderDialogAccuracyNegative.isSelected = false

            this.accuracyWasGood = true

            this.isRated = true
            validateFields()
        }

        rateLastOrderDialogAccuracyNegative.setOnClickListener {
            rateLastOrderDialogAccuracyPositive.isSelected = false
            rateLastOrderDialogAccuracyNegative.isSelected = true

            this.accuracyWasGood = false

            this.isRated = true
            validateFields()
        }

        rateLastOrderDialogDeliveryPositive.setOnClickListener {
            rateLastOrderDialogDeliveryPositive.isSelected = true
            rateLastOrderDialogDeliveryNegative.isSelected = false

            this.deliveryWasGood = true

            this.isRated = true
            validateFields()
        }

        rateLastOrderDialogDeliveryNegative.setOnClickListener {
            rateLastOrderDialogDeliveryPositive.isSelected = false
            rateLastOrderDialogDeliveryNegative.isSelected = true

            this.deliveryWasGood = false

            this.isRated = true
            validateFields()
        }

        rateLastOrderDialogDoneBtn.setBtnEnabled(false)

        rateLastOrderDialogDoneBtn.setOnClickListener {
            onDoneClick()
            listener.onDoneRateClick()
        }

        viewModel.orderDetails.observe(this, Observer { orderDetails ->
            handleOrderDetails(orderDetails)
        })

        viewModel.getDumbOrderDetails()
    }

    private fun handleOrderDetails(orderDetails: RateLastOrderViewModel.OrderDetails?) {
        if (orderDetails != null) {

            rateLastOrderDialogDishesRecyclerView.layoutManager = LinearLayoutManager(context)
            adapter = RateLastOrderAdapter(context!!, orderDetails.orders, this)
            rateLastOrderDialogDishesRecyclerView.adapter = adapter



            rateLastOrderDialogUserImage.setImage(orderDetails.orders[0].dish.cook!!.thumbnail!!)
            rateLastOrderDialogUserName.text = orderDetails.orders[0].dish.cook!!.getFullName()
        }
    }

    override fun onDishRate() {
        isRated = true
        validateFields()
    }

    override fun onInputTitleChange(str: String?) {
        isRated = !str.isNullOrEmpty() and isRated
        validateFields()
    }

    private fun validateFields() {
        rateLastOrderDialogDoneBtn.setBtnEnabled(isRated)

        if (isRated) {
            rateLastOrderDialogDoneBtn.alpha = 1f
        } else {
            rateLastOrderDialogDoneBtn.alpha = 0.3f
        }
    }

    private fun onDoneClick() {
        Toast.makeText(context, "Rated Accuracy as $accuracyWasGood", Toast.LENGTH_SHORT).show()
        Toast.makeText(context, "Rated Delivery as $deliveryWasGood", Toast.LENGTH_SHORT).show()


        var hashMap = adapter.getRatedDishes()

        for (orderItem in hashMap) {
            Toast.makeText(context,
                "Rated dish ''${orderItem.key.dish.name}'' as ${orderItem.value}",
                Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(context, "Notes: ${rateLastOrderDialogNotes.getText()}", Toast.LENGTH_SHORT).show()

    }
}