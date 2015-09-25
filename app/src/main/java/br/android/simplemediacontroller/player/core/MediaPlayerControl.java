package br.android.simplemediacontroller.player.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;
import java.util.ArrayList;

import br.android.simplemediacontroller.player.listeners.OnInfoUpdateListener;
import br.android.simplemediacontroller.player.listeners.OnProgressMaxUpdateListener;
import br.android.simplemediacontroller.player.listeners.OnProgressUpdateListener;
import br.android.simplemediacontroller.player.model.Metadata;


/**
 * Created by diogojayme on 9/18/15.
 *
 * This class is a instance of MediaPlayer and contains all methods to control its events
 *
 *
 */
public class MediaPlayerControl extends MediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    PlaybackState state = PlaybackState.IDLE;

    int position;
    ArrayList<Metadata> metaData;

    boolean error;
    Context context;
    Handler handler;
    MediaPlayer mediaPlayer;
    OnInfoUpdateListener infoUpdateListener;
    OnProgressUpdateListener progressUpdateListener;
    OnProgressMaxUpdateListener progressMaxUpdateListener;

    /**
     * Constructor you must call from your activity or your service
     *
     * */
    public MediaPlayerControl(Context context){
        this.context = context;
        this.handler = new Handler();
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setOnErrorListener(this);
        this.mediaPlayer.setOnPreparedListener(this);
        this.mediaPlayer.setOnCompletionListener(this);
        this.mediaPlayer.setOnSeekCompleteListener(this);
    }

    /**
     * ############Callbacks#############
     *
     * */
    public void addOnInfoUpdateListener(OnInfoUpdateListener infoUpdateListener){
        this.infoUpdateListener = infoUpdateListener;
    }

    public void addOnProgressUpdateListener(OnProgressUpdateListener progressListener){
        this.progressUpdateListener = progressListener;
    }

    public void addOnProgressMaxUpdateListener(OnProgressMaxUpdateListener progressMaxListener){
        this.progressMaxUpdateListener = progressMaxListener;
    }

    public void removeCallbacks(){
        this.infoUpdateListener = null;
        this.progressUpdateListener = null;
        this.progressMaxUpdateListener = null;
    }

    /**
     * This method contains the current song metadata that you passed into the source
     * @return a instance of the current info of song {@link Metadata}
     *
     * */
    public Metadata getCurrentMetadataItem(){
        return metaData.get(getTrackPosition());
    }

    /***
     * Tell when the media player can be played, paused or resumed
     * @return boolean if it have source added
     *
     */
    public boolean isActive(){
        return metaData != null;
    }

    public boolean isStatePlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * Used to notify the view when the activity become resumed
     *
     * */
    public void resume(){
        if(isActive()){
            infoUpdateListener.onMediaPlayerInfoChanged(getCurrentMetadataItem(), getState());
        }
    }

    /**
     * Use to change the current track position
     * @param position
     *
     * */
    public void setTrackPosition(int position){
        this.position = position;
    }

    /***
     * Retrieve the current playing song position
     * @return int position
     *
     */
    public int getTrackPosition(){
        return this.position;
    }

    /**
     * Add the source songs to the media player
     *  The songs must be a ArrayList of {@link Metadata} Object
     *  if the source is null or the source not contains all attributes an exception will be raised
     *  @param data
     *
     * */
    public void addSource(ArrayList<Metadata> data) throws Exception{
        if(data == null)
            throw new NullPointerException("Source cannot be null");

        this.metaData = data;
    }

    /**
     * Change current state of the MediaPlayer
     * Pause, Play otherwise resume if already playing
     *
     * */
    public void toggle(){
        if(getState() == PlaybackState.PLAYING){
            pause();
        }else if(getState() == PlaybackState.PAUSED){
            play();
        }else{
            prepare();
        }
    }

    /**
     * Tell media player to start
     * this method must be called after prepareAsync or prepare
     *
     * */
    public void play(){
        mediaPlayer.start();
        setState(PlaybackState.PLAYING);
        handler.post(progress);
    }

    /***
     * Pause MediaPlayer and stop progress update
     *
     */
    public void pause(){
        mediaPlayer.pause();
        setState(PlaybackState.PAUSED);
        handler.removeCallbacks(progress);
    }

    /**
     * Change the current position to the next position
     * and tell MediaPlayer to prepare the new song from the list
     *
     * */
    public void next(){
        if(this.position == this.metaData.size() - 1){
            this.position = 0;
        }else{
            this.position ++;
        }

        setState(PlaybackState.IDLE);
        prepare();
    }

    /**
     * The same of next, but the index in decreased by one
     *
     * */
    public void previous(){
        if(this.position == 0){
            position = this.metaData.size() - 1;
        }else{
            this.position --;
        }

        setState(PlaybackState.IDLE);
        prepare();
    }

    /**
     * Prepare media player to execute selected song
     *
     * */
    public void prepare(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(metaData.get(getTrackPosition()).getMp3Path());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change media player audio to the current seek bar position that user changed
     *
     * */
    public void seekTo(int position){
        mediaPlayer.seekTo(position * 1000);
    }

    public Runnable progress = new Runnable() {
        @Override
        public void run() {

            if(mediaPlayer.isPlaying()) {
                if(progressMaxUpdateListener != null)
                    progressMaxUpdateListener.onProgressMaxUpdate(mediaPlayer.getDuration() / 1000);

                if(progressUpdateListener != null)
                    progressUpdateListener.onProgressUpdate(mediaPlayer.getCurrentPosition() / 1000);
            }

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!error){
            next();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(infoUpdateListener != null)
            infoUpdateListener.onMediaPlayerInfoChanged(getCurrentMetadataItem(), getState());
        play();
        error = false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        error = true;
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        handler.removeCallbacks(progress);
        play();
    }


    public void setState(PlaybackState state){
        this.state = state;
    }

    /**
     * Get the current state of media player
     * The state IDLE indicate when media player is waiting for user action and the state that will
     * be executed is PLAYING.
     * The state PAUSED and PLAYING is when user executes a toggle button to start or stop the player
     *
     * {@link br.android.simplemediacontroller.player.core.MediaPlayerControl.PlaybackState}
     * */
    public PlaybackState getState(){
        return state;
    }

    public enum PlaybackState{
        IDLE,
        PAUSED,
        PLAYING
    }

}
