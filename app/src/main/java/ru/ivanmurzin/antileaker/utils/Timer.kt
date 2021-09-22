package ru.ivanmurzin.antileaker.utils

import android.os.CountDownTimer
import android.util.Log
import ru.ivanmurzin.antileaker.MY_TIMER_LOGGER

class Timer(var mills: Long, val period: Long, val myOnFinish: () -> Unit) {

    inner class LocalCountDownTimer : CountDownTimer(mills, 1000L) {

        override fun onTick(millsToFinish: Long) {
            expire = millsToFinish
            Log.d(MY_TIMER_LOGGER, "remaining: ${millsToFinish / 1000} period: $mills")
        }

        override fun onFinish() {
            Log.d(MY_TIMER_LOGGER, "Time's finished!")
            myOnFinish() // выполняю внешнюю лямбда-функцию
            if (mills != period) { // если количество секунд до конца != периоду, то обновляю таймер
                cancel()
                update()
            } else start() // иначе просто перезапускаю
        }
    }

    private var localTimer = LocalCountDownTimer()
    var expire: Long = mills // показывает, сколько секунд осталось до конца отчета
        private set

    fun start(): CountDownTimer = localTimer.start()
    fun cancel() = localTimer.cancel()

    private fun update() {
        mills = period // заменяю количество секунд до конца периодом
        localTimer = LocalCountDownTimer() // обновляю основной таймер
        start() // запускаю обновленный таймер
    }
}
