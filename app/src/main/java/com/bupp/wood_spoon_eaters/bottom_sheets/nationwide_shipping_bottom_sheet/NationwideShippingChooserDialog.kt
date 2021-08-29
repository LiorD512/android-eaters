package com.bupp.wood_spoon_eaters.bottom_sheets.nationwide_shipping_bottom_sheet

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.NationwideShippingChooserDialogBinding
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NationwideShippingChooserDialog :  BottomSheetDialogFragment(), NationwideShippingChooserAdapter.NationwideShippingAdapterListener {

    private val binding: NationwideShippingChooserDialogBinding by viewBinding()
    private var adapter: NationwideShippingChooserAdapter? = null
    private var listener: NationwideShippingChooserListener? = null

    interface NationwideShippingChooserListener {
        fun onShippingMethodChoose(chosenShippingMethod: ShippingMethod)
    }

    private var shippingMethods: List<ShippingMethod>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTransparentStyle)
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
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding){
            nationwideShippingChooserDialogBkg.setOnClickListener {
                dismiss()
            }
            nationwideShippingChooserDialogClose.setOnClickListener {
                dismiss()
            }

            adapter = NationwideShippingChooserAdapter(requireContext(), this@NationwideShippingChooserDialog)
            nationwideShippingChooserDialogRecycler.layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            nationwideShippingChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
            nationwideShippingChooserDialogRecycler.adapter = adapter

            shippingMethods?.let{
                adapter?.submitList(it.toList())
            }
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
            throw RuntimeException("$context must implement NationwideShippingAdapterListener")
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

    override fun onDestroyView() {
    adapter = null
    super.onDestroyView()
    }

}