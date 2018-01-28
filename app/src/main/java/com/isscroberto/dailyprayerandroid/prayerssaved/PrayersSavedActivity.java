package com.isscroberto.dailyprayerandroid.prayerssaved;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stkent.amplify.utils.StringUtils;
import com.isscroberto.dailyprayerandroid.R;
import com.isscroberto.dailyprayerandroid.data.models.Prayer;
import com.isscroberto.dailyprayerandroid.data.source.PrayerLocalDataSource;
import com.isscroberto.dailyprayerandroid.data.source.PrayerRemoteDataSource;
import com.isscroberto.dailyprayerandroid.prayer.PrayerContract;
import com.isscroberto.dailyprayerandroid.prayerdetail.PrayerDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class PrayersSavedActivity extends AppCompatActivity implements PrayersSavedContract.View {

    //----- Bindings.
    @BindView(R.id.list_prayers)
    RecyclerView listPrayers;

    private PrayersSavedContract.Presenter mPresenter;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Prayers Saved");

        // Setup recycler view.
        listPrayers.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listPrayers.setLayoutManager(mLayoutManager);

        // Create the presenter
        new PrayersSavedPresenter(new PrayerLocalDataSource(), this);
        mPresenter.start();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        return true;
    }

    @Override
    public void setPresenter(PrayersSavedContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showPrayers(RealmResults<Prayer> prayers) {
        // Setup recycler view adapter.
        mPrayers = prayers;
        mAdapter = new PrayerAdapter(this, mPrayers);
        listPrayers.setAdapter(mAdapter);
    }

    private static class PrayerAdapter extends RealmRecyclerViewAdapter<Prayer, PrayerAdapter.ViewHolder> {

        private Context mContext;

        public PrayerAdapter(Context context, RealmResults<Prayer> prayers) {
            super(prayers, true);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Prayer prayer = getItem(position);
            holder.textTitle.setText(prayer.getTitle());
            holder.textPreview.setText(getExcerpt(prayer.getDescription()));

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textTitle;
            public TextView textPreview;

            public ViewHolder(View v) {
                super(v);
                textTitle = (TextView) v.findViewById(R.id.text_title);
                textPreview = (TextView) v.findViewById(R.id.text_preview);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PrayerDetailActivity.class);
                        intent.putExtra("id", getItem(getAdapterPosition()).getId());
                        intent.putExtra("title", getItem(getAdapterPosition()).getTitle());
                        intent.putExtra("description", getItem(getAdapterPosition()).getDescription());
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
