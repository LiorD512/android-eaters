package com.bupp.wood_spoon_eaters.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL

class PutActionManager(val context: Context) {
    private var preSignedUrl: String? = null
    private var listener: PutActionListener? = null

    interface PutActionListener {
        fun onPutDone(type: Int, preSignedUrl: Uri, isLast: Boolean)
    }

    internal inner class PutActionASYNCTask(
        private val context: Context,
        private val type: Int,
        private val urlStr: String,
        private val imgUri: Uri,
        private val isLast: Boolean
    ) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            putMedia(context, type, urlStr, imgUri, isLast)
            return "Executed"
        }

        override fun onPostExecute(result: String) {
            Log.d("wow", "asynctask result: $result")
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        override fun onPreExecute() {}

        override fun onProgressUpdate(vararg values: Void) {}
    }


    fun initPutAction(type: Int, urlStr: String, imgUri: Uri, isLast: Boolean, listener: PutActionListener?) {
        this.listener = listener
        PutActionASYNCTask(context, type, urlStr, imgUri, isLast).execute("")
    }

    fun putMedia(context: Context, type: Int, urlStr: String, imgUri: Uri, isLast: Boolean) {
        when (type) {
            Constants.PUT_ACTION_COOK_THUMBNAIL -> {
                putImage(context, type, urlStr, imgUri, isLast)
            }

            Constants.PUT_ACTION_COOK_VIDEO -> {
                putVideo(context, type, urlStr, imgUri, isLast)
            }
        }
    }

    private fun putVideo(context: Context, type: Int, urlStr: String, imgUri: Uri, isLast: Boolean) {
        this.preSignedUrl = urlStr
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.requestMethod = "PUT"

            val outputStream = connection.outputStream
            val fileInputStream = FileInputStream(getPath(context, imgUri))
            pipe(fileInputStream, outputStream)

            val responseCode = connection.responseCode
            Log.d("wowPutImage", "RESPONSE: $responseCode")

            if (listener != null) {
                listener!!.onPutDone(type, imgUri, isLast)
            }
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun putImage(context: Context, type: Int, urlStr: String, imgUri: Uri, isLast: Boolean) {
        this.preSignedUrl = urlStr
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.requestMethod = "PUT"
            val outputStream = connection.outputStream
            var inputStream: FileInputStream? = null
            var b: Bitmap? = null
            try {
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                inputStream = FileInputStream(getPath(context, imgUri)!!)
                BitmapFactory.decodeStream(inputStream, null, o)
                inputStream.close()
                var scale = 1
                if (o.outHeight > 1000 || o.outWidth > 1000) {
                    scale = Math.pow(
                        2.0,
                        Math.ceil(
                            Math.log(
                                1000 / Math.max(
                                    o.outHeight,
                                    o.outWidth
                                ).toDouble()
                            ) / Math.log(0.5)
                        ).toInt().toDouble()
                    ).toInt()
                }
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                inputStream = FileInputStream(getPath(context, imgUri)!!)
                b = BitmapFactory.decodeStream(inputStream, null, o2)
                inputStream.close()
            } catch (e: Exception) {
                Log.d("wowExcwption", "error: " + e.message)
            }

            val bos = ByteArrayOutputStream()
            b!!.compress(Bitmap.CompressFormat.PNG, 80 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()
            val bs = ByteArrayInputStream(bitmapdata)

            pipe(bs, outputStream)

            outputStream.close()
            inputStream!!.close()
            bos.close()
            bs.close()

            val responseCode = connection.responseCode
            Log.d("wowPutImage", "RESPONSE: $responseCode")

            if (listener != null) {
                listener!!.onPutDone(type, imgUri, isLast)
            }
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun pipe(inputStream: InputStream, os: OutputStream) {
        val buffer = ByteArray(1024)
        var n: Int = inputStream.read(buffer)
        while (n > -1) {
            os.write(buffer, 0, n)
            n = inputStream.read(buffer)
        }

    }

    companion object {
        fun getPath(context: Context, uri: Uri): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                    )

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
            } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                return uri.path
            }// File
            return null
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }
}
