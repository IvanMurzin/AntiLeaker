package ru.ivanmurzin.antileaker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import ru.ivanmurzin.antileaker.alarm.Alarm

class Storage(context: Context) {
    private val storage = context.getSharedPreferences("storage", MODE_PRIVATE)


    fun getIds() =
        storage.getString("ids", "")!!.split("#").run { subList(0, lastIndex) }.sorted()

    fun getAlarms() = getIds().map { getAlarm(it) }

    fun addAlarm(alarm: Alarm) {
        storage.edit().apply {
            putString("ids", pushId(alarm.id))
            putLong("${alarm.id} Interval", alarm.interval)
            putLong("${alarm.id} Expire", System.currentTimeMillis() + alarm.interval)
            putBoolean("${alarm.id} isOn", alarm.isOn)
            apply()
        }
    }

    fun deleteAlarm(id: String) {
        storage.edit().apply {
            putString("ids", removeId(id))
            remove("$id Interval")
            remove("$id Expire")
            remove("$id isOn")
            apply()
        }
    }

    private fun removeId(id: String): String {
        val ids = storage.getString("ids", "")!!.replace("$id#", "")
        return if (ids.last() != '#') "$ids#" else ids
    }

    fun getAlarm(id: String) = Alarm(
        id,
        storage.getLong("$id Interval", 60 * 1000L),
        storage.getLong("$id Expire", 60 * 1000L),
        storage.getBoolean("$id isOn", false)
    )


    fun setOn(alarm: Alarm) {
        storage.edit().apply() {
            putBoolean("${alarm.id} isOn", true)
            apply()
        }
    }

    fun setOff(alarm: Alarm) {
        storage.edit().apply() {
            putBoolean("${alarm.id} isOn", false)
            apply()
        }
    }

    fun clean() {
        storage.edit().apply {
            clear()
            apply()
        }
    }

    private fun pushId(id: String) = storage.getString("ids", "")!! + "$id#"
}