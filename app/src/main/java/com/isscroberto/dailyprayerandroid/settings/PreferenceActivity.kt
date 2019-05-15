package com.isscroberto.dailyprayerandroid.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.isscroberto.dailyprayerandroid.R
import android.widget.Toast
import com.isscroberto.dailyprayerandroid.BuildConfig
import androidx.preference.Preference


class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup toolbar.
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // Setup preference fragment.
        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, SettingsFragment()).commit()
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
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content) as SettingsFragment
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    class SettingsFragment : PreferenceFragmentCompat(), BillingProcessor.IBillingHandler {

        private var mBillingProcessor: BillingProcessor? = null

        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.preferences)

            // Verify if ads are enabled.
            val adsEnabled = activity!!.getSharedPreferences("com.isscroberto.dailyprayerandroid", Context.MODE_PRIVATE).getBoolean("AdsEnabled", true)
            if (adsEnabled) {
                // Initialize the billing processor.
                mBillingProcessor = BillingProcessor(activity, getString(R.string.billing_license_key), this)
                // Add click listener to preference.
                var preferenceAds = findPreference("preference_ads")
                preferenceAds.setOnPreferenceClickListener {
                    if (BuildConfig.DEBUG) {
                        mBillingProcessor!!.purchase(activity, "android.test.purchased")
                    } else {
                        mBillingProcessor!!.purchase(activity, "com.isscroberto.dailyprayerandroid.removeads")
                    }
                    true
                }
            } else {
                var preferenceAds = findPreference("preference_ads")
                var preferenceCategory = findPreference("preference_general") as PreferenceCategory
                preferenceCategory.removePreference(preferenceAds)
            }
        }

        override fun onDestroy() {
            if (mBillingProcessor != null) {
                mBillingProcessor!!.release()
            }
            super.onDestroy()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (!mBillingProcessor!!.handleActivityResult(requestCode, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data)
        }

        override fun onProductPurchased(productId: String, details: TransactionDetails?) {
            // Product was purchased succesfully.
            disableAds()
        }

        override fun onPurchaseHistoryRestored() {}

        override fun onBillingError(errorCode: Int, error: Throwable?) {}

        override fun onBillingInitialized() {
            // Verify if user already removed ads.
            val purchased: Boolean
            if (BuildConfig.DEBUG) {
                purchased = mBillingProcessor!!.isPurchased("android.test.purchased")
            } else {
                purchased = mBillingProcessor!!.isPurchased("com.isscroberto.dailyprayerandroid.removeads")
            }

            if (purchased) {
                disableAds()
                Toast.makeText(activity, "Ads Removed!", Toast.LENGTH_SHORT).show()
            }
        }

        fun disableAds() {
            val editor = activity!!.getSharedPreferences("com.isscroberto.dailyprayerandroid", Context.MODE_PRIVATE).edit()
            editor.putBoolean("AdsEnabled", false)
            editor.apply()

            var preferenceAds = findPreference("preference_ads")
            var preferenceCategory = findPreference("preference_general") as PreferenceCategory
            preferenceCategory.removePreference(preferenceAds)
        }

    }
}
