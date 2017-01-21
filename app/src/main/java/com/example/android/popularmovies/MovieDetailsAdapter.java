//package com.example.android.popularmovies;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CursorAdapter;
//
///**
// * Created by prernamanaktala on 1/19/17.
// */
//
//public class MovieDetailsAdapter extends CursorAdapter {
//
//    private static final int TRAILERS = 0;
//    private static final int OVERVIEW = 1;
//    private static final int REVIEWS = 2;
//
//    public MovieDetailsAdapter(Context context, Cursor c, int flags) {
//        super(context, c, flags);
//    }
//
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        int viewType = getItemViewType(cursor.getPosition());
//        int layoutId = -1;
//        switch(viewType)
//        {
//            case OVERVIEW :{
//                layoutId = R.layout.fragment_detail;
//            }
//
//            case TRAILERS :{
//                layoutId = R.layout.trailer_list;
//            }
//
//
//        }
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//
//    }
//}
