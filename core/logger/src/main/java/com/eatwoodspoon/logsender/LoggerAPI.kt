package com.eatwoodspoon.logsender

import android.content.Context
import android.util.Log
import com.eatwoodspoon.logsender.persistence.JSONSerializer
import com.eatwoodspoon.logsender.persistence.files.AndroidLoggingFileSystem
import com.eatwoodspoon.logsender.persistence.files.FilePersistence
import com.eatwoodspoon.logsender.s3sender.S3Sender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

interface Logger {

    fun log(logItem: Map<String, Any>)

    companion object {

        var instance: Logger = NoOpLogger()
            private set

        @JvmStatic
        fun createSingletonInstance(
            context: Context,
            config: LoggerConfig,
            preprocessor: Preprocessor? = null,
            outerCoroutineScope: CoroutineScope = GlobalScope
        ): Logger {
            instance = create(
                context,
                config,
                preprocessor,
                outerCoroutineScope
            )
            return instance
        }

        @JvmStatic
        fun create(
            context: Context,
            config: LoggerConfig,
            preprocessor: Preprocessor? = null,
            outerCoroutineScope: CoroutineScope = GlobalScope
        ): Logger {
            if (!config.enabled) {
                Log.w("Logger", "LogSender is disabled")
                return NoOpLogger()
            }

            val loggingFileSystem = AndroidLoggingFileSystem(context, config)

            val loggerPersistence = FilePersistence(loggingFileSystem, config)

            val s3Sender = S3Sender(loggingFileSystem, config)

            val serializer = JSONSerializer()

            return LoggerImpl(
                preprocessor,
                serializer,
                loggerPersistence,
                s3Sender,
                config,
                outerCoroutineScope
            )
        }
    }
}

internal interface Persistence<FORMAT> {

    suspend fun persist(logItem: FORMAT)
}

internal interface Serializer<FORMAT> {

    fun serializeLog(logItem: Map<String, Any>): FORMAT
}

interface Preprocessor {

    fun process(logItem: Map<String, Any>): Map<String, Any>
}

private class NoOpLogger : Logger {

    override fun log(logItem: Map<String, Any>) {}
}
