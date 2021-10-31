package ru.ivanmurzin.antileaker.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.ivanmurzin.antileaker.utils.Storage

@SuppressLint("UnspecifiedImmutableFlag")
class MyAlarmManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, MyReceiver::class.java)
    private val storage = Storage(context)

    fun createAlarm(alarm: Alarm) {
        storage.addAlarm(alarm)
        startAlarm(alarm)
    }

    fun startAlarm(alarm: Alarm) {
        Log.d("RRR start", alarm.id)
        storage.setOn(alarm)
        alarmIntent.putExtra("id", alarm.id)
        alarmIntent.putExtra("clean", true)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            alarmIntent,
            FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarm.expire,
            alarm.interval,
            pendingIntent
        )
        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra("id", alarm.id)
        intent.putExtra("clean", false)
        context.startForegroundService(intent)
    }

    fun deleteAlarm(alarm: Alarm) {
        storage.deleteAlarm(alarm.id)
        cancelAlarm(alarm)
    }

    fun cancelAlarm(alarm: Alarm) {
        storage.setOff(alarm)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            alarmIntent,
            FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}