package com.bupp.wood_spoon_chef.common

import android.app.Dialog
import android.content.Context
import com.bupp.wood_spoon_chef.R

class WSProgressBar {

    private var dialog: Dialog? = null

    fun show(context: Context?) {
        if (dialog == null || dialog?.isShowing == false) {
            context?.let {
                dialog = Dialog(context).apply {
                    setContentView(R.layout.woodspoon_progress_bar)
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    show()
                }
            }
        }
    }

    fun dismiss() {
        dialog?.apply {
            if (isShowing) {
                dismiss()
            }
        }
    }

    companion object {
        var instance: WSProgressBar? = null

        fun instance() = instance ?: run {
            instance = WSProgressBar()
            instance
        }
    }
}