package com.bupp.wood_spoon_eaters.dialogs.order_date_chooser

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import kotlinx.android.synthetic.main.nationwide_shipping_chooser_dialog.*


class NationwideShippingChooserDialog() : DialogFragment(), NationwideShippingChooserAdapter.NationwideShippingAdapterListener {

    private var newSelectedItem: ShippingMethod? = null
    private lateinit var adapter: NationwideShippingChooserAdapter
    private var listener: NationwideShippingChooserListener? = null

    interface NationwideShippingChooserListener {
        fun onShippingMethodChoose(choosenShippingMethod: ShippingMethod)
    }

    private var shippingMethods: ArrayList<ShippingMethod>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
        arguments?.let {
            shippingMethods = it.getParcelableArrayList(DIALOG_ARGS)
        }
    }

    companion object {
        const val DIALOG_ARGS = "shippingData"
        @JvmStatic
        fun newInstance(shippingMethods: ArrayList<ShippingMethod>) =
            NationwideShippingChooserDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(DIALOG_ARGS, shippingMethods)
                }
            }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.nationwide_shipping_chooser_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        nationwideShippingChooserDialogBkg.setOnClickListener {
            dismiss()
        }
        nationwideShippingChooserDialogClose.setOnClickListener {
            dismiss()
        }

        adapter = NationwideShippingChooserAdapter(context!!, this)
        nationwideShippingChooserDialogRecycler.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider))
        nationwideShippingChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
        nationwideShippingChooserDialogRecycler.adapter = adapter

        shippingMethods?.let{
            adapter.submitList(it.toList())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NationwideShippingChooserListener) {
            listener = context
        }
        else if (parentFragment is NationwideShippingChooserListener){
            this.listener = parentFragment as NationwideShippingChooserListener
        }
        else {
            throw RuntimeException(context.toString() + " must implement NationwideShippingAdapterListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onShippingMethodClick(selected: ShippingMethod) {
        listener?.onShippingMethodChoose(selected)
        dismiss()
    }

}