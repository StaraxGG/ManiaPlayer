package com.example.nicolas.maniaplayer;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
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
    private AudioAttributes mPlaybackAttributes;
    private AudioFocusRequest mFocusRequest;
    private boolean mPlaybackDelayed = false;
    private boolean mResumeOnFocusGain = false;
    private final Object mFocusLock = new Object();

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    // implementation of the OnAudioFocusChangeListener
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener1 =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            if (mPlaybackDelayed || mResumeOnFocusGain) {
                                synchronized (mFocusLock) {
                                    mPlaybackDelayed = false;
                                    mResumeOnFocusGain = false;
                                }
                                //playbackNow();
                                mMediaPlayer.start();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            synchronized (mFocusLock) {
                                // this is not a transient loss, we shouldn't automatically resume for now
                                mResumeOnFocusGain = false;
                                mPlaybackDelayed = false;
                            }
                            //pausePlayback();
                            releaseMediaPlayer();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // we handle all transient losses the same way because we never duck audio books
                            synchronized (mFocusLock) {
                                // we should only resume if playback was interrupted
                                mResumeOnFocusGain = mMediaPlayer.isPlaying();
                                mPlaybackDelayed = false;
                            }
                            //pausePlayback();
                            mMediaPlayer.pause();
                            mMediaPlayer.seekTo(0);
                            break;
                    }
                }
            };
    /*
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener1 =
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
    */


    public void releaseMediaPlayer(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        }

    }

    public TitelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_list,container,false);

        //AudioFocus Stuff -----------------------------------------------------

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // initialization of the audio attributes and focus request
        mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(mAudioFocusChangeListener1)
                .build();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(mPlaybackAttributes);

        mPlaybackDelayed = false;



        //AudioFocus Stuff -----------------------------------------------------

        final ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("AMG","Fler",R.drawable.fler, R.raw.amg));
        songs.add(new Song("Flizzy","Fler",R.drawable.flizzy, R.raw.amg));
        songs.add(new Song("Meister Yoda","Fler",R.drawable.azet, R.raw.amg));

        SongAdapter songAdapter = new SongAdapter(getActivity(),songs);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(songAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                releaseMediaPlayer();
                Song song = songs.get(position);

                // requesting audio focus
                int res = mAudioManager.requestAudioFocus(mFocusRequest);
                synchronized (mFocusLock) {
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                        mPlaybackDelayed = false;
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mPlaybackDelayed = false;
                        //playbackNow();
                        mMediaPlayer = MediaPlayer.create(getActivity(), song.getAudioResourceId());
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                        mPlaybackDelayed = true;
                    }
                }

            }
        });


        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}
