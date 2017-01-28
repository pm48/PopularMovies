package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import static android.content.Intent.ACTION_VIEW;
import static com.example.android.popularmovies.DiscoveryFragment.EXTRA_PARAMS;

/**
 * Created by prernamanaktala on 1/21/17.
 */

public class DetailFragment extends Fragment {
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
    @BindView(R.id.btnFavorite)
    Button btnFavorite;
    private TrailerAdapter trailerAdapter;
    private RecyclerView mRecyclerViewTrailer;
    private ReviewAdapter reviewAdapter;
    private RecyclerView mRecyclerViewReview;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract._ID,
            MovieContract.COLUMN_MOVIE_ID,
            MovieContract.COLUMN_TITLE,
            MovieContract.COLUMN_IMAGE,
            MovieContract.COLUMN_FAVORITE
    };
    private MovieDbHelper mOpenHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        mOpenHelper = new MovieDbHelper(getContext());



        if (intent != null && intent.hasExtra(EXTRA_PARAMS)) {
            final Movie movie = getActivity().getIntent().getExtras().getParcelable(EXTRA_PARAMS);
            Picasso.with(getContext()).load(movie.getPoster()).into(iconView);
            tvTitle.setText(movie.getTitle());
            tvSynopsis.setText(movie.getSynopsis());
            tvVote.setText(movie.getVoteAverage());
            tvReleaseDate.setText(movie.getReleaseDate());
            final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
            // have we created the tables we want?
            Cursor c = db.rawQuery("SELECT favorite FROM favorites", null);
            if (c.moveToFirst()){
                btnFavorite.setText(R.string.marked_favorite);
            }
            else
                btnFavorite.setText(R.string.mark_favorite);


            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isfavorite()) {
                        addToFavorite();
                    } else {
                        removeFromFav();
                    }
                }

                private boolean isfavorite() {
                    String mSelectionClause = MovieContract.COLUMN_MOVIE_ID + " = ?";
                    Cursor cursor = getActivity().getContentResolver().query(MovieContract.CONTENT_URI, MOVIE_COLUMNS, mSelectionClause, new String[]{String.valueOf(movie.getId())}, null);
                    if (cursor.moveToFirst()) {
                        return true;
                    } else
                        return false;
                }
                private void addToFavorite() {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.COLUMN_MOVIE_ID, movie.getId());
                    contentValues.put(MovieContract.COLUMN_TITLE, movie.getTitle());
                    contentValues.put(MovieContract.COLUMN_IMAGE, movie.getPoster());
                    contentValues.put(MovieContract.COLUMN_FAVORITE,1);
                    getActivity().getContentResolver().insert(MovieContract.CONTENT_URI, contentValues);
                    btnFavorite.setText(R.string.marked_favorite);
                    movie.setFavorite(1);
                    Toast.makeText(getContext(), "Added to favourites. . .", Toast.LENGTH_SHORT).show();
                }

                private void removeFromFav() {
                    getActivity().getContentResolver().delete(MovieContract.CONTENT_URI, MovieContract.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movie.getId())});
                    btnFavorite.setText(R.string.mark_favorite);
                    movie.setFavorite(0);
                    Toast.makeText(getContext(), "Removed from favourites. . .", Toast.LENGTH_SHORT).show();
                }
            });

            FetchTrailers fetchTrailers = new FetchTrailers();
            fetchTrailers.execute(movie.getId());
            FetchReviews fetchReviews = new FetchReviews();
            fetchReviews.execute(movie.getId());
        }
        trailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trailer trailer) {
                Intent appIntent = new Intent(ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                Intent webIntent = new Intent(ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }

            }
        });

        mRecyclerViewTrailer = (RecyclerView) rootView.findViewById(R.id.recycler_Trailers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewTrailer.setLayoutManager(linearLayoutManager);
        mRecyclerViewTrailer.setNestedScrollingEnabled(false);
        mRecyclerViewTrailer.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(new ArrayList<Review>());
        mRecyclerViewReview = (RecyclerView) rootView.findViewById(R.id.recycler_Reviews);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewReview.setLayoutManager(linearLayoutManager1);
        //mRecyclerViewReview.setNestedScrollingEnabled(false);
        mRecyclerViewReview.setAdapter(reviewAdapter);

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
                trailer.setId(jsonDetails.getString("id"));
                trailers.add(trailer);
            }
            return trailers;

        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                trailerAdapter.clear();
                trailerAdapter.addAll(trailers);
            } else {
                Toast.makeText(getActivity(), " Something went wrong, please check your internet connection and try again! ", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public class FetchReviews extends AsyncTask<Integer, Void, List<Review>> {

        private final String LOG_TAG = FetchReviews.class.getSimpleName();
        private List<Review> reviews;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Fetching Trailer URI", Toast.LENGTH_SHORT).show();
        }


        @Override
        protected List<Review> doInBackground(Integer... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String appId = "7ec8978a5e5bc92a1e837697e5ca866f";

            String reviewsString = null;

            try {
                final String MOVIEDETAIL_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";

                String apiKey = "?api_key=" + appId;
                Log.v(LOG_TAG, "REVIEW URL IS " + MOVIEDETAIL_URL.concat(apiKey));
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
                reviewsString = buffer.toString();
                Log.v("message", reviewsString);
                try {
                    reviews = getDatafromJson(reviewsString);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return reviews;

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

        private List<Review> getDatafromJson(String reviewsString) throws JSONException {

            JSONObject jsonObj = new JSONObject(reviewsString);
            JSONArray jsonArray = jsonObj.getJSONArray("results");
            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonDetails = jsonArray.getJSONObject(i);
                Review review = new Review();
                review.setAuthor(jsonDetails.getString("author"));
                review.setContent(jsonDetails.getString("content"));
                reviews.add(review);
            }
            return reviews;

        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                reviewAdapter.clear();
                reviewAdapter.addAll(reviews);
            } else {
                Toast.makeText(getActivity(), " Something went wrong, please check your internet connection and try again! ", Toast.LENGTH_SHORT).show();
            }


        }
    }
}