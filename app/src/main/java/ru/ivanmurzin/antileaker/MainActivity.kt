package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_main.*
import ru.ivanmurzin.antileaker.service.TimerService

class MainActivity : AppCompatActivity() {

    private lateinit var storage: SharedPreferences // локальное хранилище

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions() // запрашиваю разрешения на чтение и запись файловой системы
        storage = getSharedPreferences("storage", MODE_PRIVATE)
        setupUI()
    }

    private fun setupUI() {
        button_apply.setOnClickListener {
            val period = field_period.text.toString().toLong() * 1000 // считываю период удаления
            val folderName = field_folder.text.toString() // считываю название удаляемой папки
            text_info.text = "Папка: $folderName\nУдаляется каждые: ${period / 1000} sec"
            val expireTime = System.currentTimeMillis() + period // время первого удаления
            storage.edit {
                putLong("expireTime", expireTime) // записываю время первого удаления
                putLong("period", period) // записываю период удаления
                putString("folderName", folderName) // записываю название удаляемой папки
            }
            startService(Intent(this, TimerService::class.java)) // запускаю сервис
        }
    }


    private fun requestPermissions() {
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

}