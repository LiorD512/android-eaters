package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.LinearLayout
import com.bupp.wood_spoon_eaters.databinding.NumpadViewBinding


class NumPadView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: NumpadViewBinding = NumpadViewBinding.inflate(LayoutInflater.from(context), this, true)

     var inputEditText: EditText = EditText(context)

    fun getInputText(): EditText {
        return inputEditText
    }

    init{
        with(binding){
            inputEditText = EditText(context)

            numPadBtn0.setOnClickListener { inputEditText.append("0") }
            numPadBtn1.setOnClickListener { inputEditText.append("1") }
            numPadBtn2.setOnClickListener { inputEditText.append("2") }
            numPadBtn3.setOnClickListener { inputEditText.append("3") }
            numPadBtn4.setOnClickListener { inputEditText.append("4") }
            numPadBtn5.setOnClickListener { inputEditText.append("5") }
            numPadBtn6.setOnClickListener { inputEditText.append("6") }
            numPadBtn7.setOnClickListener { inputEditText.append("7") }
            numPadBtn8.setOnClickListener { inputEditText.append("8") }
            numPadBtn9.setOnClickListener { inputEditText.append("9") }

            numPadBtnDeleteBtn.setOnClickListener(OnClickListener {
                if (inputEditText.text.toString().isEmpty())
                    return@OnClickListener
                inputEditText.setText(inputEditText.text.toString().substring(0, inputEditText.text.toString().length - 1))
            })
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        with(binding){
            numPadBtn0.isEnabled = enabled
            numPadBtn1.isEnabled = enabled
            numPadBtn2.isEnabled = enabled
            numPadBtn3.isEnabled = enabled
            numPadBtn4.isEnabled = enabled
            numPadBtn5.isEnabled = enabled
            numPadBtn6.isEnabled = enabled
            numPadBtn7.isEnabled = enabled
            numPadBtn8.isEnabled = enabled
            numPadBtn9.isEnabled = enabled
            numPadBtnDeleteBtn.isEnabled = enabled
        }
    }
}