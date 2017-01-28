package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies.MovieContract.*;


/**
 * Created by prernamanaktala on 1/22/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "movie.db";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLENAME + " (" + MovieContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MOVIE_ID
            + " INTEGER NOT NULL, " + COLUMN_TITLE
            + " TEXT NOT NULL, " + COLUMN_IMAGE
            +  " TEXT, " + COLUMN_FAVORITE
            + " INTEGER);";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }
}
