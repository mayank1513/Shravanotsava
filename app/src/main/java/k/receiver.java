package k;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import java.util.Objects;

import static k.s.mPlayer;

public class receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case "android.intent.action.MEDIA_BUTTON":
                KeyEvent key = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                onKeyDown(key.getKeyCode(), key);
                break;
            case "com.krishnaapps.karnamrita.playpause":
                s.callBacks.playPause();
                break;
            case "com.krishnaapps.karnamrita.next":
                s.callBacks.playNext();
                break;
            case "com.krishnaapps.karnamrita.prev":
                s.callBacks.playPrevious();
                break;
            case "com.krishnaapps.karnamrita.like":
                s.callBacks.setLike("");
                break;
            case "com.krishnaapps.karnamrita.share":
                s.callBacks.shareSong("");
                break;
            case "com.krishnaapps.karnamrita.play_pause_chant_with_sp":
                s.callBacks.playPauseChantWithSP();
                break;
            case "com.krishnaapps.karnamrita.pause_chant_with_sp":
                s.callBacks.stopChantWithSP();
                break;
            case "com.krishnaapps.karnamrita.sp_chant_vol_down":
                s.callBacks.adjustSPChantVol(false);
                break;
            case "com.krishnaapps.karnamrita.sp_chant_vol_up":
                s.callBacks.adjustSPChantVol(true);
                break;
        }
    }
    public static void onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    s.callBacks.playNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    s.callBacks.playPrevious();
                    break;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    if(mPlayer!=null&&mPlayer.isPlaying())
                        mPlayer.seekTo(mPlayer.getCurrentPosition()+10000);
                    break;
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    if(mPlayer!=null&&mPlayer.isPlaying())
                        mPlayer.seekTo(mPlayer.getCurrentPosition()-10000);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    s.callBacks.playPause();
                    break;
            }
        }
    }
}
