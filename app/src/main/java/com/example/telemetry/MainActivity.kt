package com.example.telemetry

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.telemetry.repository.ContactRepository
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch contacts and process
        fetchContactsAndProcess()
    }

    private fun fetchContactsAndProcess() {
        val contactRepository = ContactRepository(this)
        val contacts = contactRepository.getAllContacts()

        // JSONObject.put ambiguity දෝෂය මඟහරවා ගැනීමට values වලට 'as Any' යොදා ඇත
        val json = JSONObject()
        json.put("total_contacts", contacts.size as Any)
        
        // අවශ්‍ය නම් මෙහිදී අනෙකුත් දත්ත එකතු කර ටෙලිග්‍රෑම් වෙත යැවීමේ කටයුතු සිදු කළ හැක
    }
}
