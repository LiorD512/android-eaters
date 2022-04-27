package com.bupp.wood_spoon_eaters.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import com.bupp.wood_spoon_eaters.WoodSpoonApplication
import java.io.File
import java.io.FileOutputStream


private const val EXTERNAL_STORAGE_URI = "com.android.externalstorage.documents"
private const val DOWNLOADS_URI = "com.android.providers.downloads.documents"
private const val MEDIA_URI = "com.android.providers.media.documents"
private const val GOOGLE_PHOTOS_URI = "com.google.android.apps.photos.content"
private const val GOOGLE_DOCS_URI = "com.google.android.apps.docs.storage"

private fun isExtStorageDocument(uri: Uri): Boolean {
    return EXTERNAL_STORAGE_URI == uri.authority
}

private fun isDownloadsDocument(uri: Uri): Boolean {
    return DOWNLOADS_URI == uri.authority
}

private fun isMediaDocument(uri: Uri): Boolean {
    return MEDIA_URI == uri.authority
}

private fun isGooglePhotosUri(uri: Uri): Boolean {
    return GOOGLE_PHOTOS_URI == uri.authority
}

private fun isGoogleDriveUri(uri: Uri): Boolean {
    return uri.authority!!.contains(GOOGLE_DOCS_URI)
}

fun getFilePath(context: Context, uri: Uri): String? {
    return getFilePathFromUriForNewAPI(context, uri)
}

@SuppressLint("NewApi")
private fun getFilePathFromUriForNewAPI(context: Context, uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(context, uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        if (isExtStorageDocument(uri)) {
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(
                    uri,
                    arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )
                cursor!!.moveToNext()
                val fileName = cursor.getString(0)
                val path =
                    Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                if (!TextUtils.isEmpty(path)) {
                    return path
                }
            } finally {
                cursor?.close()
            }
            val id = DocumentsContract.getDocumentId(uri)
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:".toRegex(), "")
            }
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads"),
                java.lang.Long.valueOf(id)
            )

            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            var contentUri: Uri? = null
            when (type) {
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if (isGoogleDriveUri(uri)) {
        val file = loadFileFromGoogleDocs(uri, context)
        return file?.path
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }

    return if (DocumentsContract.isDocumentUri(context, uri) && isGoogleDriveUri(uri)) {
        val driveFile = loadFileFromGoogleDocs(uri, context)
        driveFile?.path
    } else {
        null
    }
}

private fun loadFileFromGoogleDocs(uri: Uri, context: Context): File? {
    var driveFile: File? = null
    val cursor =
        WoodSpoonApplication.getInstance().contentResolver.query(uri, null, null, null, null)
    if (cursor != null) {
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()

        val name = cursor.getString(nameIndex)
        driveFile = File(context.cacheDir, name)

        val input = context.contentResolver.openInputStream(uri)
        val output = FileOutputStream(driveFile)
        try {
            input.use {
                output.use {
                    input?.copyTo(output)
                }
            }
        } catch (e: Exception) {
            Log.d("ImageUtils", e.message.toString())
        } finally {
            cursor.close()
        }
    }
    return driveFile
}

private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    }catch (ex: java.lang.Exception){
        Log.d("wowImageUtils","ex: $ex")
    } finally {
        cursor?.close()
    }
    return null
}
