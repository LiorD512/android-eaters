package com.bupp.wood_spoon_chef.presentation.features.base

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_chef.common.WSProgressBar
import com.bupp.wood_spoon_chef.di.abs.LiveEvent
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError
import com.bupp.wood_spoon_chef.utils.extensions.getErrorMsgByType
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.shipbook.shipbooksdk.ShipBook

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        ShipBook.screen(this.javaClass.simpleName)
        super.onCreate(savedInstanceState)
    }

    fun handleErrorEvent(errorEvent: LiveEvent<MTError>, viewGroup: ViewGroup?) {
        errorEvent.getContentIfNotHandled()?.let { mtError ->
            viewGroup?.let {
                showErrorToast(mtError.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
            }
        }
    }

    fun handleErrorEvent(error: MTError, viewGroup: ViewGroup?) {
        viewGroup?.let {
            showErrorToast(error.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
        }
    }

    fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            WSProgressBar.instance()?.show(requireContext())
        } else {
            WSProgressBar.instance()?.dismiss()
        }
    }

    override fun onDestroyView() {
        clearClassVariables()
        super.onDestroyView()
    }

    abstract fun clearClassVariables()

}

abstract class BaseDialogFragment(@LayoutRes contentLayoutId: Int) :
    DialogFragment(contentLayoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        ShipBook.screen(this.javaClass.simpleName)
        super.onCreate(savedInstanceState)
    }

    open fun handleErrorEvent(errorEvent: LiveEvent<MTError>, viewGroup: ViewGroup?) {
        errorEvent.getContentIfNotHandled()?.let { mtError ->
            viewGroup?.let {
                showErrorToast(mtError.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
            }
        }
    }

    open fun handleErrorEvent(error: MTError, viewGroup: ViewGroup?) {
        viewGroup?.let {
            showErrorToast(error.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
        }
    }

    open fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            WSProgressBar.instance()?.show(requireContext())
        } else {
            WSProgressBar.instance()?.dismiss()
        }
    }

    override fun onDestroyView() {
        clearClassVariables()
        super.onDestroyView()
    }

    abstract fun clearClassVariables()

}

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ShipBook.screen(this.javaClass.simpleName)
        super.onCreate(savedInstanceState)
    }

    open fun handleErrorEvent(errorEvent: LiveEvent<MTError>, viewGroup: ViewGroup?) {
        errorEvent.getContentIfNotHandled()?.let { mtError ->
            viewGroup?.let {
                showErrorToast(mtError.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
            }
        }
    }

    open fun handleErrorEvent(error: MTError, viewGroup: ViewGroup?) {
        viewGroup?.let {
            showErrorToast(error.getErrorMsgByType(), viewGroup, Toast.LENGTH_LONG)
        }
    }

    open fun handleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            WSProgressBar.instance()?.show(requireContext())
        } else {
            WSProgressBar.instance()?.dismiss()
        }
    }

    override fun onDestroyView() {
        clearClassVariables()
        super.onDestroyView()
    }

    abstract fun clearClassVariables()

}

