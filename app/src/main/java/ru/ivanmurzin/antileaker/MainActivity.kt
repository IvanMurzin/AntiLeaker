package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import ru.ivanmurzin.antileaker.alarm.Alarm
import ru.ivanmurzin.antileaker.alarm.AlarmService
import ru.ivanmurzin.antileaker.utils.MainRecyclerAdapter
import ru.ivanmurzin.antileaker.utils.Storage
import java.io.File
import java.io.RandomAccessFile


class MainActivity : AppCompatActivity() {

    private lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAllFilesPermission()
        requestManifestPermissions()
        storage = Storage(this)
        if (storage.isEmpty()) {
            storage.addAlarm(Alarm("WhatsApp", 60 * 1000L))
            storage.addAlarm(Alarm("Telegram", 60 * 1000L))
        }
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
            doAsync { createAndDeleteTrashFile() }
            finish()
            startActivity(intent)
            toast("Файл на ${(getAvailableInternalMemory() * 0.7 / 1024 / 1024 / 1024).toInt()}Гб успено создан")
        }
        button_add.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
    }

    private fun getAvailableInternalMemory(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks * blockSize
    }

    private fun createAndDeleteTrashFile() {
        val availableSize = getAvailableInternalMemory()
        val size = (availableSize * 0.7 / 1024 / 1024 / 1024).toInt()
        val dir = Environment.getExternalStorageDirectory()
        val file = File(dir.absolutePath + "/trash.txt")
        val trashFile = RandomAccessFile(file.absolutePath, "rw")
        trashFile.setLength(size * 1024 * 1024 * 1024L)
        trashFile.close()
        file.delete()
    }

    fun requestAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                longToast("Пожалуйста, предоставте приложению доступ к файлам")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
        }
    }

    private fun requestManifestPermissions() {
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