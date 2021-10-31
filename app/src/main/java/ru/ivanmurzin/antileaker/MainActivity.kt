package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ivanmurzin.antileaker.alarm.AlarmService
import ru.ivanmurzin.antileaker.utils.MainRecyclerAdapter
import ru.ivanmurzin.antileaker.utils.Storage


class MainActivity : AppCompatActivity() {

    private lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions() // запрашиваю разрешения на чтение и запись файловой системы
        storage = Storage(this)
//        storage.clean()
//        storage.addAlarm(Alarm("WhatsApp", 60 * 1000L))
//        storage.addAlarm(Alarm("Telegram", 60 * 1000L))
        setupUI()
    }

    override fun onResume() {
        recycler.adapter = MainRecyclerAdapter(storage.getAlarms(), this)
        super.onResume()
    }

    private fun setupUI() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MainRecyclerAdapter(storage.getAlarms(), this)

        button_red.setOnClickListener {

            val intent = intent
            finish()
            startActivity(intent)
        }
    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
        }

        val necessaryPermissions = arrayOf( // массив необходимых разрешений
            Manifest.permission.READ_EXTERNAL_STORAGE, // разрешение на чтение хранилища
            Manifest.permission.WRITE_EXTERNAL_STORAGE // разрешение на запись в хранилище
        )
        val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // если пользователь еще не предоставел разрешений, то запршиваю их
        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, necessaryPermissions, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        storage.getAlarms().forEach { if (it.isOn) return }
        stopService(Intent(this, AlarmService::class.java))
    }

}