package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.databinding.InputTitleViewBinding
import com.bupp.wood_spoon_eaters.utils.Utils
import render.animations.Attention
import render.animations.Render


class InputTitleView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: InputTitleViewBinding = InputTitleViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var inputType: Int = 0
    private var listener: InputTitleViewListener? = null
    var maxChar = -1
    var throttlingTimeout: Long = 0
    var watcher: AutoCompleteTextWatcher? = null

    interface InputTitleViewListener {
        fun onInputTitleChange(str: String?) {}
    }

    var isMandatory = false

    init {
        with(binding) {
            attrs?.let {
                val a = context.obtainStyledAttributes(it, R.styleable.InputTitleView)
                if (a.hasValue(R.styleable.InputTitleView_title)) {
                    isMandatory = a.getBoolean(R.styleable.InputTitleView_isMandatory, false)
                    val title = a.getString(R.styleable.InputTitleView_title)
                    if (isMandatory) {
                        inputTitleViewSuffix.visibility = View.VISIBLE
                    }
                    inputTitleViewTitle.text = title
                } else {
                    inputTitleViewTitle.visibility = View.GONE
                }

                //DEFINE INPUT TEXT HEIGHT AND GRAVITY
                val lines = a.getInt(R.styleable.InputTitleView_lines, -1)
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

                val textGravity = a.getInteger(R.styleable.InputTitleView_android_gravity, Gravity.START)
                inputTitleViewInput.gravity = textGravity

                val textSize = a.getDimension(R.styleable.InputTitleView_android_textSize, -1f)
                if (textSize > -1) {
                    inputTitleViewInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.toSp(textSize.toInt()).toFloat())
                }

                throttlingTimeout = a.getInt(R.styleable.InputTitleView_throttlingTimeout, 0).toLong()
                watcher = getAutoCompleteTextWatcher()

                maxChar = a.getInt(R.styleable.InputTitleView_maxChar, -1)
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
                    hint?.let {
                        if (hint.isNotEmpty()) {
                            inputTitleViewInput.hint = hint
                        }
                    }
                }

                if (a.hasValue(R.styleable.InputTitleView_error)) {
                    val error = a.getString(R.styleable.InputTitleView_error)
                    inputTitleViewInputError.text = error
                }

                if (a.hasValue(R.styleable.InputTitleView_inputType)) {
                    inputType = a.getInt(R.styleable.InputTitleView_inputType, Constants.INPUT_TYPE_TEXT)
                    when (inputType) {
                        Constants.INPUT_TYPE_FULL_NAME -> {
                            inputTitleViewInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_TEXT -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_TEXT
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_TEXT_WITH_NUMBER -> {
                            inputTitleViewInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_NUMBER -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_NUMBER
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_MAIL -> {
                            inputTitleViewInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_LONG_TEXT -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            inputTitleViewTitle.visibility = VISIBLE
                        }
                        Constants.INPUT_TYPE_TEXT_NO_TITLE -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_TEXT
                            inputTitleViewTitle.visibility = GONE
                        }
                        Constants.INPUT_TYPE_DONE_BTN -> {
                            inputTitleViewInput.imeOptions = EditorInfo.IME_ACTION_DONE
                            inputTitleViewInput.isSingleLine = true
                        }
                        Constants.INPUT_TYPE_PHONE -> {
                            inputTitleViewInput.inputType = InputType.TYPE_CLASS_PHONE
                            inputTitleViewInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
                        }
                    }
                }

                inputTitleViewInput.addTextChangedListener(watcher)

                a.recycle()
            }
        }
    }

    fun getText(): String {
        return binding.inputTitleViewInput.text.toString()
    }

    fun setText(text: String) {
        return binding.inputTitleViewInput.setText(text)
    }

    fun showError() {
        binding.inputTitleViewInputError.visibility = View.VISIBLE

        val render = Render(context)
        render.setDuration(450)
        render.setAnimation(Attention().Swing(binding.inputTitleViewInput))
        render.start()
    }

    fun hideError() {
        binding.inputTitleViewInputError.visibility = View.GONE
    }

    private fun getAutoCompleteTextWatcher(): AutoCompleteTextWatcher {
        return object : AutoCompleteTextWatcher(throttlingTimeout) {
            override fun handleInputString(inputVal: String) {
                Log.d(TAG, "throttlingTimeout: $throttlingTimeout, afterTextChanged: $inputVal")
                if (maxChar != -1) {
                    binding.inputTitleViewCounter.text = "${inputVal.length} / $maxChar"
                }
                listener?.onInputTitleChange(inputVal)
                hideError()
            }
        }
    }

    companion object {
        const val TAG = "wowInputTitleView"
    }

}