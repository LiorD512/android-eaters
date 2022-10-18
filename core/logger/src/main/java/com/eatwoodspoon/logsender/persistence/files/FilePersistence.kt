package com.eatwoodspoon.logsender.persistence.files

import com.eatwoodspoon.logsender.LoggerConfig
import com.eatwoodspoon.logsender.Persistence
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.io.Writer

/**
 * Writes files string items according to provided `config` with new line separator.
 * In case the items are in compact JSON format (one line), the resulting files follow
 * https://jsonlines.org format
 */
internal class FilePersistence(
    private val fileSystem: LoggingFileSystem,
    private val config: LoggerConfig
) : Persistence<String> {

    private var logFileWriter: LogFileWriter? = null

    init {
        openFile()
    }

    override suspend fun persist(logItem: String) {
        val logWriter = logFileWriter ?: return

        logWriter.write(logItem)
        logWriter.write("\n")

        ensureSizeLimits()
    }

    private fun ensureSizeLimits() {

        val logWriter = logFileWriter ?: return

        if (logWriter.bytesWritten >= config.maxFileSize) {
            nextFile()
            fileSystem.ensureDirectorySize()
        }
    }

    @Synchronized
    private fun closeFile() {
        logFileWriter?.close()
        logFileWriter = null
    }

    @Synchronized
    private fun openFile() {
        logFileWriter = LogFileWriter(fileSystem)
    }

    @Synchronized
    private fun nextFile() {
        closeFile()
        openFile()
    }
}

private class LogFileWriter(private val fileSystem: LoggingFileSystem) : Writer() {

    private val file = fileSystem.newCurrentFile()
    private val writer = FileWriter(file)

    var bytesWritten: Long = 0
        private set

    override fun close() {
        writer.close()
        fileSystem.unMarkCurrentFile(file)
    }

    override fun flush() {
        writer.flush()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        writer.write(cbuf, off, len)
        bytesWritten += len
    }
}
