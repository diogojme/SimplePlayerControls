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
    public MediaPlayerControl(Context context){
        this.context = context;
        this.handler = new Handler();
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setOnErrorListener(this);
        this.mediaPlayer.setOnPreparedListener(this);
        this.mediaPlayer.setOnCompletionListener(this);
        this.mediaPlayer.setOnSeekCompleteListener(this);
    }

    public Metadata getCurrentMetadataItem(){
        return metaData.get(getTrackPosition());
    }

    //Indicate when you can manipulate media player
    public boolean isActive(){
        return metaData != null;
    }

    public boolean isStatePlaying(){
        return mediaPlayer.isPlaying();
    }

    public void resume(){
        if(isActive()){
            infoUpdateListener.onMediaPlayerInfoChanged(getCurrentMetadataItem(), getState());
        }
    }

    public void addOnInfoUpdateListener(OnInfoUpdateListener infoUpdateListener){
        this.infoUpdateListener = infoUpdateListener;
    }

    public void addOnProgressUdpdateListener(OnProgressUpdateListener progressListener){
        this.progressUpdateListener = progressListener;
    }

    public void addOnProgressMaxUdpdateListener(OnProgressMaxUpdateListener progressMaxListener){
        this.progressMaxUpdateListener = progressMaxListener;
    }

    public void removeCallbacks(){
        this.infoUpdateListener = null;
        this.progressUpdateListener = null;
        this.progressMaxUpdateListener = null;
    }

    public void setTrackPosition(int position){
        this.position = position;
    }

    public int getTrackPosition(){
        return this.position;
    }

    public void addSource(ArrayList<Metadata> metaData){
        this.metaData = metaData;
    }

    public void toggle(){
        if(getState() == PlaybackState.PLAYING){
            pause();
        }else if(getState() == PlaybackState.PAUSED){
            play();
        }else{
            prepare();
        }
    }

    public void play(){
        mediaPlayer.start();
        setState(PlaybackState.PLAYING);
        handler.post(progress);
    }

    public void pause(){
        mediaPlayer.pause();
        setState(PlaybackState.PAUSED);
        handler.removeCallbacks(progress);
    }

    public void next(){
        if(this.position == this.metaData.size() - 1){
            this.position = 0;
        }else{
            this.position ++;
        }

        setState(PlaybackState.IDLE);
        prepare();
    }

    public void previous(){
        if(this.position == 0){
            position = this.metaData.size() - 1;
        }else{
            this.position --;
        }

        setState(PlaybackState.IDLE);
        prepare();
    }

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

    public PlaybackState getState(){
        return state;
    }

    public enum PlaybackState{
        IDLE,
        PAUSED,
        PLAYING
    }

}
