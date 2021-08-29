

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FreeTextBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.ToolTipBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ToolTipBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ToolTipBottomSheetBinding

    companion object {
        private const val TOOL_TIP_TITLE = "tool_tip_title"
        private const val TOOL_TIP_BODY = "tool_tip_body"
        fun newInstance(title: String, body: String): ToolTipBottomSheet {
            return ToolTipBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(TOOL_TIP_TITLE, title)
                    putString(TOOL_TIP_BODY, body)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tool_tip_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

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

        binding = ToolTipBottomSheetBinding.bind(view)

        arguments?.let {
            val title = it.getString(TOOL_TIP_TITLE) ?: ""
            val body = it.getString(TOOL_TIP_BODY)
            binding.toolTipTitle.text = title
            binding.toolTipBody.text = body
        }

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
    }

    private fun initUI() {
        with(binding) {
            toolTipBtn.setOnClickListener {
                dismiss()
            }
        }
    }

}

