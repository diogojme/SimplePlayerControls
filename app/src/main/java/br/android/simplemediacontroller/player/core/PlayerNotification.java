package br.android.simplemediacontroller.player.core;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import br.android.simplemediacontroller.R;


/**
 * Created by diogojayme on 7/17/15.
 */
public class PlayerNotification {
    Context context;
    Notification notification;
    NotificationManager notifyManager;

    public static final int NOTIFICATION_PLAYER_ID = 9781211;

    public PlayerNotification(Context context){
        this.context = context;
        this.notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ServiceCast")
    public Notification buildNotification(Class clazz){
        Intent notificationIntent = new Intent(context, clazz);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews smallView = new RemoteViews(context.getPackageName(), R.layout.notification_player);

        addListeners(smallView);

        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_play_circle_filled_white_48dp)
                .setContentTitle("Music Playing")
                .setContentText("Click to Access Music Player")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notification.contentView = smallView;

        return notification;
    }

    public void addListeners(RemoteViews view) {
        Intent play = new Intent("NEXT");
        Intent next = new Intent("TOOGLE");
        Intent previous = new Intent("PREV");

        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_player_prev, pPrevious);

        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_player_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_player_toogle, pPlay);

    }

    public void cancel(){
        notifyManager.cancel(NOTIFICATION_PLAYER_ID);
    }

    public void update(){
        notifyManager.notify(NOTIFICATION_PLAYER_ID, notification);
    }
}