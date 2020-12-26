package com.isscroberto.dailyprayerandroid.prayer;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.isscroberto.dailyprayerandroid.analytics.AnalyticsHelper;
import com.isscroberto.dailyprayerandroid.analytics.EventType;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.isscroberto.dailyprayerandroid.databinding.ActivityPrayerBinding;
import com.isscroberto.dailyprayerandroid.prayerssaved.PrayersSavedActivity;
import com.isscroberto.dailyprayerandroid.preference.PreferenceActivity;
import com.isscroberto.dailyprayerandroid.data.models.Item;
import com.isscroberto.dailyprayerandroid.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class PrayerActivity extends AppCompatActivity implements PrayerContract.View, SwipeRefreshLayout.OnRefreshListener, TextToSpeech.OnInitListener {

    private PrayerContract.Presenter presenter;
    private FirebaseAnalytics firebaseAnalytics;
    private Item prayer;
    private ActivityPrayerBinding binding;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding.
        binding = ActivityPrayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonFav.setOnClickListener((View v) -> buttonFavOnClick(view));
        binding.buttonPlay.setOnClickListener((View v) -> playPrayer());

        // Setup toolbar.
        setSupportActionBar(binding.toolbar);

        // Feedback.
        if (savedInstanceState == null) {
            DefaultLayoutPromptView promptView = findViewById(R.id.prompt_view);
            Amplify.getSharedInstance().promptIfReady(promptView);
        }

        // Setup swipe refresh layout.
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        // Setup text to speech.
        tts = new TextToSpeech(this, this);

        // AdMob.
        MobileAds.initialize(this, initializationStatus -> setupAds());

        // Analytics.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the presenter.
        presenter = new PrayerPresenter(new PrayerRemoteDataSource(), new PrayerLocalDataSource(), new ImageRemoteDataSource());
        presenter.takeView(this);

        // Load the prayer.
        presenter.reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        tts.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.dropView();
        tts.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);
        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            if (prayer != null) {
                // Log share event.
                AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Share, null);

                // App's link to append.
                String link = "Daily Prayer https://play.google.com/store/apps/details?id=com.isscroberto.dailyprayerandroid";

                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Prayer");
                i.putExtra(android.content.Intent.EXTRA_TEXT, prayer.getDescription() + link);
                startActivity(Intent.createChooser(i, "Share this Daily Prayer"));
            }
        } else if (id == R.id.menu_item_favorites) {
            navigateToFavorites();
        } else if (id == R.id.menu_item_settings) {
            navigateToSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                // Verify if ads are enabled.
                boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
                if (!adsEnabled) {
                    binding.adWrapper.setVisibility(View.GONE);
                }
                break;
            case 2:
                // Verify if prayer is favorited.
                presenter.reload();
                break;
        }
    }

    @Override
    public void showPrayer(Item p) {
        prayer = p;
        String description = prayer.getDescription();
        if (description.contains("<hr>")) {
            prayer.setDescription(description.substring(0, description.lastIndexOf("<hr>")));
        }
        prayer.setDescription(Html.fromHtml(prayer.getDescription()).toString());
        binding.textTitle.setText(prayer.getTitle());
        binding.textContent.setText(prayer.getDescription());
        if (prayer.getFav()) {
            binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_24dp));
        } else {
            binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_24dp));
        }
    }

    @Override
    public void showError() {
        binding.textTitle.setText("Error loading prayer. Please try again.\nPull down to refresh.");
        binding.textContent.setText("");
    }

    @Override
    public void logError(String message) {
        // Log error.
        Bundle params = new Bundle();
        params.putString("error_message", message);
        AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Error, params);
    }

    @Override
    public void showImage(String url) {
        Picasso.with(this).load(url).fit().centerCrop().into(binding.imageBack);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            binding.layoutProgress.setVisibility(View.VISIBLE);
            binding.buttonFav.setVisibility(View.GONE);
            binding.buttonPlay.setVisibility(View.GONE);
        } else {
            binding.layoutProgress.setVisibility(View.GONE);
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Prayer Updated!", Toast.LENGTH_SHORT).show();
            }
            redrawFab();
        }
    }

    @Override
    public void onRefresh() {
        presenter.reload();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                binding.buttonPlay.setEnabled(false);
            } else {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_arrow_white_24dp));
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onStop(String utteranceId, boolean interrupted) {
                        super.onStop(utteranceId, interrupted);
                        binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_arrow_white_24dp));
                    }
                });
            }
        } else {
            binding.buttonPlay.setEnabled(false);
        }
    }

    public void buttonFavOnClick(View view) {
        if (prayer != null) {
            // Create prayer id based on the date.
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String id = df.format(new Date());

            if (!prayer.getFav()) {
                // Prepare prayer for storage.
                Prayer newPrayer = new Prayer();
                newPrayer.setId(id);
                newPrayer.setTitle(prayer.getTitle());
                newPrayer.setDescription(prayer.getDescription());

                // Save prayer.
                presenter.savePrayer(newPrayer);
                prayer.setFav(true);

                AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Favorite, null);
            } else {
                // Remove prayer from favorites.
                presenter.deletePrayer(id);
                prayer.setFav(false);
            }

            redrawFab();
        }
    }

    private void navigateToSettings() {
        // Settings.
        Intent intent = new Intent(this, PreferenceActivity.class);
        startActivityForResult(intent, 1);
    }

    private void navigateToFavorites() {
        // Favorites.
        Intent intent = new Intent(this, PrayersSavedActivity.class);
        startActivityForResult(intent, 2);
    }

    private void redrawFab() {
        // Fav button.
        binding.buttonFav.hide();
        binding.buttonPlay.hide();
        if (prayer != null) {
            if (prayer.getFav()) {
                binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_24dp));
            } else {
                binding.buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_24dp));
            }
        }
        binding.buttonFav.show();
        binding.buttonPlay.show();
    }

    private void setupAds() {
        // Verify if ads are enabled.
        boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
        if (adsEnabled) {
            // Load Ad Banner.
            AdRequest adRequest = new AdRequest.Builder().build();
            binding.adView.loadAd(adRequest);

            binding.adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    binding.adWrapper.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    super.onAdFailedToLoad(adError);
                    binding.adWrapper.setVisibility(View.GONE);
                }
            });
        }
    }

    private void playPrayer() {
        if (tts.isSpeaking()) {
            tts.stop();
            binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp));
        } else {
            tts.speak(prayer.getDescription(), TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
            binding.buttonPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_stop_white_24dp));
            AnalyticsHelper.LogEvent(firebaseAnalytics, EventType.Play, null);
        }
    }
}
