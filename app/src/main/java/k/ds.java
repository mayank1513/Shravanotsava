package k;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static k.helper.decode;
import static k.k.AUDIOS;
import static k.k.AUDIO_TABLE;
import static k.k.OFFLINE;
import static k.k.SIZE;
import static k.k.TITLE;
import static k.k.download;
import static k.k.logoDir;
import static k.k.months;
import static k.k.rawBase;
import static k.k.spQuoteDir;
import static k.m.animDirLandScape;
import static k.m.animRawBase;
import static k.m.appGit;
import static k.m.apps;
import static k.m.baseDir;

public class ds extends Service {
    private static final String ACTION_INIT = "com.mayank.krishnaapps.shravanotsava.action.init";
    private static final String SET_OFFLINE = "com.mayank.krishnaapps.shravanotsava.action.set_offline";
    static CallBacks callBacks;
    public static PowerManager.WakeLock mWakeLock;

    public static void setUp(Context context) {
        Intent intent = new Intent(context, ds.class);
        intent.setAction(ACTION_INIT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent);
//        else
        context.startService(intent);
        callBacks = (CallBacks) context;
    }

    public static void setOffline(Context context, SQLiteDatabase db, String audioDir) {
        context.startService(new Intent(context, ds.class).setAction(SET_OFFLINE)
                .putExtra("db", db.getPath()).putExtra("dir", audioDir));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appGit = new String[]{getString(R.string.sp), getString(R.string.rns), getString(R.string.rgs), getString(R.string.lks),
                getString(R.string.bsrm), getString(R.string.mvg), getString(R.string.bcs), getString(R.string.bnds),
                getString(R.string.bps), getString(R.string.bsds), getString(R.string.ds), getString(R.string.gkg),
                getString(R.string.rps), getString(R.string.kkp), getString(R.string.dnp)};

        baseDir = getExternalFilesDir("base");
        animDirLandScape = new File(baseDir + "/anim/l");
        if (!animDirLandScape.exists()) animDirLandScape.mkdirs();
        logoDir = new File(baseDir + "/apps");
        if (!logoDir.exists()) logoDir.mkdirs();
        spQuoteDir = new File(baseDir + "/sp/q");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    int runningThreads = 0, maxThreads = 10, runningTasks = 0;

    @SuppressLint("DefaultLocale")
    protected void handleIntent(final Intent intent) {
        mWakeLock = ((PowerManager) Objects.requireNonNull(getSystemService(Context.POWER_SERVICE))).
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        if (intent != null && SET_OFFLINE.equals(intent.getAction())) {
            new Thread(() -> {
                runningTasks++;
                SQLiteDatabase db = SQLiteDatabase.openDatabase(intent.getStringExtra("db"), null, SQLiteDatabase.OPEN_READWRITE);
                Cursor c = db.rawQuery("select * from " + AUDIO_TABLE + " where " + OFFLINE + " = ?", new String[]{"1"});
                File aDir = new File(intent.getStringExtra("dir"));
                if (aDir.listFiles().length != c.getCount()) {
                    c.close();
                    for (File f : aDir.listFiles()) {
                        long id = decode(f.getName());
                        c = db.rawQuery("select * from " + AUDIO_TABLE + " where _id=?", new String[]{id + ""});
                        if (c.moveToFirst() && f.length() >= .95 * decode(c.getString(c.getColumnIndex(SIZE)))) {
                            ContentValues values = new ContentValues();
                            values.put(OFFLINE, "1");
                            db.update(AUDIO_TABLE, values, "_id=?", new String[]{id + ""});
                        }
                        c.close();
                    }
                } else
                    c.close();
                ContentValues values = new ContentValues();
                values.put("_id", "Offline");
                values.put(TITLE, "Offline");
                Cursor c1 = db.rawQuery("select * from " + AUDIO_TABLE + " where " + OFFLINE + " = ?", new String[]{"1"});
                values.put(AUDIOS, c1.getCount());
                c1.close();
                db.insertWithOnConflict("playlist", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();
                runningTasks--;
                if (runningTasks <= 0 && mWakeLock.isHeld()) {
                    mWakeLock.release();
                    stopSelf();
                }
            }).start();
        } else if (intent != null && ACTION_INIT.equals(intent.getAction())) {
            if (getPackageName().contains("shravanotsava")) {
                new Thread(() -> {
                    runningTasks++;
                    if (!mWakeLock.isHeld()) mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                    final String[] s = {"a", "b", "m", "i", "l", "h", "p"};
                    final File sDir = new File(baseDir + "/" + apps[0] + "/s");
                    if (!sDir.exists()) {
                        sDir.mkdirs();
                        for (final String s1 : s)
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(sDir, s1),
                                        "https://raw.githubusercontent.com/" + appGit[0].replace(".github.io", "") + "/exp/master/" + s1);
                                runningThreads--;
                            }).start();
                        download(new File(logoDir, apps[0]), animRawBase + "pps/" + apps[0] + ".webp");
                    }

                    for (int i = 1; i < apps.length; i++) {
                        final String app = apps[i];
                        final File sDir1 = new File(baseDir + "/" + app + "/s");
                        if (!sDir1.exists()) {
                            sDir1.mkdirs();
                            final int finalI = i;
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(logoDir, app), animRawBase + "pps/" + app + ".webp");
                                for (String s1 : s)
                                    download(new File(sDir1, s1),
                                            "https://raw.githubusercontent.com/" + appGit[finalI].replace(".github.io", "") + "/exp/master/" + s1);
                                runningThreads--;
                            }).start();
                        }
                    }

                    final ArrayList<String> qNames = new ArrayList<>();
                    for (int month = 0; month < 12; month++) {
                        for (int j = 1; j <= (month == 1 ? 28 : (month < 7 ? (month % 2 == 0 ? 31 : 30) : (month % 2 == 0 ? 30 : 31))); j++)
                            qNames.add(months[month] + "_" + (j > 9 ? j : "0" + j));
                    }
                    int n = animDirLandScape.listFiles() == null ? 0 : animDirLandScape.listFiles().length;
                    int n1 = spQuoteDir.listFiles() == null ? 0 : spQuoteDir.listFiles().length;
                    while (n < 4110 || n1 < qNames.size()) {
                        if (n + n1 > 4365) maxThreads = (4475 - n) / 5;
                        if (spQuoteDir.exists()) {
                            while (runningThreads < maxThreads / 2 && n1 < qNames.size()) {
                                final int i = n1;
                                new Thread(() -> {
                                    runningThreads++;
                                    download(new File(spQuoteDir, qNames.get(i)), rawBase + "srila-prabhupada-vani/q/master/" + qNames.get(i) + ".webp");
                                    runningThreads--;
                                }).start();
                                n1++;
                            }
                        }
                        while (runningThreads < maxThreads && n < 4100) {
                            final int i = n;
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(animDirLandScape, "a" + i), animRawBase + (i / 500) + "/f" + String.format("%04d", i) + ".webp");
                                runningThreads--;
                            }).start();
                            n++;
                        }
                        download(new File(animDirLandScape, "a" + n), animRawBase + (n / 500) + "/f" + String.format("%04d", n) + ".webp");
                        n++;
                        if (n > 500 && callBacks != null) callBacks.startAnim();
                    }

                    runningTasks--;
                    if (runningTasks <= 0 && mWakeLock.isHeld()) {
                        mWakeLock.release();
                        stopSelf();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    runningTasks++;
                    if (!mWakeLock.isHeld()) mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                    final String[] s = {"a", "b", "m", "i", "l", "h", "p"};
                    final File sDir = new File(baseDir + "/" + apps[0] + "/s");
                    if (!sDir.exists()) {
                        sDir.mkdirs();
                        for (final String s1 : s)
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(sDir, s1),
                                        "https://raw.githubusercontent.com/" + appGit[0].replace(".github.io", "") + "/exp/master/" + s1);
                                runningThreads--;
                            }).start();
                        download(new File(logoDir, apps[0]), animRawBase + "pps/" + apps[0] + ".webp");
                    }
                    String p = getPackageName();
                    p = p.replace("radhanathswami", "rns").replace("lokanathswami", "lks");
                    for (int i = 1; i < apps.length; i++) {
                        if (!p.contains(apps[i])) continue;
                        final String app = apps[i];
                        final File sDir1 = new File(baseDir + "/" + app + "/s");
                        if (!sDir1.exists()) {
                            sDir1.mkdirs();
                            final int finalI = i;
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(logoDir, app), animRawBase + "pps/" + app + ".webp");
                                for (String s1 : s)
                                    download(new File(sDir1, s1),
                                            "https://raw.githubusercontent.com/" + appGit[finalI].replace(".github.io", "") + "/exp/master/" + s1);
                                runningThreads--;
                            }).start();
                        }
                    }

                    final ArrayList<String> qNames = new ArrayList<>();
                    for (int month = 0; month < 12; month++) {
                        for (int j = 1; j <= (month == 1 ? 28 : (month < 7 ? (month % 2 == 0 ? 31 : 30) : (month % 2 == 0 ? 30 : 31))); j++)
                            qNames.add(months[month] + "_" + (j > 9 ? j : "0" + j));
                    }
                    int n = animDirLandScape.listFiles() == null ? 0 : animDirLandScape.listFiles().length;
                    int n1 = spQuoteDir.listFiles() == null ? 0 : spQuoteDir.listFiles().length;
                    while (n < 4110 || n1 < qNames.size()) {
                        if (n + n1 > 4365) maxThreads = (4475 - n) / 5;
                        if (spQuoteDir.exists()) {
                            while (runningThreads < maxThreads / 2 && n1 < qNames.size()) {
                                final int i = n1;
                                new Thread(() -> {
                                    runningThreads++;
                                    download(new File(spQuoteDir, qNames.get(i)), rawBase + "srila-prabhupada-vani/q/master/" + qNames.get(i) + ".webp");
                                    runningThreads--;
                                }).start();
                                n1++;
                            }
                        }
                        while (runningThreads < maxThreads && n < 4100) {
                            final int i = n;
                            new Thread(() -> {
                                runningThreads++;
                                download(new File(animDirLandScape, "a" + i), animRawBase + (i / 500) + "/f" + String.format("%04d", i) + ".webp");
                                runningThreads--;
                            }).start();
                            n++;
                        }
                        download(new File(animDirLandScape, "a" + n), animRawBase + (n / 500) + "/f" + String.format("%04d", n) + ".webp");
                        n++;
                        if (n > 500 && callBacks != null) callBacks.startAnim();
                    }

                    runningTasks--;
                    if (runningTasks <= 0 && mWakeLock.isHeld()) {
                        mWakeLock.release();
                        stopSelf();
                    }
                }).start();
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    interface CallBacks {
        void startAnim();
    }
}
