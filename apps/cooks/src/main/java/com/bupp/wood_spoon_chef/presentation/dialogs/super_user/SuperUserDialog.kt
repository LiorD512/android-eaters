package com.bupp.wood_spoon_chef.presentation.dialogs.super_user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.databinding.DialogSuperUserBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SuperUserDialog : BaseDialogFragment(R.layout.dialog_super_user) {

    var listener: SuperUserListener? = null

    interface SuperUserListener {
        fun onEnvironmentChanged(forceRestart: Boolean? = false)
    }

    private var binding: DialogSuperUserBinding? = null
    val viewModel: SuperUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = DialogSuperUserBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding?.apply {
            superUserDialogBtn.setOnClickListener {
                val input = superUserDialogInput.getText()
                updateEnv(input)
            }
            superUserDialogReset.setOnClickListener {
                updateEnv("")
            }
            superUserDialogLog.setOnClickListener {
                MTLogger.instance?.getCachedLog()?.let { logs -> sendMailWithLogs(logs) }
            }
        }
    }

    private fun sendMailWithLogs(msg: String) {
        //todo create util method for sending email in this way ! - most correct one
        val title = "WoodSpoonChefs - version:${BuildConfig.VERSION_NAME}, ErrorLogs"
        val address = "nicole@monkeytech.co.il"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "message/rfc822"
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        startActivity(Intent.createChooser(intent, "Send mail using..."))
    }

    private fun updateEnv(env: String) {
        Log.d(TAG, "updateEnv with $env")
        viewModel.setEnvironment(env)
        listener?.onEnvironmentChanged(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SuperUserListener) {
            listener = context
        } else if (parentFragment is SuperUserListener) {
            this.listener = parentFragment as SuperUserListener
        } else {
            throw RuntimeException("$context must implement SuperUserListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        const val TAG = "wowSuperUserDialog"
    }

}