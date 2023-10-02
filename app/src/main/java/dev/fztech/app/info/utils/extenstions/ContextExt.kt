package dev.fztech.app.info.utils.extenstions

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import dev.fztech.app.info.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Find the closest Activity in a given Context.
 */
@Suppress("unused")
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

/**Save Bitmap To Gallery
 * @param bitmap The bitmap to be saved in Storage/Gallery*/
fun Context.saveBitmapImage(bitmap: Bitmap, fileNameToSave: String) {
    try {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            loge(Environment.DIRECTORY_PICTURES)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/${getString(R.string.app_name)}")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
            val outputStream = contentResolver.openOutputStream(uri) ?: return

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            values.put(MediaStore.Images.Media.IS_PENDING, false)
            contentResolver.update(uri, values, null, null)

            Toast.makeText(this, "Icon Saved...", Toast.LENGTH_SHORT).show()
        } else {
            val imageFileFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$fileNameToSave.png"
            val imageFile = File(imageFileFolder, mImageName)
            val outputStream: OutputStream = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        loge("saveBitmapImage: $e")
    }
}

fun Context.saveApk(path: String, fileNameToSave: String) {
    try {
        val file = File(path)
        val newPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$fileNameToSave.apk"
        val newFile = File(newPath)
        if (newFile.exists()) {
            Toast.makeText(this, "Apk has been saved", Toast.LENGTH_SHORT).show()
            return
        }
        val inputStream = FileInputStream(file)
        val outputStream = FileOutputStream(newFile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(inputStream, outputStream)
        } else {
            val buffer = ByteArray(4096)
            var length = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            inputStream.close()
            outputStream.close()
        }

        Toast.makeText(this, "Apk saved successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "Failed to save apk!", Toast.LENGTH_LONG).show()
    }

}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

