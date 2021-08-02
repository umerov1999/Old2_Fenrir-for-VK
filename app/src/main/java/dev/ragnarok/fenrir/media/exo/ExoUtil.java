package dev.ragnarok.fenrir.media.exo;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class ExoUtil {
    public static void pausePlayer(SimpleExoPlayer player) {
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    public static void startPlayer(SimpleExoPlayer player) {
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
}