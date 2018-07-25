package com.udacityproject.cmcmc.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.udacityproject.cmcmc.popularmovies.Model.MovieInfo;
//import com.udacityproject.cmcmc.popularmovies.database.FavoritesContract;
import com.udacityproject.cmcmc.popularmovies.database.FavoritesContract.FavoritesEntry;
import com.udacityproject.cmcmc.popularmovies.database.FavoritesDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;

public class MainDiscovery extends AppCompatActivity {

    private ArrayList<MovieInfo> mMovies;
    private MoviePostersAdapter mMoviePostersAdapter;
//    private SQLiteDatabase mFavoritesDb;
    private String mSortMethod;
    private String mSelectionChosen;
    private static final String MOVIES_KEY = "movies arraylist (parcelable) key";
    private static final String SORT_METHOD = "the sorting method selected key";
    private static final String SELECTION_CHOSEN = "Sort selection chosen key";
    private static final String SELECTION_NONE = "nothing selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);

        // database setup
//        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
//        mFavoritesDb = dbHelper.getReadableDatabase();

        mMovies = new ArrayList<MovieInfo>();
        if(savedInstanceState == null){
            mMovies = new ArrayList<MovieInfo>();
            Log.d("fart","savedInstanceState is null");
        }else if(savedInstanceState.containsKey(MOVIES_KEY)){
            Log.d("fart","Loading from savedInstanceState");
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        }else{
            Log.d("fart", "savedInstanceState is not null, AND it doesn't have a movies_key");
        }
        Log.d("fart", "73: [mMoviePostersAdapter init with mMovies]");
        mMoviePostersAdapter = new MoviePostersAdapter(this, mMovies, getFavoritesFromDb(), false);

        GridView mMoviePosters;
        mMoviePosters = findViewById(R.id.gv_moviePosters);
        mMoviePosters.setAdapter(mMoviePostersAdapter);
        mMoviePosters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchMovieDetailsActivity(position);
                //load detail view from here, sending/setting movie info selected
            }
        });

        if(savedInstanceState != null) {
            mSortMethod = savedInstanceState.getString(SORT_METHOD);
            mSelectionChosen = savedInstanceState.getString(SELECTION_CHOSEN);
        }
        //Start by loading based on most popular
        if(mSortMethod == null || mSortMethod.isEmpty()) {
            mSortMethod = this.getString(R.string.get_most_popular);
            mSelectionChosen = SELECTION_NONE;
        }

        loadMovieData(mSortMethod);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        //save the items that are currently in the adapter
        state.putParcelableArrayList(MOVIES_KEY, mMoviePostersAdapter.getItems()); //used to be mMovies
        state.putString(SORT_METHOD, mSortMethod);
        state.putString(SELECTION_CHOSEN, mSelectionChosen);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadMovieData(String sortMethod) {
        new FetchMoviesTask().execute(sortMethod);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            if(params[0].equals(MainDiscovery.this.getString(R.string.load_db_favorites))){
                // If the parameter is to load from the DB, skip the network code
                return params[0];
            }else{
                mMoviePostersAdapter.useFavoritesDb(false);
            }

            // Before bothering to do networking, see if there are already movies loaded
            if(mMoviePostersAdapter.getItems().size() > 0) {
                Log.d("fart", "134: mMoviePostersAdapter is currently populated");
                // If the current sort method is the same as the selection, use the existing data
                if(mSortMethod.equals(mSelectionChosen)) {
                    Log.d("fart", "137: mSortMethod ='" + mSortMethod + "'= mSelectionChosen = '" + mSelectionChosen + "'.");
                    Log.d("fart", "(137) -- avoided NETWORK pull --");
                    return MOVIES_KEY;
                }
                // Movies are loaded, but
                Log.d("fart", "141: pull movie data from network because adapter doesn't reflect selection");
            }else{
                // No movies are loaded, so the network calls will happen.
                Log.d("fart", "142: pull movie data from network because adapter is empty");
            }

            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager != null) {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        String sortMethod = params[0];
                        URL moviesRequestUrl = buildSortAPIUrl(sortMethod);

                        try {
                            return getResponseFromUrl(moviesRequestUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String movieData) {
            if (movieData != null) {
                if(movieData.equals(MainDiscovery.this.getString(R.string.load_db_favorites))){
                    Log.d("fart", "174: Movie Posters Adapter should load Favorites");
                    mMoviePostersAdapter.useFavoritesDb(true);
                    if(mMoviePostersAdapter.getItems().size() > 0 && mSortMethod.equals(movieData)){
                        Log.d("fart", "177: mMoviePostersAdapter is populated already, and mSortMethod = '" + mSortMethod + "'");
                        Log.d("fart", "(177) -- Favorites avoided DB pull --");
                    }else {
                        Log.d("fart", "180: Adapter doesn't reflect Favorites, or is empty. Should load Favorites from DB");
                        mMoviePostersAdapter.swapCursor(getFavoritesFromDb());
                    }
                    Log.d("fart", "185: [Fresh FAVORITES shown in mMoviePostersAdapter]");
                    mMoviePostersAdapter.notifyDataSetChanged();
                }else {
                    try {
                        if(!movieData.equals(MOVIES_KEY)) {
                            Log.d("fart", "190: -- Parse JSON into mMovies --");
                            mMovies.clear();
                            JSONObject movieJson = new JSONObject(movieData);
                            int page = movieJson.getInt("page");
                            int total_results = movieJson.getInt("total_results");
                            int total_pages = movieJson.getInt("total_pages");
                            JSONArray results = movieJson.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentObj = results.getJSONObject(i);
                                MovieInfo tempMovie = new MovieInfo(currentObj);
                                mMovies.add(tempMovie);
                            }
                        }else{
                            Log.d("fart", "203: -- NOT Favorites avoided JSON parse --");
                        }
                        Log.d("fart", "205: [Fresh POPULAR/RATED shown in mMoviePostersAdapter]");
                        mMoviePostersAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainDiscovery.this, "Data Issue", Toast.LENGTH_LONG).show();
                    }
                }
                Log.d("fart", "212: Setting mSortMethod(" + mSortMethod + ") to mSelectionChosen(" + mSelectionChosen +").");
                if(mSelectionChosen.equals(SELECTION_NONE)) {
                    Log.d("fart", "(once) 214: -- initialize selection chosen to sortmethod -- (once)");
                    mSelectionChosen = mSortMethod;
                }
                Log.d("fart", "217: -- syncing mSortMethod with mSelectionChosen --");
                mSortMethod = mSelectionChosen;
            }
            else{
                Toast.makeText(MainDiscovery.this, "Network Issue", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sortselection, menu);
        return true;
    }

    // If the sort selected is the same as the current sort, do nothing.
    // Otherwise, if it is different, set the selectionChosen to it,
    // and send the selection to the data loader to determine how to load the data.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            if(!mSortMethod.equals(this.getString(R.string.get_most_popular))) {
                mSelectionChosen = this.getString(R.string.get_most_popular);
                loadMovieData(mSelectionChosen);
            }

            return true;
        }
        else if(id == R.id.action_highest_rated){
            if(!mSortMethod.equals(this.getString(R.string.get_top_rated))) {
                mSelectionChosen = this.getString(R.string.get_top_rated);
                loadMovieData(mSelectionChosen);
            }

            return true;
        }
        else if(id == R.id.action_show_favorited){
            if(!mSortMethod.equals(this.getString(R.string.load_db_favorites))) {
                mSelectionChosen = this.getString(R.string.load_db_favorites);
                loadMovieData(mSelectionChosen);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public URL buildSortAPIUrl(String sortAPI) {
        /*  Example API call
        https://api.themoviedb.org/3
            /movie/popular
            ?
            api_key=012987c6c4644746653878570fdf79dd
            &language=en-US
            &page=1
        */

        String apiKey = this.getString(R.string.query_apikey);
        int pageToReturn = 1;
        Uri builtUri = Uri.parse(this.getString(R.string.base_url)).buildUpon()
                .appendEncodedPath(sortAPI)
                .appendQueryParameter(this.getString(R.string.param_api), apiKey)
                .appendQueryParameter(this.getString(R.string.param_language), this.getString(R.string.query_language))
                .appendQueryParameter(this.getString(R.string.param_page), Integer.toString(pageToReturn))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void launchMovieDetailsActivity(int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity._ID, ((MovieInfo)mMoviePostersAdapter.getItem(position)).get_rowId());
        intent.putExtra(MovieDetailsActivity.MOVIE_ID, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getId());
        intent.putExtra(MovieDetailsActivity.MOVIE_VOTE_AVERAGE, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getVote_average());
        intent.putExtra(MovieDetailsActivity.MOVIE_TITLE, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getTitle());
        intent.putExtra(MovieDetailsActivity.MOVIE_POSTER_PATH, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getPoster_path());
        intent.putExtra(MovieDetailsActivity.MOVIE_OVERVIEW, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getOverview());
        intent.putExtra(MovieDetailsActivity.MOVIE_RELEASE_DATE, ((MovieInfo)mMoviePostersAdapter.getItem(position)).getRelease_date());
        startActivity(intent);
    }

    private Cursor getFavoritesFromDb(){
        return getContentResolver().query(FavoritesEntry.CONTENT_URI, null, null, null, FavoritesEntry.COLUMN_TITLE);
    }
}
