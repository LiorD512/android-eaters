package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.InputTitleViewBinding


class InputTitleView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr){


    private var binding: InputTitleViewBinding = InputTitleViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var inputType: Int = 0
    private var listener: InputTitleViewListener? = null

    interface InputTitleViewListener {
        fun onInputTitleChange(str: String?)
    }

    private var isMandatory = false

    init{
        if (attrs != null) {
            with(binding) {
                val a = context.obtainStyledAttributes(attrs, R.styleable.InputTitleView)
                if (a.hasValue(R.styleable.InputTitleView_title)) {
                    isMandatory = a.getBoolean(R.styleable.InputTitleView_isMandatory, false)
                    val title = a.getString(R.styleable.InputTitleView_title)
                    if (isMandatory) {
                        inputTitleViewSuffix.visibility = View.VISIBLE
                    }
                    inputTitleViewTitle.text = title
                }

                //DEFINE INPUT TEXT HEIGHT AND GRAVITY
                val lines = a.getInt(R.styleable.InputTitleView_maxLines, -1)
                fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()
                val size = 32.dpToPx(context.resources.displayMetrics) * lines

                if (lines != -1) {
                    val layoutParams = inputTitleViewInput.layoutParams as LayoutParams
                    layoutParams.height = size
                    inputTitleViewInput.layoutParams = layoutParams
                    inputTitleViewInput.setLines(lines)

                    if (lines > 1) {
                        inputTitleViewInput.gravity = Gravity.TOP and Gravity.START
                    } else {
                        inputTitleViewInput.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    }
                }

                val maxChar = a.getInt(R.styleable.InputTitleView_maxChar, -1)
                //limit edit text length
                if (maxChar != -1) {
                    val filterArray = arrayOfNulls<InputFilter>(1)
                    filterArray[0] = InputFilter.LengthFilter(maxChar)
                    inputTitleViewInput.filters = filterArray

                    inputTitleViewCounter.text = "0/$maxChar"
                    inputTitleViewCounter.visibility = View.VISIBLE
                }

                if (a.hasValue(R.styleable.InputTitleView_hint)) {
                    val hint = a.getString(R.styleable.InputTitleView_hint)
                    if (!hint.isNullOrEmpty()) {
                        inputTitleViewInput.hint = hint
                    }
                }

                val textGravity = a.getInteger(R.styleable.InputTitleView_android_gravity, Gravity.START)
                inputTitleViewInput.gravity = textGravity

                val imeOption = a.getInteger(R.styleable.InputTitleView_android_imeOptions, EditorInfo.IME_ACTION_NEXT)
                inputTitleViewInput.imeOptions = imeOption


                if (a.hasValue(R.styleable.InputTitleView_error)) {
                    val error = a.getString(R.styleable.InputTitleView_error)
                    inputTitleViewInputError.text = error
                }

                if (a.hasValue(R.styleable.InputTitleView_inputType)) {
                    inputType = a.getInt(R.styleable.InputTitleView_inputType, Constants.INPUT_TYPE_TEXT)
                    when (inputType) {
                        Constants.INPUT_TYPE_TEXT -> {
                            inputTitleViewInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                        }
                        Constants.INPUT_TYPE_NUMBER -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_NUMBER
                        }
                        Constants.INPUT_TYPE_MAIL -> {
                            inputTitleViewInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                        Constants.INPUT_TYPE_LONG_TEXT -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        }
                        Constants.INPUT_TYPE_PHONE -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_PHONE
                            inputTitleViewInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
                        }
                    }
                }

                if (a.hasValue(R.styleable.InputTitleView_toolTipType)) {
                    when (a.getInt(R.styleable.InputTitleView_toolTipType, -1)) {
                        Constants.INPUT_TOOL_TIP_SSN -> {
                            inputTitleViewToolTip.visibility = View.VISIBLE
                            inputTitleViewToolTip.setToolTipType(Constants.TOOL_TIP_SSN)
                        }
                    }
                }

                inputTitleViewInput.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (maxChar != -1) {
                            inputTitleViewCounter.text = (s!!.length.toString()) + "/" + maxChar
                        }

                        if (s.toString().isNotEmpty()) {
                            listener?.onInputTitleChange(s.toString())
                            hideError()
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })

                a.recycle()

//            Log.d("wowInputTitle", "lines: $lines, size: $size")
            }
        }
    }

    fun getText(): String {
        return binding.inputTitleViewInput.text.toString()
    }

    fun setText(text: String) {
        return binding.inputTitleViewInput.setText(text)
    }

    fun hideError() {
        with(binding) {
            inputTitleViewInputError.visibility = View.GONE
            inputTitleViewInput.setTextColor(ContextCompat.getColor(context, R.color.dark))
        }
    }

}