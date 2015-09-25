package br.android.simplemediacontroller.player.widgets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import br.android.simplemediacontroller.R;
import br.android.simplemediacontroller.player.core.PlayerService;
import br.android.simplemediacontroller.player.listeners.OnConnectionListener;

/**
 * Created by diogojayme on 9/18/15.
 */
public class MediaButtons extends FrameLayout {

    Context context;
    boolean connected;
    PlayerService service;
    LayoutBigView bigView;
    LayoutSmallView smallView;
    OnConnectionListener listener;

    public static final int BIG_VIEW = 0;
    public static final int SMALL_VIEW = 1;

    public MediaButtons(Context context) {
        super(context);
        init(context);
    }

    public MediaButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context){
        this.context = context;
    }

    public void inflateView(int id){
        if(id == BIG_VIEW){
            View view = inflate(context, R.layout.player_big_view, this);
            bigView = new LayoutBigView(view);
        }else{
            View view = inflate(context, R.layout.player_small_view, this);
            smallView = new LayoutSmallView(view);
        }
    }

    public void bindService(@NonNull OnConnectionListener listener){
        this.listener = listener;
        Intent intent = new Intent(context, PlayerService.class);
        context.startService(intent);
        context.bindService(intent, playerConnection, PlayerService.BIND_AUTO_CREATE);
    }

    public void unbindService(){
        context.unbindService(playerConnection);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbindService();
    }

    public void setNotificationActive(boolean active) {
        try {
            if(service == null){
                throw new NullPointerException("You must connect to the service before call this method");
            }

            if(active){
                service.showNotification();
            }else{
                service.hideNotification();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LayoutBigView getBigView(){
        return bigView;
    }

    public LayoutSmallView getSmallView(){
        return smallView;
    }

    public class LayoutBigView{
        public ImageView cover;
        public SeekBar seekBar;
        public ImageButton next;
        public TextView duration;
        public TextView progress;
        public ImageButton toggle;
        public ImageButton shuffle;
        public ImageButton previous;

        public LayoutBigView(View view){
            cover = (ImageView) view.findViewById(R.id.player_big_view_cover);
            next = (ImageButton) view.findViewById(R.id.player_big_view_next);
            seekBar = (SeekBar) view.findViewById(R.id.player_big_view_seek_bar);
            toggle = (ImageButton) view.findViewById(R.id.player_big_view_toggle);
            shuffle = (ImageButton) view.findViewById(R.id.player_big_view_shuffle);
            previous = (ImageButton) view.findViewById(R.id.player_big_view_previous);
            duration = (TextView) view.findViewById(R.id.player_big_view_duration);
            progress = (TextView) view.findViewById(R.id.player_big_view_progress);
        }
    }

    public class LayoutSmallView{
        public TextView song;
        public TextView album;
        public View container;
        public TextView artist;
        public ImageView cover;
        public ImageButton next;
        public ImageButton toggle;

        public LayoutSmallView(View view){
            container = view.findViewById(R.id.player_small_view_container);
            cover = (ImageView) view.findViewById(R.id.player_small_view_cover);
            next = (ImageButton) view.findViewById(R.id.player_small_view_next);
            toggle = (ImageButton) view.findViewById(R.id.player_small_view_toggle);
            song = (TextView) view.findViewById(R.id.player_small_view_song);
            artist = (TextView) view.findViewById(R.id.player_small_view_artist);
        }
    }

    /**
     * The service that the view will connect
     * it is automatically connected when the view is placed in the activity
     * and disconnected when you go back from activity
     *
     * */
    public ServiceConnection playerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            connected = true;
            service = ((PlayerService.PlayerBinder) iBinder).getService();
            listener.onMediaButtonsConnected(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };



}
