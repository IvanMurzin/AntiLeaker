package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ivanmurzin.antileaker.entivity.TimerProcess
import ru.ivanmurzin.antileaker.utils.MainRecyclerAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var storage: SharedPreferences // локальное хранилище

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions() // запрашиваю разрешения на чтение и запись файловой системы
        storage = getSharedPreferences("storage", MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    private fun setupUI() {
        if (storage.getInt("count", 0) == 0)
            storage.edit {
                putInt("count", 2)
                putString("folderName1", "AwesomeFolder")
                putLong("period1", 2 * 60 * 60 * 1000L)
                putString("folderName2", "CoolFolder")
                putLong("period2", 30 * 60 * 1000L)
            }
        val count = storage.getInt("count", 0)
        val processes = mutableListOf<TimerProcess>()
        for (i in 1..count) {
            val folderName = storage.getString("folderName$i", "SomeError")!!
            val time = storage.getLong("period$i", 1 * 60 * 60 * 1000L)
            processes.add(TimerProcess(folderName, time))
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MainRecyclerAdapter(processes)

        button_red.setOnClickListener {
            storage.edit { clear() }
            val intent = intent
            finish()
            startActivity(intent)
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