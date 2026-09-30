package com.example.a24012011013_mad_practical4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class AlarmBroadcastReceiver : BroadcastReceiver() {
    companion object{
        val SERVICE_KEY = "Service1"
        val START_VAL = "start"
        val STOP_VAL = "stop"
    }

    val TAG = "AlarmBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val str1 = intent.getStringExtra(SERVICE_KEY)

        if(str1 == START_VAL || str1 == STOP_VAL){
            Log.i(TAG, "onReceive: Received value:$str1")

            val intentService = Intent(context, AlarmService::class.java)

            if(str1 == START_VAL){
                // An alarm can fire while the app is in the background,
                // so start the audio service as a foreground service.
                ContextCompat.startForegroundService(context, intentService)
            } else {
                context.stopService(intentService)
            }
        }
    }
}