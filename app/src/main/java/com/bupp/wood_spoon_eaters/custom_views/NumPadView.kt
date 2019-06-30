package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.R


class NumPadView : LinearLayout {
    internal lateinit var context: Context

     lateinit var inputEditText: EditText

    fun getInputText(): EditText {
        return inputEditText
    }

    fun setInputText(inputEditText: EditText) {
        this.inputEditText = inputEditText
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    protected fun init(context: Context) {
        this.context = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.numpad_view, this)

        inputEditText = EditText(context)

        findViewById<View>(R.id.numpad_btn_0).setOnClickListener { inputEditText.append("0") }
        findViewById<View>(R.id.numpad_btn_1).setOnClickListener { inputEditText.append("1") }
        findViewById<View>(R.id.numpad_btn_2).setOnClickListener { inputEditText.append("2") }
        findViewById<View>(R.id.numpad_btn_3).setOnClickListener { inputEditText.append("3") }
        findViewById<View>(R.id.numpad_btn_4).setOnClickListener { inputEditText.append("4") }
        findViewById<View>(R.id.numpad_btn_5).setOnClickListener { inputEditText.append("5") }
        findViewById<View>(R.id.numpad_btn_6).setOnClickListener { inputEditText.append("6") }
        findViewById<View>(R.id.numpad_btn_7).setOnClickListener { inputEditText.append("7") }
        findViewById<View>(R.id.numpad_btn_8).setOnClickListener { inputEditText.append("8") }
        findViewById<View>(R.id.numpad_btn_9).setOnClickListener { inputEditText.append("9") }

        findViewById<View>(R.id.numpad_btn_backspace).setOnClickListener(OnClickListener {
            if (inputEditText.text.toString().isEmpty())
                return@OnClickListener
            inputEditText.setText(inputEditText.text.toString().substring(0, inputEditText.text.toString().length - 1))
        })
    }

    fun clearInput() {
        inputEditText.setText("")
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        findViewById<View>(R.id.numpad_btn_0).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_1).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_2).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_3).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_4).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_5).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_6).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_7).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_8).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_9).isEnabled = enabled
        findViewById<View>(R.id.numpad_btn_backspace).isEnabled = enabled
    }
}