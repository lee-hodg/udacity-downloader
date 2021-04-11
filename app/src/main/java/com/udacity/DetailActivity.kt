package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var status: String? = ""
    private var url: String? = ""
    private var repoName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // get the extras from the intent
        val extras = intent.extras!!
        downloadID = extras.getLong("downloadID")
        status = extras.getString("status")
        url = extras.getString("url")
        repoName = extras.getString("repoName")

        // get rid of any notifications
        val notificationManager = ContextCompat.getSystemService(this,
            NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        // set the values in the view (could do it with binding and view model perhaps too)
        val filenameValueView: TextView = findViewById(R.id.filename_value)
        filenameValueView.text = repoName
        val statusView: TextView = findViewById(R.id.status_value)
        statusView.text = status

        // go back to main activity
        ok_button.setOnClickListener {
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }
    }

}
