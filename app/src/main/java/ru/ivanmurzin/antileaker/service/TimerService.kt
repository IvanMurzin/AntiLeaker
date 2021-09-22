package ru.ivanmurzin.antileaker.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import androidx.core.content.edit
import ru.ivanmurzin.antileaker.MY_SERVICE_LOGGER
import ru.ivanmurzin.antileaker.utils.FileManager
import ru.ivanmurzin.antileaker.utils.Timer

class TimerService : Service() {

    private lateinit var storage: SharedPreferences // локальное хранилище
    private lateinit var timer: Timer // мой таймер


    override fun onCreate() {
        Log.d(MY_SERVICE_LOGGER, "Service created")
        storage = getSharedPreferences("storage", MODE_PRIVATE)
        // по умолчанию время истечения и период - 2 минуты позже
        val expireTime = storage.getLong("expireTime", System.currentTimeMillis() + 2 * 60 * 1000L)
        val period = storage.getLong("period", 2 * 60 * 1000L)
        val time = expireTime - System.currentTimeMillis() // сколько осталось до обновления таймера
        val dir = Environment.getExternalStorageDirectory() // папка поиска
        val fileManager = FileManager(dir)
        val folderName = storage.getString("folderName", "tester_folder")!!
        timer = Timer(time, period) {
            // по окончании таймера перезаписываю время истечения
            storage.edit { putLong("expireTime", System.currentTimeMillis() + period) }
            fileManager.clearDirectory(folderName) // чищю директорию
        }
        timer.start() // запускаю таймер
        super.onCreate()
    }


    /** При закрытии приложения почему-то сразу же вызывается onDestroy,
     * поэтому, чтобы не прекращать работу сервиса, я проверяю, кто вызвал onDestroy
     * если это был я, то по ключу fromMain в storage лежит true => надо закрыть таймер,
     * если это проделки системы, то там будет лежать false,
     * а значит и таймер закрывать не надо **/

    override fun onDestroy() {
        Log.d(MY_SERVICE_LOGGER, "onDestroy")
        val isFromMain = storage.getBoolean("fromMain", false) // я вызвал onDestroy?
        if (isFromMain) { // если да, то
            timer.cancel() // закрываю таймер
            storage.edit {
                putBoolean(
                    "fromMain",
                    false
                )
            } // помечаю, что остальные вызовы не мои
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}