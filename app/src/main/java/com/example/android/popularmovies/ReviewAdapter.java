package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prernamanaktala on 1/19/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviews;


    public ReviewAdapter(List<Review> reviews){
        this.reviews = reviews;
    }

    public void clear() {
        reviews.clear();
        notifyDataSetChanged();
        
    }

    public void addAll(List<Review> items) {
        reviews.addAll(items);
        notifyDataSetChanged();
    }



    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item,parent,false);
       ViewHolder holder = new ViewHolder(v);return holder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Review review = reviews.get(position);
        holder.content.setText(review.getContent());
        holder.author.setText(review.getAuthor());

    }




    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView content;
        public TextView author;

        public ViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.tvContent);
            author = (TextView) v.findViewById(R.id.tvAuthor);
        }
    }
}
