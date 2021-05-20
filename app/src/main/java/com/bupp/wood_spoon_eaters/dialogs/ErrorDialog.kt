//package com.bupp.wood_spoon_eaters.dialogs
//
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import com.bupp.wood_spoon_eaters.R
//import kotlinx.android.synthetic.main.error_dialog.*
//
//class ErrorDialog: DialogFragment(){
//
//    private var msg: String? = ""
//
//    companion object {
//        private val MSG_PARAM = "msg"
//
//        fun newInstance(msg: String): ErrorDialog {
//            val fragment = ErrorDialog()
//            val args = Bundle()
//            args.putString(MSG_PARAM, msg)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.error_dialog, null)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
//        arguments?.let{
//            msg = requireArguments().getString(MSG_PARAM)
//        }
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initUi()
//}
//
//    private fun initUi() {
//        msg?.let{
//            errorDialogMsg.text = it
//        }
//        errorDialogCloseBtn.setOnClickListener{
//            dismiss()}
//        errorDialogBkg.setOnClickListener{
//            dismiss()}
//    }
//
//}