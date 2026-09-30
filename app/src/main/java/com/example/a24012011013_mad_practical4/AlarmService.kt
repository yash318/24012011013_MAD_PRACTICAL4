package com.example.a24012011013_mad_practical4

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class AlarmService : Service() {
    var mp: MediaPlayer?=null
    val TAG = "AlarmService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            if(mp == null){
                mp = MediaPlayer.create(this,R.raw.alarm)
            }
            Log.i(TAG, "onStartCommand: In Service starting media player")
            mp?.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        mp?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}