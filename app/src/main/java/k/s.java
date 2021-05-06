package k;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static k.helper.decode;
import static k.helper.isNetworkAvailable;
import static k.k.AUDIO_TABLE;
import static k.k.REPEAT_NONE;
import static k.k.REPEAT_ONE;
import static k.k.arteDir;
import static k.k.audioDir;
import static k.k.mAudioDb;
import static k.k.nowPlaying;
import static k.m.apps;
import static k.m.baseDir;

public class s extends Service {
    IBinder mBinder = new MBinder();
    static AudioManager mAudioManager;
    static int maxStreamVolume;
    public static Player mPlayer;
    public static MediaPlayer player1;
    public ComponentName mRemoteControlResponder;

    ArrayList<String> Queue = new ArrayList<>();
    int nowPlayingInd = 0;
    s mContext;
    static CallBacks callBacks;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mRemoteControlResponder = new ComponentName(getPackageName(), receiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
    }

    public void instantiatePlayer() {
        if (mPlayer != null) {
            if (mPlayer.isPrepared) mPlayer.stop();
            mPlayer.reset();
        }
        mPlayer = new Player();
    }

    public void startPlaying() {
        if (mPlayer == null) mPlayer = new Player();
        else {
            if (mPlayer.isPrepared) mPlayer.stop();
            mPlayer.reset();
        }
        if (nowPlaying == null) {
            if (Queue.isEmpty()) Queue = callBacks.getQueue();
            Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id=?", new String[]{Queue.get(nowPlayingInd)});
            if (c.moveToFirst()) callBacks.setNowPlaying(c);
        }
        File f = new File(audioDir, nowPlaying.id);
        if (f.exists()) {
            try {
                mPlayer.setDataSource(f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (k.prefs.shuffle) shuffleQueue();
            if (!preventDownload && f.length() < .95 * nowPlaying.size) {
                downloadNowPlaying();
            }
            preventDownload = false;
        } else if (isNetworkAvailable(mContext)) {
            preventDownload = true;
            downloadNowPlaying();
        } else {
            if (!preventPlay)
                callBacks.showNoDataWarning();
            preventPlay = false;
        }
    }

    private void downloadNowPlaying() {
        ArrayList<String> downloadQue = new ArrayList<>();
        downloadQue.add(decode(nowPlaying.id) + "");
        if (!k.prefs.shuffle && !Queue.isEmpty())
            downloadQue.add(Queue.get((nowPlayingInd + 1) % Queue.size()));
        callBacks.download(downloadQue);
    }

    PendingIntent getBroadcast(String action) {
        return PendingIntent.getBroadcast(mContext, 0, new Intent(
                "com.krishnaapps.karnamrita." + action).setClass(mContext, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    Random random = new Random();
    boolean preventPlay = false, preventDownload = false;

    public void shuffleQueue() {
        for (int i = 0; i < Queue.size(); i++) {
            String temp = Queue.get(i);
            int tempInd = random.nextInt(Queue.size());
            Queue.set(i, Queue.get(tempInd));
            Queue.set(tempInd, temp);
        }
    }

    void createNotification() {
        if (nowPlaying == null) return;
        PendingIntent pendInt = PendingIntent.getActivity(mContext, 0, new Intent(mContext, k.class)
                .putExtra("s", nowPlaying.app), PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap albumArte;
        File file = new File(arteDir, "t_" + nowPlaying.arte);
        if (file.exists()) {
            albumArte = BitmapFactory.decodeFile(file.getPath());
        } else {
            albumArte = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        }
        int playPauseBtn = android.R.drawable.ic_media_play;
        if (mPlayer != null && mPlayer.isPlaying())
            playPauseBtn = android.R.drawable.ic_media_pause;
        int likeBtn = nowPlaying.fav ? R.drawable.thumb_pink : R.drawable.thumb;

        Notification.Builder builder = new Notification.Builder(mContext);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(nowPlaying.title)
                .addAction(android.R.drawable.ic_media_previous, "Previous", getBroadcast("prev")) // #0
                .addAction(playPauseBtn, "Pause", getBroadcast("playpause"))  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", getBroadcast("next")) // #2
                .addAction(likeBtn, "Like", getBroadcast("like"))
                .addAction(R.drawable.share, "Share", getBroadcast("share"))
                .setLargeIcon(albumArte);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
                    // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(1 /* #1: pause button */))
                    .setColor(Color.rgb(216, 27, 96));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("mPlayer", "Music Controls", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music Controls");
            channel.enableLights(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
            builder.setChannelId("mPlayer");
        }
        int i = 0;
        while (i < apps.length && !apps[i].equals(nowPlaying.app)) i++;
        startForeground(100 + i, builder.build());
        if (!mPlayer.isPlaying())
            stopForeground(false);
    }

    static final int MAX_VOLUME = 99;

    public class Player extends MediaPlayer implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
        private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();
        private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        boolean isPreparing = false, isPrepared = false;
        long fileLength = 0, currentPos = 0;

        Player() {
            setAudioStreamType(AudioManager.STREAM_MUSIC);
            setOnPreparedListener(mp -> {
                isPreparing = false;
                isPrepared = true;
                start();
                fileLength = new File(audioDir, nowPlaying.id).length();
                if (getDuration() > currentPos)
                    seekTo((int) currentPos - 500);
                currentPos = 0;
            });
            setOnCompletionListener(this);
            setOnErrorListener((mp, what, extra) -> {
                isPrepared = false;
                isPreparing = false;
                mPlayer = null;
                preventDownload = true;
                startPlaying();
//                    Log.d("hari", nowPlaying.getUrl());
                /*switch (what) {
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        Log.d("hari-MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Log.d("hari-MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.d("hari-MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                        break;
                }*/
                return true;
            });
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            isPrepared = false;
            isPreparing = false;
            callBacks.setPlayPauseButtons();
            File file = new File(audioDir, nowPlaying.id);
            if (fileLength < file.length() || k.prefs.repeat == REPEAT_ONE || (nowPlaying.size > 100 && mPlayer.getDuration() < 10000)) { //restart same song
                currentPos = getCurrentPosition() + 100;
                stop();
                reset();
                try {
                    preventDownload = true;
                    setDataSource(Uri.fromFile(file).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!Queue.isEmpty() && k.prefs.repeat != REPEAT_NONE) {
                nowPlayingInd = (nowPlayingInd + 1) % Queue.size();
                callBacks.setNowPlaying(Queue.get(nowPlayingInd));
                startPlaying();
            } else if (k.prefs.repeat != REPEAT_NONE) {
                nowPlaying = null;
                startPlaying();
            } else {
                stop();
                reset();
            }
        }

        @Override
        public void start() throws IllegalStateException {
            if (isPreparing) return;
            if (!preventPlay && mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                return;
            if (!myNoisyAudioStreamReceiver.isRegistered) {
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                myNoisyAudioStreamReceiver.isRegistered = true;
            }
            super.start();
            float v1 = (float) (1 - (Math.log(MAX_VOLUME - nowPlaying.volume) / Math.log(MAX_VOLUME)));
            mPlayer.setVolume(v1, v1);
            if (preventPlay) mPlayer.setVolume(0, 0);
            final int pos = mPlayer.getCurrentPosition();
            new Handler().postDelayed(() -> {
                if (preventPlay) {
                    pause();
                    preventPlay = false;
                } else if (mPlayer.getCurrentPosition() < pos + 200) {
                    currentPos = mPlayer.getCurrentPosition() + 600;
                    startPlaying();
                }
                nowPlaying.volume = nowPlaying.volume >= MAX_VOLUME ? MAX_VOLUME - 1 : nowPlaying.volume;
//                    TODO: write vol to database
                float v = (float) (1 - (Math.log(MAX_VOLUME - nowPlaying.volume) / Math.log(MAX_VOLUME)));
                mPlayer.setVolume(v, v);
            }, 500);
            callBacks.setPlayPauseButtons();
        }

        @Override
        public void stop() throws IllegalStateException {
            if (isPreparing) return;
            if (myNoisyAudioStreamReceiver.isRegistered) {
                try {
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                    myNoisyAudioStreamReceiver.isRegistered = false;
                } catch (Exception ignored) {
                }
            }
            abandonAudioFocus();
            super.stop();
            callBacks.setPlayPauseButtons();
        }

        @Override
        public void pause() throws IllegalStateException {
            if (isPreparing)
                return;
            if (myNoisyAudioStreamReceiver.isRegistered) {
                try {
                    unregisterReceiver(myNoisyAudioStreamReceiver);
                } catch (Exception ignored) {
                }
                myNoisyAudioStreamReceiver.isRegistered = false;
            }
            super.pause();
            callBacks.setPlayPauseButtons();
        }

        @Override
        public void seekTo(int msec) throws IllegalStateException {
            super.seekTo(msec);
            callBacks.setSeekBarProgress(msec);
        }

        @Override
        public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
            if (isPreparing) return;
            super.setDataSource(path);
            prepareAsync();
            isPrepared = false;
            isPreparing = true;
        }

        @Override
        public void reset() {
            super.reset();
            isPrepared = false;
            isPreparing = false;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(this);
                pause();
            }
            if (player1 != null) {
                if (k.prefs.backgroundVolume == 0) {
                    callBacks.stopChantWithSP();
                } else {
                    final float volume = (float) (1 - (Math.log(MAX_VOLUME - k.prefs.backgroundVolume) / Math.log(MAX_VOLUME)));
                    player1.setVolume(volume, volume);
                }
            }
        }
    }

    void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(mPlayer);
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        boolean isRegistered = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
                switch (becomingNoisy[k.prefs.becomingNoisyAction]) {
                    case "Pause":
                        mPlayer.pause();
                        abandonAudioFocus();
                        break;
                    case "Lower Volume":
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 3, 0);
                }
//                    pause();
        }
    }

    public static final String[] becomingNoisy = {"Pause", "Lower Volume", "Do Nothing"},
            onShake = {"Do Nothing ", "Change Song ", "Play/Pause ", "Play/Pause Prabhupāda Chant "};

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        File f = new File(getFilesDir(), "serv");
        if (intent == null && f.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String line = reader.readLine();
                if (line.equals("1")) {
                    File f1 = new File(baseDir + "/sp/chant.wav");
                    if (f1.exists()) {
                        player1 = new MediaPlayer();
                        player1.setDataSource(f1.getAbsolutePath());
                        player1.setOnPreparedListener(musicPlayer -> {
                            musicPlayer.start();
                            musicPlayer.setLooping(true);
                            float v1 = (float) (1 - (Math.log(MAX_VOLUME - 50) / Math.log(MAX_VOLUME)));
                            if (player1 != null) player1.setVolume(v1, v1);
                            setCallStateListener();
                        });
                        player1.prepareAsync();
                    }
                }
                reader.readLine();//spchantpos
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Service.START_STICKY;
    }

    class MBinder extends Binder {
        s getService() {
            return s.this;
        }
    }

    public interface CallBacks {
        void setPlayPauseButtons();

        void setSeekBarProgress(long milliSec);

        ArrayList<String> getQueue();

        void setNowPlaying(Cursor c);

        void setNowPlaying(String audioId);

        void download(ArrayList<String> downloadTasks);

        void playPause();

        void playNext();

        void playPrevious();

        void setLike(String ind);

        void shareSong(String id);

        void stopChantWithSP();

        void playPauseChantWithSP();

        void adjustSPChantVol(boolean up);

        void showNoDataWarning();
    }

    @Override
    public void onDestroy() {
//        do something to keep service alive
//        else remove all notifications
//        release resources
        mAudioManager.unregisterMediaButtonEventReceiver(mRemoteControlResponder);
        File f = new File(getFilesDir(), "serv");
        StringBuilder sb = new StringBuilder();
        if (player1 != null && player1.isPlaying())
            sb.append("1\n").append(player1.getCurrentPosition()).append("\n");
        else
            sb.append("0\n\n");
        if (mPlayer != null && mPlayer.isPlaying())
            sb.append("1\n").append(mPlayer.getCurrentPosition()).append("\n").append(audioDir).append("/").append(nowPlaying.id);
        else
            sb.append("0\n");
        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(sb.toString().getBytes());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    public void setCallStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (player1 != null) {
                            player1.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (player1 != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                player1.start();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void unregisterPhoneStateListener() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
}
