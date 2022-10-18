package com.eatwoodspoon.timber

import android.util.Log
import com.eatwoodspoon.logsender.Logger
import timber.log.Timber

class S3SenderTree(private val logger: Logger): Timber.Tree() {
    @Suppress("UNCHECKED_CAST")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logger.log(mapOf<String, Any?>(
            "severity" to mapPriorityToString(priority).lowercase(),
            "tag" to tag,
            "message" to message,
            "throwable" to t?.message
        ).filterValues { it != null } as Map<String, Any>)
    }
}

private fun mapPriorityToString(priority: Int): String {
    return when (priority) {
        Log.VERBOSE -> "VERBOSE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> "UNKNOWN"
    }
}