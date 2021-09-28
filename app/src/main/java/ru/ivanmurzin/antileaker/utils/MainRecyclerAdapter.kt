package ru.ivanmurzin.antileaker.utils

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
import ru.ivanmurzin.antileaker.entivity.MyTimeFormat
import ru.ivanmurzin.antileaker.entivity.TimerProcess
import ru.ivanmurzin.antileaker.service.TimerServiceTelegram
import ru.ivanmurzin.antileaker.service.TimerServiceWhatsapp

class MainRecyclerAdapter(private val data: List<TimerProcess>) :
    RecyclerView.Adapter<MainRecyclerAdapter.MyHolder>() {
    private val timeFormat = MyTimeFormat()

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
        val process = data[position]
        holder.folderName.text = process.folderName
        holder.period.text = timeFormat.format(process.period)
        //holder.date.text = timeFormat.format(process.expireTime)
        val context = holder.timeLayout.context
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            Log.d(MY_SERVICE_LOGGER, isChecked.toString())
            if (isChecked) {
                if (position == 0) {
                    context.startService(Intent(context, TimerServiceTelegram::class.java))
                } else {
                    context.startService(Intent(context, TimerServiceWhatsapp::class.java))
                }
            } else {
                if (position == 0) {
                    context.stopService(Intent(context, TimerServiceTelegram::class.java))
                } else {
                    context.stopService(Intent(context, TimerServiceWhatsapp::class.java))
                }
            }
        }
        holder.timeLayout.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("number", position + 1)
            intent.putExtra("folderName", process.folderName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = data.size
}