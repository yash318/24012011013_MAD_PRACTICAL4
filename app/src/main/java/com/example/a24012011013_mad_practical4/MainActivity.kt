package com.example.a24012011013_mad_practical4

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var textAlarm: TextView
    lateinit var cardSetAlarm: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        textAlarm = findViewById<TextView>(R.id.textView8)
        cardSetAlarm = findViewById(R.id.MaterialCardView2)
        cardSetAlarm.visibility = View.GONE //MOST IMP THIS WILL HIDE THE CARD IF ALARM IS NOT SET!
        findViewById<MaterialButton>(R.id.set_alarm_btn).setOnClickListener {
            showTimeDialog()
        }
        findViewById<MaterialButton>(R.id.cancel_alarm_btn).setOnClickListener {
            setAlarm(0, AlarmBroadcastReceiver.STOP_VAL)
            cardSetAlarm.visibility = View.GONE
        }
    }

    private fun showTimeDialog(){
        val cldr: Calendar= Calendar.getInstance()
        val hrs:Int = cldr.get(Calendar.HOUR_OF_DAY)
        val mns:Int = cldr.get(Calendar.MINUTE)
        val picker = TimePickerDialog(
            this, {tp,sHour,sMinute -> sendDialogDataToActivity(sHour,sMinute)},hrs,mns,false
        )
        picker.show()
    }
    private fun sendDialogDataToActivity(hour:Int, minute:Int){
        val alarmCalendar = Calendar.getInstance()
        val year: Int = alarmCalendar.get(Calendar.YEAR)
        val month: Int = alarmCalendar.get(Calendar.MONTH)
        val day: Int = alarmCalendar.get(Calendar.DATE)
        alarmCalendar.set(year, month, day, hour, minute, 0)
        if(setAlarm(alarmCalendar.timeInMillis, AlarmBroadcastReceiver.START_VAL))
        {
            textAlarm.text="$hour:$minute"
            cardSetAlarm.visibility = View.VISIBLE
        }

        Toast.makeText(this, "Time: hours:${hour}, minutes:${minute}, millis:${alarmCalendar.timeInMillis}", Toast.LENGTH_SHORT)
    }

    private fun setAlarm(millisTime: Long, str: String): Boolean{
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra(AlarmBroadcastReceiver.SERVICE_KEY, str)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 23245, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if(str == AlarmBroadcastReceiver.START_VAL){
            if(alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    millisTime,
                    pendingIntent
                )
                return true
            }
            else
            {
                Toast.makeText(this, "Can't schedule alarm", Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,"package:$packageName".toUri()).apply { startActivity(this) }
                return false
            }
        } else if (str == AlarmBroadcastReceiver.STOP_VAL){
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intent)
            Toast.makeText(this, "Alarm is stopped!", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }
}