package ru.ivanmurzin.antileaker.alarm

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.jetbrains.anko.doAsync
import ru.ivanmurzin.antileaker.MainActivity
import ru.ivanmurzin.antileaker.R
import java.io.File
import java.util.*


class AlarmService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val clean = intent?.extras?.getBoolean("clean") ?: false
        val id = intent?.extras?.getString("id") ?: "EXCEPTIONFOUNDED"

        Log.d("RRR onStartCommand", "$id $clean")
        if (!clean) doAsync { onAlarm(id) }

        val notification = createNotification(id)
        startForeground(1, notification)
        return START_STICKY
    }

    private fun onAlarm(id: String) {
        val dir = Environment.getExternalStorageDirectory()
        val date = Date().toString().replace(":", "-").substring(11, 19)
        val file = File(dir.absolutePath + "/" + id + date + ".txt")
        //Log.d("RRR", file.absolutePath)
        file.createNewFile()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(id: String): Notification {
        val channelId = "my_service$id"
        createNotificationChannel(channelId)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        return NotificationCompat
            .Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel(channelId: String) {
        val channel = NotificationChannel(
            channelId,
            "Background Service",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    override fun onBind(intent: Intent): IBinder? = null
}