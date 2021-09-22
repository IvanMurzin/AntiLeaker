package ru.ivanmurzin.antileaker.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.process_card.view.*
import ru.ivanmurzin.antileaker.MY_SERVICE_LOGGER
import ru.ivanmurzin.antileaker.R
import ru.ivanmurzin.antileaker.entivity.MyTimeFormat
import ru.ivanmurzin.antileaker.entivity.TimerProcess

class MainRecyclerAdapter(private val data: Array<TimerProcess>) :
    RecyclerView.Adapter<MainRecyclerAdapter.MyHolder>() {
    val timeFormat = MyTimeFormat()

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val period: TextView = view.text_period
        val folderName: TextView = view.text_folder_name
        val date: TextView = view.text_date
        val switch: SwitchButton = view.switch_button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.process_card, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val process = data[position]
        holder.folderName.text = process.folderName
        holder.period.text = timeFormat.format(process.period)
        //holder.date.text = timeFormat.format(process.expireTime)
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            Log.d(MY_SERVICE_LOGGER, isChecked.toString())
        }
    }

    override fun getItemCount() = data.size
}