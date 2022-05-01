package com.bupp.wood_spoon_chef.presentation.views

import android.animation.ObjectAnimator
import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.WsEditTextBinding
import com.bupp.wood_spoon_chef.utils.Utils


class WSEditText @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr), OnEditorActionListener {

    private var binding: WsEditTextBinding =
        WsEditTextBinding.inflate(LayoutInflater.from(context), this, true)
    private var isEditable = false

    init {
        initUi(attrs)
    }

    var listener: WSEditTextListener? = null

    interface WSEditTextListener {
        fun onWSEditUnEditableClick() {}
        fun onTooltipClick(text: String) {}
        fun onIMEDoneClick(text: String) {}
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)

                val hint = attr.getString(R.styleable.WSEditText_hint)
                wsEditTextInput.hint = hint

                val error = attr.getString(R.styleable.WSEditText_error)
                error?.let { setError(error) }

                val title = attr.getString(R.styleable.WSEditText_title)
                title?.let { setTitle(it) }

                val prefix = attr.getString(R.styleable.WSEditText_prefix)
                prefix?.let { setPrefix(it) }

                val imeOptions = attr.getInt(R.styleable.WSEditText_imeOptions, -1)
                if (imeOptions > -1) {
                    setImeOptions(imeOptions)
                }

                inputType = attr.getInt(R.styleable.WSEditText_inputType, 0)
                setInputType(inputType)

                val isEditable = attr.getBoolean(R.styleable.WSEditText_isEditable, true)
                setIsEditable(isEditable, null)

                val icon = attr.getDrawable(R.styleable.WSEditText_wsIcon)
                icon?.let {
                    wsEditIcon.setImageDrawable(icon)
                    wsEditIcon.visibility = View.VISIBLE
                }

                val maxChar = attr.getInt(R.styleable.WSEditText_maxChar, -1)
                //limit edit text length
                if (maxChar > -1) {
                    val filterArray = arrayOfNulls<InputFilter>(1)
                    filterArray[0] = InputFilter.LengthFilter(maxChar)
                    wsEditTextInput.filters = filterArray

                    wsEditTextCounter.text = "0/$maxChar"
                    wsEditTextCounter.visibility = View.VISIBLE
                }


                attr.recycle()


                wsEditTextInput.addTextChangedListener(object : SimpleTextWatcher() {
                    override fun afterTextChanged(s: Editable) {
                        hideError()
                        if (s.isEmpty()) {
                            val face = ResourcesCompat.getFont(context, R.font.lato_italic)
                            wsEditTextInput.typeface = face
                            wsEditTextPrefix.typeface = face
                        } else {
                            val face = ResourcesCompat.getFont(context, R.font.lato_bold)
                            wsEditTextInput.typeface = face
                            wsEditTextPrefix.typeface = face

                        }
                        if (maxChar > -1) {
                            val length = s.length
                            wsEditTextCounter.text = "$length/$maxChar"
                        }
                        super.afterTextChanged(s)
                    }
                })
                wsEditTextInput.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        wsEditTextUnderline.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.greyish_brown
                            )
                        )
                    } else {
                        wsEditTextUnderline.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.light_periwinkle
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setImeOptions(imeOptions: Int) {
        binding.wsEditTextInput.imeOptions = imeOptions
    }

    private fun setTitle(title: String) {
        binding.wsEditTextTitle.text = title
        binding.wsEditTextTitle.visibility = View.VISIBLE
    }

    private var inputType: Int = 0

    fun showError() {
        with(binding) {
            Utils.vibrate(context)
            if (wsEditTextErrorIcon.alpha > 0) {
                animateErrorIconBounce()
            } else {
                animateErrorIconEntrance()
                animateErrorTextEntrance()
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
        val measuredHeight = binding.wsEditTextCheck.measuredHeight.toFloat()
        ObjectAnimator.ofFloat(
            binding.wsEditTextCheck, "translationY",
            measuredHeight, -30f, 5f, 0f,
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
        if (binding.wsEditTextErrorIcon.alpha > 0) {
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

    private fun setError(error: String) {
        binding.wsEditTextErrorText.text = error
    }

    private fun setInputType(type: Int) {
        when (type) {
            Constants.INPUT_TYPE_PHONE -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_PHONE
                binding.wsEditTextInput.imeOptions = EditorInfo.IME_ACTION_DONE
                binding.wsEditTextInput.setOnEditorActionListener(this)
                binding.wsEditTextInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            Constants.INPUT_TYPE_FULL_NAME -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            }
            Constants.INPUT_TYPE_TEXT_CAP_SENTENCE -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
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
                binding.wsEditTextInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
            Constants.INPUT_TYPE_TEXT_NO_TITLE -> {
                binding.wsEditTextInput.inputType = InputType.TYPE_CLASS_TEXT
            }
            Constants.INPUT_TYPE_DONE_BTN -> {
                binding.wsEditTextInput.isSingleLine = true
                binding.wsEditTextInput.imeOptions = EditorInfo.IME_ACTION_DONE
                binding.wsEditTextInput.setOnEditorActionListener(this)
            }
        }
    }


    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onEditorAction: $actionId")
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            view?.let {
                val text = view.text.toString()
                text.isNotEmpty().let {
                    listener?.onIMEDoneClick(text)
                    view.text = ""
                    return true
                }
            }
        }
        return false
    }

    fun getTextOrNull(): String? {
        var textOrNull: String? = null
        if (binding.wsEditTextInput.text.isNotEmpty()) {
            textOrNull = binding.wsEditTextInput.text.toString()
        }
        return textOrNull
    }

    fun getText(): String {
        var textOrEmpty: String = ""
        if (binding.wsEditTextInput.text.isNotEmpty()) {
            textOrEmpty = binding.wsEditTextInput.text.toString()
        }
        return textOrEmpty
    }

    fun setText(text: String?) {
        text?.let {
            binding.wsEditTextInput.setText(text)
            if (!isEditable) {
                animateVerifiedIcon()
            }
        }
    }

    fun setWSEditTextListener(listener: WSEditTextListener?) {
        this.listener = listener
    }

    fun setIsEditable(editable: Boolean, listener: WSEditTextListener?) {
        this.listener = listener
        with(binding) {
            wsEditTextInput.isFocusable = editable
            wsEditTextInput.isClickable = editable
            this@WSEditText.isEditable = editable

            wsEditTextInput.setOnClickListener {
                listener?.onWSEditUnEditableClick()
            }
        }
    }

    fun setToolTip(tooltipText: String, listener: WSEditTextListener?) {
        this.listener = listener
        with(binding) {
            wsEditTextTooltip.visibility = View.VISIBLE
            wsEditTextTooltip.setOnClickListener {
                listener?.onTooltipClick(tooltipText)
            }
        }
    }

    fun setPrefix(prefix: String) {
        binding.wsEditTextPrefix.visibility = View.VISIBLE
        binding.wsEditTextPrefix.setText(prefix)
    }

    fun checkIfValidAndSHowError(): Boolean {
        val text = binding.wsEditTextInput.text
        if (text.isNullOrEmpty()) {
            showError()
            return false
        }
        return true
    }

    fun checkIfLongerThenAndSHowError(size: Int): Boolean {
        val text = binding.wsEditTextInput.text
        if (text.isNullOrEmpty() || text.count() < size) {
            showError()
            return false
        }
        return true
    }

    fun checkIfValidEmailAndSHowError(): Boolean {
        val text = binding.wsEditTextInput.text
        if (inputType == Constants.INPUT_TYPE_MAIL) {
            return if (!text.isNullOrEmpty() && Utils.isValidEmailAddress(text.toString())) {
                true
            } else {
                showError()
                false
            }
        }
        return false
    }

    companion object {
        const val TAG = "wowWSEditText"
    }


}
