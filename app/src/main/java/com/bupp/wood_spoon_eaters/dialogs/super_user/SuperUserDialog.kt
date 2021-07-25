package com.bupp.wood_spoon_eaters.dialogs.super_user

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.databinding.DialogSuperUserBinding
import com.bupp.wood_spoon_eaters.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class SuperUserDialog : DialogFragment() {

    var listener: SuperUserListener? = null
    interface SuperUserListener{
        fun onEnvironmentChanged(forceRestart: Boolean? = false)
    }

    private val binding: DialogSuperUserBinding by viewBinding()
    val viewModel: SuperUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_super_user, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding) {
            superUserDialogBtn.setOnClickListener {
                val input = superUserDialogInput.getText()
                input?.let { it1 -> updateEnv(it1) }

                val baseUrlInput = superUserDialogInput.getText()
                baseUrlInput?.let { it1 -> updateBaseUrl(it1) }
            }
            superUserDialogReset.setOnClickListener {
                updateEnv("")
            }
            superUserDialogLog.setOnClickListener {
                MTLogger.instance?.getCachedLog(superUserDialogGravy.isChecked)?.let { it1 -> Utils.shareText(requireActivity(), it1) }
            }
        }
    }

    private fun updateEnv(env: String){
        Log.d(TAG, "updateEnv with $env")
        viewModel.setEnvironment(env)
        listener?.onEnvironmentChanged(true)
    }

    private fun updateBaseUrl(baseUrl: String){
        Log.d(TAG, "updateBaseUrl with $baseUrl")
        viewModel.updateBaseUrl(baseUrl)
        listener?.onEnvironmentChanged(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SuperUserListener) {
            listener = context
        }
        else if (parentFragment is SuperUserListener){
            this.listener = parentFragment as SuperUserListener
        }
        else {
            throw RuntimeException("$context must implement SuperUserListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "wowSuperUserDialog"
    }

}