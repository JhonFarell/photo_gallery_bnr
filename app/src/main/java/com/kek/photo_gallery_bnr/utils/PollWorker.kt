package com.kek.photo_gallery_bnr.utils

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kek.photo_gallery_bnr.MainActivity
import com.kek.photo_gallery_bnr.R
import com.kek.photo_gallery_bnr.api.PhotoRepository
import com.kek.photo_gallery_bnr.main_screen.PreferenceRepository
import com.kek.photo_gallery_bnr.utils.Constances.LOG_TAG_WORKER
import com.kek.photo_gallery_bnr.utils.Constances.NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.flow.first

class PollWorker (
    private val context: Context,
    private val workerParameters: WorkerParameters
    ): CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {

        val preferenceRepository = PreferenceRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferenceRepository.storedQuery.first()
        val lastId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.d(LOG_TAG_WORKER, "No saved query")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultId = items.first().id

                if (newResultId == lastId) {
                    Log.d(LOG_TAG_WORKER, "Result is the same: $newResultId")
                } else {
                    Log.d(LOG_TAG_WORKER, "New result set: $newResultId")
                    preferenceRepository.setLastResultId(newResultId)
                    notifyUser()
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(LOG_TAG_WORKER, "Worker finished with an error", ex)
            Result.failure()
        }

    }

    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val resources = context.resources

        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_titile))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_titile))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }

}