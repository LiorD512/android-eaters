package com.bupp.wood_spoon_eaters.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import me.ibrahimsn.lib.PhoneNumberKit
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

object CountryCodeUtils {

    data class CountryCodeResult(
        val countryCodeIso: Int?,
        val flag: String?
    )

    fun getCountryCodeData(context: Context): CountryCodeResult{
        val phoneNumberKit = PhoneNumberKit(context)
        val countryCodeStr = getDeviceCountryCode(context)
        val exampleNumber = phoneNumberKit.getExampleNumber(countryCodeStr)
        val flag = countryCodeToEmojiFlag(countryCodeStr)
        return CountryCodeResult(exampleNumber?.countryCode, flag)
    }

    private fun getDeviceCountryCode(context: Context): String {
        var countryCode: String?

        // try to get country code from TelephonyManager service
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            // query first getSimCountryIso()
        countryCode = tm.simCountryIso
        if (countryCode != null && countryCode.length == 2) return countryCode.lowercase()
        countryCode = if (tm.phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            // special case for CDMA Devices
            getCDMACountryIso()
        } else {
            // for 3G devices (with SIM) query getNetworkCountryIso()
            tm.networkCountryIso
        }
        if (countryCode != null && countryCode.length == 2) return countryCode.lowercase()

        // if network country not available (tablets maybe), get country code from Locale class
        countryCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country
        } else {
            context.resources.configuration.locale.country
        }
        return if (countryCode != null && countryCode.length == 2) countryCode.lowercase() else "us"

        // general fallback to "us"
    }

    @SuppressLint("PrivateApi")
    private fun getCDMACountryIso(): String? {
        try {
            // try to get country code from SystemProperties private class
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get: Method = systemProperties.getMethod("get", String::class.java)

            // get homeOperator that contain MCC + MNC
            val homeOperator = get.invoke(
                systemProperties,
                "ro.cdma.home.operator.numeric"
            ) as String

            // first 3 chars (MCC) from homeOperator represents the country code
            if(homeOperator.isNotEmpty()){
                val mcc = homeOperator.substring(0, 3).toInt()
                when (mcc) {
                    330 -> return "PR"
                    310 -> return "US"
                    311 -> return "US"
                    312 -> return "US"
                    316 -> return "US"
                    283 -> return "AM"
                    460 -> return "CN"
                    455 -> return "MO"
                    414 -> return "MM"
                    619 -> return "SL"
                    450 -> return "KR"
                    634 -> return "SD"
                    434 -> return "UZ"
                    232 -> return "AT"
                    204 -> return "NL"
                    262 -> return "DE"
                    247 -> return "LV"
                    255 -> return "UA"
                }
            }
        } catch (ignored: ClassNotFoundException) {
        } catch (ignored: NoSuchMethodException) {
        } catch (ignored: IllegalAccessException) {
        } catch (ignored: InvocationTargetException) {
        } catch (ignored: NullPointerException) {
        }
        return null
    }

    fun countryCodeToEmojiFlag(countryCode: String): String {
        return countryCode
            .uppercase(Locale.US)
            .map { char ->
                Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
            }
            .map { codePoint ->
                Character.toChars(codePoint)
            }
            .joinToString(separator = "") { charArray ->
                String(charArray)
            }
    }

    fun simplifyNumber(phoneStr: String): String {
        return phoneStr.replace("+","").replace(" ","").replace("(","").replace(")","").replace("-","")
    }

    fun isPhoneValid(phone: String?): Boolean{
        phone?.let{
            val re = Regex("[^A-Za-z0-9 ]")
            val phone2 = re.replace(phone, "").replace(" ", "")
            if(phone2.isNotEmpty() && phone2.length >= 8)
                return true
        }
        return false
    }
}