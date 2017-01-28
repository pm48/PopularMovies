package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by prernamanaktala on 1/22/17.
 */

public class MovieContract {

    public static final String TABLENAME = "favorites";
    public static final String _ID = "_id";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_FAVORITE = "favorite";

    private static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.MovieProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_MOVIE = "movie";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

}
