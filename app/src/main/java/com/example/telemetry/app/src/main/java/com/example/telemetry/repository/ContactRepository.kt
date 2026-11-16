package com.example.telemetry.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import org.json.JSONArray
import org.json.JSONObject

class ContactRepository(private val context: Context) {

    fun fetchContactsJson(): JSONArray {
        val contactsMap = mutableMapOf<Long, MutableList<String>>()
        val namesMap = mutableMapOf<Long, String>()
        val contentResolver: ContentResolver = context.contentResolver

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        )

        cursor?.use {
            val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getLong(idIdx)
                val name = it.getString(nameIdx) ?: "Unknown"
                val number = it.getString(numIdx) ?: continue

                namesMap[id] = name
                contactsMap.getOrPut(id) { mutableListOf() }.add(number)
            }
        }

        val result = JSONArray()
        namesMap.forEach { (id, name) ->
            val item = JSONObject().apply {
                put("contact_id", id)
                put("display_name", name)
                put("phone_numbers", JSONArray(contactsMap[id] ?: emptyList()))
            }
            result.put(item)
        }
        return result
    }
}
