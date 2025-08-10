package com.img.nirbhayx.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.img.nirbhayx.data.EmergencyContact

object SmsUtils {

    private const val SMS_SENT_ACTION = "SMS_SENT"
    private const val SMS_DELIVERED_ACTION = "SMS_DELIVERED"

    fun sendSmsToContacts(context: Context, message: String, contacts: List<EmergencyContact>) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("SMS", "❌ SMS permission not granted")
            return
        }

        val smsManager = SmsManager.getDefault()

        val sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val phoneNumber = intent?.getStringExtra("phone_number") ?: "Unknown"
                when (resultCode) {
                    android.app.Activity.RESULT_OK -> {
                        Log.d("SMS", "✅ SMS successfully sent to $phoneNumber")
                    }

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        Log.e("SMS", "❌ Generic failure sending SMS to $phoneNumber")
                    }

                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        Log.e("SMS", "❌ No service - SMS to $phoneNumber failed")
                    }

                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        Log.e("SMS", "❌ Null PDU - SMS to $phoneNumber failed")
                    }

                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        Log.e("SMS", "❌ Radio off - SMS to $phoneNumber failed")
                    }
                }
            }
        }

        val deliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val phoneNumber = intent?.getStringExtra("phone_number") ?: "Unknown"
                when (resultCode) {
                    android.app.Activity.RESULT_OK -> {
                        Log.d("SMS", "📨 SMS delivered to $phoneNumber")
                    }

                    android.app.Activity.RESULT_CANCELED -> {
                        Log.e("SMS", "❌ SMS delivery failed to $phoneNumber")
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            sentReceiver,
            IntentFilter(SMS_SENT_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            context,
            deliveredReceiver,
            IntentFilter(SMS_DELIVERED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        for (contact in contacts) {
            try {
                val sentIntent = PendingIntent.getBroadcast(
                    context,
                    contact.phoneNumber.hashCode(),
                    Intent(SMS_SENT_ACTION).apply {
                        putExtra("phone_number", contact.phoneNumber)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val deliveredIntent = PendingIntent.getBroadcast(
                    context,
                    contact.phoneNumber.hashCode() + 1000,
                    Intent(SMS_DELIVERED_ACTION).apply {
                        putExtra("phone_number", contact.phoneNumber)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val parts = smsManager.divideMessage(message)

                if (parts.size == 1) {
                    smsManager.sendTextMessage(
                        contact.phoneNumber,
                        null,
                        message,
                        sentIntent,
                        deliveredIntent
                    )
                } else {
                    val sentIntents = arrayListOf<PendingIntent>()
                    val deliveredIntents = arrayListOf<PendingIntent>()

                    for (i in parts.indices) {
                        sentIntents.add(sentIntent)
                        deliveredIntents.add(deliveredIntent)
                    }

                    smsManager.sendMultipartTextMessage(
                        contact.phoneNumber,
                        null,
                        parts,
                        sentIntents,
                        deliveredIntents
                    )
                }

                Log.d("SMS", "📤 SMS queued for ${contact.name} (${contact.phoneNumber})")

            } catch (e: Exception) {
                Log.e("SMS", "❌ Failed to send SMS to ${contact.name}: ${e.message}")
            }
        }
    }

}