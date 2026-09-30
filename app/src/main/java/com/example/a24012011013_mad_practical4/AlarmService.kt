package com.example.a24012011013_mad_practical4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class AlarmService : Service() {

    companion object {
        private const val CHANNEL_ID = "alarm_service_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private var mp: MediaPlayer? = null
    private val TAG = "AlarmService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm is ringing")
            .setContentText("Alarm sound is playing")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        // Promote the service immediately after it is started.
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )

        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.alarm)
            mp?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mp?.isLooping = true
        }

        Log.i(TAG, "onStartCommand: starting alarm media player")
        if (mp?.isPlaying != true) {
            mp?.start()
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Notification shown while the alarm sound is playing"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mp?.stop()
        mp?.release()
        mp = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}