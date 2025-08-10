package com.img.nirbhayx.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class SosConfirmedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        Intent(context, SosService::class.java).let {
            context.startForegroundService(it)
        }
    }
}