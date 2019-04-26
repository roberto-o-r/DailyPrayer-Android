package com.isscroberto.dailyprayerandroid.prayerssaved;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isscroberto.dailyprayerandroid.R;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.prayerdetail.PrayerDetailActivity;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class PrayersSavedActivity extends DaggerAppCompatActivity implements PrayersSavedContract.View {

    //----- Bindings.
    @BindView(R.id.list_prayers)
    RecyclerView listPrayers;

    @Inject
    PrayersSavedContract.Presenter mPresenter;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmResults<Prayer> mPrayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayers_saved);

        // Bind views with Butter Knife.
        ButterKnife.bind(this);

        // Setup toolbar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Prayers Saved");
        }

        // Setup recycler view.
        listPrayers.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listPrayers.setLayoutManager(mLayoutManager);
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
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    public void showPrayers(RealmResults<Prayer> prayers) {
        // Setup recycler view adapter.
        mPrayers = prayers;
        mAdapter = new PrayerAdapter(this, mPrayers);
        listPrayers.setAdapter(mAdapter);
    }

    private static class PrayerAdapter extends RealmRecyclerViewAdapter<Prayer, PrayerAdapter.ViewHolder> {

        private final Context mContext;

        public PrayerAdapter(Context context, RealmResults<Prayer> prayers) {
            super(prayers, true);
            mContext = context;
        }

        @Override
        public @Nonnull ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
            final Prayer prayer = getItem(position);
            if(prayer != null) {
                holder.textTitle.setText(prayer.getTitle());
                holder.textPreview.setText(getExcerpt(prayer.getDescription()));
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView textTitle;
            public final TextView textPreview;

            public ViewHolder(View v) {
                super(v);
                textTitle = v.findViewById(R.id.text_title);
                textPreview = v.findViewById(R.id.text_preview);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PrayerDetailActivity.class);
                        Prayer prayer = getItem(getAdapterPosition());
                        if(prayer != null) {
                            intent.putExtra("id", prayer.getId());
                            intent.putExtra("title", prayer.getTitle());
                            intent.putExtra("description", prayer.getDescription());
                        }
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        private String getExcerpt(String input) {
            String excerpt = input;
            if(excerpt.lastIndexOf(" ") > -1 && excerpt.length() > 99) {
                excerpt = excerpt.substring(0, 100);
                excerpt = excerpt.substring(0, excerpt.lastIndexOf(" "));
            }
            excerpt = excerpt + "...";
            return excerpt;
        }
    }
}
