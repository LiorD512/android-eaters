package com.bupp.wood_spoon_eaters.common

import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import io.shipbook.shipbooksdk.Log as SBLog

class MTLogger {

    companion object {
        var instance: MTLogger? = null
        private var showLinks: Boolean? = null // show a clickable link to where log
        private var autoTag: Boolean? = null
        private var tagPrefix: String = ""

        fun c(tag: String? = null, body: String) {
            instance?.log(Log.DEBUG, tag, body, true)
        }

        fun v(body: String, tag: String? = null, cacheLog: Boolean = false) {
            instance?.log(Log.VERBOSE, tag, body, cacheLog)
        }

        fun d(body: String, tag: String? = null, cacheLog: Boolean = false) {
            instance?.log(Log.DEBUG, tag, body, cacheLog)
        }

        fun i(body: String, tag: String? = null, cacheLog: Boolean = false) {
            instance?.log(Log.INFO, tag, body, cacheLog)
        }

        fun w(body: String, tag: String? = null, cacheLog: Boolean = false) {
            instance?.log(Log.WARN, tag, body, cacheLog)
        }

        fun e(body: String, tag: String? = null, cacheLog: Boolean = false) {
            instance?.log(Log.ERROR, tag, body, cacheLog)
        }
    }

    data class Builder(
        var showLinks: Boolean? = null,
        var autoTag: Boolean? = null,
        var tagPrefix: String? = ""
    ) {

        fun showLinks(showLinks: Boolean): Builder {
            Companion.showLinks = showLinks
            return this
        }

        fun autoTag(autoTag: Boolean): Builder {
            Companion.autoTag = autoTag
            return this
        }

        fun tagPrefix(tagPrefix: String): Builder {
            Companion.tagPrefix = tagPrefix
            return this
        }

        fun build(): Builder {
            instance = MTLogger()
            return this
        }

    }

    private val stringBuilder = StringBuilder().append("\n")

    private fun getLink(): String {
        if (showLinks == false) {
            return ""
        }
        val stackTrace = Exception().stackTrace[6]
        var fileName = stackTrace.fileName
        val methodName = stackTrace.methodName

        if (fileName == null) fileName =
            "" // It is necessary if you want to use proguard obfuscation.
        return ".($fileName:${stackTrace.lineNumber}).${methodName}"
    }

    private fun log(level: Int, tag: String?, msg: String, cacheLog: Boolean) {
        try {
            val body = if (tag.isNullOrEmpty()) {
                "${getLink()} --> $msg"
            } else {
                "$tag --> $msg"
            }

            when (level) {
                Log.INFO ->
                    SBLog.i(BuildConfig.TAG, body)
                Log.VERBOSE ->
                    SBLog.v(BuildConfig.TAG, body)
                Log.DEBUG ->
                    SBLog.d(BuildConfig.TAG, body)
                Log.WARN ->
                    SBLog.w(BuildConfig.TAG, body)
                Log.ERROR ->
                    SBLog.e(BuildConfig.TAG, body)
            }
            if (cacheLog) {
                stringBuilder.append(body).append("\n")
            }
        } catch (ex: Exception) {
            Log.e("MTLogger", ex.localizedMessage ?: "")
        }
    }

    fun getCachedLog(extraGravy: Boolean = false): String {
        if (extraGravy) {
            return stringBuilder.toString()
        }
        return toString()
    }

}