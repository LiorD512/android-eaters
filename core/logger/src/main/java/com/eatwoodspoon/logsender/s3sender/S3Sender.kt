package com.eatwoodspoon.logsender.s3sender

import android.util.Log
//import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
//import aws.sdk.kotlin.services.s3.S3Client
//import aws.sdk.kotlin.services.s3.model.PutObjectRequest
//import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
//import aws.smithy.kotlin.runtime.client.SdkLogMode
//import aws.smithy.kotlin.runtime.content.asByteStream
import com.eatwoodspoon.logsender.LoggerConfig
import com.eatwoodspoon.logsender.Metrics
import com.eatwoodspoon.logsender.persistence.files.LoggingFileSystem
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random.Default.nextDouble

internal class S3Sender(
    loggingFileSystem: LoggingFileSystem,
    private val config: LoggerConfig
) {

    private val fileSenderChannel = loggingFileSystem.createLogFilesChannel()

    suspend fun start(scope: CoroutineScope = GlobalScope) = scope.launch {
        repeat(config.maxConcurrentNetworkRequests) {
            launch(Dispatchers.IO) {
                try {
                    for (file in fileSenderChannel) {
                        sendFileAndHandleResult(
                            file,
                            config.maxSendRetries
                        )
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "", ex)
                }
            }
        }
    }

    private suspend fun sendFileAndHandleResult(file: File, maxAttempts: Int) {
        try {
            for (attempt in 1..maxAttempts) {
                delay(exponentialBackoff(attempt - 1))
                when (val sendFileResult = sendFile(file)) {

                    SendFileResult.NetworkError -> {
                        break
                    }
                    is SendFileResult.RateLimit -> {
                        delay(sendFileResult.retryAfterSeconds * TimeUnit.SECONDS.toMillis(1))
                    }
                    SendFileResult.ServerError -> {
                        if (attempt == maxAttempts) {
                            file.delete()
                        }
                        break
                    }
                    SendFileResult.Success -> {
                        file.delete()
                        Metrics.BatchesSent.count()
                        break
                    }
                    SendFileResult.FileError -> {
                        file.delete()
                        break
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun exponentialBackoff(attempt: Int) = when (attempt) {
        0 -> 0
        else -> (2.0.pow(attempt) * TimeUnit.SECONDS.toMillis(10) * nextDouble(from = 1.0, until = 1.5)).toLong()
    }

    sealed class SendFileResult {
        object Success : SendFileResult()
        class RateLimit(val retryAfterSeconds: Int) : SendFileResult()
        object ServerError : SendFileResult()
        object NetworkError : SendFileResult()
        object FileError : SendFileResult()
    }

    private suspend fun sendFile(file: File): SendFileResult {
        return try {
            s3Upload(file, createCloudFilePathAndName(file))
        } catch (ex: FileNotFoundException) {
            SendFileResult.FileError
        } catch (ex: Exception) {
            Log.e(TAG, "S3 sender error", ex)
            SendFileResult.NetworkError
        }
    }

    private suspend fun s3Upload(file: File, remoteFileName: String): SendFileResult {
//        val s3Credentials = config.s3config.credentials
//
//        if (file.length() == 0L) {
//            return SendFileResult.Success
//        }
//        val request = PutObjectRequest {
//            bucket = s3Credentials.s3BucketName
//            key = remoteFileName
//            body = file.asByteStream()
//        }
//
//        S3Client {
//            region = s3Credentials.region
//            sdkLogMode = SdkLogMode.LogResponseWithBody
//            credentialsProvider = StaticCredentialsProvider(
//                credentials = Credentials(
//                    accessKeyId = s3Credentials.awsAccessKey,
//                    secretAccessKey = s3Credentials.awsSecretKey
//                )
//            )
//        }.use { s3 ->
//            val response = s3.putObject(request)
//            if (response.eTag != null) {
//                Log.d(TAG, "File uploaded successfully")
//                return SendFileResult.Success
//            }
//        }

        return SendFileResult.FileError
    }


    private fun createCloudFilePathAndName(file: File): String {

        val lastModified = file.lastModified()
        val fileDate = if (lastModified > 0) Date(lastModified) else Date()

        val directoryNameDateFormat = SimpleDateFormat("yyyy-MM-dd/HH", Locale.US)

        val fileNameDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSSS", Locale.US)

        return listOfNotNull(
            config.s3config.logsDirectoryName,
            config.s3config.deviceId,
            directoryNameDateFormat.format(fileDate),
            "${fileNameDateFormat.format(fileDate)}.log",
        ).joinToString(separator = "/")
    }
}

private const val TAG = "S3Sender"
