package ru.ivanmurzin.antileaker.entivity

class MyTimeFormat {

    fun format(mills: Long): String {
        val hours = mills / 1000 / 60 / 60
        val minutes = mills / 1000 / 60 % 60
        return "$hours:$minutes"
    }
}