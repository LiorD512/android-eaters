//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.telephony.PhoneNumberFormattingTextWatcher
//import android.text.Editable
//import android.text.InputType
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
//import kotlinx.android.synthetic.main.input_with_error_text_field_view.view.*
//
//class InputWithErrorTextField @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    FrameLayout(context, attrs, defStyleAttr) {
//
//    private var listener: InputWithErrorTextFieldListener? = null
//    interface InputWithErrorTextFieldListener{
//        fun afterTextChanged(str: String)
//    }
//
//    init {
//        LayoutInflater.from(context).inflate(R.layout.input_with_error_text_field_view, this, true)
//        attrs.let {
//            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.InputWithErrorTextFieldView)
//            if (attrArray.hasValue(R.styleable.InputWithErrorTextFieldView_hint)) {
//                val hint = attrArray.getString(R.styleable.InputWithErrorTextFieldView_hint)
//                inputWithErrorViewInput.hint = hint
//            }
//            if (attrArray.hasValue(R.styleable.InputWithErrorTextFieldView_error)) {
//                val error = attrArray.getString(R.styleable.InputWithErrorTextFieldView_error)
//                inputWithErrorViewError.text = error
//            }
//            if (attrArray.hasValue(R.styleable.InputWithErrorTextFieldView_inputType)) {
//                val inputType = attrArray.getInt(R.styleable.InputWithErrorTextFieldView_inputType, -1)
//               updateInputType(inputType)
//            }
//            attrArray.recycle()
//        }
//
//        initUi()
//    }
//
//    private fun updateInputType(inputType: Int) {
//        when(inputType){
//            Constants.INPUT_TYPE_PHONE -> {
//                inputWithErrorViewInput.inputType = InputType.TYPE_CLASS_PHONE
//                inputWithErrorViewInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
//            }
//        }
//    }
//
//    private fun initUi() {
//        inputWithErrorViewInput.addTextChangedListener(object: SimpleTextWatcher(){
//            override fun afterTextChanged(s: Editable) {
//                listener?.let{
//                    val str = s.toString()
//                    it.afterTextChanged(s.toString())
//                    if(str.isNotEmpty()){
//                        hideError()
//                    }
//                }
//            }
//        })
//
//        inputWithErrorViewInput.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) {
//                verificationFragmentInputLayout.elevation = 18f
//            } else {
//                verificationFragmentInputLayout.elevation = 8f
//            }
//        }
//    }
//
//    fun registerListener(listener: InputWithErrorTextFieldListener){
//        this.listener = listener
//    }
//
//    fun showError() {
//        inputWithErrorViewError.visibility = View.VISIBLE
//    }
//
//    fun hideError() {
//        inputWithErrorViewError.visibility = View.GONE
//    }
//
//
//}
//
