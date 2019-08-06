package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import kotlinx.android.synthetic.main.certificates_dialog.*


class CertificatesDialog(val certificates: ArrayList<String>) : DialogFragment(), HeaderView.HeaderViewListener {



    companion object {
        fun newInstance(certificates: ArrayList<String>) = CertificatesDialog(certificates)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.certificates_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        certificatesHeader.setHeaderViewListener(this)

        certificatesList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        certificatesList.addItemDecoration(divider)
        var adapter =
            CertificatesAdapter(
                context!!,
                certificates
            )
        certificatesList.adapter = adapter
    }

    override fun onHeaderBackClick() {
        dismiss()
    }


}
