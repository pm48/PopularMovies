package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * Created by prernamanaktala on 11/20/16.
 */

public  class DiscoveryFragment extends Fragment {
    public static final String EXTRA_PARAMS = "movie";
    private MovieAdapter movieAdapter;
    private  GridView gridView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery,container,false);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra(EXTRA_PARAMS,movie);
                startActivity(intent);

            }
        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings){
            startActivity(new Intent(getActivity(),SettingsActivity.class));}
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMode = preferences.getString(getString(R.string.pref_sort_key),getString(R.string.pref_most_popular));
        moviesTask.execute(sortMode);
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,List<Movie>> {

        private List<Movie> parseJSON(String movieJsonStr) throws JSONException{
            List<Movie> movieList = new ArrayList<>();
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray resultArray = movieJson.getJSONArray("results");

            for(int i=0;i<resultArray.length();i++)
            {
                JSONObject movieDetails = resultArray.getJSONObject(i);
                Movie movieObj = new Movie();
                movieObj.setPoster(movieDetails.getString("poster_path"));
                movieObj.setTitle(movieDetails.getString("title"));
                movieObj.setReleaseDate(movieDetails.getString("release_date"));
                movieObj.setSynopsis(movieDetails.getString("overview"));
                movieObj.setVoteAverage(movieDetails.getString("vote_average"));
                movieList.add(movieObj);
            }

            return movieList;

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies!=null)
            {
                movieAdapter.clear();
                for(Movie movie: movies)
                {
                    movieAdapter.add(movie);
                }
            }

        }

        @Override
        protected List<Movie> doInBackground(String...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try{
                String baseUrl = "http://api.themoviedb.org/3/movie/".concat(params[0]);

                String apiKey = "?api_key=" +BuildConfig.API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=reader.readLine())!=null)
                {
                    buffer.append(line +"\n");
                }
                if (buffer.length()==0)
                {
                    return null;
                }
                movieJsonStr = buffer.toString();
                return parseJSON(movieJsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection !=null)
                {
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }

    }



}
