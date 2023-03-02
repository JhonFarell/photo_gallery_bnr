package com.kek.photo_gallery_bnr.main_screen

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kek.photo_gallery_bnr.R
import com.kek.photo_gallery_bnr.utils.Constances.NOTIFICATION_CHANNEL_ID

class PhotoGalleryApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceRepository.initialize(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel (NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
    }
}