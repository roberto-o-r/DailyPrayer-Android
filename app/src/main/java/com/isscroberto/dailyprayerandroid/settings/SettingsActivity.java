package com.isscroberto.dailyprayerandroid.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.isscroberto.dailyprayerandroid.BuildConfig;
import com.isscroberto.dailyprayerandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    //----- Bindings.
    @BindView(R.id.button_remove_ads)
    Button buttonRemoveAds;

    private BillingProcessor mBillingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Verify if ads are enabled.
        boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if(adsEnabled) {
            // Initialize billing processor.
            mBillingProcessor = new BillingProcessor(this, getString(R.string.billing_license_key), this);
        } else {
            buttonRemoveAds.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        if (mBillingProcessor != null) {
            mBillingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        // Product was purchased succesfully.
        disableAds();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        // Verify if user already removed ads.
        boolean purchased;
        if (BuildConfig.DEBUG) {
            purchased = mBillingProcessor.isPurchased("android.test.purchased");
        } else {
            purchased = mBillingProcessor.isPurchased("com.isscroberto.dailyprayerandroid.removeads");
        }

        if(purchased) {
            disableAds();
            Toast.makeText(this, "Ads Removed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void disableAds(){
        SharedPreferences.Editor editor = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).edit();
        editor.putBoolean("AdsEnabled", false);
        editor.apply();

        buttonRemoveAds.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_remove_ads)
    public void btnRemoveAdsOnClick(View view) {
        if (BuildConfig.DEBUG) {
            mBillingProcessor.purchase(this, "android.test.purchased");
        } else {
            mBillingProcessor.purchase(this, "com.isscroberto.dailyprayerandroid.removeads");
        }
    }

    @OnClick(R.id.text_more_apps)
    public void imageDailyBibleOnClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:isscroberto")));
    }

    @OnClick(R.id.text_privacy_policy)
    public void textPrivacyPolicy(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://isscroberto.com/daily-bible-privacy-policy/")));
    }

}
