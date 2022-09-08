package com.bupp.wood_spoon_eaters.custom_views.phone_number

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.databinding.PhoneSelectorViewBinding
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import me.ibrahimsn.lib.Countries
import me.ibrahimsn.lib.PhoneNumberKit
import timber.log.Timber
import java.lang.Exception
import java.util.*

class PhoneSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    data class Phone(
        val prefix: String,
        val number: String,
        val flag: String
    ) {
        override fun toString() = prefix + number
    }

    private var phoneNumberKit = PhoneNumberKit(context)
    private var phone: Phone = Phone("", "", "")

    fun interface OnCountryClickListener {
        fun onCountryClick(view: PhoneSelectorView)
    }

    private val binding = PhoneSelectorViewBinding.inflate(LayoutInflater.from(context), this)

    var onCountryClickListener: OnCountryClickListener? = null

    val phoneInput = binding.phoneNumberInput

    init {
        initUI()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.phoneCountryButton.isEnabled = enabled
        binding.phoneNumberInput.setIsEditable(enabled, null)
    }

    private fun initUI() {
        val deviceCountryCode = CountryCodeUtils.getCountryCodeData(context)
        setCountryCode(deviceCountryCode)

        binding.phoneCountryButton.setOnClickListener {
            onCountryClickListener?.onCountryClick(this)
        }
    }

    private fun setPhone(phone: Phone) {
        this.phone = phone
        binding.phoneNumberInput.setPrefix(phone.prefix)
        binding.phoneCountryButton.text = phone.flag
        binding.phoneNumberInput.setText(phone.number)
    }

    private fun getPhone() = phone.copy(number = binding.phoneNumberInput.getText() ?: "")

    internal fun update(processor: (Phone) -> Phone) {
        setPhone(processor(phone))
    }

    fun getPhoneString() = getPhone().toString()

    fun setPhoneString(phoneString: String?) {
        try {
            phoneNumberKit.parsePhoneNumber(phoneString, null)?.toSelectorViewPhone()?.let {
                setPhone(it)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun showError(message: String? = null) {
        binding.phoneNumberInput.showError(message)
    }
}

fun PhoneSelectorView.setCountryCode(countryCode: CountryCodeUtils.CountryCodeResult) {
    update {
        it.copy(
            prefix = "+${countryCode.countryCodeIso}",
            flag = countryCode.flag ?: ""
        )
    }
}

fun PhoneSelectorView.setCountryCode(countryCode: CountriesISO) {
    update {
        it.copy(
            prefix = "+${countryCode.country_code}",
            flag = countryCode.flag ?: "️"
        )
    }
}

private fun me.ibrahimsn.lib.Phone.toSelectorViewPhone(): PhoneSelectorView.Phone {
    return PhoneSelectorView.Phone(
        prefix = "+${this.countryCode ?: 1}",
        number = this.nationalNumber?.toString() ?: "",
        flag = Countries.list.firstOrNull { it.countryCode == this.countryCode }
            ?.let {
                CountryCodeUtils.countryCodeToEmojiFlag(it.iso2.uppercase(Locale.ROOT))
            } ?: ""
    )
}
