package com.bupp.wood_spoon_eaters.bottom_sheets.support_center

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.SupportCenterBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SupportCenterBottomSheet: BottomSheetDialogFragment(), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener {

    private var binding: SupportCenterBottomSheetBinding? = null
    private val viewModel: SupportViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.support_center_bottom_sheet, container, false)
        binding = SupportCenterBottomSheetBinding.bind(view)
        return view
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
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

//        Analytics.with(requireContext()).screen("Support center")
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_SUPPORT_CENTER)

        initUI()
    }

    private fun initUI() {
        with(binding!!){
            supportDialogHeader.setHeaderViewListener(this@SupportCenterBottomSheet)
            supportDialogNext.setBtnEnabled(false)
            supportDialogCommentInput.setWSCounterListener(this@SupportCenterBottomSheet)

            supportDialogCallButton.setOnClickListener {
                (activity as MainActivity).onContactUsClick()
            }
            supportDialogTextButton.setOnClickListener {
                (activity as MainActivity).sendSmsText()
            }
            supportDialogQA.setOnClickListener{ openQaUrl()}
            supportDialogNext.setOnClickListener { sendMail() }
        }
    }

    private fun openQaUrl() {
        WebDocsDialog(Constants.WEB_DOCS_QA).show(childFragmentManager, Constants.WEB_DOCS_DIALOG)
    }

    private fun sendMail() {
        val text = binding!!.supportDialogCommentInput.getText()
        val address = viewModel.getAdminMailAddress()

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.getEmailSubject())
        emailIntent.putExtra(Intent.EXTRA_TEXT, text)
        emailIntent.selector = selectorIntent

        activity?.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }


    override fun onInputTitleChange(str: String?) {
        with(binding!!){
            if (str.isNullOrEmpty()) {
                supportDialogNext.setBtnEnabled(false)
            } else {
                supportDialogNext.setBtnEnabled(true)
            }
        }
    }

    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}