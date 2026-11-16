package com.example.telemetry.scanner

import android.content.Context
import android.provider.MediaStore
import org.json.JSONArray
import org.json.JSONObject

object MediaStoreScanner {

    fun scanMedia(context: Context): JSONArray {
        val result = JSONArray()
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID
        )

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val mimeIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val sizeIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val item = JSONObject().apply {
                    put("name", cursor.getString(nameIdx))
                    put("mime_type", cursor.getString(mimeIdx))
                    put("size_bytes", cursor.getLong(sizeIdx))
                    put("date_added", cursor.getLong(dateIdx))
                    put("type", "image")
                }
                result.put(item)
            }
        }
        return result
    }
}
