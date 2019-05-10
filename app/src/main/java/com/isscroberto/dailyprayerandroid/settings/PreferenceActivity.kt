package com.isscroberto.dailyprayerandroid.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.isscroberto.dailyprayerandroid.R

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, SettingsFragment()).commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.preferences)
        }
    }
}
