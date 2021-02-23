package com.bupp.wood_spoon_eaters.views

import android.animation.ObjectAnimator
import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.databinding.WsEditTextBinding

class WSEditText @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: WsEditTextBinding = WsEditTextBinding.inflate(LayoutInflater.from(context), this, true)
    private var isEditable = false

    init {
         initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            with(binding){
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)

                val hint = attr.getString(R.styleable.WSEditText_hint)
                wsEditTextInput.hint = hint

                val error = attr.getString(R.styleable.WSEditText_error)
                error?.let { setError(error) }

                inputType = attr.getInt(R.styleable.WSEditText_inputType, 0)
                setInputType(inputType)

                val isEditable = attr.getBoolean(R.styleable.WSEditText_isEditable, true)
                wsEditTextInput.isFocusable = isEditable
                wsEditTextInput.isClickable = isEditable
                this@WSEditText.isEditable = isEditable


                attr.recycle()


                wsEditTextInput.addTextChangedListener(object : SimpleTextWatcher() {
                    override fun afterTextChanged(s: Editable) {
                        hideError()
                        if (s.isEmpty()) {
                            val face = ResourcesCompat.getFont(context, R.font.lato_reg)
                            wsEditTextInput.typeface = face
                            wsEditTextPrefix.typeface = face
                        }else{
                            val face = ResourcesCompat.getFont(context, R.font.lato_bold)
                            wsEditTextInput.typeface = face
                            wsEditTextPrefix.typeface = face
                        }
                        super.afterTextChanged(s)
                    }
                })
                wsEditTextInput.setOnFocusChangeListener(object: OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        if(hasFocus){
                            wsEditTextUnderline.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_blue))
                        }else{
                            wsEditTextUnderline.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                        }
                    }

                })
            }


        }
    }

    private var inputType: Int = 0

    fun showError() {
        with(binding){
            if(wsEditTextErrorIcon.alpha > 0){
                animateErrorIconBounce()
            }else{
                animateErrorIconEntrance()
                animateErrorTextEntrance()
//                wsEditTextErrorText.visibility = View.VISIBLE

            }
        }
    }

    private fun animateErrorTextEntrance() {
        ObjectAnimator.ofFloat(
            binding.wsEditTextErrorText, "translationY",
            -50f, 0f,
        ).apply {
            duration = 1000
            interpolator = BounceInterpolator()
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            binding.wsEditTextErrorText, "alpha",
            0f, 1f,
        ).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

    private fun animateErrorIconEntrance() {
        ObjectAnimator.ofFloat(
            binding.wsEditTextErrorIcon, "translationX",
            50f, 0f,
        ).apply {
            duration = 1000
            interpolator = BounceInterpolator()
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            binding.wsEditTextErrorIcon, "alpha",
            0f, 1f,
        ).apply {
            duration = 250
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

    private fun animateErrorIconBounce() {
        ObjectAnimator.ofFloat(
            binding.wsEditTextErrorIcon, "translationY",
            0f, -5f, -15f, 0f,
        ).apply {
            duration = 1000
            interpolator = BounceInterpolator()
            repeatCount = 0
            start()
        }
    }

    private fun animateVerifiedIcon() {
        val measured_height = binding.wsEditTextCheck.measuredHeight.toFloat()
        ObjectAnimator.ofFloat(
            binding.wsEditTextCheck, "translationY",
            measured_height, -30f, 5f, 0f,
        ).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }

        ObjectAnimator.ofFloat(
            binding.wsEditTextCheck, "alpha",
            0f, 1f,
        ).apply {
            duration = 500
            interpolator = AccelerateInterpolator()
            repeatCount = 0
            start()
        }
    }

    fun hideError() {
        if(binding.wsEditTextErrorIcon.alpha > 0){
            ObjectAnimator.ofFloat(
                binding.wsEditTextErrorIcon, "alpha",
                1f, 0f,
            ).apply {
                duration = 250
                interpolator = AccelerateInterpolator()
                repeatCount = 0
                start()
            }
            ObjectAnimator.ofFloat(
                binding.wsEditTextErrorText, "alpha",
                1f, 0f,
            ).apply {
                duration = 250
                interpolator = AccelerateInterpolator()
                repeatCount = 0
                start()
            }
        }

    }

    fun setError(error: String) {
        binding.wsEditTextErrorText.text = error
    }

    fun setTextColor(color: Int) {
        binding.wsEditTextInput.setHintTextColor(color)
    }

    private fun setInputType(type: Int) {
        when (type) {
            Constants.INPUT_TYPE_PHONE -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_PHONE
                binding.wsEditTextInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            Constants.INPUT_TYPE_FULL_NAME -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            }
            Constants.INPUT_TYPE_TEXT -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_TEXT
            }
            Constants.INPUT_TYPE_TEXT_WITH_NUMBER -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            Constants.INPUT_TYPE_NUMBER -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_NUMBER
            }
            Constants.INPUT_TYPE_MAIL -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            Constants.INPUT_TYPE_LONG_TEXT -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
            Constants.INPUT_TYPE_TEXT_NO_TITLE -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_TEXT
            }
            Constants.INPUT_TYPE_DONE_BTN -> {
                binding.wsEditTextInput.imeOptions = EditorInfo.IME_ACTION_DONE
            }
        }
    }

    fun getText(): String? {
        var textOrNull: String? = null
        if(!binding.wsEditTextInput.text.isEmpty()){
            textOrNull = binding.wsEditTextInput.text.toString()
        }
        return textOrNull
    }

    fun setText(text: String?) {
        text?.let{
            binding.wsEditTextInput.setText(text)
            if(!isEditable){
                animateVerifiedIcon()
            }
        }
    }

    fun setPrefix(prefix: String) {
        binding.wsEditTextPrefix.visibility = View.VISIBLE
        binding.wsEditTextPrefix.setText(prefix)
    }

    fun getPrefix(): String {
        return binding.wsEditTextPrefix.text.toString()
    }


}
