package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by prernamanaktala on 1/19/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter {
    private List<Review> reviews;
    private final OnItemClickListener listener;


    public ReviewAdapter(List<Review> reviews, OnItemClickListener listener){
        this.reviews = reviews;
        this.listener = listener;
    }

    private interface OnItemClickListener{
         void onItemClick();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
