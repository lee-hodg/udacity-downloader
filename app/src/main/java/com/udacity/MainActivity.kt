package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import timber.log.Timber.DebugTree


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var url: String? = null
    private var repoName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logging
        Timber.plant(DebugTree())

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // when the download completes callback the receiver (defined below)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // button click triggers the download to begin
        custom_button.setOnClickListener {
            custom_button.setState(ButtonState.Clicked)

            Timber.d("Checked radio id is ${radio_group.checkedRadioButtonId}")

            // set the button to loading state
            custom_button.setState(ButtonState.Loading)

            when (radio_group.checkedRadioButtonId) {
                R.id.radio_glide_repo -> {
                    url = getString(R.string.glide_repo_url)
                    repoName = getString(R.string.glide_repo_name)
                    download()
                }
                R.id.radio_nd940_repo -> {
                    url = getString(R.string.nd940_repo_url)
                    repoName = getString(R.string.nd940_repo_name)
                    download()
                }
                R.id.radio_retrofit_repo -> {
                    url = getString(R.string.retrofit_repo_url)
                    repoName = getString(R.string.retrofit_repo_name)
                    download()
                }
                else -> {
                    url = null
                    repoName = null
                    Toast.makeText(this, "Select a file to download",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }

        // notifications channel
        createChannel(
            getString(R.string.download_status_notification_channel_id),
            getString(R.string.download_status_notification_channel_name)
        )

    }

    private fun createChannel(channelId: String, channelName: String) {
        // START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                // disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download done"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            custom_button.setState(ButtonState.Completed)

            Timber.d("Context onReceive was $context")
            Timber.d("Intent onReceive was $intent")
            val id = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val notificationManager = context?.let {
                ContextCompat.getSystemService(
                    it,
                    NotificationManager::class.java
                )
            } as NotificationManager

            // download status
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(id)
            val cursor = downloadManager.query(query)
            cursor.moveToFirst()
            val status = when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> "Successful download"
                else -> "Failed download"
            }

            Timber.d("The download status was: $status for url $url and id $id, repoName: $repoName")

            notificationManager.sendNotification(
                context.getText(R.string.notification_description).toString(),
                context, id, status, url!!, repoName!!)

        }
    }

    private fun download() {

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


}
