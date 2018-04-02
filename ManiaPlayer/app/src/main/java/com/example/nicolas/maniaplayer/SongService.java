package com.example.nicolas.maniaplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SongService extends Service implements MediaPlayer.OnPreparedListener {

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
                            break;
                    }
                }
            };

    public SongService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /** Sets Audio Attributes */
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


        int res = mAudioManager.requestAudioFocus(mFocusRequest);
        synchronized (mFocusLock) {
            if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                mPlaybackDelayed = false;
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mPlaybackDelayed = false;
                playbackNow();

            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                mPlaybackDelayed = true;
            }
        }
        return START_STICKY;
    }

    public void playbackNow(){
        mMediaPlayer = MediaPlayer.create(this, R.raw.amg);
        //mMediaPlayer.setOnPreparedListener(this);
        //mMediaPlayer.prepareAsync();
        onPrepared(mMediaPlayer);
    }
    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    public void releaseMediaPlayer(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        }

    }
}
