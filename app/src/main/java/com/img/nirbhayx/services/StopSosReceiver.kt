package com.img.nirbhayx.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class StopSosReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stopIntent = Intent(context, SosService::class.java).apply {
            action = "STOP_SOS"
        }
        ContextCompat.startForegroundService(context, stopIntent)
    }
}
