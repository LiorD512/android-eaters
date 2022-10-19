package com.eatwoodspoon.logsender

import com.eatwoodspoon.logsender.s3sender.S3Sender
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.TimeUnit

internal class LoggerImpl(
    private val preprocessor: Preprocessor?,
    private val serializer: Serializer<String>,
    private val persistence: Persistence<String>,
    private val sender: S3Sender,
    config: LoggerConfig,
    outerCoroutineScope: CoroutineScope
) : Logger {

    private val channel = Channel<Map<String, Any>>(config.maxLogBufferSize)

    private val loggerJob: Job

    init {

        loggerJob = outerCoroutineScope.launch {
            try {
                coroutineScope {
                    launch {
                        startLogChannelHandling()
                    }
                    launch {
                        startLogSender()
                    }
                    if (config.enableStatsReporting) {
                        launch {
                            startLogStatReporting()
                        }
                    }
                }
            } catch (ex: Exception) {
                println("LoggerImpl got $ex")
            }
        }
    }

    override fun log(logItem: Map<String, Any>) {
        sendToChannel(logItem)
    }

    fun stop() {
        loggerJob.cancel()
    }

    /**
     * This function will wait till all the messages are persisted and then will cancel the log job
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun stopGracefully() {
        channel.close()
        try {
            withTimeout(1000) {
                while (!channel.isClosedForReceive) {
                    delay(100)
                }
            }
        } catch (ex: TimeoutCancellationException) {
            println("LoggerImpl: stopGracefully: TimeoutCancellationException")
        }
        loggerJob.cancelAndJoin()
    }

    private fun sendToChannel(logItem: Map<String, Any>) {

        if (channel.trySend(logItem).isSuccess) {
            Metrics.LoggerThroughput.count()
        } else {
            Metrics.DroppedLogs.count()
        }
    }

    private suspend fun startLogChannelHandling() {
        for (logItem in channel) {
            val preprocessedLogItem = preprocessor?.process(logItem) ?: logItem
            val serializedItem = serializer.serializeLog(preprocessedLogItem)
            persistence.persist(serializedItem)
        }
    }

    private suspend fun startLogSender() {
        sender.start()
    }

    private suspend fun startLogStatReporting() {
        while (true) {
            log(
                mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "message" to "Logger metrics",
                    "attributes" to Metrics.dumpMetrics()
                )
            )
            delay(TimeUnit.SECONDS.toMillis(30))
        }
    }
}
