package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ivanmurzin.antileaker.entivity.TimerProcess
import ru.ivanmurzin.antileaker.utils.MainRecyclerAdapter
import java.text.SimpleDateFormat

const val MY_TIMER_LOGGER = "MY_LOGGER_TAG_TIMER" // тэг, по которому доступны логи приложения
const val MY_FILE_LOGGER = "MY_LOGGER_TAG_FILE" // тэг, по которому доступны логи приложения
const val MY_SERVICE_LOGGER = "MY_LOGGER_TAG_SERVICE" // тэг, по которому доступны логи приложения

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
        var period = 25*60*1000L // считываю период удаления
        var folderName = "AwesomeFolder" // считываю название удаляемой папки
        var expireTime = System.currentTimeMillis() + period // время первого удаления
        val firstProcess = TimerProcess(folderName, period)
        period = 17*60*1000L// считываю период удаления
        folderName = "CoolFolder" // считываю название удаляемой папки
        expireTime = System.currentTimeMillis() + period // время первого удаления
        val secondProcess = TimerProcess(folderName, period)
        val processes = arrayOf(firstProcess, secondProcess)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MainRecyclerAdapter(processes)

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