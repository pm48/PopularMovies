package com.example.android.popularmovies;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prernamanaktala on 1/17/17.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer>{


    public TrailerAdapter(Context context, List<Trailer> objects) {
        super(context, 0,objects);
    }

    private static class ViewHolder {
        TextView trailer_title;
        ImageView trailer_thumbnail;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.trailer_list, parent, false);
            viewHolder.trailer_title = (TextView) convertView.findViewById(R.id.list_item_trailer_title);
            viewHolder.trailer_thumbnail = (ImageView) convertView.findViewById(R.id.image_arrow_play);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


       viewHolder.trailer_title.setText(trailer.getName());



        return convertView;

    }
}
