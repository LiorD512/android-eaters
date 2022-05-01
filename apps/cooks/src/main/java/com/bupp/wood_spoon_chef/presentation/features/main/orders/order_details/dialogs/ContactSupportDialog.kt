package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.BottomSheetDialogContactSupportBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ContactSupportDialog(
    var listener: ContactSupportClickListener
) : BottomSheetDialogFragment() {

    private var binding: BottomSheetDialogContactSupportBinding? = null

    interface ContactSupportClickListener {
        fun onContactSupportClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_dialog_contact_support, container, false)
        binding = BottomSheetDialogContactSupportBinding.bind(view)
        binding?.btnSaveSlot?.setOnClickListener {
            listener.onContactSupportClick()
            this.dismiss()
        }
        isCancelable = true
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is ContactSupportClickListener -> {
                listener = context
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.popup_window_transparent)
    }

}
