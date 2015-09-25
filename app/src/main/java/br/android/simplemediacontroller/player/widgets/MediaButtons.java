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

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
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
        @Bind(R.id.player_big_view_cover) public ImageView cover;
        @Bind(R.id.player_big_view_next) public ImageButton next;
        @Bind(R.id.player_big_view_seek_bar) public SeekBar seekBar;
        @Bind(R.id.player_big_view_toggle) public ImageButton togle;
        @Bind(R.id.player_big_view_shuffle) public ImageButton shuffle;
        @Bind(R.id.player_big_view_previous) public ImageButton previous;
        @Bind(R.id.player_big_view_duration) public TextView duration;
        @Bind(R.id.player_big_view_progress) public TextView progress;

        public LayoutBigView(View view){
            ButterKnife.bind(this, view);
        }
    }

    public class LayoutSmallView{
        @Bind(R.id.player_small_view_song) public TextView song;
        @Bind(R.id.player_small_view_album) public TextView album;
        @Bind(R.id.player_small_view_next) public ImageButton next;
        @Bind(R.id.player_small_view_artist) public TextView artist;
        @Bind(R.id.player_small_view_container) public View container;
        @Bind(R.id.player_small_view_toggle) public ImageButton toggle;

        public LayoutSmallView(View view){
            ButterKnife.bind(this, view);
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
