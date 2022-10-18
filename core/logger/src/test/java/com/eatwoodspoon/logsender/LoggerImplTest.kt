package com.eatwoodspoon.logsender


import com.eatwoodspoon.logsender.persistence.JSONSerializer
import com.eatwoodspoon.logsender.persistence.files.FilePersistence
import com.eatwoodspoon.logsender.persistence.files.LoggingFileSystem
import com.eatwoodspoon.logsender.s3sender.S3Sender
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.json.JSONObject
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoggerImplTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var loggerJob: Job
    lateinit var loggerScope: CoroutineScope

    lateinit var preprocessor: Preprocessor
    lateinit var serializer: Serializer<String>
    lateinit var loggingFileSystem: LoggingFileSystem
    lateinit var persistence: Persistence<String>
    lateinit var sender: S3Sender
    lateinit var logger: LoggerImpl
    lateinit var logDirectory: File

    private val commonAttributes = mutableMapOf<String, Any>()

    private val defaultConfig = LoggerConfig(
        s3config = S3SenderConfig(
            credentials = S3SenderConfig.Credentials(
                "", "", "", ""
            ), deviceId = "deviceId", logsDirectoryName = ""
        ),
        enableStatsReporting = false
    )

    private fun initLogger(
        config: LoggerConfig = defaultConfig, scope: CoroutineScope
    ) {
        // create temp directory for logs
        logDirectory = createTempDirectory("test_logs").toFile()

        preprocessor = object : Preprocessor {
            override fun process(logItem: Map<String, Any>): Map<String, Any> {
                return commonAttributes + logItem
            }
        }
        serializer = JSONSerializer()
        loggingFileSystem = LoggingFileSystem(logDirectory, config)
        persistence = spy(FilePersistence(loggingFileSystem, config))
        sender = mock()
        logger = LoggerImpl(preprocessor, serializer, persistence, sender, config, scope)
    }

    @After
    fun tearDown() {
        logDirectory.deleteRecursively()
    }

    @Test
    fun ensureCommonAttributesAddedBeforeLogItem() = runTest {
        commonAttributes.putAll(mapOf("key1" to "value1", "key2" to "value2"))

        initLogger(scope = this)

        logger.log(
            mapOf(
                "message" to "message",
                "key2" to "new_value2",
            )
        )

        advanceUntilIdle()

        argumentCaptor<String>().apply {
            verify(persistence, times(1)).persist(capture())
            assert(JSONObject(firstValue)["key1"] == "value1")
            assert(JSONObject(firstValue)["key2"] == "new_value2")
            assert(JSONObject(firstValue)["message"] == "message")
        }

        logger.stop()
    }

    @Test
    fun checkExpectedFileCountMany() = runTest {

        val maxFileSize = 512L
        val expectedFilesCount = 10
        initLogger(config = defaultConfig.copy(maxFileSize = maxFileSize), scope = this)


        val logItem = mapOf("message" to "message")
        val serializedLogItemSize = serializer.serializeLog(logItem).length

        val repeatCount = (maxFileSize * expectedFilesCount) / serializedLogItemSize + 1
        repeat(repeatCount.toInt()) {
            logger.log(logItem)
        }

        logger.stopGracefully()

        assert(logDirectory.listFiles()?.size == expectedFilesCount + 1) // +1 for current log file
    }

    @Test
    fun checkExpectedFileCount1() = runTest {
        initLogger(scope = this)

        logger.log(
            mapOf(
                "message" to "message"
            )
        )
        advanceUntilIdle()

        logger.stop()

        assert(logDirectory.listFiles()?.size == 1)
    }

    @Test
    fun checkMaxDirectorySize() = runTest {

        val maxDirectorySize = 100 * 1024L
        initLogger(config = defaultConfig.copy(maxDirectorySize = maxDirectorySize), scope = this)


        val logItem = mapOf("message" to "message")
        val serializedLogItemSize = serializer.serializeLog(logItem).length

        val repeatCount = (maxDirectorySize * 10) / serializedLogItemSize + 1
        repeat(repeatCount.toInt()) {
            logger.log(logItem)
        }

        logger.stopGracefully()

        val directorySize = logDirectory.listFiles()?.sumOf { it.length() } ?: 0

        assert(directorySize < maxDirectorySize) // +1 for current log file
    }
}