package k;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.text.format.DateFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static k.m.baseDir;
import static k.k.spQuoteDir;

public class Alarm extends BroadcastReceiver {
    final public static String FOR_SP_QUOTE_NOT = "spQuoteNotification";
    final public static int SP_QUOTE_NOT_ID = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "brsm:Alarm");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        wl.acquire();
        if (FOR_SP_QUOTE_NOT.equals(intent.getExtras().getString("for"))) {
            if (!k.prefs.spQuoteRingTone.isEmpty()) soundUri = Uri.parse(k.prefs.spQuoteRingTone);
            String today = DateFormat.format("MMMM-dd", new Date()).toString();
            baseDir = context.getExternalFilesDir("base");
            spQuoteDir = new File(baseDir + "/sp/q");
            final File file = new File(spQuoteDir, today.replace('-', '_') + ".png");
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                Intent notIntent1 = new Intent(context, k.class);
                notIntent1.putExtra("action", "show_sp_quote");
                notIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt1 = PendingIntent.getActivity(context, SP_QUOTE_NOT_ID,
                        notIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder mBuilder1 =
                        new Notification.Builder(context)
                                .setContentIntent(pendInt1)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setContentTitle("Srila Prabhupada Today")
                                .setLargeIcon(bitmap)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));
                if (k.prefs.SPQuoteNotification == 2)
                    mBuilder1.setSound(soundUri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder1.setVisibility(Notification.VISIBILITY_PUBLIC);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("sp", "Srila Prabhupada Today", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("Srila Prabhupada Today");
                    channel.enableLights(true);
                    mNotificationManager.createNotificationChannel(channel);
                    mBuilder1.setChannelId("sp");
                }
                mNotificationManager.notify(SP_QUOTE_NOT_ID, mBuilder1.build());
            }
        }
        wl.release();
    }

    ArrayList<PendingIntent> pendingIntents = new ArrayList<>();

    public void setAlarm(Context context, String setFor, int requestCode, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("for", setFor);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar setTime = Calendar.getInstance();
        setTime.setTimeInMillis(System.currentTimeMillis());
        setTime.set(Calendar.HOUR_OF_DAY, time / 60);
        setTime.set(Calendar.MINUTE, time % 60);
        setTime.set(Calendar.SECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        if (pendingIntents.isEmpty())
            for (int i = 0; i < 5; i++)
                pendingIntents.add(pendingIntent);
        pendingIntents.set(requestCode, pendingIntent);
    }

    public void setAlarm(Context context) {
//        if(k.mDb==null) {
//            TODO
//        }
        setAlarm(context, FOR_SP_QUOTE_NOT, SP_QUOTE_NOT_ID, k.prefs.spQuoteTime);
        if (k.prefs.SPQuoteNotification == 0)
            CancelAlarm(context, SP_QUOTE_NOT_ID);
    }

    public void CancelAlarm(Context context, int requestCode) {
        if (pendingIntents.get(requestCode) != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntents.get(requestCode));
        }
    }
}
