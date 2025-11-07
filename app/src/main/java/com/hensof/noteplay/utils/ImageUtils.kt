package com.hensof.noteplay.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {
    
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val fileName = "note_image_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, "images")
                if (!file.exists()) {
                    file.mkdirs()
                }
                
                val imageFile = File(file, fileName)
                val outputStream = FileOutputStream(imageFile)
                
                stream.copyTo(outputStream)
                outputStream.close()
                stream.close()
                
                imageFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getImageUri(context: Context, filePath: String): Uri? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun deleteImage(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
