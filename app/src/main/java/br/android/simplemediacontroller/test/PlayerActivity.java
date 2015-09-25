package br.android.simplemediacontroller.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;

import br.android.simplemediacontroller.R;
import br.android.simplemediacontroller.player.core.MediaPlayerControl;
import br.android.simplemediacontroller.player.core.PlayerService;
import br.android.simplemediacontroller.player.listeners.OnConnectionListener;
import br.android.simplemediacontroller.player.listeners.OnInfoUpdateListener;
import br.android.simplemediacontroller.player.listeners.OnProgressMaxUpdateListener;
import br.android.simplemediacontroller.player.listeners.OnProgressUpdateListener;
import br.android.simplemediacontroller.player.model.Metadata;
import br.android.simplemediacontroller.player.utils.TimerConverter;
import br.android.simplemediacontroller.player.widgets.MediaButtons;

public class PlayerActivity extends AppCompatActivity implements OnConnectionListener, OnInfoUpdateListener, OnProgressMaxUpdateListener, OnProgressUpdateListener {

    MediaButtons mediaButtons;
    MediaPlayerControl mediaPlayerControl;
    MediaButtons.LayoutBigView bigView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mediaButtons = (MediaButtons) findViewById(R.id.controller);
        mediaButtons.inflateView(MediaButtons.BIG_VIEW);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaButtons.bindService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mediaButtons.setNotificationActive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMediaButtonsConnected(PlayerService service) {
        mediaPlayerControl = service.getMediaController();
        service.hideNotification();
        ArrayList<Metadata> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Metadata metadata = new Metadata();
            metadata.setId(i);
            metadata.setSongName("sample name" + i);
            metadata.setAlbumName("sample album" + i);
            metadata.setArtistName("sample artist" + i);
            metadata.setDuration(i * 15);
            metadata.setProgress(i * 4);
            metadata.setArtistUrl("http://cdn.shopify.com/s/files/1/0193/6253/products/Heisenberg_-_Bryan_Cranston.jpg?v=1406264978");
            metadata.setCoverUrl("https://m2.behance.net/rendition/pm/11263263/disp/5f9aa9b91e5310955f650191590ae165.jpg");

            if(i == 1){
                metadata.setMp3Path("http://1k6.sscdn.co/8/2/8/6/mcmayara-ai-como-eu-to-bandida-dois-322664.mp3?");
            }else if(i == 2){
                metadata.setMp3Path("http://cdn-redir.terra.com/sscdn/1ey/d/8/6/0/mcmayara-mayara-para-tudo-producao-efb-928664.mp3?");
            }else if(i == 3){
                metadata.setMp3Path("http://alfacentauro.sscdn.co/1k6/0/3/1/b/mcmayara-megamix-oficial-641e40.mp3?");
            }else if(i == 4){
                metadata.setMp3Path("http://cdn-redir.terra.com/sscdn/1j4/5/e/4/b/mcmayara-julieta-do-futuro-290302.mp3?");
            }else{
                metadata.setMp3Path("http://cdn-redir.terra.com/sscdn/1j4/c/6/9/d/mcmayara-ela-sabe-rebolar-2e4d19.mp3?");
            }

            data.add(metadata);
        }

        bigView = mediaButtons.getBigView();
        addMediaButtonListener();

        try {
            mediaPlayerControl.addSource(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mediaPlayerControl.addOnInfoUpdateListener(this);
        mediaPlayerControl.addOnProgressUpdateListener(this);
        mediaPlayerControl.addOnProgressMaxUpdateListener(this);
    }

    private void addMediaButtonListener(){
        bigView.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerControl.next();
            }
        });

        bigView.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerControl.previous();
            }
        });

        bigView.toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerControl.toggle();
            }
        });

        bigView.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayerControl.seekTo(progress);
                }

                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onMediaPlayerInfoChanged(Metadata metadata, MediaPlayerControl.PlaybackState state) {

    }

    @Override
    public void onProgressMaxUpdate(int duration) {
        bigView.seekBar.setMax(duration);
        bigView.duration.setText(TimerConverter.progressToTime(duration));
    }

    @Override
    public void onProgressUpdate(int progress) {
        bigView.seekBar.setProgress(progress);
        bigView.progress.setText(TimerConverter.progressToTime(progress));
    }
}
