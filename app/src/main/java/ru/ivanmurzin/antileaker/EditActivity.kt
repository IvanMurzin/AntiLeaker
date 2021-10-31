package ru.ivanmurzin.antileaker

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.toast
import ru.ivanmurzin.antileaker.alarm.Alarm
import ru.ivanmurzin.antileaker.alarm.MyAlarmManager
import ru.ivanmurzin.antileaker.utils.Storage


class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupUI()
    }

    private fun setupUI() {
        time_picker.setIs24HourView(true)
        val id = intent?.extras?.get("id") ?: ""
        folder_picker.setText(id as CharSequence)
        folder_picker.isEnabled = false
        val storage = Storage(this)
        val alarm = storage.getAlarm(id.toString())
        val previousTime = alarm.interval
        time_picker.hour = (previousTime / 60 / 60 / 1000).toInt()
        time_picker.minute = ((previousTime / 60 / 1000) % 60).toInt()
        cancel_button.setOnClickListener { this.finish() }
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
            val newID = folder_picker.text.toString()
            val newInterval = hour * 60 * 60 * 1000L + minute * 60 * 1000L
            val alarmManager = MyAlarmManager(this)
            alarmManager.deleteAlarm(alarm)
            alarmManager.createAlarm(Alarm(newID, newInterval))
            this.finish()
        }
    }
}