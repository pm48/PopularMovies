package com.example.android.popularmovies;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prernamanaktala on 1/17/17.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> trailers;
    private final OnItemClickListener listener;




    // Provide a suitable constructor (depends on the kind of dataset)
    public TrailerAdapter(List<Trailer> trailers,OnItemClickListener listener) {
        this.trailers = trailers;
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(Trailer item);

    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Trailer trailer = trailers.get(position);
        holder.playButton.setImageBitmap(null);
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(trailer);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView playButton;
        public TextView trailer_Title;

        public ViewHolder(View v) {
            super(v);
            playButton = (ImageView) v.findViewById(R.id.image_arrow_play);
            trailer_Title = (TextView) v.findViewById(R.id.list_item_trailer_title);
        }


    }


    public void addAll(List<Trailer> items) {
        trailers.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        trailers.clear();
        notifyDataSetChanged();
    }



}
