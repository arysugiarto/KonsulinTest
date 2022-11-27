package com.arysugiarto.konsulin.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arysugiarto.konsulin.R
import com.arysugiarto.konsulin.util.DialogStyle
import com.arysugiarto.konsulin.util.dialog
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

@AndroidEntryPoint
class   ImagePickerActivity : AppCompatActivity() {
    companion object {
        private val TAG = ImagePickerActivity::class.java.simpleName
        const val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        const val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        const val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        const val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        const val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        const val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        const val INTENT_BITMAP_MAX_WIDTH = "max_width"
        const val INTENT_BITMAP_MAX_HEIGHT = "max_height"

        private var lockAspectRatio = false
        private var setBitmapMaxWidthHeight = false
        private var ASPECT_RATIO_X = 16
        private var ASPECT_RATIO_Y = 9
        private var bitmapMaxWidth = 1000
        private var bitmapMaxHeight = 1000
        private var IMAGE_COMPRESSION = 85
        var fileName = ""

        const val REQUEST_IMAGE_CAPTURE = 0
        const val REQUEST_GALLERY_IMAGE = 1
        const val REQUEST_CAMERA_IMAGE_GALLERY_CODE = 2
        const val REQUEST_IMAGE_GALLERY_CODE = 3
    }

    interface PickerOptionListener {
        fun onTakeCameraSelected()
        fun onChooseGallerySelected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        intent?.apply {
            ASPECT_RATIO_X = getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X)
            ASPECT_RATIO_Y = getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y)
            IMAGE_COMPRESSION = getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION)
            lockAspectRatio = getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
            setBitmapMaxWidthHeight = getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
            bitmapMaxWidth = getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth)
            bitmapMaxHeight = getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight)
        }

        val requestCode = intent?.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1)

        if (requestCode == REQUEST_IMAGE_CAPTURE) takeCameraImage()
        else chooseImageFromGallery()

    }

    fun showImagePickerOptions(context: Context, listener: PickerOptionListener) {
        context.dialog(
            title = context.getString(R.string.image_picker_select_image_dialog_title),
            items = arrayOf("Take Camera Picture", "Choose From Gallery"),
            style = DialogStyle.SIMPLE,
            onClickedAction = { _, position ->
                when (position) {
                    0 -> listener.onTakeCameraSelected()
                    1 -> listener.onChooseGallerySelected()
                }
            }
        ).show()
    }

    private fun confirmPermissionAfterDeniedCamera() {
        dialog(
            centered = true,
            title = getString(R.string.image_picker_information),
            message = getString(R.string.image_picker_no_gallery_access_warning),
            onClickedAction = { _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_CAMERA_IMAGE_GALLERY_CODE
                )
            }
        ).show()
    }

    private fun confirmPermissionAfterDeniedGallery() : AlertDialog {
        return dialog(
            title = getString(R.string.image_picker_information),
            message = getString(R.string.image_picker_no_gallery_access_warning),
            onClickedAction = { _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_IMAGE_GALLERY_CODE
                )
            }
        ).setNegativeButton(getString(R.string.image_picker_close)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }.setCancelable(true).create()
    }

    @AfterPermissionGranted(REQUEST_CAMERA_IMAGE_GALLERY_CODE)
    private fun takeCameraImage() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            fileName = System.currentTimeMillis().toString() + ".jpg"
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName))

            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "We need Camera and Storage Permission To Access Camera",
                REQUEST_CAMERA_IMAGE_GALLERY_CODE,
                *permissions
            )
        }
    }

    @AfterPermissionGranted(REQUEST_IMAGE_GALLERY_CODE)
    private fun chooseImageFromGallery() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (EasyPermissions.hasPermissions(this, permission)) {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickPhoto.type = "image/*"
            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "We Need Storage Permission To Access Gallery",
                REQUEST_IMAGE_GALLERY_CODE,
                permission
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    getCacheImagePath(fileName)?.let { cropImage(it) }
                } else {
                    setResultCancelled()
                }
            }

            REQUEST_GALLERY_IMAGE -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val imageUri = data?.data
                    imageUri?.let { cropImage(it) }
                } else {
                    setResultCancelled()
                }
            }

            UCrop.REQUEST_CROP -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    handleUCropResult(data)
                } else {
                    setResultCancelled()
                }
            }

            UCrop.RESULT_ERROR -> {
                val cropError = data?.let { UCrop.getError(it) }
                Log.e(TAG, "Crop error: $cropError")
                setResultCancelled()
            }

            else -> setResultCancelled()
        }
    }

    private fun cropImage(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, queryName(contentResolver, sourceUri).toString()))
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.purple))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.purple))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple))
        if (lockAspectRatio) options.withAspectRatio(
            ASPECT_RATIO_X.toFloat(),
            ASPECT_RATIO_Y.toFloat()
        )
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        resultUri?.let { setResultOk(it) }
    }

    private fun setResultOk(imagePath: Uri) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            image
        )
    }

    @SuppressLint("Recycle")
    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun clearCache(context: Context) {
        val path = File(context.externalCacheDir, "camera")
        if (path.exists() && path.isDirectory) {
            for (child in path.listFiles().orEmpty()) {
                child.delete()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}