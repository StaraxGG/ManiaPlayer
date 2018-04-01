package com.example.nicolas.maniaplayer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicolas on 30.03.2018.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(@NonNull Context context, ArrayList<Song> songs) {
        super(context,0,songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Song currentSong = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID interpret
        TextView interpretTextView = (TextView) listItemView.findViewById(R.id.interpret);
        // Get the Interpret  from the currentSong object and set this text on
        // the interpret TextView.
        interpretTextView.setText(currentSong.getInterpret());

        // Find the TextView in the list_item.xml layout with the ID titel
        TextView titelTextview = (TextView) listItemView.findViewById(R.id.titel);
        // Get the titel from the currentSong object and set this text on
        // the titel TextView.
        titelTextview.setText(currentSong.getTitel());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.cover);
        imageView.setImageResource(currentSong.getImageResourceId());

        // Find the ImageView in the list_item.xml layout with the ID image.
        //ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        // Check if an image is provided for this word or not
        //imageView.setImageResource(currentSong.getImageResourceId());
        // Make sure the view is visible
        imageView.setVisibility(View.VISIBLE);

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
