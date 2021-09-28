package ru.ivanmurzin.antileaker

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.toast


class EditActivity : AppCompatActivity() {

    private lateinit var storage: SharedPreferences // локальное хранилище

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        storage = getSharedPreferences("storage", MODE_PRIVATE)
        setupUI()
    }

    private fun setupUI() {
        time_picker.setIs24HourView(true)
        val number = intent?.extras?.get("number") ?: -1
        val previousFolderName = intent?.extras?.get("folderName") ?: ""
        folder_picker.setText(previousFolderName as CharSequence)
        folder_picker.isEnabled = false
        val previousTime = storage.getLong("period$number", 1 * 60 * 60 * 1000L)
        time_picker.hour = (previousTime / 60 / 60 / 1000).toInt()
        time_picker.minute = ((previousTime / 60 / 1000) % 60).toInt()
        cancel_button.setOnClickListener {
            this.finish()
        }
        save_button.setOnClickListener {
            val hour = time_picker.hour
            val minute = time_picker.minute
            if (hour == 0 && minute == 0) {
                toast("Нельзя запускать таймер на 0 минут")
                return@setOnClickListener
            }
            if (folder_picker.text.toString().isEmpty()) {
                toast("Имя папки не может быть пустым")
                return@setOnClickListener
            }
            val folderName = folder_picker.text.toString()
            val time = hour * 60 * 60 * 1000L + minute * 60 * 1000L
            storage.edit {
                putString("folderName$number", folderName)
                putLong("period$number", time)
            }
            this.finish()
        }
    }
}