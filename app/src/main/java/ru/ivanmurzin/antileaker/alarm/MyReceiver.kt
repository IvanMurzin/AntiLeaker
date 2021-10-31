package ru.ivanmurzin.antileaker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.extras?.getString("id") ?: "EXCEPTIONFOUNDED???"
        Log.d("RRR onReceive", id)
        val serviceIntent = Intent(context, AlarmService::class.java)
        serviceIntent.putExtra("clean", false)
        serviceIntent.putExtra("id", id)
        context?.startService(serviceIntent)
    }
}