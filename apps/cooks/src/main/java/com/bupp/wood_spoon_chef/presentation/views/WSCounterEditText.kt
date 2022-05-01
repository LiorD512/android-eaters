package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.WsCounterEditTextBinding
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.Utils

class WSCounterEditText @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {


    private var minChar = -1
    private var maxChar = -1

    private var binding: WsCounterEditTextBinding = WsCounterEditTextBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }


    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSCounterEditText)

                val hint = attr.getString(R.styleable.WSCounterEditText_hint)
                counterEditTextInput.hint = hint

                val title = attr.getString(R.styleable.WSCounterEditText_title)
                title?.let { setTitle(it) }


                maxChar = attr.getInt(R.styleable.WSCounterEditText_maxChar, -1)
                //limit edit text length
                if (maxChar != -1) {
                    val filterArray = arrayOfNulls<InputFilter>(1)
                    filterArray[0] = InputFilter.LengthFilter(maxChar)
                    counterEditTextInput.filters = filterArray

                    counterEditTextCounter.text = "0/$maxChar"
                    counterEditTextCounter.visibility = View.VISIBLE
                }

                minChar = attr.getInt(R.styleable.WSCounterEditText_minChar, -1)
                //limit edit text length
                if (minChar != -1) {
                    counterEditTextCounter.text = "Minimum $minChar characters"
                    counterEditTextCounter.visibility = View.VISIBLE
                }

                val lines = attr.getInt(R.styleable.WSCounterEditText_maxLines, -1)
                if (lines != -1) {
                    counterEditTextInput.setLines(lines)
                }


                attr.recycle()


                counterEditTextInput.addTextChangedListener(object : SimpleTextWatcher() {
                    override fun afterTextChanged(s: Editable) {
                        val charCount = s.length
                        if(maxChar > -1){
                            counterEditTextCounter.text = "$charCount/$maxChar"
                        }
                        if (minChar != -1) {
                            counterEditTextCounter.text = "Minimum $minChar characters (${charCount})"
                            counterEditTextCounter.visibility = View.VISIBLE
                        }
                        if (s.isEmpty()) {
                            val face = ResourcesCompat.getFont(context, R.font.lato_italic)
                            counterEditTextInput.typeface = face
                        } else {
                            val face = ResourcesCompat.getFont(context, R.font.lato_bold)
                            counterEditTextInput.typeface = face
                        }
                        super.afterTextChanged(s)
                    }
                })

            }
        }
    }

    private fun setTitle(title: String) {
        with(binding) {
            counterEditTextTitle.text = title
            counterEditTextTitle.visibility = View.VISIBLE
        }
    }


    private fun showError() {
        with(binding) {
            Utils.vibrate(context)
            AnimationUtil().shakeView(counterEditTextLayout, binding.root.context)
        }
    }



    fun getText(): String? {
        var textOrNull: String? = null
        if (binding.counterEditTextInput.text.isNotEmpty()) {
            textOrNull = binding.counterEditTextInput.text.toString()
        }
        return textOrNull
    }

    fun setText(text: String?) {
        text?.let {
            binding.counterEditTextInput.setText(text)
        }
    }

    fun checkIfValidAndSHowError(): Boolean {
        val text = binding.counterEditTextInput.text
        if(minChar > -1){
            if(text.length < minChar){
                showError()
                return false
            }
        }
        if (text.isNullOrEmpty()) {
            showError()
            return false
        }
        return true
    }

}
