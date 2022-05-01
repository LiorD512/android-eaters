package com.bupp.wood_spoon_chef.utils.extensions

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.remote.network.base.*

fun MTError.getErrorMsgByType(): String {
    when (this) {
        is FormattedError -> {
            if (this.hasWsError()) {
                return this.errors.getErrorsString()
            } else if (this.message.isNotEmpty()) {
                return this.message
            }
            return "Ops! something went wrong \nPlease try again later"
        }
        is HTTPError -> {
            return "Ops! something went wrong \nPlease try again later"
        }
        is JsonError -> {
            return "Ops! something went wrong \nPlease try again later"
        }
        is NetworkError -> {
            return "Ops! NetworkError \nPlease check your network connection and try again"
        }
        is UnknownError -> {
            return "Ops! something went wrong \nPlease try again later"
        }
        is CustomError -> {
            return this.message
        }
    }
}

fun List<WSError>?.getErrorsString(): String {
    this?.let { list ->
        var errorStr = ""
        list.forEach {
            errorStr += "${it.msg} \n"
        }
        return errorStr
    }
    return "No WsErrors Received"
}

fun Activity.showErrorToast(title: String, anchorView: ViewGroup, length: Int = Toast.LENGTH_SHORT) {
    val customLayout = layoutInflater.inflate(R.layout.error_toast, anchorView, false)
    val titleView = customLayout.findViewById<TextView>(R.id.errorTitle)
    titleView.text = title
    val toast = Toast(this)
    toast.duration = length
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)
    toast.view = customLayout
    toast.show()
}

fun Fragment.showErrorToast(title: String, anchorView: ViewGroup, length: Int = Toast.LENGTH_SHORT) {
    val customLayout = layoutInflater.inflate(R.layout.error_toast, anchorView, false)
    val titleView = customLayout.findViewById<TextView>(R.id.errorTitle)
    titleView.text = title.trim()
    val toast = Toast(requireContext())
    toast.duration = length
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 50)
    toast.view = customLayout
    toast.show()
}