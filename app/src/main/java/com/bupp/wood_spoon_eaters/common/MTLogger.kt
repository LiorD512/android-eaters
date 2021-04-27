package com.bupp.wood_spoon_eaters.common

import android.util.Log


class MTLogger {

    companion object {
        var instance: MTLogger? = null
        private var showLinks: Boolean? = null // show a clickable link to where log
        private var autoTag: Boolean? = null
        private var tagPrefix: String = ""

        fun c(tag: String? = null, body: String) {
            d(tag, body, true)
        }

        fun d(tag: String? = null, body: String, cacheLog: Boolean = false) {

            var msg = body
            var finalTag = ""
            if(tag != null){
                finalTag = tag
            }else{
                autoTag = true
            }
            val stackTrace = Exception().stackTrace[2]

            var fileName = stackTrace.fileName

            if (showLinks == true) {
                if (fileName == null) fileName = "" // It is necessary if you want to use proguard obfuscation.
                val link = ".($fileName:${stackTrace.lineNumber})"
                msg = "$link $msg"
            }

            if (autoTag == true) {
                finalTag = fileName.replace(".kt","")
            }

            instance?.log(tagPrefix + finalTag, msg, cacheLog)
        }


    }

    data class Builder(
            var showLinks: Boolean? = null,
            var autoTag: Boolean? = null,
            var tagPrefix: String? = "") {

        fun showLinks(showLinks: Boolean): Builder {
            MTLogger.Companion.showLinks = showLinks
            return this
        }

        fun autoTag(autoTag: Boolean): Builder {
            MTLogger.Companion.autoTag = autoTag
            return this
        }

        fun tagPrefix(tagPrefix: String): Builder {
            MTLogger.Companion.tagPrefix = tagPrefix
            return this
        }

        fun build(): Builder {
            instance = MTLogger()
            return this
        }
    }

    val stringBuilder = StringBuilder().append("\n")

    private fun log(tag: String, msg: String, cacheLog: Boolean) {
        Log.d(tag, msg)
        if(cacheLog){
            stringBuilder.append(msg).append("\n")
        }
    }

    fun getCachedLog(): String {
        return stringBuilder.toString()
    }

}