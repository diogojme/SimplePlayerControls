package br.android.simplemediacontroller.player.listeners;

import br.android.simplemediacontroller.player.core.MediaPlayerControl;
import br.android.simplemediacontroller.player.model.Metadata;

public interface OnInfoUpdateListener {
    void onMediaPlayerInfoChanged(Metadata metadata, MediaPlayerControl.PlaybackState state);
}