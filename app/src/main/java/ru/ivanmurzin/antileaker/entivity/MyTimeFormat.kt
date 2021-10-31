package ru.ivanmurzin.antileaker.entivity

object MyTimeFormat {

    fun format(mills: Long): String {
        var hours = (mills / 1000 / 60 / 60).toString()
        var minutes = (mills / 1000 / 60 % 60).toString()
        if (hours.length == 1) hours = "0$hours"
        if (minutes.length == 1) minutes = "0$minutes"
        return "$hours:$minutes"
    }
}