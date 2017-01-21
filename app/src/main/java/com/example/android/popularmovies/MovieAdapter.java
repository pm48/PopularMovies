package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by prernamanaktala on 11/17/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movies;
    private final OnItemClickListener listener;




    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieAdapter(List<Movie> movies,OnItemClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(Movie item);

    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Movie movie = movies.get(position);
        holder.iconView.setImageBitmap(null);
        holder.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(movie);
            }
        });
        Picasso.with(holder.iconView.getContext()).load(movie.getPoster()).into(holder.iconView);


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView iconView;

        public ViewHolder(View v) {
            super(v);
            iconView = (ImageView) v.findViewById(R.id.list_item_icon);
        }


    }


    public void addAll(List<Movie> items) {
        movies.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }



}
