import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun Context.savePhotoToInternalStorage(uri: Uri): String? {
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val fileName = "pet_${UUID.randomUUID()}.jpg"
        val file = File(filesDir, fileName)

        val outputStream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        inputStream?.close()

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}