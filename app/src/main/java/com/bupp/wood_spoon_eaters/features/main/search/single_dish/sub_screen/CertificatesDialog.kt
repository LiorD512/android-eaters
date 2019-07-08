package com.bupp.wood_spoon_eaters.features.main.search.single_dish.sub_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import com.bupp.wood_spoon_eaters.features.main.search.single_dish.CertificatesAdapter
import kotlinx.android.synthetic.main.certificates_dialog.*


class CertificatesDialog(val certificates: ArrayList<String>) : DialogFragment() {



    companion object {
        fun newInstance(certificates: ArrayList<String>) = CertificatesDialog(certificates)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_dish_fragment_dialog_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        certificatesList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        singleDishIngredientList.addItemDecoration(divider)
        var adapter = CertificatesAdapter(context!!, certificates)
        singleDishIngredientList.adapter = adapter
    }




}
