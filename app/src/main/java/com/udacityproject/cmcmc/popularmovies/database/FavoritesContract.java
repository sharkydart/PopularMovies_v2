package com.udacityproject.cmcmc.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.udacityproject.cmcmc.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {
        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIEID = "movie_id";
        public static final String COLUMN_RELEASEDATE = "movie_release_date";
        public static final String COLUMN_VOTEAVG = "movie_vote_avg";
        public static final String COLUMN_POSTERPATH = "movie_poster_path";
        public static final String COLUMN_OVERVIEW = "movie_overview";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}