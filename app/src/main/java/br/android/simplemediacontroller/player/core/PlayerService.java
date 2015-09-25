package br.android.simplemediacontroller.player.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.android.simplemediacontroller.test.PlayerActivity;

/**
 * Created by diogojayme on 9/18/15.
 */
public class PlayerService extends Service {

    PlayerBinder binder = new PlayerBinder();
    MediaPlayerControl mediaPlayerControl;
    PlayerNotification playerNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        playerNotification = new PlayerNotification(this);
        mediaPlayerControl = new MediaPlayerControl(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification(){
        if(mediaPlayerControl != null && mediaPlayerControl.isActive() && mediaPlayerControl.isStatePlaying()) {
            startForeground(PlayerNotification.NOTIFICATION_PLAYER_ID, playerNotification.buildNotification(PlayerActivity.class));
        }
    }

    public void hideNotification(){
        stopForeground(true);
    }

    @Nullable
    @Override public IBinder onBind(Intent intent) {
        return binder;
    }

    public class PlayerBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    public MediaPlayerControl getMediaController(){
        return mediaPlayerControl;
    }
}
