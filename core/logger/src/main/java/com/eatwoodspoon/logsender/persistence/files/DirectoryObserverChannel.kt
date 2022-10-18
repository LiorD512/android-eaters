package com.eatwoodspoon.logsender.persistence.files

import android.os.FileObserver
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.FileFilter

/**
 * Channel that provides directory updates according to `fileFilter` provided.
 * Send files that were created `CREATE` or renamed/moved `MOVED_TO` to target directory.
 */
internal class DirectoryObserverChannel(
    val directory: File,
    val fileFiler: FileFilter,
    capacity: Int = 1000
) : Channel<File> by Channel(capacity) {

    private val fileObserver =
        object : FileObserver(directory, CREATE xor MOVED_TO) {
            override fun onEvent(event: Int, path: String?) {
                try {
                    path?.let {
                        File(directory, it)
                    }?.let {
                        if (fileFiler.accept(it)) {
                            trySend(it)
                        }
                    }
                } catch (ex: Exception) {
                    stopWatching()
                }
            }
        }

    init {
        directory.listFiles(fileFiler)?.sorted()?.take(capacity)?.forEach {
            trySend(it)
        }
        fileObserver.startWatching()
    }
}
