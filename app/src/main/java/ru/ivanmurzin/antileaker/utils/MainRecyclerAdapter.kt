package ru.ivanmurzin.antileaker.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.process_card.view.*
import ru.ivanmurzin.antileaker.EditActivity
import ru.ivanmurzin.antileaker.R
import ru.ivanmurzin.antileaker.alarm.Alarm
import ru.ivanmurzin.antileaker.alarm.MyAlarmManager
import ru.ivanmurzin.antileaker.entivity.MyTimeFormat

class MainRecyclerAdapter(private val data: List<Alarm>, private val context: Context) :
    RecyclerView.Adapter<MainRecyclerAdapter.MyHolder>() {

    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val period: TextView = view.text_period
        val folderName: TextView = view.text_folder_name
        val date: TextView = view.text_date
        val switch: SwitchButton = view.switch_button
        val timeLayout: LinearLayout = view.time_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.process_card, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val alarm = data[position]
        holder.folderName.text = alarm.id
        holder.period.text = MyTimeFormat.format(alarm.interval)
        holder.switch.isChecked = alarm.isOn
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            val alarmManager = MyAlarmManager(context)
            if (isChecked)
                alarmManager.startAlarm(alarm)
            else
                alarmManager.cancelAlarm(alarm)
        }

        holder.timeLayout.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("id", alarm.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = data.size
}