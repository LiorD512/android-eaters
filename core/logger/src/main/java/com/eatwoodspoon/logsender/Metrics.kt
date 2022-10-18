package com.eatwoodspoon.logsender

import com.eatwoodspoon.logsender.persistence.files.availableStorage
import com.eatwoodspoon.logsender.persistence.files.recursiveSize
import com.eatwoodspoon.logsender.persistence.files.totalStorage
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object Metrics {

    class Counter {
        private val counter = AtomicLong()

        fun count() = counter.incrementAndGet()

        fun reset() = counter.set(0)

        val value get() = counter.get()
    }

    class Throughput {

        private val MEASUREMENT_WINDOW = TimeUnit.SECONDS.toMillis(1)

        private var counter = AtomicLong()
        private var charCounter = AtomicLong()
        private var currentSpanStartTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())

        val recordsPerSecond
            get() = try {
                (TimeUnit.SECONDS.toMillis(1) * counter.get()) / currentTimespan()
            } catch (ex: ArithmeticException) {
                0
            }

        private fun currentTimespan() =
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) - currentSpanStartTime

        private inline fun resetIfNeeded(criteria: (() -> Boolean)) {
            if (criteria()) {
                synchronized(this) {
                    if (criteria()) {
                        reset()
                    }
                }
            }
        }

        private fun reset() {
            counter.set(0)
            charCounter.set(0)
            currentSpanStartTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())
        }

        fun count() {
            resetIfNeeded { currentTimespan() > MEASUREMENT_WINDOW }
            counter.incrementAndGet()
        }

        private fun countString(string: String) {
            resetIfNeeded { currentTimespan() > MEASUREMENT_WINDOW }
            charCounter.addAndGet(string.length.toLong())
        }
    }

    val DroppedLogs = Counter()

    val DroppedFiles = Counter()

    val BatchesSent = Counter()

    object Storage {

        private var file: File? = null
        fun init(file: File?) {
            Storage.file = file
        }

        val used get() = file?.recursiveSize() ?: 0

        val total get() = file?.totalStorage() ?: 0

        val available get() = file?.availableStorage() ?: 0
    }

    object Memory {
        val used get() = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    val LoggerThroughput = Throughput()

    val NetworkThroughput = Throughput()

    fun dumpMetrics() = mapOf<String, Any>(
        "throughput_lps" to LoggerThroughput.recordsPerSecond,
        "network_lps" to NetworkThroughput.recordsPerSecond,
        "memory_used" to Memory.used,
        "storage_used" to Storage.used,
        "dropped_logs" to DroppedLogs.value,
        "dropped_files" to DroppedFiles.value,
        "batches_sent" to BatchesSent.value,
    )
}
