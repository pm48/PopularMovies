package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        @BindView(R.id.list_item_trailers)
        ListView lvTrailer;
        private TrailerAdapter trailerAdapter;
        private List<Trailer> mTrailers;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ButterKnife.bind(this, rootView);


            if (intent != null && intent.hasExtra(EXTRA_PARAMS)) {
                Movie movie = getActivity().getIntent().getExtras().getParcelable(EXTRA_PARAMS);
                Picasso.with(getContext()).load(movie.getPoster()).into(iconView);
                tvTitle.setText(movie.getTitle());
                tvSynopsis.setText(movie.getSynopsis());
                tvVote.setText(movie.getVoteAverage());
                tvReleaseDate.setText(movie.getReleaseDate());

                FetchTrailers fetchTrailers = new FetchTrailers();
                fetchTrailers.execute(movie.getId());
            }
            trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
            lvTrailer.setAdapter(trailerAdapter);
            return rootView;
        }

        public class FetchTrailers extends AsyncTask<Integer, Void, List<Trailer>> {

            private final String LOG_TAG = FetchTrailers.class.getSimpleName();
            private List<Trailer> trailers;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getActivity(), "Fetching Trailer URI", Toast.LENGTH_SHORT).show();
            }


            @Override
            protected List<Trailer> doInBackground(Integer... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String appId = "7ec8978a5e5bc92a1e837697e5ca866f";

                String trailersJsonString = null;

                try {
                    final String MOVIEDETAIL_URL =
                            "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";

                    String apiKey = "?api_key=" + appId;
                    Log.v(LOG_TAG, "TRAILER URL IS " + MOVIEDETAIL_URL.concat(apiKey));
                    URL url = new URL(MOVIEDETAIL_URL.concat(apiKey));
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    trailersJsonString = buffer.toString();
                    Log.v("message", trailersJsonString);
                    try {
                        trailers = getDatafromJson(trailersJsonString);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }

                    return trailers;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;

            }

            private List<Trailer> getDatafromJson(String trailersJsonString) throws JSONException {

                JSONObject jsonObj = new JSONObject(trailersJsonString);
                JSONArray jsonArray = jsonObj.getJSONArray("results");
                List<Trailer> trailers = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonDetails = jsonArray.getJSONObject(i);
                    Trailer trailer = new Trailer();
                    trailer.setKey(jsonDetails.getString("key"));
                    trailer.setName(jsonDetails.getString("name"));
                    trailers.add(trailer);
                }
                return trailers;

            }

            @Override
            protected void onPostExecute(List<Trailer> trailers) {
                if (trailers != null) {
                    trailerAdapter.clear();
                    for (Trailer trailer : trailers) {
                        trailerAdapter.add(trailer);
                    }
                }

            }
        }
    }
}
