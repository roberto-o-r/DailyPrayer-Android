package com.isscroberto.dailyprayerandroid.prayer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.isscroberto.dailyprayerandroid.BuildConfig;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.prayerssaved.PrayersSavedActivity;
import com.isscroberto.dailyprayerandroid.settings.SettingsActivity;
import com.isscroberto.dailyprayerandroid.data.models.Item;
import com.isscroberto.dailyprayerandroid.R;
import com.isscroberto.dailyprayerandroid.data.source.ImageRemoteDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrayerActivity extends AppCompatActivity implements PrayerContract.View, SwipeRefreshLayout.OnRefreshListener {

    //----- Bindings.
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.layout_progress)
    RelativeLayout layoutProgress;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_content)
    TextView textContent;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ad_view)
    AdView adView;
    @BindView(R.id.button_fav)
    FloatingActionButton buttonFav;

    private PrayerContract.Presenter mPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Item mPrayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        setSupportActionBar(toolbar);

        // Setup swipe refresh layout.
        swipeRefreshLayout.setOnRefreshListener(this);

        // Feedback.
        if (savedInstanceState == null) {
            DefaultLayoutPromptView promptView = (DefaultLayoutPromptView) findViewById(R.id.prompt_view);
            Amplify.getSharedInstance().promptIfReady(promptView);
        }

        // Verify if ads are enabled.
        Boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
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

        // Firebase analytics.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the presenter
        new PrayerPresenter(new PrayerRemoteDataSource(), new PrayerLocalDataSource(), new ImageRemoteDataSource(), this);
        mPresenter.start();
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
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                if (mPrayer != null) {
                    // Log share event.
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "prayer");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Daily Prayer");
                    i.putExtra(android.content.Intent.EXTRA_TEXT, mPrayer.getDescription());
                    startActivity(Intent.createChooser(i, "Share this Daily Prayer"));
                }
                break;
            case R.id.menu_item_favorites:
                navigateToFavorites();
                break;
            case R.id.menu_item_settings:
                navigateToSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Verify if ads are enabled.
            Boolean adsEnabled = getSharedPreferences("com.isscroberto.dailyprayerandroid", MODE_PRIVATE).getBoolean("AdsEnabled", true);
            if (!adsEnabled) {
                adView.setVisibility(View.GONE);
            }
        }
        if (requestCode == 2) {
            // Verify if prayer is favorited.
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(PrayerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPrayer(Item prayer) {
        mPrayer = prayer;
        String description = mPrayer.getDescription();
        if (description.contains("<hr>")) {
            mPrayer.setDescription(description.substring(0, description.lastIndexOf("<hr>")));
        }
        mPrayer.setDescription(Html.fromHtml(mPrayer.getDescription()).toString());
        textTitle.setText(mPrayer.getTitle());
        textContent.setText(mPrayer.getDescription());
        if(mPrayer.getFav()) {
            buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
        } else {
            buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
        }
    }

    @Override
    public void showError() {
        textTitle.setText("Error loading prayer. Please try again.\nPull down to refresh.");
        textContent.setText("");
    }

    @Override
    public void showImage(String url) {
        Picasso.with(this).load(url).fit().centerCrop().into(imageBack);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            layoutProgress.setVisibility(View.VISIBLE);
            buttonFav.setVisibility(View.INVISIBLE);
        } else {
            layoutProgress.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Prayer Updated!", Toast.LENGTH_SHORT).show();
            }
            buttonFav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.start();
    }

    @OnClick(R.id.button_fav)
    public void buttonFavOnClick(View view) {
        if (mPrayer != null) {
            // Create prayer id based on the date.
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("gmt"));
            String id = df.format(new Date());

            if(!mPrayer.getFav()) {
                // Prepare prayer for storage.
                Prayer newPrayer = new Prayer();
                newPrayer.setId(id);
                newPrayer.setTitle(mPrayer.getTitle());
                newPrayer.setDescription(mPrayer.getDescription());

                // Save prayer.
                mPresenter.savePrayer(newPrayer);
                mPrayer.setFav(true);
                buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
            } else {
                // Remove prayer from favorites.
                mPresenter.deletePrayer(id);
                mPrayer.setFav(false);
                buttonFav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
            }
        }
    }

    private void navigateToSettings() {
        // Settings.
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    private void navigateToFavorites() {
        // Favorites.
        Intent intent = new Intent(this, PrayersSavedActivity.class);
        startActivityForResult(intent, 2);
    }

}
