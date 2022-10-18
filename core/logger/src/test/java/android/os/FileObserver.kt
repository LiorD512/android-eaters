package android.os

import java.io.File
import java.util.*

/*
* Copyright (C) 2006 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * Monitors files (using [inotify](http://en.wikipedia.org/wiki/Inotify))
 * to fire an event after files are accessed or changed by any process on
 * the device (including this one).  FileObserver is an abstract class;
 * subclasses must implement the event handler [.onEvent].
 *
 *
 * Each FileObserver instance can monitor multiple files or directories.
 * If a directory is monitored, events will be triggered for all files and
 * subdirectories inside the monitored directory.
 *
 *
 * An event mask is used to specify which changes or actions to report.
 * Event type constants are used to describe the possible changes in the
 * event mask as well as what actually happened in event callbacks.
 *
 *
 * **Warning**: If a FileObserver is garbage collected, it
 * will stop sending events.  To ensure you keep receiving events, you must
 * keep a reference to the FileObserver instance from some other live object.
 */
abstract class FileObserver(
    private val mDirectory: File,
    private val mMask: Int
) {

    companion object {
        /** Event type: Data was read from a file  */
        const val ACCESS = 0x00000001

        /** Event type: Data was written to a file  */
        const val MODIFY = 0x00000002

        /** Event type: Metadata (permissions, owner, timestamp) was changed explicitly  */
        const val ATTRIB = 0x00000004

        /** Event type: Someone had a file or directory open for writing, and closed it  */
        const val CLOSE_WRITE = 0x00000008

        /** Event type: Someone had a file or directory open read-only, and closed it  */
        const val CLOSE_NOWRITE = 0x00000010

        /** Event type: A file or directory was opened  */
        const val OPEN = 0x00000020

        /** Event type: A file or subdirectory was moved from the monitored directory  */
        const val MOVED_FROM = 0x00000040

        /** Event type: A file or subdirectory was moved to the monitored directory  */
        const val MOVED_TO = 0x00000080

        /** Event type: A new file or subdirectory was created under the monitored directory  */
        const val CREATE = 0x00000100

        /** Event type: A file was deleted from the monitored directory  */
        const val DELETE = 0x00000200

        /** Event type: The monitored file or directory was deleted; monitoring effectively stops  */
        const val DELETE_SELF = 0x00000400

        /** Event type: The monitored file or directory was moved; monitoring continues  */
        const val MOVE_SELF = 0x00000800

    }

    private lateinit var mObserverThread: Thread
    private var started = false

    protected fun finalize() {
        stopWatching()
    }

    /**
     * Start watching for events.  The monitored file or directory must exist at
     * this time, or else no events will be reported (even if it appears later).
     * If monitoring is already started, this call has no effect.
     */
    fun startWatching() {
        mObserverThread = object : Thread() {
            override fun run() {
                started = true
                var previousFiles = mDirectory.listFiles()
                while (started) {
                    val currentFiles = mDirectory.listFiles()
                    val diff = currentFiles.filterNot { previousFiles.contains(it) }
                    if (diff.isNotEmpty()) {
                        diff.forEach {
                            onEvent(CREATE, it.path)
                        }
                    }
                    previousFiles = currentFiles
                    sleep(50)
                }
            }
        }.apply {
            start()
        }
    }

    /**
     * Stop watching for events.  Some events may be in process, so events
     * may continue to be reported even after this method completes.  If
     * monitoring is already stopped, this call has no effect.
     */
    fun stopWatching() {
        started = false
        mObserverThread.join()
    }

    /**
     * The event handler, which must be implemented by subclasses.
     *
     *
     * This method is invoked on a special FileObserver thread.
     * It runs independently of any threads, so take care to use appropriate
     * synchronization!  Consider using [Handler.post] to shift
     * event handling work to the main thread to avoid concurrency problems.
     *
     *
     * Event handlers must not throw exceptions.
     *
     * @param event The type of event which happened
     * @param path The path, relative to the main monitored file or directory,
     * of the file or directory which triggered the event.  This value can
     * be `null` for certain events, such as [.MOVE_SELF].
     */
    abstract fun onEvent(event: Int, path: String?)
    /**
     * Version of [.FileObserver] that allows callers to monitor
     * multiple files or directories.
     *
     * @param mFiles The files or directories to monitor
     * @param mMask The event or events (added together) to watch for
     */
}