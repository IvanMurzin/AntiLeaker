package ru.ivanmurzin.antileaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
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
        val id = intent?.extras?.get("id") ?: "Моя папка"
        val storage = Storage(this)
        val alarm = storage.getAlarm(id.toString())
        val previousTime = alarm.interval
        val alarmManager = MyAlarmManager(this)
        val isEditable = (id != "Telegram" && id != "WhatsApp")
        folder_picker.setText(id as CharSequence)
        folder_picker.isEnabled = isEditable
        button_delete.visibility = if (isEditable) View.VISIBLE else View.GONE
        button_delete.setOnClickListener {
            alert("Вы действительно хотите удалить этот таймер?", "Удалить таймер") {
                positiveButton("Удалить") {
                    alarmManager.deleteAlarm(alarm)
                    finish()
                }
                negativeButton("Отмена") {}
            }.show()
        }

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
            if (folder_picker.text.length < 4) {
                toast("Имя папки не может быть меньше 4 символов")
                return@setOnClickListener
            }
            val newID = folder_picker.text.toString()
            val newInterval = hour * 60 * 60 * 1000L + minute * 60 * 1000L
            alarmManager.deleteAlarm(alarm)
            alarmManager.createAlarm(Alarm(newID, newInterval))
            finish()
        }
    }
}