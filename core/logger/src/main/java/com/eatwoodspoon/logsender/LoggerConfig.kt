package com.eatwoodspoon.logsender

import android.content.Context
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Keep
data class LoggerConfig(
    val enabled: Boolean = true,
    val maxDirectorySize: Long = (20 * 1024 * 1024).toLong(), // Default for 20MB
    val maxFileSize: Long = (50 * 1024).toLong(), // Default for 50KB
    val maxLogBufferSize: Int = 2000,
    val maxConcurrentNetworkRequests: Int = 3,
    val maxSendRetries: Int = 3,
    val s3config: S3SenderConfig,
    val enableStatsReporting: Boolean = true,
) : Parcelable

@Parcelize
@Keep
data class S3SenderConfig(
    val credentials: Credentials,
    val deviceId: String,
    val logsDirectoryName: String
) : Parcelable {
    @Parcelize
    @Keep
    data class Credentials(
        val awsAccessKey: String,
        val awsSecretKey: String,
        val s3BucketName: String,
        val region: String
    ) : Parcelable {
        companion object {
            fun fromAssets(context: Context, fileName: String): Credentials {
                val properties = Properties()
                properties.load(context.assets.open(fileName))
                return Credentials(
                    awsAccessKey = properties.getProperty("awsAccessKey"),
                    awsSecretKey = properties.getProperty("awsSecretKey"),
                    s3BucketName = properties.getProperty("s3BucketName"),
                    region = properties.getProperty("region")
                )
            }
        }
    }
}
