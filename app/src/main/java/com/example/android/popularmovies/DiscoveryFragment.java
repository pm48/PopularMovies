package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null || !(savedInstanceState.containsKey("movies")))
        {
            movieList = new ArrayList<Movie>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies",movieList);
        super.onSaveInstanceState(outState);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery,container,false);
        movieAdapter = new MovieAdapter(new ArrayList<Movie>(), new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra(EXTRA_PARAMS,movie);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerView.setAdapter(movieAdapter);

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
        private List<Integer> ids = new ArrayList<>();

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
                int id = movieDetails.getInt("id");
                movieObj.setId(id);
                movieList.add(movieObj);
                ids.add(id);
            }

            return movieList;

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies!=null)
            {
                movieAdapter.clear();
                movieAdapter.addAll(movies);
            }
            else
            {
                Toast.makeText(getActivity()," Something went wrong, please check your internet connection and try again! ",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected List<Movie> doInBackground(String...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String appId="7ec8978a5e5bc92a1e837697e5ca866f";
            String movieJsonStr = null;

            try{
                String baseUrl = "http://api.themoviedb.org/3/movie/".concat(params[0]);

                //String apiKey = "?api_key=" + BuildConfig.API_KEY;
                String apiKey = "?api_key=" + appId;
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
