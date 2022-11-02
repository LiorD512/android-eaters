package com.eatwoodspoon.logsender.persistence.files

import com.eatwoodspoon.logsender.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.io.path.createTempDirectory

//@OptIn(ExperimentalCoroutinesApi::class)
//class DirectoryObserverChannelTest {
//
//    private val filenameCounter = AtomicInteger(0)
//
//    private lateinit var logDirectory: File
//
//
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()
//
//    @Before
//    fun setup() {
//        logDirectory = createTempDirectory("test_logs").toFile()
//    }
//
//    @After
//    fun tearDown() {
//        logDirectory.deleteRecursively()
//    }
//
//    @Test
//    fun `test existing files received`() = runTest {
//        val expectedFilesCount = 10
//        generateFiles(expectedFilesCount)
//        val channel =
//            DirectoryObserverChannel(logDirectory, { pathname -> pathname.name.endsWith(".log") })
//        var counter = 0
//        try {
//            withTimeout(300) {
//                for (file in channel) {
//                    counter++
//                }
//            }
//        } catch (ex: TimeoutCancellationException) {
//            // ignore
//        }
//        assertEquals(counter, expectedFilesCount)
//    }
//
//    @Test
//    fun `test new files received`() = runTest {
//        val expectedFilesCount = 10
//        val channel =
//            DirectoryObserverChannel(logDirectory, { pathname -> pathname.name.endsWith(".log") })
//
//        thread {
//            Thread.sleep(100)
//            generateFiles(expectedFilesCount)
//            Thread.sleep(100)
//            channel.close()
//        }
//
//        val resultFilesCount = channel.receiveAsFlow().toList(mutableListOf()).size
//
//        assertEquals(resultFilesCount, expectedFilesCount)
//    }
//
//    @Test
//    fun `test files received with filter`() = runTest {
//        val expectedFilesCount = 5
//
//        generateFiles(expectedFilesCount)
//        generateFiles(expectedFilesCount, "other")
//
//        val channel =
//            DirectoryObserverChannel(logDirectory, { pathname -> pathname.name.endsWith(".log") })
//
//        thread {
//            Thread.sleep(100)
//            generateFiles(expectedFilesCount)
//            generateFiles(expectedFilesCount, "other")
//            Thread.sleep(100)
//            channel.close()
//        }
//
//        val resultFilesCount = channel.receiveAsFlow().toList(mutableListOf()).size
//
//        assertEquals(resultFilesCount, expectedFilesCount * 2)
//    }
//
//    private fun generateFiles(numOfFiles: Int, extension: String = "log") {
//        repeat (numOfFiles) {
//            File(logDirectory, "test_${filenameCounter.getAndIncrement()}.$extension").createNewFile()
//        }
//    }
//
//}
