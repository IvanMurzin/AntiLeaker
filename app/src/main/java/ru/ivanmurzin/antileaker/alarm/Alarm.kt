package ru.ivanmurzin.antileaker.alarm

data class Alarm(
    val id: String,
    val interval: Long,
    val expire: Long = System.currentTimeMillis() + interval,
    var isOn: Boolean = false
)
