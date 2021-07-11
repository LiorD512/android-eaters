package com.bupp.wood_spoon_eaters.bottom_sheets.join_as_chef

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.JoinAsChefBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.SupportCenterBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.viewModel

class JoinAsChefBottomSheet: BottomSheetDialogFragment(), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener {

    private val binding: JoinAsChefBottomSheetBinding by viewBinding()
//    private val viewModel: SupportViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.join_as_chef_bottom_sheet, container, false)
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

        initUI()
    }

    private fun initUI() {
        with(binding){
            joinAsChefNext.setOnClickListener {
                openWoodSpoonGooglePlay()
            }

            joinAsChefHeader.setHeaderViewListener(this@JoinAsChefBottomSheet)
        }
    }

    private fun openWoodSpoonGooglePlay() {
        val url = getString(R.string.wood_spoon_chefs_play_store_url)
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    override fun onHeaderBackClick() {
        dismiss()
    }


}