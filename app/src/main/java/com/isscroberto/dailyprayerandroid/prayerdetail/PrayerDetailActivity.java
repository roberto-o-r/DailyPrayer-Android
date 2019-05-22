package com.isscroberto.dailyprayerandroid.prayerdetail;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.isscroberto.dailyprayerandroid.BuildConfig;
import com.isscroberto.dailyprayerandroid.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class PrayerDetailActivity extends DaggerAppCompatActivity implements PrayerDetailContract.View {

    //----- UI Bindings.
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_content)
    TextView textContent;
    @BindView(R.id.ad_view)
    AdView adView;

    @Inject
    PrayerDetailContract.Presenter mPresenter;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_detail);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Prayers");
        }

        // Verify if ads are enabled.
        boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if (adsEnabled) {
            // Load Ad Banner.
            AdRequest adRequest;
            if (BuildConfig.DEBUG) {
                adRequest = new AdRequest.Builder()
                        .addTestDevice(getString(R.string.test_device))
                        .build();
            } else {
                adRequest = new AdRequest.Builder().build();
            }
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    adView.setVisibility(View.GONE);
                }
            });
        }

        // Get prayer.
        mId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        // Show prayer.
        textTitle.setText(title);
        textContent.setText(description);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.prayer, menu);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(PrayerDetailActivity.this);
            alert.setTitle("Delete");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", (dialog, which) -> {
                mPresenter.deletePrayer(mId);
                finish();
            });

            alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
