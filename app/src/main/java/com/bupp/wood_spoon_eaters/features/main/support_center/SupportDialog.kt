package com.bupp.wood_spoon_eaters.features.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.support_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel




class SupportDialog : Fragment(), HeaderView.HeaderViewListener, InputTitleView.InputTitleViewListener {

//    val viewModel by viewModel<SupportViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.support_dialog, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initUI()
//    }
//
//    private fun initUI() {
//        supportDialogTitle.setHeaderViewListener(this)
//        supportDialogTitle.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK, getString(R.string.support_dialog_title))
//
//        supportDialogNext.setBtnEnabled(false)
//        supportDialogCommentInput.setInputTitleViewListener(this)
//
//        supportDialogCallButton.setOnClickListener {
//            (activity as MainActivity).callPhoneNumber()
//        }
//        supportDialogTextButton.setOnClickListener {
//            (activity as MainActivity).sendSmsText()
//        }
//    }
//
//    override fun onHeaderBackClick() {
//        dismiss()
//    }
//
    override fun onInputTitleChange(str: String?) {
        if (supportDialogCommentInput.isValid()) {
            supportDialogNext.setBtnEnabled(true)
        } else {
            supportDialogNext.setBtnEnabled(false)
        }
    }
}
