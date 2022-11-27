package com.arysugiarto.konsulin.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.arysugiarto.konsulin.util.Const.File.Media.FILE_FIELD
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024

private fun String.getExtension() = this.let {
    val dot = lastIndexOf(".")
    if (dot >= 0) substring(dot) else emptyString
}

private fun String.isLocal() = !this.startsWith("http://") && !this.startsWith("https://")

private fun Uri?.isMedia() = this?.let {
    "media".equals(authority, ignoreCase = true)
}

private val File?.mimeType get() = this?.let {
    val extension = name.getExtension().orEmpty()
    if (extension.isNotEmpty()) MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension.substring(1)) else "application/octet-stream"
}

private fun Uri.getMimeType(context: Context) = this.let {
    val file = File(getPath(context).toString())
    file.mimeType
}

private fun Uri.isExternalStorageDocument() =
    "com.android.externalstorage.documents" == authority

private fun Uri.isDownloadsDocument() =
    "com.android.providers.downloads.documents" == authority

private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

private fun Uri.isGooglePhotosUri() =
    "com.google.android.apps.photos.content" == authority

private fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = uri?.let {
            context.contentResolver.query(
                it, projection, selection, selectionArgs,
                null
            )
        }
        if (cursor != null && cursor.moveToFirst()) {
            val indexColumn: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(indexColumn)
        }
    } finally {
        cursor?.close()
    }
    return null
}

private fun Uri.getPath(context: Context): String? {
    val isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    if (isLollipop && DocumentsContract.isDocumentUri(context, this)) {
        if (isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument()) {
            val id = DocumentsContract.getDocumentId(this)
            val contentUri: Uri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null

            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(context, contentUri, selection, selectionArgs)
        } else {
            return DocumentsContract.getDocumentId(this)
        }
    } else if ("content".equals(
            this.scheme,
            ignoreCase = true
        )
    ) {
        return if (isGooglePhotosUri()) this.lastPathSegment else getDataColumn(
            context,
            this,
            null,
            null
        )
    } else if ("file".equals(this.scheme, ignoreCase = true)) {
        return this.path
    }
    return null
}

val Context.filePath: String get() {
    val timeStamp = SimpleDateFormat.getDateTimeInstance()
        .format(Date())
        .replace(" ", "_")

    val appName = applicationInfo
        .loadLabel(packageManager)
        .toString()
        .replace(" ", "_")
        .lowercase(Locale.ROOT)

    return "${appName}_JPEG_${timeStamp}.jpg"
}

fun createImageFile(
    context: Context
): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val timeStamp = SimpleDateFormat.getDateTimeInstance()
        .format(Date())
        .replace(",", "")
        .replace(" ", "_")

    val appName = context.applicationInfo
        .loadLabel(context.packageManager)
        .toString()
        .replace(" ", "_")
        .lowercase(Locale.ROOT)

    return File.createTempFile(
        "${appName}_JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

fun Uri?.copyAndGetPath(
   context: Context
): String {
    val contentResolver = context.contentResolver

    val file = File(
        (context.applicationInfo.dataDir + File.separator + context.filePath)
    )

    try {
        val inputStream = contentResolver.openInputStream(this ?: return emptyString)
            ?: return emptyString
        val outputStream = FileOutputStream(file)

        val byteSize = ByteArray(1024)
        var length: Int

        while (inputStream.read(byteSize).also { length = it } > 0) {
            outputStream.write(byteSize, 0, length)
        }

        outputStream.close()
        inputStream.close()
    } catch (ignore: IOException) {
        return emptyString
    }

    return file.absolutePath
}


fun String.fileOf() = if (containWhiteSpace()) null else File(this.orEmpty)

fun Uri.getFile(context: Context) = this.let {
    val providerPath = getPath(context).orEmpty()
    if(providerPath.isLocal()) File(providerPath) else File(path.orEmpty)
}

fun Context?.getFile(uri: Uri) = this?.let {
    val providerPath = uri.getPath(this).orEmpty()
    if(providerPath.isLocal()) File(providerPath) else File(uri.path.orEmpty)
}

fun Uri.fileOf(): File = File(path.orEmpty)

fun String.partFile(fieldName: String = FILE_FIELD) = fileOf()
    ?.asRequestBody(Const.MediaType.image)
    ?.let { requestBody ->
        val file = fileOf()
        val fileName = file?.name + "." + file?.extension

        MultipartBody.Part.Companion.createFormData(
            fieldName,
            fileName,
            requestBody
        )
    }

fun File?.partFile(fieldName: String = FILE_FIELD) = this
    ?.asRequestBody(Const.MediaType.image)
    ?.let { requestBody ->
        MultipartBody.Part.Companion.createFormData(
            fieldName,
            name,
            requestBody
        )
    }
