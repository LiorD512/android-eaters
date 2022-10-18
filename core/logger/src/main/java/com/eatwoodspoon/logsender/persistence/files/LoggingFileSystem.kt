package com.eatwoodspoon.logsender.persistence.files

import android.content.Context
import android.os.StatFs
import android.os.SystemClock
import android.util.Log
import com.eatwoodspoon.logsender.LoggerConfig
import com.eatwoodspoon.logsender.Metrics
import java.io.File
import java.io.FileFilter
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal open class LoggingFileSystem(
    private val logsDirectory: File,
    private val config: LoggerConfig
) {

    private val lock = ReentrantLock()
    private val fileCounter = AtomicInteger()

    private val logsFileFilter = FileFilter { file ->
        Boolean
        file.name.endsWith(".$logFileExtension")
    }

    private val currentLogsFileFilter = FileFilter { file ->
        Boolean
        file.name.endsWith(".$logFileExtension.$currentLogFileSuffix")
    }

    init {
        Metrics.Storage.init(logsDirectory)
        closeUnclosedFiles()
    }

    fun logFiles() = logsDirectory.listFiles(logsFileFilter)?.sorted() ?: emptyList()

    fun createLogFilesChannel() = DirectoryObserverChannel(logsDirectory, logsFileFilter)

    private fun iterateWithLock(files: Collection<File>?, forEach: ((File) -> Unit)) =
        lock.withLock {
            files?.iterator()?.forEach(forEach)
        }

    fun ensureDirectorySize(tillMaxSizeRatio: Float = 0.75f) {

        val directoryFiles = logFiles()

        val originalDirectorySize = directoryFiles.map { it.length() }.sum()
        var currentDirectorySize = originalDirectorySize

        val allowedDiskSizeCriteria = {
            currentDirectorySize < config.maxDirectorySize * tillMaxSizeRatio
        }

        iterateWithLock(directoryFiles) { fileToDelete ->
            if (allowedDiskSizeCriteria()) {
                return@iterateWithLock
            }
            val fileSize = fileToDelete.length()
            if (fileToDelete.delete()) {
                Log.d("FilePersistence", "File deleted: ${fileToDelete.name}")
                currentDirectorySize -= fileSize
                Metrics.DroppedFiles.count()
            }
        }
        Log.d(
            "FilePersistence",
            "Directory cleaned: cleared=${originalDirectorySize - currentDirectorySize}"
        )
    }

    private fun closeUnclosedFiles() =
        iterateWithLock(logsDirectory.listFiles(currentLogsFileFilter)?.asList()) { file ->
            unMarkCurrentFile(file)
        }

    fun newCurrentFile(): File {
        val fileName =
            "${SystemClock.elapsedRealtime()}_${fileCounter.getAndIncrement()}.$logFileExtension.$currentLogFileSuffix"
        return File(logsDirectory, fileName)
    }

    fun unMarkCurrentFile(file: File) {
        file.renameTo(File(file.parent, file.name.removeSuffix(".$currentLogFileSuffix")))
    }
}

internal class AndroidLoggingFileSystem(
    context: Context,
    config: LoggerConfig
) : LoggingFileSystem(
    File(context.noBackupFilesDir, "logs").apply {
        mkdir()
    },
    config
)

internal fun File.recursiveSize(): Long {
    return this.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
}

internal fun File.availableStorage(): Long {
    val stat = StatFs(this.path)
    val blockSize = stat.blockSizeLong
    val availableBlocks = stat.availableBlocksLong
    return availableBlocks * blockSize
}

internal fun File.totalStorage(): Long {
    val stat = StatFs(this.path)
    val blockSize = stat.blockSizeLong
    val availableBlocks = stat.blockSizeLong
    return availableBlocks * blockSize
}

private const val logFileExtension = "log"
private const val currentLogFileSuffix = "current"
