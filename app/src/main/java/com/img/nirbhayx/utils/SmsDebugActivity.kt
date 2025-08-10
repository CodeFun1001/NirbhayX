package com.img.nirbhayx.utils

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SmsDebugActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasReadSmsPermission()) {
            readRecentSms()
        } else {
            requestSmsPermissions()
        }
    }

    private fun hasReadSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_SMS
            ),
            100
        )
    }

    private fun readRecentSms() {
        try {
            val cursor: Cursor? = contentResolver.query(
                Uri.parse("content://sms/"),
                null,
                null,
                null,
                "date DESC LIMIT 10"
            )

            cursor?.use { c ->
                val addressIndex = c.getColumnIndex("address")
                val bodyIndex = c.getColumnIndex("body")
                val dateIndex = c.getColumnIndex("date")
                val typeIndex = c.getColumnIndex("type")

                while (c.moveToNext()) {
                    val address = if (addressIndex >= 0) c.getString(addressIndex) else "Unknown"
                    val body = if (bodyIndex >= 0) c.getString(bodyIndex) else "No body"
                    val date = if (dateIndex >= 0) c.getLong(dateIndex) else 0L
                    val type = if (typeIndex >= 0) c.getInt(typeIndex) else 0

                    val typeString = when (type) {
                        Telephony.Sms.MESSAGE_TYPE_INBOX -> "Received"
                        Telephony.Sms.MESSAGE_TYPE_SENT -> "Sent"
                        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "Outbox"
                        Telephony.Sms.MESSAGE_TYPE_FAILED -> "Failed"
                        else -> "Unknown($type)"
                    }

                    Log.d("SMS_DEBUG", "📱 SMS: $address | $typeString | $body | $date")
                }
            }
        } catch (e: Exception) {
            Log.e("SMS_DEBUG", "Error reading SMS: ${e.message}")
        }
    }

    private fun checkOutboxSms() {
        try {
            val cursor: Cursor? = contentResolver.query(
                Uri.parse("content://sms/outbox"),
                null,
                null,
                null,
                "date DESC"
            )

            cursor?.use { c ->
                Log.d("SMS_DEBUG", "📤 Outbox SMS count: ${c.count}")

                val addressIndex = c.getColumnIndex("address")
                val bodyIndex = c.getColumnIndex("body")

                while (c.moveToNext()) {
                    val address = if (addressIndex >= 0) c.getString(addressIndex) else "Unknown"
                    val body = if (bodyIndex >= 0) c.getString(bodyIndex) else "No body"
                    Log.d("SMS_DEBUG", "📤 Pending SMS to $address: $body")
                }
            }
        } catch (e: Exception) {
            Log.e("SMS_DEBUG", "Error checking outbox: ${e.message}")
        }
    }
}