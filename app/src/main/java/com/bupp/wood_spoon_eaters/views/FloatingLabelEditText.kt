//package com.bupp.wood_spoon_eaters.views
//
//import android.content.Context
//import android.telephony.PhoneNumberFormattingTextWatcher
//import android.text.Editable
//import android.text.InputType
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import android.view.inputmethod.EditorInfo
//import android.widget.LinearLayout
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
//import com.bupp.wood_spoon_eaters.databinding.FloatingLabelEditTextBinding
//import com.bupp.wood_spoon_eaters.utils.AnimationUtil
//import render.animations.Attention
//import render.animations.Render
//
//class FloatingLabelEditText @JvmOverloads
//constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//    LinearLayout(context, attrs, defStyleAttr) {
//
//    private var binding: FloatingLabelEditTextBinding = FloatingLabelEditTextBinding.inflate(LayoutInflater.from(context), this, true)
//
//    init {
//         initUi(attrs)
//    }
//
//    private fun initUi(attrs: AttributeSet?) {
//        attrs?.let{
//
//            val attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabelEditText)
//
//            val hint = attr.getString(R.styleable.FloatingLabelEditText_hint)
//            binding.floatingLabelText.setHint(hint)
//
//            val error = attr.getString(R.styleable.FloatingLabelEditText_error)
//            error?.let { setError(error) }
//
//            inputType = attr.getInt(R.styleable.FloatingLabelEditText_inputType, 0)
//            setInputType(inputType)
//
////            val icon = attr.getDrawable(R.styleable.FloatingLabelEditText_floatingIcon)
////            setIcon(icon)
//
//            attr.recycle()
//
//
//            binding.floatingLabelInput.addTextChangedListener(object : SimpleTextWatcher() {
//                override fun afterTextChanged(editable: Editable) {
//                    eventListener?.onFieldInputChange(this@FloatingLabelEditText)
//                    hideError()
//                }
//            })
//
//            binding.floatingLabelInput.setOnFocusChangeListener { v, hasFocus ->
//                binding.floatingLabelBkg.isSelected = hasFocus
//            }
//        }
//    }
//
//    private var inputType: Int = 0
//
//    interface FloatingLabelEventListener {
//        fun onFieldInputChange(curView: FloatingLabelEditText)
//    }
//
//    private var eventListener: FloatingLabelEventListener? = null
//    fun setEventListener(eventListener: FloatingLabelEventListener) {
//        this.eventListener = eventListener
//    }
//
//    fun showError() {
//        animateError()
//        binding.floatingLabelErrorIcon.visibility = View.VISIBLE
//        binding.floatingLabelErrorText.visibility = View.VISIBLE
//    }
//
//    private fun animateError() {
//        val render = Render(context)
//        render.setDuration(450)
//        render.setAnimation(AnimationUtil().Shake(binding.floatingLabelBkg))
//        render.start()
//    }
//
//    fun hideError() {
//        binding.floatingLabelErrorIcon.visibility = View.GONE
//        binding.floatingLabelErrorText.visibility = View.GONE
//    }
//
//    fun setError(error: String) {
//        binding.floatingLabelErrorText.text = error
//    }
//
//    fun setTextColor(color: Int) {
//        binding.floatingLabelInput.setHintTextColor(color)
//    }
//
//    private fun setInputType(type: Int) {
//        when (type) {
//            Constants.INPUT_TYPE_PHONE -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_CLASS_PHONE
//                binding.floatingLabelInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
//            }
//            Constants.INPUT_TYPE_FULL_NAME -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
//            }
//            Constants.INPUT_TYPE_TEXT -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_CLASS_TEXT
//            }
//            Constants.INPUT_TYPE_TEXT_WITH_NUMBER -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            }
//            Constants.INPUT_TYPE_NUMBER -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_CLASS_NUMBER
//            }
//            Constants.INPUT_TYPE_MAIL -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            }
//            Constants.INPUT_TYPE_LONG_TEXT -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
//            }
//            Constants.INPUT_TYPE_TEXT_NO_TITLE -> {
//                binding.floatingLabelInput.inputType = InputType.TYPE_CLASS_TEXT
//            }
//            Constants.INPUT_TYPE_DONE_BTN -> {
//                binding.floatingLabelInput.imeOptions = EditorInfo.IME_ACTION_DONE
//            }
//        }
//    }
//
//    fun getText(): String {
//        return binding.floatingLabelInput.text.toString()
//    }
//
//    fun setText(text: String) {
//        binding.floatingLabelInput.setText(text)
//    }
//
//}
