package ru.ivanmurzin.antileaker

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions() // запрашиваю разрешения на чтение и запись файловой системы

        val dirs = listOf( // массив папок, по которым будет происходить поиск
            Environment.getExternalStorageDirectory(), // основная папка хранилища
            filesDir.parentFile?.parentFile ?: filesDir.parentFile, // папка данных всех приложений
            Environment.getRootDirectory(), // папка корня
            Environment.getDataDirectory() // папка данных
        )

        val fileManager = FileManager(dirs)
        val result = fileManager.clearDirectory("tester_folder") // чищю директорию test
        if (!result) // если такой папки не нашлось
            longToast("Папки tester_folder не нашлось!") // сообщаю об этом
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

    private fun longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}