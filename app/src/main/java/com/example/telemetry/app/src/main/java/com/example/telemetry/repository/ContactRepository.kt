package com.example.telemetry.repository

import android.content.Context
import android.provider.ContactsContract

data class ContactModel(
    val name: String,
    val phoneNumber: String
)

class ContactRepository(private val context: Context) {

    fun getAllContacts(): List<ContactModel> {
        val contactList = mutableListOf<ContactModel>()
        val contentResolver = context.contentResolver
        
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                val number = if (numberIndex != -1) it.getString(numberIndex) ?: "Unknown" else "Unknown"
                
                contactList.add(ContactModel(name = name, phoneNumber = number))
            }
        }

        return contactList
    }
}
