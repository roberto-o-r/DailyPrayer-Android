package com.isscroberto.dailyprayerandroid.preference

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import com.isscroberto.dailyprayerandroid.R
import com.isscroberto.dailyprayerandroid.alarm.NotificationHelper


class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup toolbar.
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // Create notification channel.
        NotificationHelper.createNotificationChannel(this,
                NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
                getString(R.string.app_name), this.getString(R.string.notification_channel_description))

        // Setup preference fragment.
        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, PreferenceFragment()).commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content) as PreferenceFragment
        fragment.onActivityResult(requestCode, resultCode, data)
    }

}
