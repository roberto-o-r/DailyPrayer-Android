package com.isscroberto.dailyprayerandroid.splash;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.ads.MobileAds;
import com.isscroberto.dailyprayerandroid.R;
import com.isscroberto.dailyprayerandroid.prayer.PrayerActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // AdMob.
        MobileAds.initialize(this, getString(R.string.ad_mob_app_id));

        Intent intent = new Intent(this, PrayerActivity.class);
        startActivity(intent);
        finish();
    }
}
