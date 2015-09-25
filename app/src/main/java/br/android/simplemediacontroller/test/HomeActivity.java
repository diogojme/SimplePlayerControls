package br.android.simplemediacontroller.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import br.android.simplemediacontroller.R;
import br.android.simplemediacontroller.player.core.MediaPlayerControl;
import br.android.simplemediacontroller.player.core.PlayerService;
import br.android.simplemediacontroller.player.listeners.OnConnectionListener;
import br.android.simplemediacontroller.player.listeners.OnInfoUpdateListener;
import br.android.simplemediacontroller.player.model.Metadata;
import br.android.simplemediacontroller.player.widgets.MediaButtons;

/**
 * Created by diogojayme on 9/18/15.
 */
public class HomeActivity extends AppCompatActivity implements OnConnectionListener, OnInfoUpdateListener {
    MediaButtons mediaButtons;
    MediaPlayerControl mediaController;
    MediaButtons.LayoutSmallView smallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mediaButtons = (MediaButtons) findViewById(R.id.home_np);
        mediaButtons.inflateView(MediaButtons.SMALL_VIEW);
        startActivity(new Intent(this, PlayerActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaButtons.bindService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaButtons.unbindService();
        mediaButtons.setNotificationActive(true);
    }

    @Override
    public void onMediaButtonsConnected(PlayerService service) {
        service.hideNotification();
        mediaController  = service.getMediaController();
        smallView = mediaButtons.getSmallView();

        if(mediaController.isActive()) {
            smallView.container.setVisibility(View.VISIBLE);
            smallView.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaController.next();
                }
            });

            smallView.toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaController.toggle();
                }
            });

            smallView.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, PlayerActivity.class));
                }
            });
            mediaController.addOnInfoUpdateListener(this);
        }else{
            smallView.container.setVisibility(View.GONE);
            mediaController.removeCallbacks();
        }

        mediaButtons.setNotificationActive(false);
        mediaController.resume();
    }

    @Override
    public void onMediaPlayerInfoChanged(Metadata metadata, MediaPlayerControl.PlaybackState state) {
        smallView.song.setText(metadata.getSongName());
        smallView.album.setText(metadata.getAlbumName());
        smallView.artist.setText(metadata.getArtistName());
    }

}
