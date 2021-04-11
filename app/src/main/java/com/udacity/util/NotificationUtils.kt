package com.udacity.util

import com.udacity.DetailActivity
import com.udacity.R

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0


fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context,
                                         downloadID: Long, status: String, url: String,
                                         repoName: String) {
    /**
     * Builds and delivers the notification.
     */

    // add style
//    val notificationImage = BitmapFactory.decodeResource(
//        applicationContext.resources,
//        R.drawable.ic_assistant_black_24dp
//    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(notificationImage)
//        .bigLargeIcon(null)
    val bigTextStyle = NotificationCompat.BigTextStyle().bigText(status)


    // add detail action
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra("downloadID", downloadID)
        .putExtra("status", status)
        .putExtra("url", url)
        .putExtra("repoName", repoName)

    // notice here getActivity and not getBroadcast
    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,
        NOTIFICATION_ID, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    // Get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_status_notification_channel_id)
    )

        // Set title, text and icon to builder
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        // set content intent
        .setContentIntent(detailPendingIntent)
        .setAutoCancel(true)

        // add style to builder
        .setStyle(bigTextStyle)
        //.setLargeIcon(notificationImage)

        //  add view detail action
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.view_detail_action),
            detailPendingIntent
        )

        // set priority
        .setPriority(NotificationCompat.PRIORITY_MAX)

    // all notify
    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}