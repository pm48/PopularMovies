package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.DiscoveryFragment.EXTRA_PARAMS;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, new DetailFragment())
                    .commit();
        }
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));}
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        @BindView(R.id.ivPosterImage)
        ImageView iconView;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvReleaseDate)
        TextView tvReleaseDate;
        @BindView(R.id.tvSynopsis)
        TextView tvSynopsis;
        @BindView(R.id.tvVote)
        TextView tvVote;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ButterKnife.bind(this, rootView);


            if (intent != null && intent.hasExtra(EXTRA_PARAMS)) {
                Movie movie =  getActivity().getIntent().getExtras().getParcelable(EXTRA_PARAMS);
                Picasso.with(getContext()).load(movie.getPoster()).into(iconView);
                tvTitle.setText(movie.getTitle());
                tvSynopsis.setText(movie.getSynopsis());
                tvVote.setText(movie.getVoteAverage());
                tvReleaseDate.setText(movie.getReleaseDate());
            }
            return rootView;
        }
    }
}
