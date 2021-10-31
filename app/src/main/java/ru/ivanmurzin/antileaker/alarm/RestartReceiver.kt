package ru.ivanmurzin.antileaker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import ru.ivanmurzin.antileaker.utils.Storage
import java.io.File

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RRR restart", "it")
        doAsync {
            val storage = Storage(context)
            val alarmManager = MyAlarmManager(context)
            val dir = Environment.getExternalStorageDirectory()
            val file = File(dir.absolutePath + "/" + "falling" + ".txt")
            file.createNewFile()
            uiThread {
                storage.getAlarms().forEach {
                    Log.d("RRR restart", it.id + it.isOn)
                    if (it.isOn) alarmManager.startAlarm(it)
                }
            }
        }
        //context.startForegroundService(Intent(context, AlarmService::class.java))
    }
}