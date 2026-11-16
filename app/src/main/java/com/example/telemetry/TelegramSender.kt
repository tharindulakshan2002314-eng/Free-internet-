package com.example.telemetry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TelegramSender {

    suspend fun sendToTelegram(jsonData: String) {
        withContext(Dispatchers.IO) {
            try {
                val botToken = "9259742685:AAGMARnF3JH4BQMaj81SwnJkJukOP8agKo" //[span_0](start_span)[span_0](end_span)
                val chatId = "8626384298" //[span_1](start_span)[span_1](end_span)
                
                val encodedMessage = URLEncoder.encode(jsonData, "UTF-8")
                val urlString = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedMessage"

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                connection.disconnect()
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
