package com.example.nicolas.maniaplayer;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TitelFragment extends Fragment {

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focuschange) {
                    switch (focuschange) {
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            audioFocusLoss();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            audioFocusLoss();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mMediaPlayer.start();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            releaseMediaPlayer();
                            break;
                        default:
                            audioFocusLoss();
                    }
                }
                public void audioFocusLoss(){
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                }
    };



    public void releaseMediaPlayer(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }

    }

    public TitelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_list,container,false);
        final ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("AMG","Fler",R.drawable.fler));
        songs.add(new Song("Flizzy","Fler",R.drawable.flizzy));
        songs.add(new Song("Meister Yoda","Fler",R.drawable.azet));

        SongAdapter songAdapter = new SongAdapter(getActivity(),songs);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(songAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songs.get(position);

            }
        });


        return rootView;
    }

}
