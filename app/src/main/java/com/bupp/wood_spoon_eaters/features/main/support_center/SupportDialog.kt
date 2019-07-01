package com.bupp.wood_spoon_eaters.features.main.support_center

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.support_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel


class SupportDialog : DialogFragment(), HeaderView.HeaderViewListener {

    val viewModel by viewModel<SupportViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.support_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setTitleText()

    }

    private fun setTitleText() {
//        supportDialogTitle.setHeaderViewListener(this)
//        supportDialogTitle.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK, getString(R.string.code_fragment_title))
    }

    override fun onHeaderBackClick() {
        super.onHeaderBackClick()
        dismiss()
    }
}
