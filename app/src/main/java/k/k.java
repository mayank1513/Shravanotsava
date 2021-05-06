package k;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import k.ui.CustomWebView;
import k.ui.b;
import k.ui.d;
import k.ui.g;
import k.ui.h;
import k.ui.r;
import k.ui.t;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static k.helper.calculateInSampleSize;
import static k.helper.decode;
import static k.helper.decodeUrl;
import static k.helper.encode;
import static k.helper.encodeUrl;
import static k.helper.getTextUrls;
import static k.helper.hari;
import static k.helper.hideSystemUI;
import static k.helper.initTip;
import static k.helper.isNetworkAvailable;
import static k.helper.setFeatureDone;
import static k.helper.shouldShow;
import static k.helper.showSystemUI;
import static k.m.apps;
import static k.m.baseDir;
import static k.m.book;
import static k.m.lyDb;
import static k.s.MAX_VOLUME;
import static k.s.becomingNoisy;
import static k.s.mAudioManager;
import static k.s.mPlayer;
import static k.s.maxStreamVolume;
import static k.s.onShake;
import static k.s.player1;
import static k.ui.b.featureId;
import static k.ui.t.duration;

//todo add let/right drawer tip
public class k extends Activity implements b.CallBacks, h.SyncedScrollInterface, ServiceConnection, s.CallBacks {
    public static final int REPEAT_ONE = 0, REPEAT_LIST = 1, REPEAT_NONE = 2;
    final int RESULT_LOAD_IMAGE = 1, RESULT_BSRM_NOTIFICATION_TONE = 2, RESULT_SP_NOTIFICATION_TONE = 3, waBgColor = 0xFFEDE6DE;
    public static String infoUrl, baseUrl, fb, yt, ytBase, linkedIn, twit, shortName, replacement[],
            lang[][], rawBase = "https://raw.githubusercontent.com/", //shravanotsava/karnamrita/master/ spQuoteBase = animRawBase + "",
            vedaBaseUrl = "https://vedabase.io/en/library/", notesUrl = "https://keep.google.com/";
    public static int appInd;
    public static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};
    public static int dp, density, headerH, minHeaderH, maxHeaderH, tBarH, notchH = 0, month = -1, day = 0, wOffset;
    Calendar calendar;
    public static Point size = new Point();
    public static File publicBaseDir, arteDir, audioDir, spQuoteDir, logoDir, vbDir, lyDir;
    public static prefs prefs;
    public static Audio nowPlaying;

    static s mServ;
    k mContext;
    public ArrayList<h> syncedH = new ArrayList<>();
    public g dMenu, mTList, fsMenu, lMenu, sList;
    t mTabs;
    d mD, mRd;
    b mBottomDrawer;
    FrameLayout mHeader, listMenu, mPrefView;
    View mDMenuBtn, mToolBar, mToolBarArte, mInfo, mTBtn, q, mStatusBar, mSearchBar, mFSToolBar,
            mVolumeControls, mToastContainer, closeTip, Hari;
    SeekBar seekBar, a, r, g, b;
    public ImageButton mShuffle, mRepeat, mLike, quickBall, mActionButton;
    public ImageView mFSArte, mCForeground, spQuote;
    public TextView percent, mAppName, mToast, mDName, listMInfo;
    EditText mSearchBox;
    Button mToastButton;
    int playPauseButtons[];
    public KAdapter dMenuAdapter;
    Adapter mAdapter;

    public boolean fullScreen, showingSPQuotes = false, showingLyrics = false;
    static String pkg, appName, mParent = "", mPrevParent = "", mParentName = "", searchString = "", git = "";
    ArrayList<String> mainTabs = new ArrayList<>(Arrays.asList("Albums", "Playlist", "Songs"));
    static SQLiteDatabase mDb, mAudioDb;
    Cursor prevCursor;
    long mLang = -1, mPrevLang = -1;

    public static PowerManager.WakeLock mWakeLock;
    NotificationManager mNotificationManager;
    InputMethodManager imm;
    private SensorManager mSensorManager;
    NotificationManager notificationManager;
    private Sensor mAccelerometer, mProximity, mGravity;
    static boolean started = false;
    boolean serviceBound = false, viewsSet = false;
    static boolean[] creatingDb = new boolean[apps.length];
    CustomWebView VedabaseWebView, NotesWebView, YouTubeWebView;
    r vedabaseContainer, ytContainer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncedH.clear();
        tBarH = getResources().getDimensionPixelSize(R.dimen.t_bar_height);
        getWindowManager().getDefaultDisplay().getSize(size);
        setContentView(R.layout.k);
        mToolBar = findViewById(R.id.t);
        mStatusBar = findViewById(R.id.status_bar);
        mSearchBar = findViewById(R.id.s);
        mFSToolBar = findViewById(R.id.fs_toolbar);
        spQuote = findViewById(R.id.updatingDb);
        spQuote.setImageURI(Uri.fromFile(new File(logoDir, shortName)));
        (quickBall = findViewById(R.id.quickBall)).setOnTouchListener(fab);
        (q = findViewById(R.id.iq)).setOnTouchListener(fab);
        hideQuickBall(quickBall);
        spQuote.animate().alpha(1).setDuration(300).start();
        findViewById(R.id.hk__).animate().alpha(1).setDuration(200).start();
        findViewById(R.id.h).animate().scaleY(1).scaleX(1).alpha(1).setDuration(100).start();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onStart() {
        super.onStart();
        if (started && mTList != null) return;
        started = true;
        mContext = this;
        headerH = getResources().getDimensionPixelSize(R.dimen.header_height);
        minHeaderH = 9 * headerH / 10;
        maxHeaderH = 11 * headerH / 10;
        density = (int) (getResources().getDisplayMetrics().density * 4);
        density = density < 6 ? 6 : density < 8 ? 8 : density < 12 ? 12 : 16;
        closeTip = findViewById(R.id.close_tip);
        mToastContainer = findViewById(R.id.toast);
        mToast = mToastContainer.findViewById(R.id.toast_text);
        mToastButton = mToastContainer.findViewById(R.id.toast_button);
        mAppName = findViewById(R.id.app_name);
        mAppName.setText(appName);
        ytContainer = findViewById(R.id.youtubeContainer);
        if (appName.matches(".*[ṇ,ā].*") || appName.length() < 18)
            mAppName.setTypeface(Typeface.SERIF, Typeface.ITALIC);
        if (!serviceBound) {
            startService(new Intent(mContext, s.class));
            bindService(new Intent(mContext, s.class), mContext, Context.BIND_AUTO_CREATE);
        } else {
            finish();
            startActivity(new Intent(k.this, m.class).putExtra("s", shortName));
        }
        mActionButton = findViewById(R.id.action);
        Hari = findViewById(R.id.hari);
        Hari.setOnTouchListener((v, event) -> {
            if (creatingDb[appInd]) {
                new AlertDialog.Builder(mContext).setMessage("We are creating Database for you. This may take some time. Thank you for your patience.")
                        .setPositiveButton(appName, (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(infoUrl)))).setNegativeButton("Close", null).show();
            } else if ((fsMenu != null && fsMenu.getAdapter() != null) || q.getVisibility() == VISIBLE
                    || (listMenu != null && listMenu.getVisibility() == VISIBLE)) {
                onBackPressed();
                return true;
            }
            return false;
        });
        readFiles();
        final String dbPath = getExternalFilesDir("gopinath") + "/" + shortName + "/main";
        if (!creatingDb[appInd] && (!(new File(dbPath).exists()) || shouldShow("db_" + shortName))) {
            if (shouldShow("db_" + shortName)) new File(dbPath).delete();
            initTip(mContext, null, "We are preparing database for you. It might take a few minutes. Please don't close Application")
                    .findViewById(R.id.tip).animate().translationY(size.y / 4f).start();
            closeTip.animate().alpha(0).start();
            (new Thread(() -> {
                creatingDb[appInd] = true;
                createDb(dbPath, shortName);
                creatingDb[appInd] = false;
                runOnUiThread(() -> {
                    mDb = new SQLHelper(mContext, dbPath).getWritableDatabase();
                    mAudioDb = new SQLHelperA(mContext, dbPath).getWritableDatabase();
                    closeTip.performClick();
                    setViews();
                });
            })).start();
        } else if (creatingDb[appInd]) {
            display("Preparing Database...\n\n\n", -1, null, null);
        } else {
            mDb = (new SQLHelper(mContext, dbPath)).getWritableDatabase();
            mAudioDb = new SQLHelperA(mContext, dbPath).getWritableDatabase();
            ds.setOffline(mContext, mAudioDb, baseDir + "/" + shortName + "/f");
            setViews();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        receiver.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    protected void onResume() {
        super.onResume();
        if (null != prefs && prefs.onShake > 0) {
            mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(sensorEventListener1, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(sensorEventListener2, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            showSystemUI(mContext);
        } else {
            hideSystemUI(mContext);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mToolBar.setOnApplyWindowInsetsListener((view, insets) -> {
                View v = findViewById(R.id.h);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                p.bottomMargin = insets.getSystemWindowInsetBottom();
                p.leftMargin = insets.getSystemWindowInsetLeft();
                p.rightMargin = insets.getSystemWindowInsetRight();

                p = (ViewGroup.MarginLayoutParams) findViewById(R.id.hk__).getLayoutParams();
                p.leftMargin = insets.getSystemWindowInsetLeft();
                p.rightMargin = insets.getSystemWindowInsetRight();

//                wOffset = p.leftMargin + p.rightMargin;
                return insets;
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(() -> {
            getWindowManager().getDefaultDisplay().getSize(size);
            setToolBarArte(tBarH);
            mTabs.postDelayed(() -> {
                mTabs.goToTab(-1);
                mD.setPad();
                mRd.setPad();
                if (spQuote.getVisibility() == VISIBLE) setZoomToCenter();
                hideQuickBall(quickBall);
            }, 100);
        }, 300);

//        int visibility = mPrefView.getVisibility();
//        RelativeLayout rt = findViewById(R.id.h);
//        rt.removeView(mPrefView);
//        mPrefView = (FrameLayout) inflate(this,R.layout.ip, null);
//        rt.addView(mPrefView);
//        mPrefView.setVisibility(visibility);
        headerH = getResources().getDimensionPixelSize(R.dimen.header_height);
        findViewById(R.id.header).setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerH));
        if (headerH <= 150 * dp) {
            minHeaderH = headerH;
            maxHeaderH = 2 * headerH;
        } else {
            minHeaderH = 9 * headerH / 11;
            maxHeaderH = 12 * headerH / 10;
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.invertColor).setVisibility(GONE);
        if (ytContainer.getVisibility() == GONE) {
            findViewById(R.id.search).setVisibility(VISIBLE);
        }
        if (!viewsSet && !creatingDb[appInd]) {
            super.onBackPressed();
        } else if (mPrefView.getVisibility() == VISIBLE) {
            mPrefView.setVisibility(GONE);
            mD.collapse();
            mRd.collapse();
            prefs.appSharingSign = ((EditText) mPrefView.findViewById(R.id.appShareTxt)).getText().toString();
            prefs.songSharingSign = ((EditText) mPrefView.findViewById(R.id.songShareTxt)).getText().toString();
//            drawerList.setAdapter(dMenuAdapter = new Adapter(adptrs.dList,dMenuAdapter.ind));
        } else if (fsMenu.getAdapter() != null) {
            fsMenu.setAdapter(null);
        } else if (listMenu.getVisibility() == VISIBLE) {
            listMenu.animate().alpha(0).translationY(200 * dp).setDuration(300).start();
            listMenu.postDelayed(() -> {
                listMenu.setVisibility(GONE);
                Hari.animate().alpha(0).start();
            }, 300);
        } else if (q.getVisibility() == VISIBLE) {
            float x = quickBall.getX();
            x = x < size.x / 2f ? -x + 5 * dp : -x + size.x - 70 * dp;
            quickBall.animate().translationXBy(x).setDuration(200).start();
            q.animate().translationXBy(x).scaleX(.1f).scaleY(.1f).rotation(-90).alpha(0).setDuration(200).start();
            new Handler().postDelayed(() -> {
                q.setVisibility(GONE);
                hideQuickBall(quickBall);
                Hari.animate().alpha(0).start();
            }, 220);
        } else if (fullScreen) {
            hideFullScreenPlayer();
        } else if (!mD.isCollapsed()) {
            mD.collapse();
        } else if (!mRd.isCollapsed()) {
            mRd.collapse();
        } else if (vedabaseContainer.getVisibility() == VISIBLE) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                if (NotesWebView.canGoBack())
                    NotesWebView.goBack();
                else {
                    NotesWebView.setVisibility(GONE);
                    VedabaseWebView.setVisibility(VISIBLE);
                    ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
                }
            } else {
                if (VedabaseWebView.canGoBack())
                    VedabaseWebView.goBack();
                else
                    findViewById(R.id.fab_close).performClick();
            }
        } else if (spQuote.getVisibility() == VISIBLE) {
            spQuote.setVisibility(GONE);
        } else if (showingSPQuotes && day > 0) {
            day = 0;
            ArrayList<String> list = new ArrayList<>(Arrays.asList(months));
            mTabs.setTabs(list, month);
        } else if (showingSPQuotes && month >= 0) {
            month = -1;
            ArrayList<String> list = new ArrayList<>();
            list.add(getString(R.string.sp_quotes));
            mTabs.setTabs(list, 0);
        } else if (showingSPQuotes) {
            showingSPQuotes = false;
            findViewById(R.id.search).setVisibility(VISIBLE);
            mTList.setAdapter(mAdapter);
            mTList.setNumColumns(1);
            mTabs.setTabs((prefs.showLangViz ? mLang < 0 : mParent.isEmpty()) ? mainTabs : mTabTitles, tabInd);
            mBottomDrawer.setVisibility(VISIBLE);
            mActionButton.setVisibility(VISIBLE);
        } else if (showingLyrics) {
            if (mLyIds != null) {
                if (((Adapter) mTList.getAdapter()).type == Audio) {
                    setTab(mTabs.mTabInd);
                    return;
                }
                mLyIds = null;
                if (mLyPrevTitle == null) {
                    mLyTabTitles = null;
                    showingLyrics = false;
                    mTabs.setTabs((prefs.showLangViz ? mLang < 0 : mParent.isEmpty()) ? mainTabs : mTabTitles, tabInd);
                    mTList.setAdapter(mAdapter);
                    return;
                }
                mLyTabTitles = new ArrayList<>();
                mLyArte = new ArrayList<>();
                Cursor cursor = lyDb.rawQuery("select * from " + PARENT, null);
                if (cursor.moveToFirst()) {
                    do {
                        mLyTabTitles.add(decodeUrl(cursor.getString(cursor.getColumnIndex(TITLE))).replace("Songs by ", "").replace("Thakura", "Thakur"));
                        mLyArte.add(decodeUrl(cursor.getString(cursor.getColumnIndex(ARTE))));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                mTabs.setTabs(mLyTabTitles, mLyTabTitles.indexOf(mLyPrevTitle));
                mTList.setAdapter(mAdapter);
                mLyPrevTitle = null;
            } else if (mLyTabTitles != null) {
                mLyTabTitles = null;
                mTabs.setTabs(new ArrayList<>(Collections.singletonList("Vaishnava Songbook")), 0);
            } else {
                showingLyrics = false;
                mTabs.setTabs(mPlTitles != null ? mPlTitles : (prefs.showLangViz ? mLang < 0 : mParent.isEmpty()) ? mainTabs : mTabTitles, tabInd);
            }
        } else if (ytContainer.getVisibility() == VISIBLE) {
            if (YouTubeWebView.canGoBack()) {
                YouTubeWebView.goBack();
                YouTubeWebView.postDelayed(() -> {
                    if (!YouTubeWebView.canGoBack()) {
                        View ytFrag1 = ytContainer;
                        ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) ytFrag1.getLayoutParams();
                        p1.topMargin = -245 * dp;
                        ytFrag1.setLayoutParams(p1);
                    }
                }, 300);
            } else {
                ytContainer.setVisibility(GONE);
                dMenuAdapter.ind = 0;
                dMenuAdapter.notifyDataSetChanged();
                findViewById(R.id.search).setVisibility(VISIBLE);
            }
        } else if (mPlTitles != null) {
            mPlTitles = null;
            mTabs.setTabs((prefs.showLangViz ? mLang < 0 : mParent.isEmpty()) ? mainTabs : mTabTitles, tabInd);
            dMenuAdapter.ind = 0;
        } else if (mSearchBar.getVisibility() == VISIBLE) {
            mSearchBar.setVisibility(View.GONE);
        } else if (prefs.showLangViz && mLang >= 0) {
            if (!mPrevParent.isEmpty()) {
                Cursor c = mDb.rawQuery("select * from " + ALB + " where _id = ?", new String[]{decode(mPrevParent) + ""});
                if (c.moveToFirst()) {
                    mParent = mPrevParent;
                    mPrevParent = c.getString(c.getColumnIndex(PARENT));
                    prevCursor = mDb.rawQuery("select * from hk" + mLang + " where " + PARENT + "=?", new String[]{mPrevParent});
                    String title = "";
                    if (prevCursor.moveToFirst()) {
                        mTabTitles.clear();
                        do {
                            if (encode(prevCursor.getLong(0)).equals(mParent))
                                title = prevCursor.getString(prevCursor.getColumnIndex(TITLE));
                            mTabTitles.add(prevCursor.getString(prevCursor.getColumnIndex(TITLE)));
                        } while (prevCursor.moveToNext());
                    }
                    mTabs.setTabs(mTabTitles, mTabTitles.indexOf(title));
                }
                c.close();
            } else if (mPrevLang >= 0) {
                mPrevLang = -1;
                prevCursor = mDb.rawQuery("select * from " + LANG, null);
                mTabTitles.clear();
                String title = "";
                if (prevCursor.moveToFirst()) {
                    do {
                        if (prevCursor.getLong(0) == mLang)
                            title = prevCursor.getString(prevCursor.getColumnIndex(TITLE));
                        mTabTitles.add(prevCursor.getString(prevCursor.getColumnIndex(TITLE)));
                    } while (prevCursor.moveToNext());
                }
                mTabs.setTabs(mTabTitles, mTabTitles.indexOf(title));
            } else {
                mLang = -1;
                mTabs.setTabs(mainTabs, 0);
            }
        } else if (!mParent.isEmpty()) {
            Cursor c = mDb.rawQuery("select * from " + ALB + " where _id = ?", new String[]{decode(mParent) + ""});
            if (c.moveToFirst()) {
                mParent = c.getString(c.getColumnIndex(PARENT));
                mAdapter.swapCursor(mDb.rawQuery("select * from " + ALB + " where " + PARENT + " = ?", new String[]{mParent}));
                mAdapter.type = Alb;
            }
            c.close();
        } else if (mBottomDrawer.findViewById(R.id.playPause1).getVisibility() == GONE) {
            mBottomDrawer.collapse();
        } else if (mTabs.mTabInd != 0) {
            mTabs.goToTab(0);
        } else if (!creatingDb[appInd])
            super.onBackPressed();
    }

    protected void onPause() {
        super.onPause();
        if (null != mDb && null != nowPlaying) {
            ContentValues values = new ContentValues();
            values.put("nowPlayingId", decode(nowPlaying.id) + (mPlayer != null ? "~" + encode(mPlayer.getCurrentPosition()) : ""));
            mDb.update("prefs", values, null, null);
        }
        if (null == mSensorManager) return;
        mSensorManager.unregisterListener(sensorEventListener);
        mSensorManager.unregisterListener(sensorEventListener1);
        mSensorManager.unregisterListener(sensorEventListener2);
    }

    @Override
    protected void onDestroy() {
        if (serviceBound)
            unbindService(mContext);
        serviceBound = false;
        super.onDestroy();
    }

    float[] gravity = new float[3];
    SensorEventListener sensorEventListener2 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    boolean far;
    SensorEventListener sensorEventListener1 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            far = event.values[0] > .5;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    SensorEventListener sensorEventListener = new SensorEventListener() {
        long prevTime = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0] - gravity[0], y = event.values[1] - gravity[1], z = event.values[2] - gravity[2];
            double a = Math.sqrt(x * x + y * y);
            double b = Math.sqrt(x * x + y * y + z * z);
            long t = new Date().getTime();
            if ((a < 7 && b < 15) || t - prevTime < 1000) return;
            prevTime = t;
            switch (onShake[prefs.onShake]) {
                case "Change Song ":
                    display("Song changed on Shake", 500);
                    playNext();
                    break;
                case "Play/Pause ":
                    playPause();
                    display("Song " + (mPlayer.isPlaying() ? "paused" : "started playing") + " on Shake", 500);
                    break;
                case "Play/Pause Prabhupada Chant ":
                    playPauseChantWithSP();
                    display("Śrīla Prabhupāda Chanting " + (player1.isPlaying() ? "paused" : "started playing") + " on Shake", 500);
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void setViews() {
        Cursor c = mDb.rawQuery("select * from prefs", null);
        if (c.moveToFirst()) prefs = new prefs(c);
        c.close();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = Objects.requireNonNull(mSensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (mTList != null) return;
        viewsSet = false;
        playPauseButtons = new int[]{R.id.playPause, R.id.playPause1};
        (seekBar = findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mPlayer != null && b)
                    mPlayer.seekTo(1000 * i);
                int min = i / 60, hr = min / 60;
                int sec = i % 60;
                min = min % 60;
                String progressStr = "";
                progressStr = progressStr + (hr == 0 ? "  " : ((hr > 9 ? hr : "0" + hr) + ":"));
                progressStr = progressStr + (min > 9 ? min : "0" + min) + ":";
                progressStr = progressStr + (sec > 9 ? sec : "0" + sec);
                ((TextView) findViewById(R.id.progress)).setText(progressStr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mPrefView = findViewById(R.id.pref);
        findViewById(R.id.forward).setOnTouchListener(onTouchListener);
        findViewById(R.id.revert).setOnTouchListener(onTouchListener);
        findViewById(R.id.artist_).setSelected(true);
        findViewById(R.id.artist1).setSelected(true);
        mShuffle = findViewById(R.id.shuffle);
        mRepeat = findViewById(R.id.repeat);
        mLike = findViewById(R.id.like_);
        mTabs = findViewById(R.id.tl);
        mTList = mTabs.findViewById(R.id.container);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        audioDir = new File(baseDir + "/" + shortName + "/f");
        if (!audioDir.exists()) audioDir.mkdirs();
        mToolBarArte = findViewById(R.id.t_arte);
//        final File f = new File(baseDir + "/" + shortName + "/tArte");
//        if(f.exists())
//            ((ImageView) mToolBarArte.findViewById(R.id.arte)).setImageURI(Uri.fromFile(f));
//        else
//            new DownloadTask(animRawBase + shortName + "/t_a", f, new DownloadTask.PostExecuteListener() {
//                @Override
//                public void postExecute(String s) {
//                    ((ImageView) mToolBarArte.findViewById(R.id.arte)).setImageURI(Uri.fromFile(f));
//                }
//            }).execute("");
        mSearchBox = mSearchBar.findViewById(R.id.search_view);
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = hari(s.toString()).trim();
                if (str.isEmpty()) searchString = "";
                else searchString = TITLE + " like '%" + str.replaceAll("'", "''") + "%'";
                mTabs.goToTab(-1);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        fsMenu = findViewById(R.id.fs_menu);
        mFSArte = findViewById(R.id.f_arte);
        mCForeground = findViewById(R.id.c_foreground);
        percent = findViewById(R.id.percent);
        mVolumeControls = findViewById(R.id.volumeControls);
        findViewById(R.id.songVolume).setOnTouchListener(onVolumeTouchListener);
        findViewById(R.id.deviceVolume).setOnTouchListener(onVolumeTouchListener);
        mInfo = findViewById(R.id.info);
        mAppName.setOnTouchListener(new View.OnTouchListener() {
            float downY, h;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    downY = event.getRawY();
                    h = mToolBar.getLayoutParams().height;
                } else {
                    float rawY = event.getRawY();
                    if (action == MotionEvent.ACTION_UP && Math.abs(rawY - downY) < dp)
                        mD.expand();
                    else
                        setToolBarArte((int) (h + rawY - downY));
                }
                return false;
            }
        });

        mBottomDrawer = findViewById(R.id.bottom_drawer);
        mD = findViewById(R.id.d);
        mDName = mD.findViewById(R.id.name);
        mHeader = mD.findViewById(R.id.header);
        mRd = findViewById(R.id.rd);

        dMenu = findViewById(R.id.r1);
        dMenu.setToolBarListener(new g.ToolBarListener() {
            @Override
            public void setTabHeaders(int h, boolean actionUp) {
            }

            @Override
            public boolean setToolBarHeight(int dh) {
                if (!prefs.resizeDrawerHeader) return true;
                ViewGroup.LayoutParams p = mHeader.getLayoutParams();
                int h = p.height + dh;
                h = h < minHeaderH ? minHeaderH : Math.min(h, maxHeaderH);
                p.height = h;
                mHeader.setLayoutParams(p);
                return h == minHeaderH || h == maxHeaderH;
            }
        });
        dMenu.setAdapter(dMenuAdapter = new KAdapter(adptrs.dList, 0));
        mTList.setToolBarListener(new g.ToolBarListener() {
            @Override
            public void setTabHeaders(int dh, boolean actionUp) {
                if (!prefs.autoHideToolBar || mToolBar.getMeasuredHeight() > tBarH || fullScreen || (showingSPQuotes && day > 0))
                    return;
                int h = mToolBar.getMeasuredHeight() + dh;
                ValueAnimator animator = new ValueAnimator();
                animator.cancel();
                h = h < tBarH ? Math.max(h, dp) : tBarH;
                final ViewGroup.LayoutParams params1 = mToolBar.getLayoutParams();
                params1.height = h;
                mToolBar.setLayoutParams(params1);
                mToolBarArte.setLayoutParams(new RelativeLayout.LayoutParams(size.x, h + tBarH));
                if (actionUp) {
                    h = h < tBarH / 2 ? dp : tBarH;
                    animator.setDuration(300);
                    animator.setIntValues(mToolBar.getMeasuredHeight(), h);
                    animator.addUpdateListener(animation -> {
                        Integer h1 = (Integer) animation.getAnimatedValue();
                        params1.height = h1;
                        mToolBar.setLayoutParams(params1);
                        mToolBarArte.setLayoutParams(new RelativeLayout.LayoutParams(size.x, h1 + tBarH));
                    });
                    animator.start();
                }
            }

            @Override
            public boolean setToolBarHeight(int dh) {
                if (!prefs.expandToolBar) return true;
                int minH = tBarH, maxH = 3 * tBarH;
                int h = mToolBar.getMeasuredHeight() + dh;
                h = h < minH ? minH : Math.min(h, maxH);
                setToolBarArte(h);
                return h == minH || h == maxH;
            }
        });
        mTBtn = mTabs.findViewById(R.id.tabs);
        mainTabs = new ArrayList<>(Arrays.asList("Albums", "Playlist", "Songs"));
        mTabs.setTabs(mainTabs, 0);
        viewsSet = true;
        prevCursor = mDb.rawQuery("select * from " + (prefs.showLangViz ? LANG : ALB + " where " + PARENT + " = ?"), prefs.showLangViz ? null : new String[]{""});
        mTList.setAdapter(mAdapter = new Adapter(prevCursor));
        mTabTitles = new ArrayList<>();
        mDetails = new ArrayList<>();
        mArte = new ArrayList<>();
        if (prevCursor.moveToFirst()) {
            do {
                final long subAlb = decode(prevCursor.getString(prevCursor.getColumnIndex(SUBALBS)));
                long audios = prevCursor.getInt(prevCursor.getColumnIndex(AUDIOS));
                mTabTitles.add(prevCursor.getString(prevCursor.getColumnIndex(TITLE)));
                mDetails.add(subAlb + audios <= 0 ? "" : "(" + (subAlb > 0 ? subAlb + " sub-albums, " : "") + (audios > 0 ? audios + " audios" : "") + ")");
                mArte.add(prevCursor.getString(prevCursor.getColumnIndex(ARTE)));
            } while (prevCursor.moveToNext());
        }
//        mAudioDb.execSQL("drop table if exists playlist");
        mAudioDb.execSQL("create table if not exists playlist (_id primary key, " + TITLE + ", " + AUDIOS + " integer default 0, " + ARTE + ")");
        ContentValues values = new ContentValues();
        values.put("_id", "Favourites");
        values.put(TITLE, "Favourites");
        Cursor c1 = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + FAVOURITE + " = ?", new String[]{"1"});
        values.put(AUDIOS, c1.getCount());
        c1.close();
        mAudioDb.insertWithOnConflict("playlist", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        values = new ContentValues();
        values.put("_id", "Offline");
        values.put(TITLE, "Offline");
        c1 = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + OFFLINE + " = ?", new String[]{"1"});
        values.put(AUDIOS, c1.getCount());
        c1.close();
        mAudioDb.insertWithOnConflict("playlist", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        mTabs.setTabs(mainTabs, 0);
        spQuote.setVisibility(GONE);
        mBottomDrawer.setVisibility(VISIBLE);
        mTabs.setVisibility(VISIBLE);
        mActionButton.setVisibility(VISIBLE);
        if (serviceBound && mPlayer == null) {
            mServ.preventPlay = true;
            setNowPlaying(prefs.nowPlayingId);
            mServ.startPlaying();
        } else if (serviceBound && nowPlaying != null) {
            mServ.preventPlay = true;
            setNowPlaying(prefs.nowPlayingId);
            setPlayPauseButtons();
        }
        spQuote.setOnTouchListener(zoom);
        vedabaseContainer = findViewById(R.id.vedabase_container);
        ytContainer = findViewById(R.id.youtubeContainer);
        VedabaseWebView = initWebView(R.id.veda_web_view, R.id.toolbar2, false);
        NotesWebView = initWebView(R.id.notes_web_view, R.id.toolbar2, false);
        NotesWebView.setVisibility(View.GONE);
        YouTubeWebView = initWebView(R.id.youtube_web_view, R.id.toolbarYt, false);
        YouTubeWebView.setOnTouchListener((view, motionEvent) -> {
            youTubeOnTouch();
            return false;
        });
        if (serviceBound) handleIntent(getIntent());
        sList = findViewById(R.id.s_list);
        listMenu = findViewById(R.id.lm);
        lMenu = listMenu.findViewById(R.id.ml);
        listMInfo = listMenu.findViewById(R.id.i);
        mActionButton.setOnClickListener(v -> {
            ListAdapter adapter1 = mTList.getAdapter();
            if (adapter1 instanceof Adapter) {
                Adapter adapter = (Adapter) adapter1;
                if (adapter.type == Audio) {
                    mServ.Queue.clear();
                    nowPlaying = null;
                    mServ.startPlaying();
                } else if (adapter.type == Pl) {
                    createNewPlaylist(null);
                } else if (adapter.type == LyricsText) {
                    mTList.setAdapter(new Adapter(mAudioDb.rawQuery("select * from " + AUDIO_TABLE +
                            " where " + REF + " like '" + decodeUrl(mLyIds.get(mTabs.mTabInd)) + "'", null), Audio));
                    mActionButton.setImageResource(R.drawable.play);
                } else if (adapter.type == Alb) {
//                    adapter.gView = !adapter.gView;
//                    mTList.setNumColumns(adapter.gView ? size.x / 170 / dp : 1);
//                    mTList.invalidate();
                } else {
                    Toast.makeText(mContext, "Hare Krishna...\nWork in Progress...", Toast.LENGTH_LONG).show();
                    display("Hare Krishna...\nWork in Progress...", 5000, promptToContribute, "Contribute");
                }
            } else if (adapter1 instanceof QuoteAdapter) {
                if (month >= 0 && day > 0) {
                    final File f = new File(spQuoteDir, months[month] + "_" + (day > 9 ? day : "0" + day));
                    shareImage(f);
                }
            }
        });

        lMenu.postDelayed(() -> {
            if (shouldShow("toast")) {
                display("You will see important notifications here!", 0, (v) -> {
                    hideDisplay();
                    setFeatureDone("toast");
                    if (shouldShow(featureId))
                        lMenu.postDelayed(() -> mBottomDrawer.showFeatureTip(), 500);
                }, getString(R.string.ok));
            } else if (shouldShow(featureId)) mBottomDrawer.showFeatureTip();
        }, 2500);
    }

    public void shareImage(File f) {
        String fileName = f.getName();
        Bitmap q = BitmapFactory.decodeFile(f.getPath());
        try {
            int w = q.getWidth(), qw = 180;
            Bitmap b = Bitmap.createBitmap(w, w + qw - 8, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.drawColor(waBgColor);
            Paint paint = new Paint();
            paint.setTextSize(28);
            c.drawBitmap(q, 0, 0, paint);
            paint.setColor(waBgColor);
            c.drawRect(new RectF(.09f * w, .95f * w, .39f * w, .985f * w), paint);
            paint.setColor(Color.BLACK);
            Bitmap qr = BitmapFactory.decodeFile(new File(logoDir, "qr_" + BuildConfig.QR).getPath());
//            Bitmap qr = BitmapFactory.decodeResource(getResources(), R.drawable.qr);
            c.drawBitmap(qr, new Rect(0, 0, qr.getWidth(), qr.getHeight()), new RectF(4, w - 10, qw + 4, w + qw - 10), paint);
            c.drawText("\uD83D\uDC48 Scan to download", qw + 8, w + 56, paint);

            TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            tp.setTextSize(28);
            StaticLayout sl = new StaticLayout("\uD83D\uDC96   " + getString(R.string.app_name) + " App  \uD83D\uDC96  ", tp,
                    c.getWidth() - qw - 18, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            c.translate(qw + 10, w + 83);
            sl.draw(c);

//            c.drawText(getString(R.string.app_name), qw + 8, w + 70, paint);
            b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(getCacheDir(), fileName + ".png")));
            q.recycle();
            b.recycle();
            qr.recycle();
            runOnUiThread(() -> {
//                loadNativeAd();
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("image/png")
                        .putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" +
                                BuildConfig.IMG_PROVIDER_AUTHORITY + "/" + fileName))
                        .putExtra(Intent.EXTRA_TEXT, "\uD83D\uDC96  " + getString(R.string.app_name) + " \uD83D\uDC96  \n\n http://play.google.com/store/apps/dev?id=" + getPackageName())
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), getString(R.string.share_as_img)));
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra("s") && !shortName.equals(intent.getStringExtra("s"))) {
            startActivity(new Intent(k.this, m.class).putExtra("s", intent.getStringExtra("s")));
            finish();
        } else if (null != intent.getData()) {
            long[] linkData = getIndexFromUrl(intent.getData().toString());
            if (null != linkData) {
                if (mPlayer == null) mServ.instantiatePlayer();
                setNowPlaying(linkData[0] + "");
                mPlayer.currentPos = linkData[1];
                mServ.startPlaying();
            }
        }
    }

    private long[] getIndexFromUrl(String url) {
        long[] linkData = new long[]{0, 0};
        if (!url.contains("a=")) return null;
        String a = url.substring(url.indexOf("a=") + 2);
        if (a.contains("&")) a = a.substring(0, a.indexOf("&"));
        linkData[0] = decode(a);
        if (url.contains("pos=")) {
            a = url.substring(url.indexOf("pos=") + 4);
            if (a.contains("&")) a = a.substring(0, a.indexOf("&"));
            linkData[1] = decode(a);
        }
        return linkData;
    }

    public void setControllerUI() {
        ((TextView) findViewById(R.id.date_place_)).setText(nowPlaying.datePlace);
        ((TextView) findViewById(R.id.title1)).setText(nowPlaying.title);
        ((TextView) findViewById(R.id.title_)).setText(nowPlaying.title);
        String alb = "";
        Cursor c = mDb.rawQuery("select * from " + ALB + " where _id = ?", new String[]{decode(nowPlaying.alb.trim().split(" ")[0].trim()) + ""});
        if (c.moveToFirst()) alb = c.getString(c.getColumnIndex(TITLE));
        c.close();
        ((TextView) findViewById(R.id.artist1)).setText(alb);
        ((TextView) findViewById(R.id.artist_)).setText(alb);
//        int authorInd = nowPlaying.getLyricsInd()<0?-1:lyrics.get(nowPlaying.getLyricsInd()).authorInd;
        TextView author = findViewById(R.id.author_);
        if (nowPlaying.lang > 1) {
            final ArrayList<ArrayList<String>> textUrls = getTextUrls(nowPlaying.ref);
            author.setVisibility(VISIBLE);
            author.setText(String.format("%s ", textUrls.get(2).get(0)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                author.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(android.R.drawable.arrow_down_float), null);
            }
//            author.setBackgroundResource(R.drawable.rect);
            author.setOnClickListener(view -> {
                final PopupMenu popupMenu = new PopupMenu(k.this, view);
                for (int i = 0; i < textUrls.get(0).size(); i++)
                    popupMenu.getMenu().add(0, i, i, textUrls.get(1).get(i));
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int i = menuItem.getOrder();
                    showVedabase(textUrls.get(0).get(i));
                    return false;
                });
                popupMenu.show();
            });
        } else {
            author.setBackgroundColor(Color.argb(0, 0, 0, 0));
//            author.setText(authorInd>0&&authorInd<authors.size()-1?"by "+authors.get(authorInd).TITLE:"");
            author.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

//        setDrawerUI(maharaja = nowPlaying.albumId);
        final ImageView imageView = mD.findViewById(R.id.arte);
//        final ImageView imageView1 = fullscreen.findViewById(R.id.arte);
        final ImageView imageView2 = findViewById(R.id.arte_);
        final ImageView imageView3 = findViewById(R.id.blurredArte);
        final File file = new File(arteDir, "a_" + nowPlaying.arte);
        if (file.exists() && file.length() > 20) {
            imageView.setImageURI(Uri.fromFile(file));
//            imageView1.setImageURI(Uri.fromFile(file));
            imageView2.setImageURI(Uri.fromFile(file));
            imageView3.setImageURI(Uri.fromFile(file));
        } else {
//            imageView1.setImageResource(R.drawable.haribol);
            imageView3.setImageResource(R.drawable.sp1);
            new DownloadTask(rawBase + "shravanotsava/a/master/x" + density + "/arte_" + nowPlaying.arte, file, s -> {
                if (file.exists())
                    setControllerUI();
            }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
        }
//        if(nowPlaying.getLyricsInd()==-1)
//            ((TextView)fullScreenTL.findViewById(R.id.lyrics)).setText("\n\n\n\n\n\n\n\n\nLyrics not available\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
//        else {
//            String l = fetchLyrics(nowPlaying.getLyricsInd(), false);
//            ((TextView)fullScreenTL.findViewById(R.id.lyrics)).setText(Html.fromHtml(l.startsWith("<")?l
//                    :"<p style=\"text-align: center; font-style: italic\">"+l.replaceAll("\n","<br/>")+"</p>"));
//        }
    }

    public void setTab(int tInd) {
        if (null == mAudioDb || null == mDb) return;
        mVolumeControls.setVisibility(GONE);
        mActionButton.setVisibility(VISIBLE);
        if (fullScreen) {
            mActionButton.setVisibility(GONE);
            switch (tInd) {
                case 0:
                    StringBuilder s = new StringBuilder();
                    if (mServ.Queue.isEmpty()) getQueue();
                    for (String s1 : mServ.Queue) s.append(",").append(s1);
                    Adapter adapter = new Adapter(mAudioDb.rawQuery("select * from " + AUDIO_TABLE +
                            " where _id in (" + s.toString().substring(1) + ")", null), Audio);
                    mTList.setAdapter(adapter);
//                    TODO create rearranging list with mCursor adapter
                    break;
                case 1:
                    mTList.setAdapter(null);
                    mVolumeControls.animate().setStartDelay(0).alpha(1).setDuration(0).start();
                    mVolumeControls.setVisibility(VISIBLE);
                    mVolumeControls.animate().setStartDelay(100).alpha(.4f).setDuration(500).start();
                    break;
                case 2:
                    if (!nowPlaying.ref.isEmpty()) {
                        try {
                            mTList.setAdapter(new Adapter(lyDb.rawQuery("select * from [lx_" + nowPlaying.ref + "]", null)));
                        } catch (Exception e) {
                            Cursor c = lyDb.rawQuery("select * from " + TITLE + " where _id = ?", new String[]{decode(nowPlaying.ref) + ""});
                            fetchLyrics(c.moveToFirst() ? c.getInt(c.getColumnIndex(PARENT)) : 1);
                            c.close();
                        }
                    }
            }
        } else if (showingSPQuotes) {
            day = day < 1 ? day : (tInd + 1);
            month = month < 0 || day >= 1 ? month : tInd;
            mTList.setAdapter(new QuoteAdapter());
        } else if (showingLyrics) {
            if (mLyTabTitles == null) {
                mAdapter.type = LyricsAuthor;
                mAdapter.swapCursor(lyDb.rawQuery("select * from " + PARENT + mAlbSort, null));
            } else if (mLyTabTitles.get(0).equals("Prāṇama Mantras")) {
                mAdapter.type = Lyrics;
                mAdapter.swapCursor(lyDb.rawQuery("select * from " + TITLE + " where " + PARENT + "=?" + mLySort, new String[]{encodeUrl((tInd + 1) + "")}));
                fetchLyrics(tInd);
            } else {
                mTList.setAdapter(new Adapter(lyDb.rawQuery("select * from [lx_" + mLyIds.get(tInd) + "]", null)));
                mActionButton.setImageResource(R.drawable.music_note);
            }
        } else if (mPlTitles != null) {
            mAdapter.type = PlAudio;
            switch (mPlTitles.get(tInd)) {
                case "Favourites":
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + FAVOURITE
                            + "=?" + mAudioSort, new String[]{"1"}));
                    dMenuAdapter.ind = 4;
                    dMenuAdapter.notifyDataSetChanged();
                    break;
                case "Offline":
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE +
                            " where " + OFFLINE + "=?" + mAudioSort, new String[]{"1"}));
                    dMenuAdapter.ind = 5;
                    dMenuAdapter.notifyDataSetChanged();
                    break;
                default:
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id in (select _id from [" + mPlTitles.get(tInd) + "])" + mAudioSort, null));
            }
        } else if (mParent.isEmpty() && (!prefs.showLangViz || mLang < 0)) {
            Cursor cursor = mDb.rawQuery("select * from " + (prefs.showLangViz ? LANG +
                    (searchString.isEmpty() ? "" : " where " + searchString) : ALB + " where " + PARENT + " = ?" +
                    (searchString.isEmpty() ? "" : " and " + searchString)), prefs.showLangViz ? null : new String[]{""});
            if (cursor.moveToFirst()) {
                mParentName = cursor.getString(cursor.getColumnIndex(TITLE));
                mDName.setText(mParentName);
            }
            cursor.close();
            switch (tInd) {
                case 0:
                    mAdapter.type = Alb;
                    if (prefs.showLangViz)
                        mAdapter.swapCursor(mDb.rawQuery("select * from " + LANG + (searchString.isEmpty() ? "" : " where " + searchString) + mAlbSort, null));
                    else
                        mAdapter.swapCursor(mDb.rawQuery("select * from " + ALB + " where " + PARENT + " = ?" + (searchString.isEmpty() ? "" : " and " + searchString) + mAlbSort, new String[]{mParent = ""}));
                    mActionButton.setImageResource(R.drawable.g);
                    mActionButton.setVisibility(GONE);
                    break;
                case 1:
                    mAdapter.type = Pl;
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from playlist" + (searchString.isEmpty() ? "" : " where " + searchString) + mAlbSort, null));
                    mActionButton.setImageResource(R.drawable.add1);
                    break;
                case 2:
                    mAdapter.type = Audio;
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + (searchString.isEmpty() ? "" : " where " + searchString) + mAudioSort, null));
                    mActionButton.setImageResource(R.drawable.play);
                    break;
            }
        } else if (prevCursor.moveToFirst()) {
            mDName.setText(mTabTitles.get(tInd));
            prevCursor.move(tInd);
            long subAlb = decode(prevCursor.getString(prevCursor.getColumnIndex(SUBALBS)));
//            if(subAlb>0 && mPrevLang>0){
////                final int d = prevCursor.getInt(prevCursor.getColumnIndex(DISPLAY));
//                final String id = prevCursor.getString(0);
////                mActionButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        ContentValues values = new ContentValues();
////                        values.put(DISPLAY, d);
////                        mDb.update(ALB, values, "_id=?", new String[]{id});
////                        mTabs.goToTab(-1);
////                    }
////                });
//                /*switch (d){
//                    case 0:
//                        mAdapter.type = Alb;
//                        mActionButton.setImageResource(R.drawable.g);
//                                     mActionButton.setVisibility(GONE);       break;
//                    case 1:
//                        mAdapter.type = Alb;
//                        mActionButton.setImageResource(R.drawable.list);
//                        break;
//                    case 2:
////                        mAdapter.type = Audio;
//                        mActionButton.setImageResource(R.drawable.audio_list);
//                        break;
//                }*/
//            } else {
//                mAdapter.type = Audio;
//            }
            mAdapter.type = subAlb > 0 ? Alb : Audio;
            String id = encode(prevCursor.getLong(0));
            if (prefs.showLangViz && mPrevLang < 0) {
                mLang = prevCursor.getLong(0);
                if (/*(mLang == 0 || mLang == 1) && */decode(prevCursor.getString(prevCursor.getColumnIndex(SUBALBS))) < 2) {
                    mActionButton.setImageResource(R.drawable.play);
                    mAdapter.type = Audio;
                    mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + LANG + "=?" + (searchString.isEmpty() ? "" : " and " + searchString) + mAudioSort, new String[]{encode(mLang)}));
                } else {
                    mActionButton.setImageResource(R.drawable.g);
                    mActionButton.setVisibility(GONE);
                    mAdapter.swapCursor(mDb.rawQuery("select * from hk" + mLang + " where " + PARENT + "=?" + (searchString.isEmpty() ? "" : " and " + searchString) + mAlbSort, new String[]{mParent = ""}));
                }
            } else if (prefs.showLangViz && subAlb > 0) {
                mAdapter.swapCursor(mDb.rawQuery("select * from hk" + mLang
                        + " where " + PARENT + "=?" + (searchString.isEmpty() ? "" : " and " + searchString) + mAlbSort, new String[]{mParent = id}));
            } else if (prefs.showLangViz) {
                mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB +
                        " like '% " + id.replaceAll("'", "''") + " %' and " + LANG + " = ?" +
                        (searchString.isEmpty() ? "" : " and " + searchString) + mAudioSort, new String[]{encode(mLang)}));
            } else if (subAlb > 0) {
                mAdapter.swapCursor(mDb.rawQuery("select * from " + ALB + " where " + PARENT + " = ? " +
                        (searchString.isEmpty() ? "" : " and " + searchString) + mAlbSort, new String[]{mParent = id}));
            } else {
                mAdapter.swapCursor(mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB +
                        " like '% " + id.replaceAll("'", "''") + " %'" + (searchString.isEmpty() ? "" : " and " + searchString) + mAudioSort, null));
            }
        }
    }

    private void createNewPlaylist(final String[] ids) {
        final EditText input = new EditText(k.this);
        new AlertDialog.Builder(k.this).setTitle("Create New Playlist")
                .setPositiveButton("Create", (dialog, id) -> {
                    String newPlaylistName = input.getText().toString().trim();
                    Cursor c = mAudioDb.rawQuery("select * from playlist where _id=?", new String[]{"[" + newPlaylistName + "]"});
                    if (c.moveToFirst()) {
                        new AlertDialog.Builder(mContext).setMessage("Playlist " + newPlaylistName + " Already exists ").show();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("_id", newPlaylistName);
                        values.put(TITLE, newPlaylistName);
                        mAudioDb.insert("playlist", null, values);
                        mAudioDb.execSQL("create table if not exists [" + newPlaylistName + "](_id primary key)");
                        if (mAdapter.type == Pl) setTab(1);
                        if (ids != null) {
                            addToPlaylist(newPlaylistName, ids);
                        }
                    }
                    c.close();
                }).setNegativeButton("Cancel", null).setView(input).show();
    }

    private void addToPlaylist(String playlistName, String[] ids) {
        if (playlistName.isEmpty() || playlistName.toLowerCase().trim().contains("create new playlist")) {
            createNewPlaylist(ids);
            return;
        }
        ContentValues values;
        for (String i : ids) {
            values = new ContentValues();
            values.put("_id", i);
            mAudioDb.insertWithOnConflict("[" + playlistName + "]", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
        values = new ContentValues();
        Cursor c = mAudioDb.rawQuery("select * from [" + playlistName + "]", null);
        values.put(AUDIOS, c.getCount());
        mAudioDb.update("playlist", values, "_id=?", new String[]{playlistName});
        c.close();
    }

    private void fetchLyrics(int tInd) {
        Cursor cursor = lyDb.rawQuery("select * from " + TITLE + " where " + PARENT + "=?", new String[]{encodeUrl((tInd + 1) + "")});
        if (cursor.moveToFirst())
            do {
                final String s = encode(cursor.getLong(0));
                try {
                    Cursor c = lyDb.rawQuery("select * from [lx_" + s + "]", null);
                    c.close();
                } catch (Exception e1) {
                    new Thread(() -> {
                        mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                        download(new File(lyDir, s), rawBase + "shravanotsava/l/master/" + s);
                        lyDb.execSQL("create table [lx_" + s + "] (_id integer primary key autoincrement, " + TITLE + ", c integer default " + ly_txt + ")");
                        try {
                            String line;
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lyDir, s))));
                            line = reader.readLine();
                            ContentValues values = new ContentValues();
                            values.put(TITLE, line);
                            values.put("c", ly_title);
                            lyDb.insert("[lx_" + s + "]", null, values);
                            while ((line = reader.readLine()) != null) {
                                values = new ContentValues();
                                String s1;
                                if (line.startsWith(s1 = "fficial Name:")) {
                                    values.put("c", ly_official_name);
                                } else if (line.startsWith(s1 = "Author:")) {
                                    values.put("c", ly_author);
                                } else if (line.startsWith(s1 = "ook Name:")) {
                                    values.put("c", ly_book);
                                } else if (line.startsWith(s1 = "t:")) {
                                    values.put("c", ly_translation);
                                } else if (line.startsWith(s1 = ":")) {
                                    if (line.substring(1).trim().startsWith("http"))
                                        values.put("c", ly_url);
                                    else
                                        line = "";
                                }
                                values.put(TITLE, line.replace(s1, "").trim());
                                lyDb.insert("[lx_" + s + "]", null, values);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    if (mWakeLock.isHeld() && runningDownloadTasks <= 0) mWakeLock.release();
                }
            } while (cursor.moveToNext());
        cursor.close();
    }

    boolean searchIn[] = {true, true, false, false};
    final String searchInOpt[] = {"Default", "Current List Only", "All Songs", "All Lyrics"};
    boolean searchFor[] = {true, true, true, true, true};
    final String searchForOpt[] = {"Title", "Singer", "Author", "Date", "Place"};

    PopupWindow popup = null;
    int pauseVisible = GONE;

    private void showPopup(final Activity context, View view) {
        ViewGroup viewGroup = context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater != null ? layoutInflater.inflate(R.layout.m, viewGroup) : null;
        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.setAnimationStyle(R.style.Animation);
        Objects.requireNonNull(layout).findViewById(R.id.stop).setVisibility(pauseVisible);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popup.setBackgroundDrawable(getDrawable(R.drawable.kbtn));
            popup.setElevation(25);
        }
        if (/*fullScreen || */ytContainer.getVisibility() == VISIBLE)
            layout.findViewById(R.id.buttons).setVisibility(GONE);
        popup.showAsDropDown(view, 0, -50 * dp);
    }

    public void onClick(final View view) {
        if (!viewsSet) return;
        hideKeyboard();
        int i = view.getId();
        boolean disablePopup = true;
        Intent intent = null;
        if (i == R.id.searchSettings) {
            final PopupMenu popupMenu = new PopupMenu(k.this, view);
            hideKeyboard();
//            if(searchIn[0]) setDefaultSearchIn();
            popupMenu.getMenu().add("Search In All");
            popupMenu.getMenu().addSubMenu("Search In");
            for (int j = 0; j < searchInOpt.length; j++) {
                popupMenu.getMenu().getItem(1).getSubMenu().add(0, j, j, searchInOpt[j]);
                popupMenu.getMenu().getItem(1).getSubMenu().getItem(j).setChecked(searchIn[j]);
            }
            popupMenu.getMenu().getItem(1).getSubMenu().setGroupCheckable(0, true, false);
            popupMenu.getMenu().addSubMenu("Search For");
            for (int j = 0; j < searchForOpt.length; j++) {
                popupMenu.getMenu().getItem(2).getSubMenu().add(1, j, j, searchForOpt[j]);
                popupMenu.getMenu().getItem(2).getSubMenu().getItem(j).setChecked(searchFor[j]);
            }
            popupMenu.getMenu().getItem(2).getSubMenu().getItem(0).setEnabled(false);
            popupMenu.getMenu().getItem(2).getSubMenu().setGroupCheckable(1, true, false);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.isCheckable()) {
                    int i1 = item.getItemId();
                    if (i1 == 0) {
//                            setDefaultSearchIn();
                    } else
                        searchIn[0] = false;
                    popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0]);
                    if (item.getGroupId() == 0) {
                        item.setChecked(searchIn[i1] = !searchIn[i1]);
                        if (i1 == 0) {
//                                setDefaultSearchIn();
                            for (int j = 0; j < searchIn.length; j++)
                                popupMenu.getMenu().getItem(1).getSubMenu().getItem(j).setChecked(searchIn[j]);
                        } else if (i1 == 1) {
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(2).setChecked(searchIn[2] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(3).setChecked(searchIn[3] = false);
                        } else {
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(searchIn[0] = false);
                            popupMenu.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(searchIn[1] = false);
                            if (!searchIn[2] && !searchIn[3])
                                popupMenu.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(searchIn[1] = true);
                        }
                    } else
                        item.setChecked(searchFor[i1] = !searchFor[i1]);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(getApplicationContext()));
                    item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return false;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            return false;
                        }
                    });
                }
                return false;
            });
            popupMenu.setOnDismissListener(popupMenu1 -> imm.toggleSoftInput(0, 0));
            popupMenu.show();
        } else if (i == R.id.clearSearch) {
            mSearchBox.setText("");
        } else if (i == R.id.back || mSearchBar.getVisibility() == VISIBLE)
            onBackPressed();
        if (i == R.id.drawer_btn || i == R.id.t) {
            mD.expand();
        } else if (i == R.id.name) {
            if (((KAdapter) dMenu.getAdapter()).adptr == adptrs.dList) {
                dMenu.setAdapter(new KAdapter(adptrs.mList, 0));
                dMenu.smoothScrollToPosition(mTabs.mTabInd);
            } else
                dMenu.setAdapter(dMenuAdapter);
        } else if (i == R.id.menu) {
            int[] position = new int[2];
//            Point p = new Point();
            view.getLocationOnScreen(position);
//            p.set(position[0], position[1]);
            showPopup(k.this, view);
            disablePopup = false;
        } else if (i == R.id.feedback) {
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://f-qr.github.io/r/"));
        } else if (i == R.id.share_app || i == R.id.send) {
            if (!prefs.appSharingSign.contains("@appUrl")) prefs.appSharingSign += "\n\n@appUrl";
            String s = "http://play.google.com/store/apps/dev?id=" + getPackageName();
            intent = new Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, prefs.appSharingSign.replace("@appUrl", " " +
                            s + " ").replace("@App", appName));
        } else if (i == R.id.rate_app) {
            String uriString = "http://play.google.com/store/apps/dev?id=" + getPackageName();
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uriString));
        } else if (i == R.id.prabhupada_today) {
            tabInd = mTabs.mTabInd;
            showingSPQuotes = true;
//            mBottomDrawer.setVisibility(GONE);
            calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
            ArrayList<String> list = new ArrayList<>();
            for (int j = 1; j <= (month == 1 ? 28 : (month < 7 ? (month % 2 == 0 ? 31 : 30) : (month % 2 == 0 ? 30 : 31))); j++)
                list.add(j + " " + months[month]);
            mTabs.setTabs(list, day - 1);
            spQuote.setScaleType(ImageView.ScaleType.MATRIX);
            spQuote.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            spQuote.setBackgroundColor(Color.BLACK);
            findViewById(R.id.search).setVisibility(GONE);
            setZoomToCenter();
        } /*else if (i == R.id.playPauseChantWithSP) {
            playPauseChantWithSP();
        } else if (i == R.id.stopChantWithSP) {
            stopChantWithSP();
        } else if (i == R.id.chantWithSPFAB) {
            showChantWithSP();
        }*/ else if (i == R.id.quickBall) {
            float y = view.getY(), x = view.getX(), x1 = x < size.x / 2f ? 80 * dp : -80 * dp,
                    y1 = y > size.y - 200 * dp ? -80 * dp : y < 180 * dp ? 80 * dp : 0;
            q.setX(x - 43 * dp);
            q.setY(y - 43 * dp);
            q.animate().scaleY(.1f).scaleX(.1f).alpha(0).rotation(-180).setDuration(0).start();
            q.setVisibility(VISIBLE);
            q.animate().translationXBy(x1).translationYBy(y1).scaleX(1).scaleY(1).alpha(1).rotation(0).setDuration(200).start();
            view.animate().translationXBy(x1).translationYBy(y1).setDuration(200).start();
        } else if (i == R.id.stop) {
            stopChantWithSP();
        } else if (i == R.id.chant_with_prabhupada || i == R.id.pl_chant || i == R.id.mahamantra) {
            startChantWithSP();
        } /*else if(i==R.id.daily_nectar){
            TextView textView = findViewById(R.id.daily_nectar);
            textView.setText(Html.fromHtml(getDailyNectar(this)));
            findViewById(R.id.daily_nectar_container).setVisibility(VISIBLE);
        } else if(i == R.id.daily_nectar_container){
            view.setVisibility(GONE);
        } else if(i == R.id.send_daily_nectar){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ((TextView)findViewById(R.id.daily_nectar)).getText() + "-His Holiness Bhakti Rasamrita Swami\n\n" + getString(R.string.app_url));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } */ else if (i == R.id.search) {
            setToolBarArte(tBarH);
            mSearchBar.setVisibility(VISIBLE);
            mSearchBox.setText("");
            mSearchBox.requestFocus();
            imm.toggleSoftInput(0, 0);
        } /*else if(i == R.id.filter){
            PopupMenu p = new PopupMenu(k.this,findViewById(R.id.menu));
            for(int j = 0; j<filterOptions.length; j++) p.getMenu().add(0, j, j, filterOptions[j]);
            Menu m = p.getMenu();
            m.setGroupCheckable(0,true,false);
            for(int j = 0; j<filterOptions.length; j++)
                m.getItem(j).setChecked((prefs.filterFlags & flg[j]) == flg[j]);
            p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    item.setChecked(!item.isChecked());
                    if(item.isChecked()) prefs.filterFlags |= flg[id];
                    else prefs.filterFlags &= ~flg[id];
                    setAdapters();
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(getApplicationContext()));
                    item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return false;
                        }
                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            return false;
                        }
                    });
                    return false;
                }
            });
            p.show();
        }*/ else if (i == R.id.sort) {
            String options[];
            /*if(!showingLyrics && mParent.isEmpty() && (!prefs.showLangViz || mLang < 0) && mTabs.mTabInd==2)
                options = new String[]{"Sort by Title", "Sort by Album", "Sort by Place", "Sort by Date"};
            else */
            if (mAdapter.type == Audio || mAdapter.type == PlAudio)
                options = new String[]{"Sort by Title", "Sort by Place", "Sort by Date", "Sort by Size"};
            else if (mAdapter.type == Alb || mAdapter.type == Pl || mAdapter.type == LyricsAuthor)
                options = new String[]{"Sort by Name", "Sort by Song Count"};
            else
                options = new String[]{"Sort by Title", "Sort by Book"};
            PopupMenu p = new PopupMenu(k.this, popup != null && popup.isShowing() ? findViewById(R.id.menu) : view);
            for (String s : options) p.getMenu().add(s);
            p.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getTitle().toString()) {
                    case "Sort by Name":
                        mAlbSort = " order by " + TITLE + (!mAlbSort.isEmpty() && !mAlbSort.contains("desc") ? " desc " : "");
                        break;
                    case "Sort by Title":
                        if (showingLyrics)
                            mLySort = " order by " + TITLE + (!mLySort.isEmpty() && !mLySort.contains("desc") ? " desc " : "");
                        else
                            mAudioSort = " order by " + TITLE + (!mAudioSort.isEmpty() && !mAudioSort.contains("desc") ? " desc " : "");
                        break;
                    case "Sort by Song Count":
//                            mAlbSort = " order by " + AUDIOS + (!mAlbSort.isEmpty()&&!mAlbSort.contains("desc")?" desc " :"");
                        break;
                    case "Sort by Place":
                        mAudioSort = " order by " + PLACE + (!mAudioSort.isEmpty() && !mAudioSort.contains("desc") ? " desc " : "");
                        break;
                    case "Sort by Size":
                        mAudioSort = " order by " + SIZE + (!mAudioSort.isEmpty() && !mAudioSort.contains("desc") ? " desc " : "");
                        break;
                    case "Sort by Date":
                        mAudioSort = " order by " + DATE + (!mAudioSort.isEmpty() && !mAudioSort.contains("desc") ? " desc " : "");
                        break;
                    case "Sort by Book":
                        mLySort = " order by " + book + (!mLySort.isEmpty() && !mLySort.contains("desc") ? " desc " : "");
                }
                setTab(mTabs.mTabInd);
                return false;
            });
            p.show();
        } else if (i == R.id.shuffle_all) {
            prefs.shuffle = true;
            mServ.shuffleQueue();
            playNext();
        } else if (i == R.id.preferences_btn) {
            hideFullScreenPlayer();
            setPrefView();
        } else if (i == R.id.f_menu) {
            mTabs.goToTab(1);
            fsMenu.setAdapter(new KAdapter(adptrs.fullScreenMenu, -1));
        } /*else if(i == R.id.tab1_ && wifi.getVisibility()==VISIBLE){
            wifiTL.setTabButtons(0);
        } else if(i == R.id.tab2_ && wifi.getVisibility()==VISIBLE){
            wifiTL.setTabButtons(1);
        } else if(i == R.id.tab3_ && wifi.getVisibility()==VISIBLE){
            wifiTL.setTabButtons(2);
        } else if(i == R.id.selectAll){
            findViewById(R.id.unselectAll).setVisibility(VISIBLE);
            Adapter adapter = (Adapter) wifiList.getAdapter();
            adapter.wifiSelectedItems = adapter.getCount();
            for(int j = 0; j<adapter.getCount(); j++) adapter.selectedItems[j] = true;
            adapter.notifyDataSetChanged();
            setWifiCount();
        } else if(i == R.id.unselectAll){
            view.setVisibility(GONE);
            Adapter adapter = (Adapter) wifiList.getAdapter();
            adapter.wifiSelectedItems = 0;
            for(int j = 0; j<adapter.getCount(); j++) adapter.selectedItems[j] = false;
            adapter.notifyDataSetChanged();
            setWifiCount();
        }*/ else if (i == R.id.shuffle) {
            prefs.shuffle = !prefs.shuffle;
            setShuffleAndRepeat();
            mServ.shuffleQueue();
            ContentValues values = new ContentValues();
            values.put("shuffle", prefs.shuffle ? 1 : 0);
            mDb.update("prefs", values, null, null);
        } else if (i == R.id.repeat) {
            prefs.repeat = (prefs.repeat + 1) % 3;
            setShuffleAndRepeat();
            ContentValues values = new ContentValues();
            values.put("repeat", prefs.repeat);
            mDb.update("prefs", values, null, null);
        } else if (i == R.id.share) {
            shareSong("");
        } /*else if(i == R.id.share) {
            if(clipContainer.getVisibility()==VISIBLE) {
                shareAudioClip(((EditText)findViewById(R.id.clipTitle)).getText().toString());
            } else {
                PopupMenu p = new PopupMenu(k.this, view);
                p.getMenu().add("Share Url");
                p.getMenu().add("Share Clip");
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()) {
                            case "Share Url":
                                shareSong(-1);
                                break;
                            case "Share Clip":
                                shareAudioClip();
                                break;
                        }
                        return true;
                    }
                });
                p.show();
            }
        } else if(i == R.id.cut) {
            shareAudioClip();
        } else if(i == R.id.clip) {
            shareAudioClip(((EditText)findViewById(R.id.clipTitle)).getText().toString());
        }*/ else if (i == R.id.like_) {
            setLike("");
        } else if ((i == R.id.playPause || i == R.id.playPause1)) {
            playPause();
        } else if (i == R.id.next) {
            playNext();
        } else if (i == R.id.prev) {
            playPrevious();
        } /*else if(i == R.id.invertColor1||i==R.id.invertColor){
            opaqueBackground = !opaqueBackground;
            TextView lyrics = holderTL.findViewById(R.id.lyrics);
            TextView lyrics1 = fullScreenTL.findViewById(R.id.lyrics);
            if(opaqueBackground) {
                lyrics.setBackgroundResource(R.color.colorPrimary);
                lyrics.setTextColor(Color.BLUE);
                lyrics.setShadowLayer(0,0,0,0);
                lyrics1.setBackgroundResource(R.color.colorPrimary);
                lyrics1.setTextColor(Color.BLUE);
                lyrics1.setShadowLayer(0,0,0,0);
            } else {
                lyrics.setBackgroundResource(android.R.color.transparent);
                lyrics.setTextColor(Color.WHITE);
                lyrics.setShadowLayer(8,0,0,Color.RED);
                lyrics1.setBackgroundResource(android.R.color.transparent);
                lyrics1.setTextColor(Color.WHITE);
                lyrics1.setShadowLayer(8,0,0,Color.RED);
            }
        } else if(i == R.id.audio){
            if(fullscreen.getVisibility() == VISIBLE) {
                fsList.setAdapter(lsAdapter = new Adapter(adptrs.ls,nowPlaying.getLyricsInd()));
                fsList.setOnItemClickListener(audioItemClickListener);
                fullScreenTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
            } else {
                holderList.setAdapter(lsAdapter = new Adapter(adptrs.ls,lyricsAdapter.indices.get(holderTL.tabInd)));
                holderList.setOnItemClickListener(audioItemClickListener);
                holderTL.findViewById(R.id.lyricsHolder).setVisibility(GONE);
            }
            findViewById(R.id.invertColor).setVisibility(GONE);
            findViewById(R.id.search).setVisibility(VISIBLE);
        } */ else if (i == R.id.pref_gen) {
            setPrefView(R.id.pref_gen_btns);
        } else if (i == R.id.pref_notification) {
            setPrefView(R.id.pref_notification_btns);
        } else if (i == R.id.pref_storage) {
            setPrefView(R.id.storage);
        } else if (i == R.id.becomingNoisyAction) {
            PopupMenu popupMenu = new PopupMenu(k.this, view);
            for (int j = 0; j < becomingNoisy.length; j++)
                popupMenu.getMenu().add(0, j, j, becomingNoisy[j]);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                prefs.becomingNoisyAction = menuItem.getOrder();
                ((Button) view).setText(becomingNoisy[menuItem.getOrder()]);
                return false;
            });
            popupMenu.show();
        } else if (i == R.id.shakeAction) {
            PopupMenu popupMenu = new PopupMenu(k.this, view);
            for (int j = 0; j < onShake.length; j++)
                popupMenu.getMenu().add(0, j, j, onShake[j]);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                prefs.onShake = menuItem.getOrder();
                ((Button) view).setText(onShake[menuItem.getOrder()]);
//                    if(menuItem.getOrder() == 2) shakeDelay = 1000;
                return false;
            });
            popupMenu.show();
        } else if (i == R.id.setBackground) {
            PopupMenu p = new PopupMenu(k.this, view);
            p.getMenu().add(0, 0, 0, "Select Color");
            p.getMenu().add(1, 1, 1, "Select From Album Arte");
            p.getMenu().add(2, 2, 2, "Select From Device");
            p.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case 0:
                        mFSArte.setImageBitmap(null);
                        ((ImageView) findViewById(R.id.prevBackground)).setImageBitmap(null);
                        prefs.bgUri = "";
                        setBackground();
                        findViewById(R.id.colorPicker).setVisibility(VISIBLE);
                        break;
                    case 1:
                        sList.setAdapter(new KAdapter(adptrs.arte, -1));
                        break;
                    case 2:
                        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*"), "Choose Image")
                                , RESULT_LOAD_IMAGE);
                }
                return false;
            });
            p.show();
            findViewById(R.id.colorPicker).setVisibility(GONE);
        } else if (i == R.id.showFab) {
            prefs.showQuickBall = (prefs.showQuickBall + 1) % 2;
            if (prefs.showQuickBall == 1) {
                quickBall.setVisibility(VISIBLE);
                float x = quickBall.getX(), y = quickBall.getY();
                float x1 = x < size.x / 2f ? dp * 5 : size.x - 70 * dp,
                        y1 = y > size.y - dp * 10 * 12 ? size.y - dp * 10 * 12 : y < dp * 10 * 10 ? dp * 10 * 10 : y;
                quickBall.animate().translationXBy(x1 - x).translationYBy(y1 - y).setDuration(200).start();
                hideQuickBall(quickBall);
            } else quickBall.setVisibility(GONE);
        } else if (i == R.id.fixedBackground) {
            prefs.fixedBackground = (prefs.fixedBackground + 1) % 2;
//            mFSArte.setImageURI(prefs.fixedBackground==0?Uri.fromFile():null); ToDo
        } else if (i == R.id.offlineVedabase) {
            prefs.offlineVedabase = (prefs.offlineVedabase + 1) % 2;
        } else if (i == R.id.pref_notification_prabhupada_today) {
            prefs.SPQuoteNotification = (prefs.SPQuoteNotification + 1) % 3;
            setNotificationIcon(k.this, prefs.SPQuoteNotification, i, true);
        } else if (view.getId() == R.id.vedabasebtn) {
            if (vedabaseContainer.getVisibility() == View.GONE) {
                final String url = VedabaseWebView.getUrl();
                showVedabase(url == null ? vedaBaseUrl : url);
            } else
                vedabaseContainer.setVisibility(View.GONE);
        } /*else if(i == R.id.sp_time){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TimePicker timePicker = findViewById(R.id.sp_time_picker);
                if(timePicker.getVisibility() == View.GONE) {
                    timePicker.setVisibility(VISIBLE);
                    ((Button)view).setText(R.string.set_time);
                } else {
                    timePicker.setVisibility(View.GONE);
                    prefs.spQuoteTime = timePicker.getHour()*60 + timePicker.getMinute();
                    setNotificationTime();
                    (new Alarm()).setAlarm(k.this,Alarm.FOR_SP_QUOTE_NOT,Alarm.SP_QUOTE_NOT_ID, prefs.spQuoteTime);
                }
            }
        } else if(i == R.id.sp_not_sound){
            setNotificationTone(RESULT_SP_NOTIFICATION_TONE);
        } else if(i == R.id.download_manager) {
            view.setVisibility(GONE);
        } else if(i == R.id.updating_resources) {
            findViewById(R.id.download_manager).setVisibility(VISIBLE);
            ((ListView)findViewById(R.id.download_list)).setAdapter(downloadManagerAdapter = new Adapter(adptrs.downloadManager, -1));
        } else if(i == R.id.cancel_download_manager && !downloadQStack.isEmpty()) {
            DownloadQ d = downloadQStack.pop();
            for(int j = 0; j<downloadQStack.size(); j++) if(downloadQStack.get(j).type!=-10) downloadQStack.remove(j);
            downloadQStack.push(d);
            downloadManagerAdapter.notifyDataSetChanged();
//            ((ListView)findViewById(R.id.download_list)).setAdapter(downloadManagerAdapter = new Adapter(adptrs.downloadManager, -1));
        } else if(i == R.id.done) {
            findViewById(R.id.colorPicker).setVisibility(GONE);
            Preferences.animate().alpha(0).setDuration(100).start();
            handler.postDelayed(showPref, 300);
        }*/ else if (view.getId() == R.id.fab_close) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams();
            if (p.topMargin == 0) findViewById(R.id.goFullScreen).performClick();
            else vedabaseContainer.setVisibility(View.GONE);
        } else if (view.getId() == R.id.reload) {
            TextView myTextView = vedabaseContainer.findViewById(R.id.textView2);
            myTextView.setText(getString(R.string.loading));
            err = false;
            if (NotesWebView.getVisibility() == VISIBLE) {
                NotesWebView.reload();//this could result in vedabase view opening
            } else {
                VedabaseWebView.reload();
            }
        } else if (view.getId() == R.id.goForward) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                if (NotesWebView.canGoForward())
                    NotesWebView.goForward();
                else
                    Toast.makeText(k.this, getString(R.string.cant_go_further), Toast.LENGTH_SHORT).show();
            } else {
                if (VedabaseWebView.canGoForward())
                    VedabaseWebView.goForward();
                else
                    Toast.makeText(k.this, R.string.cant_go_further, Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.goFullScreen) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams();
            if (p.topMargin == 0) {
                setToolBarArte(mToolBar.getMeasuredHeight());
                if (vedabaseContainer.top_margine == 0)
                    p.topMargin = 250 * dp;
                else
                    p.topMargin = vedabaseContainer.top_margine;
                vedabaseContainer.setLayoutParams(p);
                ((ImageButton) view).setImageResource(R.drawable.fullscreen);
                mD.collapse();
                mRd.collapse();
            } else {
                p.topMargin = 0;
                mStatusBar.animate().alpha(0).start();
                ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) VedabaseWebView.getLayoutParams();
                p1.topMargin = 0;
                VedabaseWebView.setLayoutParams(p1);
                NotesWebView.setLayoutParams(p1);
                vedabaseContainer.setLayoutParams(p);
                ((ImageButton) view).setImageResource(R.drawable.fullscreen_exit);
                mD.disableDrawer();
                mRd.disableDrawer();
            }
        } else if (view.getId() == R.id.fab_notes) {
            if (findViewById(R.id.notes_web_view).getVisibility() == VISIBLE) {
                NotesWebView.setVisibility(View.GONE);
                VedabaseWebView.setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
            } else {
                NotesWebView.setVisibility(VISIBLE);
                VedabaseWebView.setVisibility(GONE);
                if (NotesWebView.getUrl() == null) NotesWebView.loadUrl(notesUrl);
                ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.sp1);
            }
        } else if (i == R.id.open_in_yt) {
            Toast.makeText(k.this, getString(R.string.open_in_yt), Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(YouTubeWebView.getUrl()));
            startActivity(intent1);
        } else if (i == R.id.forward) {
            if (YouTubeWebView.canGoForward()) YouTubeWebView.goForward();
            youTubeOnTouch();
        } else if (i == R.id.refresh) {
            YouTubeWebView.setVisibility(View.GONE);
            TextView myTextView = findViewById(R.id.textView2);
            myTextView.setText(getString(R.string.loading));
            err = false;
            YouTubeWebView.reload();
        } /*else if (i == R.id.about) {
            showSupportPopup(k.this);
            disablePopup = false;
        } else if (i == R.id.bookmarks) {
            drawer.collapse();
            rDrawer.expand();
        } else if (i == R.id.rd_title) {
            TextView t = (TextView) view;
            if(t.getText().toString().trim().equals("Bookmarks")){
                t.setText(" Audio Clips");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.music_q),null,getDrawable(R.drawable.bookmark),null);
                }
                rDrawer.findViewById(R.id.add_bookmark).setVisibility(GONE);
                rDrawer.findViewById(R.id.cut).setVisibility(VISIBLE);
                ((ListView) findViewById(R.id.rd_list)).setAdapter(clipAdapter = new Adapter(adptrs.clip, -1));
                ((ListView) findViewById(R.id.rd_list)).setOnItemClickListener(audioClipOnItemClickListener);
            } else {
                t.setText(" Bookmarks");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bookmark),null,getDrawable(R.drawable.music_q),null);
                }
                rDrawer.findViewById(R.id.add_bookmark).setVisibility(VISIBLE);
                rDrawer.findViewById(R.id.cut).setVisibility(GONE);
                ((ListView) findViewById(R.id.rd_list)).setAdapter(bookmarkAdapter);
                ((ListView) findViewById(R.id.rd_list)).setOnItemClickListener(bookmarkOnItemClickListener);
            }
        } else if (i == R.id.add_bookmark) {
            AddBookmark(-1);
        } else if (i == R.id.addPlaylist) {
            createNewPlaylist(-10,true,-10);
        } else if (i == R.id.initWifi) {
            wifiStatus = wifiManager.isWifiEnabled();
            adapters = new Adapter[]{offlineAdapter, arteAdapter, vbAdapter};
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);// Indicates a change in the list of available peers.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);// Indicates the state of Wi-Fi P2P connectivity has changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);// Indicates this device's details have changed.
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);
            receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
            registerReceiver(receiver, intentFilter);
            wifi.setVisibility(VISIBLE);
            offlineAdapter.selectedItems = new boolean[offlineAdapter.getCount()];
            arteAdapter.selectedItems = new boolean[arteAdapter.getCount()];
            vbAdapter.selectedItems = new boolean[vbAdapter.getCount()];
            wifiTL.setTabButtons(0);
            final File f = new File(INTERNAL_DIR, "wifi_name_");
            try {
                wifiName = (new BufferedReader(new InputStreamReader(new FileInputStream(f)))).readLine();
                try {
                    Method m = mManager.getClass().getMethod("setDeviceName",
                            WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
                    m.invoke(mManager, mChannel, wifiName, null);
                } catch (Exception ignored) {}
            } catch (IOException e) {
                final EditText input = new EditText(k.this);
                dBuilder.setTitle("Set Device Name").setMessage("This TITLE will be visible to your peers.")
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wifiName = "sp_" + input.getText().toString();
                                try {
                                    Method m = mManager.getClass().getMethod("setDeviceName",
                                            WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
                                    m.invoke(mManager, mChannel, wifiName, null);
                                    (new FileOutputStream(f)).write(wifiName.getBytes());
                                } catch (Exception ignored) {}
                                dBuilder.setCancelable(true);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        dBuilder.setCancelable(true);
                    }
                }).setView(input).setCancelable(false).show();
            }
//            findViewById(R.id.radar).startAnimation(AnimationUtils.loadAnimation(k.this, R.anim.rotate));
        } else if (i == R.id.wifiSend || i == R.id.wifiSend_){
            if(info!=null && info.groupFormed && !sendingFiles)
                (new RunThread()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sendFile);
            else if(!searchPeers(true)){
                Toast.makeText(k.this, "Enabling Wifi. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        } else if (i == R.id.wifiReceive){
            if(!searchPeers(false)){
                Toast.makeText(k.this, "Enabling Wifi. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        }*/ else if (i == R.id.buybooks) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://amzn.to/2BsTL4h")));
        } else {
            String uriStr = "";
            if (i == R.id.fb) {
                uriStr = "https://facebook.com/" + fb;
            } else if (i == R.id.app) {
                uriStr = pkg.isEmpty() ? "https://play.google.com/store/apps/dev?id=5009060970068759882" : "http://play.google.com/store/apps/dev?id=" + pkg;
            } else if (i == R.id.twitter) {
                uriStr = "https://twitter.com/" + twit;
            } else if (i == R.id.in) {
                uriStr = "https://linkedin/" + linkedIn;
            } else if (i == R.id.yt) {
                uriStr = "https://youtube.com/" + yt;
            } else if (i == R.id.web) {
                uriStr = infoUrl;
            }
            if (!uriStr.equals("")) {
                intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(uriStr));
            } else if (i != R.id.toast) {
                Toast.makeText(mContext, "Hare Krishna...\nComing Soon...", Toast.LENGTH_LONG).show();
                display("Hare Krishna...\nComing Soon...", 2000, v -> display(getString(R.string.workingonit), 5000, promptToContribute, "Help Us"), "Check Updates");
            }
        }
        if (popup != null && disablePopup) popup.dismiss();
        if (intent != null) startActivity(intent);
    }

    public void setZoomToCenter() {
        spQuote.setVisibility(VISIBLE);
        File f = new File(spQuoteDir, months[month] + "_" + (day > 9 ? day : "0" + day));
        if (f.exists()) {
            savedMatrix = new Matrix();
            Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
            Bitmap b1 = Bitmap.createBitmap(b.getWidth(), b.getHeight(), b.getConfig());
            Canvas canvas = new Canvas(b1);
            canvas.drawColor(Color.parseColor("#f6e3e3"));
            canvas.drawBitmap(b, 0, 0, null);
            spQuote.setImageMatrix(matrix = new Matrix());
            spQuote.setImageBitmap(b1);
            matrix = spQuote.getImageMatrix();
            matrix.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, size.x, size.y - notchH), Matrix.ScaleToFit.CENTER);
            spQuote.setImageMatrix(matrix);
        }
    }

    @Override
    public void scrollAll(int x, int y) {
        for (h s : syncedH) s.scrollTo(x, y);
    }

    int tabInd;

    public void showFullScreenPlayer() {
        if (shouldShow(featureId)) {
            mBottomDrawer.expand();
            return;
        }
        mInfo.animate().scaleX(.05f).scaleY(.05f).alpha(0).setDuration(150).start();
        mBottomDrawer.expand();
        this.findViewById(R.id.blurredArte).animate().alpha(0).setDuration(duration).start();
        this.findViewById(R.id.blurredArte_).animate().alpha(0).setDuration(duration).start();
        fullScreen = true;
        tabInd = mTabs.mTabInd;
        mTabs.setTabs(mainTabs, 1);
        mTabs.mTabs.animate().alpha(0).start();
        mStatusBar.animate().alpha(0).start();
        mToolBarArte.animate().alpha(0).start();
        ViewGroup.LayoutParams p = mToolBar.getLayoutParams();
        p.height = 0;
        mToolBar.setLayoutParams(p);
        mToolBarArte.setLayoutParams(p);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTabs.getLayoutParams();
        params.setMargins(0, 0, 0, -130 * dp);
        mTabs.setLayoutParams(params);
        mCForeground.setVisibility(VISIBLE);
        File f = new File(arteDir, "a_" + nowPlaying.arte);
        if (f.exists()) mFSArte.setImageURI(Uri.fromFile(f));
        else mFSArte.setImageResource(R.drawable.sp);
        mFSToolBar.setVisibility(VISIBLE);
        mD.disableDrawer();
        mRd.disableDrawer();
    }

    public void hideFullScreenPlayer() {
        fullScreen = false;
        mVolumeControls.setVisibility(GONE);
        mBottomDrawer.collapse();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTabs.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mTabs.setLayoutParams(params);
        mTList.setAdapter(mAdapter);
        mTabs.setTabs((prefs.showLangViz ? mLang < 0 : mParent.isEmpty()) ? mainTabs : mTabTitles, tabInd);
        mTabs.mTabs.animate().alpha(1).start();
        mStatusBar.animate().alpha(1).start();
        mToolBarArte.animate().alpha(1).start();
        setToolBarArte(tBarH);
        mCForeground.setVisibility(GONE);
        mFSArte.setImageURI(null);
        mFSToolBar.setVisibility(GONE);
        mD.collapse();
        mRd.collapse();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServ = ((s.MBinder) service).getService();
        serviceBound = true;
        s.callBacks = this;
        if (viewsSet && mPlayer == null) {
            mServ.preventPlay = true;
            setNowPlaying(prefs.nowPlayingId);
            mServ.startPlaying();
        } else if (viewsSet && nowPlaying != null) {
            mServ.preventPlay = true;
            setNowPlaying(prefs.nowPlayingId);
            setPlayPauseButtons();
        }
        if (viewsSet) handleIntent(getIntent());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceBound = false;
    }

    @Override
    public void setPlayPauseButtons() {
        if (nowPlaying != null) {
            setControllerUI();
            initSeekBar();
            if (mAdapter.type == Audio) {
                Cursor c = mAdapter.getCursor();
                int i = 0;
                if (c.moveToFirst()) do {
                    if (c.getLong(0) == decode(nowPlaying.id)) break;
                    i++;
                } while (c.moveToNext());
                mTList.setVerticalScrollbarPosition(i);
            }
        }
        if (mPlayer.isPlaying()) {
            for (int b : playPauseButtons) {
                ImageButton imageButton = findViewById(b);
                if (imageButton != null)
                    imageButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            for (int b : playPauseButtons) {
                ImageButton imageButton = findViewById(b);
                if (imageButton != null)
                    imageButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }
        setShuffleAndRepeat();
        if (!mServ.preventPlay) mServ.createNotification();
//        mServ.preventPlay = false;
    }

    public void setShuffleAndRepeat() {
        mShuffle.setColorFilter(prefs.shuffle ? Color.parseColor("#D81B60") : Color.GRAY);
        mLike.setColorFilter(nowPlaying != null && nowPlaying.fav ? Color.parseColor("#D81B60") : Color.GRAY);
        switch (prefs.repeat) {
            case REPEAT_NONE:
                mRepeat.setColorFilter(Color.GRAY);
                break;
            case REPEAT_LIST:
                mRepeat.setColorFilter(Color.parseColor("#D81B60"));
                mRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                break;
            case REPEAT_ONE:
                mRepeat.setColorFilter(Color.parseColor("#D81B60"));
                mRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
        }
    }

    void initSeekBar() {
        k.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPlayer.isPrepared) {
                    long duration_ = mPlayer.getDuration();
                    if (duration_ > 0) {
                        seekBar.setMax((int) (duration_ / 1000));
                        seekBar.setProgress(0);

                        long sec = duration_ / 1000, min = sec / 60, hr = min / 60;
                        sec = sec % 60;
                        min = min % 60;
                        String durationStr = "";
                        durationStr = durationStr + (hr == 0 ? "  " : ((hr > 9 ? hr : "0" + hr) + ":"));
                        durationStr = durationStr + (min > 9 ? min : "0" + min) + ":";
                        durationStr = durationStr + (sec > 9 ? sec : "0" + sec);

                        TextView duration = findViewById(R.id.duration);
                        if (duration != null)
                            duration.setText(durationStr);
                    }
                    long progress = mPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress((int) progress);
                }
                if (mPlayer.isPlaying())
                    new Handler().postDelayed(this, 1000);
            }
        });
    }

    public void setSeekBarProgress(long milliSec) {
        seekBar.setProgress((int) (milliSec / 1000));
    }

    @Override
    public ArrayList<String> getQueue() {
        mServ.Queue.clear();
        Adapter adapter = (Adapter) mTList.getAdapter();
        adapter = null == adapter ? mAdapter : adapter;
        if (adapter.type == Audio) {
            Cursor c = adapter.getCursor();
            if (c.moveToFirst())
                do {
                    mServ.Queue.add(c.getString(0));
                } while (c.moveToNext());
        } else {
            Cursor c;
            if (prefs.showLangViz && mLang < 0 || mParent.isEmpty())
                c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE, null);
            else
                c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB + " like '% " +
                                mParent.replaceAll("'", "''") + " %'" + (prefs.showLangViz ? " and " + LANG + " = ?" : ""),
                        prefs.showLangViz ? new String[]{encode(mLang)} : null);
            if (c.moveToFirst()) do {
                mServ.Queue.add(c.getString(0));
            } while (c.moveToNext());
            c.close();
        }
        return mServ.Queue;
    }

    @Override
    public void setNowPlaying(Cursor c) {
        nowPlaying = new Audio(c);
        if (mServ.Queue.isEmpty()) getQueue();
    }

    @Override
    public void setNowPlaying(String audioId) {
        String s[] = audioId.split("~");
        Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id = ?", new String[]{s[0]});
        if (s.length > 1) {
            mServ.instantiatePlayer();
            mPlayer.currentPos = decode(s[1]);
        }
        if (c.moveToFirst()) nowPlaying = new Audio(c);
        c.close();
    }

    @Override
    public void download(ArrayList<String> downloadTasks) {
        new AudioDownloadTask(mContext, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadTasks.get(0));
    }

    @Override
    public void playPause() {
        if (mPlayer == null) {
            mServ.startPlaying();
        } else if (mPlayer.isPrepared && mPlayer.isPlaying()) {
            mPlayer.pause();
            mServ.abandonAudioFocus();
        } else if (!mPlayer.isPrepared) {
            mServ.startPlaying();
        } else if (!mPlayer.isPreparing) {
            mPlayer.start();
        }
    }

    @Override
    public void playNext() {
        if (!mServ.Queue.isEmpty())
            mServ.nowPlayingInd = (mServ.nowPlayingInd + 1) % mServ.Queue.size();
        nowPlaying = null;
        mServ.startPlaying();
    }

    @Override
    public void playPrevious() {
        if (!mServ.Queue.isEmpty())
            mServ.nowPlayingInd = (mServ.Queue.size() + mServ.nowPlayingInd - 1) % mServ.Queue.size();
        nowPlaying = null;
        mServ.startPlaying();
    }

    @Override
    public void setLike(String ind) {
        if (ind == null || ind.isEmpty()) {
            ind = nowPlaying.id;
            nowPlaying.fav = !nowPlaying.fav;
        }
        ind = decode(ind) + "";
        Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id=?", new String[]{ind});
        if (c.moveToFirst()) {
            boolean fav = c.getInt(c.getColumnIndex(FAVOURITE)) == 1;
            fav = !fav;
            mLike.setColorFilter(fav ? Color.rgb(216, 27, 96) : Color.rgb(224, 244, 244));
            ContentValues values = new ContentValues();
            values.put(FAVOURITE, fav ? 1 : 0);
            mAudioDb.update(AUDIO_TABLE, values, "_id=?", new String[]{ind});
            mTabs.goToTab(-1);
        }
        c.close();
        ContentValues values = new ContentValues();
        values.put("_id", "Favourites");
        values.put(TITLE, "Favourites");
        Cursor c1 = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + FAVOURITE + " = ?", new String[]{"1"});
        values.put(AUDIOS, c1.getCount());
        c1.close();
        mAudioDb.insertWithOnConflict("playlist", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        mServ.createNotification();
    }

    private boolean invalidateList = false;

    private void startChantWithSP() {
        File file = downloadSpChant();
        if (prefs == null) return;
        if (!file.exists()) return;
        if (invalidateList) {
            invalidateList = false;
        } else if (player1 == null) {
            player1 = new MediaPlayer();
            try {
                player1.setDataSource(Uri.fromFile(file).toString());// initialize it here
            } catch (IOException e) {
                e.printStackTrace();
            }
            player1.setOnPreparedListener(musicPlayer -> {
                musicPlayer.start();
                pauseVisible = VISIBLE;
//                    findViewById(R.id.chantWithSPFAB).setVisibility(VISIBLE);
//                    hideQuickBall(findViewById(R.id.chantWithSPFAB));
                musicPlayer.setLooping(true);
                float v1 = (float) (1 - (Math.log(MAX_VOLUME - prefs.spChantVol) / Math.log(MAX_VOLUME)));
                if (player1 != null) player1.setVolume(v1, v1);
                createChantWithPrabhupadaNotification();
                mServ.setCallStateListener();
            });
            player1.prepareAsync();
        }
    }

    private File downloadSpChant() {
        File file = new File(baseDir, "sp");
        if (file.exists() || file.mkdirs()) file = new File(file, "chant.wav");
        if (!file.exists()) {
            new DownloadTask(rawBase + "srila-prabhupada-vani/q/master/chant.wav", file, s -> {
                if (null == s)
                    startChantWithSP();
                else
                    display(getString(R.string.fix_chant), -1, v -> display(getString(R.string.fix_chant), 50), "Ok");
            }).execute("");
        }
        return file;
    }

    public void createChantWithPrabhupadaNotification() {
        PendingIntent pendInt = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, k.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setAction("chant_with_prabhupada"), PendingIntent.FLAG_UPDATE_CURRENT);
        int plBtn = player1 != null && player1.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.sp1)
                .setContentTitle("Chant with Srila Prabhupada")
                .setContentText("Volume: " + k.prefs.spChantVol + " % of system volume")
                .addAction(R.drawable.ic_volume_down_black_24dp, "Decrease Volume", getBroadcast("sp_chant_vol_down"))
                .addAction(R.drawable.ic_volume_up_black_24dp, "Increase Volume", getBroadcast("sp_chant_vol_up"))
                .addAction(plBtn, "Pause", getBroadcast("play_pause_chant_with_sp"))
                .addAction(R.drawable.stop, "Stop", getBroadcast("pause_chant_with_sp"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sp));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC)
                    // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(2))
                    .setColor(Color.rgb(216, 27, 96));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("spchant", "SP Chant Controls", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Music Controls for Chant with Srila Prabhupada");
            channel.enableLights(false);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("spchant");
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(101, notification);
    }

    PendingIntent getBroadcast(String action) {
        return PendingIntent.getBroadcast(mContext, 0, new Intent(
                "com.krishnaapps.karnamrita." + action).setClass(mContext, receiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void stopChantWithSP() {
        mServ.unregisterPhoneStateListener();
        if (player1 != null) {
            player1.stop();
            player1.release();
            player1 = null;
//            if(findViewById(R.id.chantWithSPController).getVisibility()==VISIBLE)
//                hideChantWithSP();
            pauseVisible = GONE;
//            findViewById(R.id.chantWithSPFAB).setVisibility(GONE);
            invalidateList = true;
        }
        notificationManager.cancel(101);
    }

    @Override
    public void playPauseChantWithSP() {
        if (player1 == null) startChantWithSP();
        else if (player1.isPlaying()) player1.pause();
        else player1.start();
        createChantWithPrabhupadaNotification();
    }

    @Override
    public void adjustSPChantVol(boolean up) {
        prefs.spChantVol += up ? 10 : -10;
        prefs.spChantVol = prefs.spChantVol > 100 ? 100 : Math.max(prefs.spChantVol, 0);
        ContentValues values = new ContentValues();
        values.put("spChantVol", prefs.spChantVol);
        mDb.update("prefs", values, null, null);
        float v1 = (float) (1 - (Math.log(MAX_VOLUME - prefs.spChantVol) / Math.log(MAX_VOLUME)));
        if (player1 != null) player1.setVolume(v1, v1);
        createChantWithPrabhupadaNotification();
    }

    @Override
    public void showNoDataWarning() {
        display("Network not available\n\nWork in Progress...\n\nHare Krishna...", 5000, promptToContribute, "Help Us");
    }

    enum adptrs {dList, fullScreenMenu, mList, arte}

    public class KAdapter extends BaseAdapter {
        String[] txt = {"Library", "Video Library", "Song Book", "\uD83D\uDC96 Buy Books \uD83D\uDC96", "Favourites", "Offline", "Playing Queue",
                "Now Playing", "Preferences", "More Apps", "About Us"};
        int[] resources = {R.drawable.music_lib, R.drawable.video_library, R.drawable.book, R.drawable.thumb_pink,
                R.drawable.thumb_pink, R.drawable.download, R.drawable.music_q, R.drawable.music_note,
                R.drawable.setting, R.drawable.apps, R.drawable.info};
        File[] arteFiles;
        adptrs adptr;
        int layoutId = R.layout.lid, count = txt.length, ind;

        KAdapter(adptrs adptr, int ind) {
            this.adptr = adptr;
            this.ind = ind;
            switch (adptr) {
                case fullScreenMenu:
                    layoutId = R.layout.lifm;
                    txt = new String[]{"Add to Playlist", "Go to Album", "Share", "Favourite", "Tag Lyrics"};
                    resources = new int[]{R.drawable.music_q, R.drawable.music_lib,
                            R.drawable.reply, R.drawable.fav, R.drawable.book};
                    count = resources.length;
                    break;
                case mList:
                    layoutId = R.layout.lidm;
                    count = mTabTitles.size();
                    break;
                case arte:
                    arteFiles = arteDir.listFiles();
                    count = arteFiles.length;
                    break;
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (adptr == adptrs.arte) {
                ImageView v = view == null ? new ImageView(mContext) : (ImageView) view;
                v.setImageURI(Uri.fromFile(arteFiles[i]));
                v.setOnClickListener(v1 -> {
                    sList.setAdapter(null);
                    prefs.bgUri = Uri.fromFile(arteFiles[i]).toString();
                    setBackground();
                });
                return v;
            }
            final ListViewHolder holder;
            if (view == null) {
                holder = new ListViewHolder();
                view = LayoutInflater.from(k.this).inflate(layoutId, viewGroup, false);
                holder.arte = view.findViewById(R.id.arte);
                holder.title = view.findViewById(R.id.title);
                holder.details = view.findViewById(R.id.details);
                view.setTag(holder);
            } else
                holder = (ListViewHolder) view.getTag();
            view.setOnClickListener(v -> {
                switch (adptr) {
                    case mList:
                        if ((prefs.showLangViz && mLang < 0) || mParent.isEmpty()) {
                            mLang = 0;
                            mTabs.setTabs(mTabTitles, i);
                        } else
                            mTabs.goToTab(i);
                        mD.collapse();
                        break;
                    case dList:
                        ytContainer.setVisibility(GONE);
                        while (showingLyrics) onBackPressed();
                        int i1 = i;
                        switch (txt[i]) {
                            case "Library":
                                mTabs.setTabs(mainTabs, 0);
                                break;
                            case "Video Library":
                                dMenuAdapter.notifyDataSetChanged();
                                setToolBarArte(tBarH);
                                ytContainer.setVisibility(VISIBLE);
                                findViewById(R.id.search).setVisibility(GONE);
                                if (YouTubeWebView.getUrl() == null)
                                    YouTubeWebView.loadUrl("https://www.youtube.com/" + ytBase);
                                break;
                            case "\uD83D\uDC96 Buy Books \uD83D\uDC96":
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://amzn.to/2BsTL4h")));
                                break;
                            case "Song Book":
                                showingLyrics = true;
                                mTabs.setTabs(new ArrayList<>(Collections.singletonList("Vaishnava Songbook")), 0);
                                break;
                            case "Now Playing":
                                while (mPlTitles != null) onBackPressed();
                                showFullScreenPlayer();
                                break;
                            case "Playing Queue":
                                while (mPlTitles != null) onBackPressed();
                                showFullScreenPlayer();
                                new Handler().postDelayed(() -> {
                                    mTabs.goToTab(0);
                                    mD.disableDrawer();
                                }, 300);
                                break;
                            case "More Apps":
                                i1 = ind;
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/dev?id=5009060970068759882")));
                                break;
                            case "About Us":
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://krishna-apps.web.app")));
                                break;
                            case "Offline":
                            case "Favourites":
                                if (mPlTitles == null) tabInd = mTabs.mTabInd;
                                Cursor cursor = mAudioDb.rawQuery("select * from playlist" + mAlbSort, null);
                                mPlTitles = new ArrayList<>();
                                if (cursor.moveToFirst()) do {
                                    mPlTitles.add(cursor.getString(1));
                                } while (cursor.moveToNext());
                                mTabs.setTabs(mPlTitles, mPlTitles.indexOf(txt[i]));
                                cursor.close();
                                break;
                            case "Preferences":
                                i1 = ind;
                                setPrefView();
                                break;
                            default:
                                i1 = ind;
                                Toast.makeText(mContext, "Coming Soon...\nHare Krishna", Toast.LENGTH_LONG).show();
                                display("Coming soon... \nHare Krishna...", 1500, promptToContribute, "Contribute");
                        }
                        ind = i1;
                        mD.collapse();
                        break;
                    case fullScreenMenu:
                        switch (txt[i]) {
                            case "Add to Playlist":
                                Cursor c1 = mAudioDb.rawQuery("select * from playlist", null);
                                PopupMenu p = new PopupMenu(mContext, v);
                                if (c1.moveToFirst()) do {
                                    final String s = c1.getString(1);
                                    if (!s.trim().toLowerCase().equals("offline"))
                                        p.getMenu().add(s);
                                } while (c1.moveToNext());
                                c1.close();
                                p.getMenu().add("Create New Playlist");
                                p.setOnMenuItemClickListener(item -> {
                                    addToPlaylist(item.getTitle().toString(), new String[]{nowPlaying.id});
                                    return true;
                                });
                                p.show();
                                break;
                            case "Go to Album":
                                Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id = ?", new String[]{decode(nowPlaying.id) + ""});
                                if (c.moveToFirst()) {
                                    String[] s = c.getString(c.getColumnIndex(ALB)).split(" ");
                                    final long lang = decode(c.getString(c.getColumnIndex(LANG)));
                                    StringBuilder sb = new StringBuilder();
                                    for (String s1 : s)
                                        sb.append(",").append(decode(s1.trim()));
                                    c.close();
                                    c = mDb.rawQuery("select * from " + ALB + " where _id in (" + sb.toString().substring(1) + ")", null);
                                    final ArrayList<String> parents = new ArrayList<>();
                                    PopupMenu p1 = new PopupMenu(mContext, v);
                                    int i2 = 0;
                                    if (c.moveToFirst()) do {
                                        p1.getMenu().add(0, c.getInt(0), i2++, c.getString(c.getColumnIndex(TITLE)));
                                        parents.add(c.getString(c.getColumnIndex(PARENT)));
                                    } while (c.moveToNext());
                                    p1.setOnMenuItemClickListener(item -> {
                                        while (fullScreen || showingSPQuotes || showingLyrics || mPlTitles != null)
                                            onBackPressed();
                                        mParent = encode(item.getItemId());
                                        mPrevParent = parents.get(item.getOrder());
                                        mLang = lang;
                                        mPrevLang = mLang;
                                        if (prevCursor != null) prevCursor.close();
                                        prevCursor = mDb.rawQuery("select * from hk" + lang + " where " + PARENT + "=?" + mAlbSort, new String[]{mPrevParent});
                                        if (prevCursor.moveToFirst()) {
                                            mTabTitles.clear();
                                            do {
                                                mTabTitles.add(prevCursor.getString(prevCursor.getColumnIndex(TITLE)));
                                            } while (prevCursor.moveToNext());
                                            mTabs.setTabs(mTabTitles, mTabTitles.indexOf(item.getTitle().toString()));
                                        }
                                        return true;
                                    });
                                    p1.show();
                                }
                                c.close();
                                break;
                            case "Share":
                                shareSong(null);
                                break;
                            case "Favourite":
                                setLike(null);
                                break;
                            case "Tag Lyrics":
                            case "Set Text":
                            default:
                                display("Coming soon... \nHare Krishna...", 1500, promptToContribute, "Contribute");
                        }
                        break;
                }
            });
            switch (adptr) {
                case fullScreenMenu:
                    holder.title.setText(txt[i]);
                    holder.arte.setImageResource(resources[i]);
                    if (i == 2) {
                        holder.arte.animate().rotationY(180).setDuration(0).start();
                    }
                    view.animate().translationY(-45 * dp * i).alpha(0).setDuration(0).start();
                    holder.title.animate().translationX(2000 * dp).alpha(0).setDuration(0).start();
                    holder.title.animate().translationX(0).alpha(1).setDuration(250 + 50 * i).start();
                    view.animate().translationY(0).alpha(1).setDuration(150 + 50 * i).start();
                    return view;
                case dList:
                    holder.title.setText(txt[i]);
                    holder.arte.setImageResource(resources[i]);
                    if (i == ind) {
                        mDMenuBtn = holder.arte;
                        mDMenuBtn.setPressed(true);
                    } else
                        holder.arte.setPressed(false);
                    return view;
                case mList:
                    holder.title.setText(mTabTitles.get(i));
                    holder.arte.setImageURI(Uri.fromFile(new File(arteDir, "t_" + mArte.get(i))));
                    holder.details.setText(mDetails.get(i));
                    if (i == mTabs.mTabInd) {
                        mDMenuBtn = holder.arte;
                        mDMenuBtn.setPressed(true);
                    } else
                        holder.arte.setPressed(false);
                    return view;
            }
            return null;
        }

        class ListViewHolder {
            ImageView arte;
            TextView title, details;
        }
    }

    public void setPrefView(int id) {
        int[] ids = {R.id.pref_gen_btns, R.id.pref_notification_btns, R.id.bsrm_time_picker, R.id.sp_time_picker, R.id.storage};
        for (int id1 : ids) {
            if (id1 == id) continue;
            findViewById(id1).setVisibility(View.GONE);
        }
        if (findViewById(id).getVisibility() == View.GONE)
            findViewById(id).setVisibility(VISIBLE);
        else
            findViewById(id).setVisibility(View.GONE);
    }

    public void setPrefView() {
        ((EditText) mPrefView.findViewById(R.id.appShareTxt)).setText(prefs.appSharingSign);
        ((EditText) mPrefView.findViewById(R.id.songShareTxt)).setText(prefs.songSharingSign);
        ((Button) mPrefView.findViewById(R.id.becomingNoisyAction)).setText(becomingNoisy[prefs.becomingNoisyAction]);
        ((Button) mPrefView.findViewById(R.id.shakeAction)).setText(s.onShake[prefs.onShake]);
//        ((Button)findViewById(R.id.bsrm_not_sound)).setText(prefs.bsrmNotUri.substring(prefs.bsrmNotUri.lastIndexOf('/')+1));
//        ((Button)findViewById(R.id.sp_not_sound)).setText(prefs.spNotUri.substring(prefs.spNotUri.lastIndexOf('/')+1));
        ((CheckBox) findViewById(R.id.showFab)).setChecked(prefs.showQuickBall == 1);
//        ((Button)findViewById(R.id.showHK)).setChecked(prefs.showHk==1);
        ((CheckBox) findViewById(R.id.fixedBackground)).setChecked(prefs.fixedBackground == 1);
        ((CheckBox) findViewById(R.id.offlineVedabase)).setChecked(prefs.offlineVedabase == 1);
//        setNotificationTime();
        mD.disableDrawer();
        mRd.disableDrawer();
//        storageList.setAdapter(storageAdapter = new Adapter(adptrs.storage, -1));
//        ViewGroup.LayoutParams params = storageList.getLayoutParams();
//        params.height = (int)(storageAdapter.count*6.9*dp10);
//        storageList.setLayoutParams(params);
        mPrefView.setVisibility(VISIBLE);
    }

    public void setNotificationTime() {
        setNotificationTime(prefs.dailyNectarTime, findViewById(R.id.bsrm_time));
        setNotificationTime(prefs.spQuoteTime, findViewById(R.id.sp_time));
    }

    public void setNotificationTime(int time, Button btn) {
        int hours = time / 60, minutes = time % 60;
        String am = hours >= 12 ? " PM" : " AM";
        hours = hours % 12;
        String timeString = (hours > 9 ? "" + hours : "0" + hours) + ":" + (minutes > 9 ? "" + minutes : "0" + minutes) + am;
        btn.setText(timeString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.alarm), null);
        }
    }

    public void setNotificationIcon(Activity context, int state, int resId, boolean showToast) {
        switch (state) {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) mPrefView.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.not_off), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification Off", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) mPrefView.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.not), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification Shown - Notification Tone Silent", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Button) mPrefView.findViewById(resId)).
                            setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.not_active), null);
                }
                if (showToast)
                    Toast.makeText(context, "Notification with Alert Tone Active", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setNotificationTone(int requestCode) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION | RingtoneManager.TYPE_ALARM)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
                .putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        Uri.parse(requestCode == RESULT_BSRM_NOTIFICATION_TONE ? prefs.dailyNectarRingTone : prefs.spQuoteRingTone))
                .putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        this.startActivityForResult(Intent.createChooser(intent, "Select Notification Tone"), requestCode);
    }

    public void setBackground() {
        if (prefs.bgUri.isEmpty()) {
            int a1 = prefs.a, r1 = prefs.r, g1 = prefs.g, b1 = prefs.b;
            a.setMax(255);
            a.setProgress(a1);
            r.setMax(255);
            r.setProgress(r1);
            g.setMax(255);
            g.setProgress(g1);
            b.setMax(255);
            b.setProgress(b1);
        } else if (prefs.bgUri.equals("sp")) {
            ((ImageView) findViewById(R.id.prevBackground)).setImageResource(R.drawable.sp);
            mFSArte.setImageResource(R.drawable.sp);
        } else if (prefs.bgUri.equals("haribol")) {
            ((ImageView) findViewById(R.id.prevBackground)).setImageResource(R.drawable.haribol);
            mFSArte.setImageResource(R.drawable.haribol);
        } else {
            Uri selectedImage = Uri.parse(prefs.bgUri);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream is = getContentResolver().openInputStream(selectedImage);
                BitmapFactory.decodeStream(is, null, options);
                if (is != null) is.close();
                options.inSampleSize = calculateInSampleSize(options, size.x, size.y);
                int prevViewSize = calculateInSampleSize(options, 100 * dp, 100 * dp);
                options.inJustDecodeBounds = false;
                is = getContentResolver().openInputStream(selectedImage);
                mFSArte.setImageBitmap(BitmapFactory.decodeStream(is, null, options));
                if (is != null) is.close();
                options.inSampleSize = prevViewSize;
                is = getContentResolver().openInputStream(selectedImage);
                ((ImageView) findViewById(R.id.prevBackground)).setImageBitmap(BitmapFactory.decodeStream(is, null, options));
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    final public static int Alb = 0, Audio = 1, Pl = 2, PlAudio = 3, LyricsAuthor = 4, Lyrics = 5, LyricsText = 6;
    final public static int ly_title = 0, ly_official_name = 1, ly_author = 2, ly_book = 3, ly_url = 4, ly_txt = 5, ly_translation = 6;
    String mAudioSort = " order by " + TITLE, mAlbSort = "", mLySort = "";
    ArrayList<String> mTabTitles, mDetails, mArte;
    ArrayList<String> mLyTabTitles, mLyArte, mLyIds, mPlTitles;
    String mLyPrevTitle;
    boolean closeCursor = true;

    public class Adapter extends CursorAdapter implements SectionIndexer {
        int type = Alb;
        AlphabetIndexer alphabetIndexer;
        Cursor mCursor;
        boolean gView = false;

        Adapter(Cursor c) {
            super(mContext, c, 0);
            alphabetIndexer = new AlphabetIndexer(c, c.getColumnIndex(TITLE), " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            mCursor = c;
        }

        Adapter(Cursor c, int type) {
            this(c);
            this.type = type;
        }

        @Override
        public Cursor swapCursor(Cursor c) {
            alphabetIndexer = new AlphabetIndexer(c, c.getColumnIndex(TITLE), " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            mCursor = c;
            if (closeCursor) getCursor().close();
            else closeCursor = true;
            return super.swapCursor(c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            if (cursor.getColumnCount() == 3 && cursor.getColumnName(2).equals("c")) {
                type = LyricsText;
                return new TextView(context);
            }
            View view = LayoutInflater.from(context).inflate(type == Alb && gView ? R.layout.gia : R.layout.lia, parent, false);
            AlbHolder holder = new AlbHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (type == LyricsText) {
                TextView t = (TextView) view;
                t.setText(cursor.getString(1));
                t.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final int c = cursor.getInt(2);
                final int vPadding = c == ly_txt ? 0 : 8 * dp;
                t.setPadding(10 * dp, vPadding, 10 * dp, vPadding);
                t.setTextAlignment(c == ly_txt || c == ly_title ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_TEXT_START);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    t.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                }
                return;
            }
            final AlbHolder holder = (AlbHolder) view.getTag();
            String[] options = null;
            int[] icons = null;
            if (holder.id != null && holder.id.equals(cursor.getString(0)) && holder.type == type &&
                    holder.l == mLang && (nowPlaying == null || !nowPlaying.id.equals(encode(cursor.getLong(0)))))
                return;

            holder.ref.setVisibility(GONE);
            holder.lang.setText("");
            holder.details.setText("");
            holder.download.setVisibility(GONE);
            holder.like.setVisibility(GONE);
            holder.arte.setImageResource(R.drawable.sp1);
            holder.id = cursor.getString(0);
            holder.type = type;
            holder.l = (int) mLang;

            if (type == Pl) {
                options = new String[]{"Play", "Enqueue", "Add To", "Share", "Download All", "Delete Playlist"};
                icons = new int[]{R.drawable.play_circle, R.drawable.music_q, R.drawable.add1, R.drawable.reply,
                        R.drawable.download, R.drawable.delete};
                final String s = cursor.getString(0);
                holder.title.setText(s);
                holder.details.setText(cursor.getString(cursor.getColumnIndex(AUDIOS)));
                view.setOnClickListener(v -> {
                    mPlTitles = new ArrayList<>();
                    if (mCursor.moveToFirst()) do {
                        mPlTitles.add(mCursor.getString(1));
                    } while (mCursor.moveToNext());
                    mTabs.setTabs(mPlTitles, mPlTitles.indexOf(s));
                });
            } else {
                int i = cursor.getColumnIndex(TITLE);
                if (i < 0) return;
                final String title = cursor.getString(i);
                holder.title.setText(title);
                String arte = type == LyricsAuthor || type == Lyrics ? decodeUrl(cursor.getString(cursor.getColumnIndex(ARTE))) : cursor.getString(cursor.getColumnIndex(ARTE));
                File f = new File(arteDir, "t_" + arte);
                if (f.exists()) {
                    holder.arte.setImageURI(Uri.fromFile(f));
                } else {
                    new DownloadTask(rawBase + "shravanotsava/a/master/x" + 16 + "/thumb_" + arte, f, s -> notifyDataSetChanged()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
                }
                if (type == Alb) {
                    options = new String[]{"Play", "Enqueue", "Add To", "Share", "Download All", "Delete All Offline"};
                    icons = new int[]{R.drawable.play_circle, R.drawable.music_q, R.drawable.add1, R.drawable.reply,
                            R.drawable.download, R.drawable.delete};
                    holder.id = encode(cursor.getLong(0));
                    final long subAlb = decode(cursor.getString(cursor.getColumnIndex(SUBALBS)));
                    long audios = cursor.getInt(cursor.getColumnIndex(AUDIOS));
                    holder.details.setText(subAlb + audios <= 0 ? "" : "(" + (subAlb > 0 ? subAlb + " sub-albums, " : "") + (audios > 0 ? audios + " audios" : "") + ")");
                    final long l = cursor.getLong(0);
                    view.setOnClickListener(v -> {
                        prevCursor = mCursor;
                        closeCursor = false;
                        mPrevParent = mParent;
                        mPrevLang = mLang;
                        mTabTitles = new ArrayList<>();
                        mDetails = new ArrayList<>();
                        mArte = new ArrayList<>();
                        if (prevCursor.moveToFirst()) {
                            if (prefs.showLangViz && mPrevLang < 0) {
                                mLang = l;
                            }
                            do {
                                final long subAlb1 = decode(prevCursor.getString(prevCursor.getColumnIndex(SUBALBS)));
                                long audios1 = prevCursor.getInt(prevCursor.getColumnIndex(AUDIOS));
                                mTabTitles.add(prevCursor.getString(prevCursor.getColumnIndex(TITLE)));
                                mDetails.add(subAlb1 + audios1 <= 0 ? "" : "(" + (subAlb1 > 0 ? subAlb1 + " sub-albums, " : "") + (audios1 > 0 ? audios1 + " audios" : "") + ")");
                                mArte.add(prevCursor.getString(prevCursor.getColumnIndex(ARTE)));
                            } while (prevCursor.moveToNext());
                        }
                        mTabs.setTabs(mTabTitles, mTabTitles.indexOf(title));
                    });
                } else if (type == Lyrics) {
                    holder.title.setText(decodeUrl(title));
                    holder.details.setText(decodeUrl(cursor.getString(cursor.getColumnIndex(m.book))));
                    view.setOnClickListener(v -> {
                        try {
                            Cursor c = lyDb.rawQuery("select * from [lx_" + encode(mCursor.getLong(0)) + "]", null);
                            c.close();
                            mLyTabTitles = new ArrayList<>();
                            mLyArte = new ArrayList<>();
                            mLyIds = new ArrayList<>();
                            if (mCursor.moveToFirst()) {
                                do {
                                    mLyIds.add(encode(mCursor.getLong(0)));
                                    mLyTabTitles.add(decodeUrl(mCursor.getString(mCursor.getColumnIndex(TITLE))));
                                    mLyArte.add(decodeUrl(mCursor.getString(mCursor.getColumnIndex(ARTE))));
                                } while (mCursor.moveToNext());
                            }
                            mTabs.setTabs(mLyTabTitles, mLyTabTitles.indexOf(decodeUrl(title)));
                        } catch (Exception e) {
                            display("Preparing Lyrics", 500);
                        }
                    });
                } else if (type == LyricsAuthor) {
                    holder.title.setText(decodeUrl(title).replace("Songs by ", "").replace("Thakura", "Thakur"));
                    holder.details.setText("(" + decode(cursor.getString(cursor.getColumnIndex("c"))) + " bhajans)");
                    view.setOnClickListener(v -> {
                        mLyTabTitles = new ArrayList<>();
                        mLyArte = new ArrayList<>();
                        if (mCursor.moveToFirst()) {
                            do {
                                mLyTabTitles.add(decodeUrl(mCursor.getString(mCursor.getColumnIndex(TITLE))).replace("Songs by ", "").replace("Thakura", "Thakur"));
                                mLyArte.add(decodeUrl(mCursor.getString(mCursor.getColumnIndex(ARTE))));
                            } while (mCursor.moveToNext());
                        }
                        mTabs.setTabs(mLyTabTitles, mLyTabTitles.indexOf(mLyPrevTitle = decodeUrl(title).replace("Songs by ", "").replace("Thakura", "Thakur")));
                    });
                } else {
                    final Audio a = new Audio(cursor);
                    options = new String[]{"Play Next", "Add To", "Enqueue", "Ringtone",
                            "Trim", "Share", type == PlAudio ? "Remove From Playlist" : (a.offline ? "Delete" : "Download"), a.lang < 2 ? "Tag Lyrics" : "Set Text"};
                    icons = new int[]{R.drawable.play_next, R.drawable.add1, R.drawable.music_q, R.drawable.not, R.drawable.cut,
                            R.drawable.reply, a.offline || type == PlAudio ? R.drawable.delete : R.drawable.download, a.lang < 2 ? R.drawable.music_note : R.drawable.book};
                    holder.download.setVisibility(a.offline ? VISIBLE : GONE);
//                    holder.details.setText(a.size/1024f + "kb");
                    holder.details.setText(a.datePlace);
                    int lan = (int) decode(cursor.getString(cursor.getColumnIndex(LANG)).trim());
                    holder.lang.setText(lan < 0 ? "" : lang[lan][1]);
                    holder.like.setVisibility(VISIBLE);
                    if (a.fav) {
                        holder.like.setImageResource(R.drawable.thumb_pink);
                        holder.like.animate().scaleY(0.5f).scaleX(0.5f).rotation(-60).setDuration(0).start();
                        holder.like.animate().scaleY(0.8f).scaleX(0.8f).translationX(5 * dp).rotation(0).setDuration(350).start();
                    } else {
                        holder.like.setImageResource(R.drawable.thumb);
                        holder.like.animate().scaleX(1.3f).scaleY(1.3f).translationX(0).start();
                    }
                    holder.like.setOnClickListener(v -> {
                        setLike(a.id);
                        a.fav = !a.fav;
                        if (a.fav) {
                            holder.like.setImageResource(R.drawable.thumb_pink);
                            holder.like.animate().scaleY(0.6f).scaleX(0.6f).rotation(-30).setDuration(0).start();
                            holder.like.animate().scaleY(.9f).scaleX(.9f).translationX(5 * dp).rotation(0).setDuration(350).start();
                        } else {
                            holder.like.setImageResource(R.drawable.thumb);
                            holder.like.animate().scaleX(0.9f).scaleY(0.9f).translationX(0).start();
                        }
                    });/*
                    holder.arte.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((TextView)view.findViewById(R.id.diagnose)).setText(a.getUrl().replace("http://audio.iskcondesiretree.com/01_-_Srila_Prabhupada/", ""));
                            return true;
                        }
                    });*/
                    if (lan > 1 && !a.ref.isEmpty()) {
                        final ArrayList<ArrayList<String>> r = getTextUrls(a.ref);
                        holder.ref.setText(r.get(2).get(0));
                        holder.ref.setOnClickListener(v -> {
                            PopupMenu p = new PopupMenu(mContext, v);
                            for (int i1 = 0; i1 < r.get(0).size(); i1++) {
                                p.getMenu().add(0, i1, i1, r.get(1).get(i1));
                            }
                            p.setOnMenuItemClickListener(item -> {
                                showVedabase(r.get(0).get(item.getOrder()));
                                return false;
                            });
                            p.show();
                        });
                        holder.ref.setVisibility(VISIBLE);
                    } else if (!a.ref.isEmpty()) {
                        Cursor c = lyDb.rawQuery("select * from " + TITLE + " where _id = ?", new String[]{decode(a.ref) + ""});
                        if (c.moveToFirst()) {
                            final int p = c.getInt(c.getColumnIndex(PARENT));
                            final int id = c.getInt(0);
                            holder.ref.setText(getString(R.string.show_lyrics));
                            holder.ref.setVisibility(VISIBLE);
                            holder.ref.setOnClickListener(v -> {
                                try {
                                    Cursor c1 = lyDb.rawQuery("select * from [lx_" + encode(id) + "]", null);
                                    c1.close();
                                    c1 = lyDb.rawQuery("select * from " + TITLE + " where " + PARENT + "=?",
                                            new String[]{p + ""});
                                    mLyTabTitles = new ArrayList<>();
                                    mLyArte = new ArrayList<>();
                                    mLyIds = new ArrayList<>();
                                    if (c1.moveToFirst()) {
                                        do {
                                            mLyIds.add(encode(c1.getLong(0)));
                                            mLyTabTitles.add(decodeUrl(c1.getString(c1.getColumnIndex(TITLE))));
                                            mLyArte.add(decodeUrl(c1.getString(c1.getColumnIndex(ARTE))));
                                        } while (c1.moveToNext());
                                    }
                                    c1.close();
                                    if (fullScreen) onBackPressed();
                                    showingLyrics = true;
                                    mTabs.setTabs(mLyTabTitles, mLyIds.indexOf(encode(id)));
                                } catch (Exception e) {
                                    display("Preparing Lyrics", 500);
                                    fetchLyrics(p);
                                }
                            });
                        }
                        c.close();
                    }
                    view.setOnClickListener(v -> {
                        nowPlaying = a;
                        getQueue();
                        mServ.startPlaying();
                    });
                }
            }
            view.setOnLongClickListener(v -> {
                if (null != holder.menu) holder.menu.performClick();
                return true;
            });
            if (null == holder.menu) return;
            holder.menu.setVisibility(type == Lyrics | type == LyricsAuthor ? GONE : VISIBLE);
            final String[] finalOptions = options;
            final int[] finalIcons = icons;
            holder.menu.setOnClickListener(v -> {
                lMenu.setAdapter(new MAdapter(holder.id, type, finalOptions, finalIcons));
                listMenu.setVisibility(VISIBLE);
//                    TODO: remove following line
                listMInfo.setText(holder.title.getText());
                listMenu.setOnClickListener(v1 -> new AlertDialog.Builder(mContext).setTitle(holder.title.getText()).show());
                Hari.animate().alpha(0.6f).start();
                listMenu.animate().alpha(1).translationY(0).setDuration(200).start();
            });
        }

        public class MAdapter extends BaseAdapter {
            String[] options;
            int[] icons;
            String id;
            int type;

            MAdapter(String id, int type, String[] options, int[] icons) {
                lMenu.setNumColumns(options.length / 2);
                this.id = id;
                this.type = type;
                this.options = options;
                this.icons = icons;
            }

            @Override
            public int getCount() {
                return options.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.liz, parent, false);
                    convertView.setTag(holder = new ViewHolder(convertView));
                } else
                    holder = (ViewHolder) convertView.getTag();
                holder.name.setText(options[position]);
                if (options[position].contains("Remove From")) {
                    holder.name.animate().translationY(-10 * dp).start();
                    convertView.setEnabled(!mPlTitles.get(mTabs.mTabInd).matches("Favourites|Offline"));
                } else if (options[position].contains("Delete Playlist")) {
                    convertView.setEnabled(!id.matches("Favourites|Offline"));
                } else {
                    holder.name.animate().translationY(0).start();
                    convertView.setEnabled(true);
                }
                holder.name.animate().scaleX(1.4f).scaleY(1.4f).start();
                holder.logo.setImageResource(icons[position]);
                holder.logo.animate().scaleY(.55f).scaleX(icons[position] == R.drawable.reply ? -.55f : .55f).start();
                holder.logo.setColorFilter(Color.rgb(250, 250, 250));
                convertView.animate().scaleX(.9f).scaleY(.9f).start();
                convertView.setOnClickListener(v -> {
                    switch (options[position]) {
                        case "Play":
                            mServ.Queue.clear();
                        case "Enqueue":
                            if (type == Audio || type == PlAudio) {
                                mServ.Queue.add(id);
                                break;
                            }
                            id = id.matches("Favourites|Offline") || type == Alb ? id : ("[" + id + "]");
                            Cursor c = mAudioDb.rawQuery("select * from " + (type == Pl ? id : AUDIO_TABLE + " where " + ALB +
                                            " like '% " + id.replaceAll("'", "''") + " %' and " + LANG + " = ?") + mAudioSort,
                                    type == Pl ? null : new String[]{encode(mLang)});
                            if (c.moveToFirst()) do {
                                mServ.Queue.add(c.getString(0));
                            } while (c.moveToNext());
                            c.close();
                            if (options[position].equals("Play")) mServ.startPlaying();
                            break;
                        case "Remove From Playlist":
                            mAudioDb.delete("[" + mPlTitles.get(mTabs.mTabInd) + "]", "_id=?", new String[]{id});
                            setTab(mTabs.mTabInd);
                            break;
                        case "Add To":
                            Cursor c1 = mAudioDb.rawQuery("select * from playlist", null);
                            PopupMenu p = new PopupMenu(mContext, v);
                            if (c1.moveToFirst()) do {
                                final String s = c1.getString(1);
                                if (!s.trim().toLowerCase().equals("offline"))
                                    p.getMenu().add(s);
                            } while (c1.moveToNext());
                            c1.close();
                            p.getMenu().add("Create New Playlist");
                            p.setOnMenuItemClickListener(item -> {
                                switch (type) {
                                    case Audio:
                                    case PlAudio:
                                        addToPlaylist(item.getTitle().toString(), new String[]{id});
                                        break;
                                    case Pl:
                                    case Alb:
                                        id = id.matches("Favourites") || type == Alb ? id : ("[" + id + "]");
                                        Cursor c2 = mAudioDb.rawQuery("select * from " + (type == Pl ? id : (AUDIO_TABLE + " where " + ALB +
                                                        " like '% " + id.replaceAll("'", "''") + " %' and " + LANG + " = ?")) + mAudioSort,
                                                type == Pl ? null : new String[]{encode(mLang)});
                                        String[] ids = new String[c2.getCount()];
                                        int i = 0;
                                        if (c2.moveToFirst()) do {
                                            ids[i++] = c2.getString(0);
                                        } while (c2.moveToNext());
                                        c2.close();
                                        addToPlaylist(item.getTitle().toString(), ids);
                                        break;
                                }
                                onBackPressed();
                                return true;
                            });
                            p.show();
                            break;
                        case "Share":
                            if (type == Audio || type == PlAudio) {
                                shareSong(id);
                            }
                            break;
                        case "Delete Playlist":
                            mAudioDb.execSQL("drop table if exists [" + id + "]");
                            mAudioDb.delete("playlist", "_id=?", new String[]{id});
                            setTab(mTabs.mTabInd);
                            break;
                        case "Play Next":
                            mServ.Queue.add(mServ.nowPlayingInd, id);
                            break;
                        case "Download All":
                        case "Ringtone":
                        case "Trim":
                        case "Delete":
                        case "Download":
                        case "Tag Lyrics":
                        case "Set Text":
                        case "Delete All Offline":
                        default:
                            display(getString(R.string.workingonit), 2000, promptToContribute, getString(R.string.contribute));
                    }
                    if (!options[position].equals("Add To")) onBackPressed();
                });
                return convertView;
            }

            class ViewHolder {
                TextView name;
                ImageView logo;

                ViewHolder(View v) {
                    name = v.findViewById(R.id.name);
                    logo = v.findViewById(R.id.logo);
                }
            }
        }

        @Override
        public Object[] getSections() {
            return alphabetIndexer.getSections();
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return alphabetIndexer.getPositionForSection(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return alphabetIndexer.getSectionForPosition(position);
        }

        class AlbHolder {
            TextView title, details;
            ImageView arte;
            TextView lang, ref;
            ImageButton like, menu, download;
            String id;
            int type = -1, l = -1;

            AlbHolder(View v) {
                title = v.findViewById(R.id.title);
                arte = v.findViewById(R.id.arte);
                details = v.findViewById(R.id.details);
                lang = v.findViewById(R.id.lang);
                ref = v.findViewById(R.id.ref);
                like = v.findViewById(R.id.like);
                menu = v.findViewById(R.id.list_menu);
                download = v.findViewById(R.id.download_);
            }
        }
    }

    public class QuoteAdapter extends BaseAdapter {
        QuoteAdapter() {
            mTList.setNumColumns(month < 0 || day <= 0 ? size.x / 170 / dp : 1);
            if (day > 0) {
                setToolBarArte(0);
                ViewGroup.LayoutParams p = mToolBar.getLayoutParams();
                p.height = 0;
                mToolBar.setLayoutParams(p);
                mToolBarArte.setLayoutParams(p);
                mActionButton.setVisibility(VISIBLE);
                mActionButton.setImageResource(R.drawable.apps);
            } else {
                mActionButton.setVisibility(GONE);
            }
        }

        @Override
        public int getCount() {
            return month < 0 ? 12 : day > 0 ? 1 : (month == 1 ? 28 : (month < 7 ? (month % 2 == 0 ? 31 : 30) : (month % 2 == 0 ? 30 : 31)));
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.liq, parent, false);
                convertView.setTag(holder = new ViewHolder(convertView));
            } else
                holder = (ViewHolder) convertView.getTag();
            convertView.setOnClickListener(v -> {
//                Toast.makeText(mContext, "Hari", Toast.LENGTH_LONG).show();
                if (month < 0) {
                    month = position;
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(months));
                    mTabs.setTabs(list, position);
                } else if (day < 1) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < getCount(); i++)
                        list.add((i + 1) + " " + months[month]);
                    day = position + 1;
                    mTabs.setTabs(list, position);
                }
            });
            if (month < 0) {
                holder.title.setText(months[position]);
                final File f = new File(spQuoteDir, months[position] + "_01");
                if (f.exists()) {
                    holder.arte.setImageURI(Uri.fromFile(f));
                } else {
                    holder.arte.setImageResource(R.drawable.sp);
                    new DownloadTask(rawBase + "srila-prabhupada-vani/q/master/" + months[position] + "_01.webp",
                            f, s -> notifyDataSetChanged()).execute("");
                }
            } else if (day <= 0) {
                int p = position + 1;
                holder.title.setText(p + " " + months[month]);
                final String qName = months[month] + "_" + (p > 9 ? p : "0" + p);
                final File f = new File(spQuoteDir, qName);
                if (f.exists()) {
                    holder.arte.setImageURI(Uri.fromFile(f));
                } else {
                    holder.arte.setImageResource(R.drawable.sp1);
                    new DownloadTask(rawBase + "srila-prabhupada-vani/q/master/" + qName + ".webp",
                            f, s -> {
                        if (f.exists()) holder.arte.setImageURI(Uri.fromFile(f));
                    }).execute("");
                }
            } else {
                convertView.setLayoutParams(new g.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size.y - notchH - tBarH - (notchH == 0 ? 0 : 42 * dp)));
                final File f = new File(spQuoteDir, months[month] + "_" + (day > 9 ? day : "0" + day));
                if (f.exists()) holder.arte.setImageURI(Uri.fromFile(f));
                holder.arte.setBackgroundColor(Color.parseColor("#f6e3e3"));
                convertView.setPadding(0, 0, 0, 0);
                holder.arte.setPadding(0, 0, 0, 0);
                holder.title.setVisibility(GONE);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView arte;
            TextView title;

            ViewHolder(View v) {
                arte = v.findViewById(R.id.arte);
                title = v.findViewById(R.id.title);
            }
        }
    }

    public void setToolBarArte(int h) {
        if (ytContainer.getVisibility() == VISIBLE) return;
        if (!prefs.expandToolBar || fullScreen || (showingSPQuotes && day > 0)) return;
        h = h < tBarH ? tBarH : Math.min(h, 3 * tBarH);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mToolBar.getLayoutParams();
        params.height = h;
        mToolBar.setLayoutParams(params);
        mToolBar.setBackgroundColor(Color.argb((3 * tBarH - h) * 255 / 2 / tBarH, 255, 87, 34));
        mStatusBar.setBackgroundColor(Color.argb((3 * tBarH - h) * 255 / 2 / tBarH, 255, 87, 34));
        mStatusBar.animate().alpha((3f * tBarH - h) / 2 / tBarH).setDuration(0).start();
        mTBtn.setBackgroundColor(Color.argb((3 * tBarH - h) * 255 / 2 / tBarH, 255, 87, 34));
        float scale = .25f * h / tBarH + .75f;
        mAppName.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
        mAppName.setX(40 * dp * (3 - 2 * scale) + (scale - 1) * (size.x - 150 * dp) / 2);
        mAppName.setY((1 - scale) * 50 * dp);
        int h1 = mTBtn.getMeasuredHeight();
        mToolBarArte.setLayoutParams(new RelativeLayout.LayoutParams(size.x, h + h1 + notchH));
        mInfo.setY(h + h1 - 15 * dp);
        if (h > 1.5 * tBarH) {
            mInfo.animate().scaleX(1.3f).scaleY(1.3f).alpha(1).setDuration(300).start();
        } else {
            mInfo.animate().scaleX(.05f).scaleY(.05f).alpha(0).setDuration(150).start();
        }
    }

    public void youTubeOnTouch() {
        new Handler().postDelayed(() -> {
            if (YouTubeWebView.canGoBack()) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) ytContainer.getLayoutParams();
                p.topMargin = -10 * dp;
                ytContainer.setLayoutParams(p);
            }
        }, 300);
    }

    View.OnTouchListener onVolumeTouchListener = new View.OnTouchListener() {
        float downRowY;
        int streamVolume, songVolume;
        boolean setSongVolume = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            setSongVolume = (view.getId() == R.id.songVolume);
            int action = motionEvent.getAction();
            percent.animate().alpha(0.8f).setDuration(0).start();
            mVolumeControls.animate().alpha(1).setDuration(0).start();
            if (action == MotionEvent.ACTION_DOWN) {
                downRowY = motionEvent.getRawY();
                streamVolume = (int) (100f * mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxStreamVolume);
                if (setSongVolume && nowPlaying != null) {
                    songVolume = nowPlaying.volume;
                    percent.setText(getString(R.string.song_volume, songVolume));
                } else
                    percent.setText(getString(R.string.device_volume, streamVolume));
            } else if (action == MotionEvent.ACTION_MOVE) {
                int v = (int) (-6 * (motionEvent.getRawY() - downRowY) / dp / 10);
                if (setSongVolume && nowPlaying != null) {
                    v += songVolume;
                    v = v > 100 ? 100 : Math.max(v, 0);
                    percent.setText(getString(R.string.song_volume, v));
                    float v1 = (float) (1 - (Math.log(MAX_VOLUME - v) / Math.log(MAX_VOLUME)));
                    if (mPlayer != null)
                        mPlayer.setVolume(v1, v1);
                    nowPlaying.volume = v;
                } else if (!setSongVolume) {
                    v += streamVolume;
                    v = v > 100 ? 100 : Math.max(v, 0);
                    percent.setText(getString(R.string.device_volume, v));
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (v * maxStreamVolume / 100f), 0);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                percent.animate().alpha(0).setDuration(500).start();
                mVolumeControls.animate().setStartDelay(300).setDuration(500).alpha(.4f).start();
            }
            return true;
        }
    };
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        boolean pressed = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int direction = view.getId() == R.id.forward ? 1 : -1;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!pressed)
                    mPlayer.seekTo(mPlayer.getCurrentPosition() + direction * 10000);
                pressed = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayer.seekTo(mPlayer.getCurrentPosition() + direction * 1000);
                        if (pressed)
                            new Handler().postDelayed(this, 50);
                    }
                });
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                pressed = false;
            return true;
        }
    };
    View.OnTouchListener fab = new View.OnTouchListener() {
        float downRawX, downRawY, dX, dY;

        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                view.animate().alpha(1).setStartDelay(0).setDuration(0).start();
                dX = (downRawX = motionEvent.getRawX()) - view.getX();
                dY = (downRawY = motionEvent.getRawY()) - view.getY();
            } else if (action == MotionEvent.ACTION_MOVE) {
                view.setX(motionEvent.getRawX() - dX);
                view.setY(motionEvent.getRawY() - dY);
            } else if (action == MotionEvent.ACTION_UP && view.getId() != R.id.iq) {
                if (Math.abs(motionEvent.getRawX() - downRawX) < dp * 3 && Math.abs(motionEvent.getRawY() - downRawY) < dp * 3)
                    view.performClick();
                else {
                    float x = view.getX(), y = view.getY();
                    float x1 = x < size.x / 2f ? dp * 5 : size.x - 70 * dp,
                            y1 = y > size.y - dp * 120 ? size.y - dp * 120 : y < dp * 100 ? dp * 100 : y;
                    view.animate().translationXBy(x1 - x).translationYBy(y1 - y).setDuration(200).start();
                    hideQuickBall(view);
                }
            }
            if (view.getId() == R.id.iq) {
                quickBall.setX(view.getX() + 43 * dp);
                quickBall.setY(view.getY() + 43 * dp);
            }
            return true;
        }
    };

    public void hideQuickBall(final View view) {
        view.animate().alpha(.7f).setStartDelay(500).setDuration(300).start();
        view.animate().setStartDelay(0);
        new Handler().postDelayed(() -> {
            float x = view.getX(), x1 = x < size.x / 2f ? -48 * dp : size.x - 15 * dp;
            if (view.getAlpha() != 1 && q.getVisibility() != VISIBLE)
                view.animate().translationXBy(x1 - x).setDuration(300).start();
        }, 2000);
    }

//    public void showVedabase(String url) {
//        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(vedaBaseUrl + url)));
//        /*File f = getFileFromUrl(url);
//        if((prefs.offlineVedabase == 1 || !mServ.isNetworkAvailable()) && f.exists())
//            VedabaseWebView.loadUrl("file:///" + f.getAbsolutePath());
//        else
//            VedabaseWebView.loadUrl(vedaBaseUrl+url);
//        vedabaseContainer.setVisibility(VISIBLE);
//        NotesWebView.setVisibility(View.GONE);*/
//    }

    ValueAnimator animator;

    public void display(final String s, final int duration) {
        runOnUiThread(() -> display(s, duration, null, null));
    }

    public void display(String s, int duration, View.OnClickListener clickListener, String btnText) {
        if (mToast == null) {
            mToastContainer = findViewById(R.id.toast);
            mToast = mToastContainer.findViewById(R.id.toast_text);
            mToastButton = mToastContainer.findViewById(R.id.toast_button);
        }
        mToast.setText(s);
        mToastContainer.animate().alpha(1).start();
        mToastContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (clickListener == null) {
            mToastButton.setVisibility(GONE);
        } else {
            mToastButton.setText(btnText);
            mToastButton.setOnClickListener(clickListener);
            mToastButton.setVisibility(VISIBLE);
        }
        if (animator != null) animator.cancel();
        if (duration <= 0) return;
        mToastContainer.postDelayed(this::hideDisplay, duration);
    }

    public void hideDisplay() {
        final int h = mToastContainer.getMeasuredHeight();
        final ViewGroup.LayoutParams p = mToastContainer.getLayoutParams();
        animator = new ValueAnimator();
        animator.setIntValues(0, 800);
        animator.setDuration(800);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int v = (int) animation.getAnimatedValue();
            if (v > 300) {
                float scale = 1 - (v - 300) / 500f;
                p.height = (int) (scale * h);
                mToastContainer.animate().alpha(scale).setDuration(0).start();
                mToastContainer.setLayoutParams(p);
            }
        });
        animator.start();
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(k.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    View.OnClickListener promptToContribute = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(mContext).setTitle(R.string.thank_you)
                    .setMessage("I heartily thank you for your kind gesture.\n\nYour appreciation and prayers are very important for me.")
                    .setPositiveButton("Contact Me", (dialog, which) -> startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mayank.srmu@gmail.com", null))
                            .putExtra(Intent.EXTRA_SUBJECT, "Subject: I want to Contribute -- (" + appName + "App)")
                            .putExtra(Intent.EXTRA_TEXT, "Thanks for approaching us.\n\nPlease do not change the subject line " +
                                    "(PhonePay 8127987043 in case you like to financially contribute. You can also make donation for ISKCON Rajkot, ISKCON GEV, ISKCON Chowpatti, " +
                                    "ISKCON Desire Tree, ISKCON Mayapur, Vedabase.io through their website)\n---\n"))).setNeutralButton("Pray/Bless", (dialog, which) -> startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mayank.srmu@gmail.com", null))
                    .putExtra(Intent.EXTRA_SUBJECT, "Subject: I am praying for/blessing you -- (" + appName + "App)")
                    .putExtra(Intent.EXTRA_TEXT, "Thanks for approaching us.\nWe are glad to hear from you.\n\nPlease do not change the subject line\n---\n"))).setNegativeButton("Get me Hired", (dialog, which) -> startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mayank.srmu@gmail.com", null))
                    .putExtra(Intent.EXTRA_SUBJECT, "Subject: I want to Hire you/get you good job -- (" + appName + "App)")
                    .putExtra(Intent.EXTRA_TEXT, "Thanks for approaching us.\nWe are glad to hear from you.\n\nPlease do not change the subject line\n---\n"))).show();
        }
    };

    static class Audio {
        String arte, title, alb, id, parent, url, datePlace, ref, app, mP;
        boolean fav, offline;
        int volume = 100, lang;
        long size;

        Audio() {
        }

        Audio(Cursor c) {
            app = shortName;
            arte = c.getString(c.getColumnIndex(ARTE));
            title = c.getString(c.getColumnIndex(TITLE));
            alb = c.getString(c.getColumnIndex(ALB));
            parent = decode(c.getString(c.getColumnIndex(PARENT))) + "";
            url = encodeUrl(c.getString(c.getColumnIndex(URL)));
            id = encode(c.getLong(0));
            fav = c.getInt(c.getColumnIndex(FAVOURITE)) == 1;
            offline = c.getInt(c.getColumnIndex(OFFLINE)) == 1;
            String place = c.getString(c.getColumnIndex(PLACE));
            datePlace = c.getString(c.getColumnIndex(DATE)) + " " + place;
            ref = encodeUrl(c.getString(c.getColumnIndex(REF))).trim();
            ref = ref.substring(0, ref.contains("..") ? ref.indexOf("..") : ref.length());
            lang = (int) decode(c.getString(c.getColumnIndex(LANG)));
            volume = c.getInt(c.getColumnIndex(VOLUME));
            volume = volume == 0 ? 100 : volume;
            size = c.getInt(c.getColumnIndex(SIZE));
            mP = mParent;
        }

        String getUrl() {
            Cursor c = mDb.rawQuery("select * from " + ALBUM + " where _id = ?", new String[]{parent});
            if (c.moveToFirst()) {
                int i = (int) decode(c.getString(c.getColumnIndex(URL_REP_ID))) - 1;
                url = url.replaceAll("#", i < 0 ? "" : replacement[i]);
            }
            c.close();
            if (url.toLowerCase().endsWith(".wma"))
                return rawBase + git.replace(".github.io", "") + "/karnamrita/master/" + id + ".mp3";
            else return getUrl(parent) + "/" + url;
        }

        String getUrl(String p) {
            Cursor c = mDb.rawQuery("select * from " + ALBUM + " where _id = ?", new String[]{p});
            if (c.moveToFirst()) {
                String url = encodeUrl(c.getString(c.getColumnIndex(URL)));
                url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
                String parent = decode(c.getString(c.getColumnIndex(PARENT))) + "";
                c.close();
                c = mDb.rawQuery("select * from " + ALBUM + " where _id = ?", new String[]{parent});
                if (c.moveToFirst()) {
                    int i = (int) decode(c.getString(c.getColumnIndex(URL_REP_ID))) - 1;
                    if (i >= 0) url = url.replaceAll("#", replacement[i]);
                }
                c.close();
                if (url.startsWith("http")) {
                    url = url.replace("http://", "https://");
                    return url;
                } else {
                    return getUrl(parent) + "/" + url;
                }
            }
            return null;
        }
    }

    public static int runningDownloadTasks = 0;

    public static class AudioDownloadTask extends AsyncTask<String, String, String> {
        boolean startPlaying;
        Audio a;
        @SuppressLint("StaticFieldLeak")
        k mContext;
        ProgressDialog p;

        AudioDownloadTask(k context, boolean startPlaying) {
            this.startPlaying = startPlaying;
            mContext = context;
        }

        @SuppressLint("WakelockTimeout")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mWakeLock.isHeld()) mWakeLock.acquire();
            runningDownloadTasks++;
            p = new ProgressDialog(mContext);
            p.setCancelable(false);
            p.setIndeterminate(true);
            p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            p.show();
        }

        @Override
        protected String doInBackground(String... audioId) {
            Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id=?", new String[]{audioId[0]});
            if (c.moveToFirst()) {
                a = new Audio(c);
                File f = new File(audioDir, a.id), f1 = null;
                String u = a.getUrl().replaceAll("&amp;", "%26");
                mContext.runOnUiThread(() -> Toast.makeText(mContext, u, Toast.LENGTH_LONG).show());
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                if (f.exists() && f.length() > .95 * a.size) return null;
                else if (f.exists()) {
                    startPlaying = false;
                    f1 = f;
                    f = new File(f.getAbsolutePath() + ".temp");
                }
                try {
                    java.net.URL url = new URL(u);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    input = connection.getInputStream();
                    output = new FileOutputStream(f);

                    byte[] data = new byte[4098];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        publishProgress();
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null) output.close();
                        if (input != null) input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
                if (f1 != null && f.length() > f1.length()) f.renameTo(f1);
                if (f.length() > .95 * a.size) {
                    ContentValues values = new ContentValues();
                    values.put(OFFLINE, "1");
                    mAudioDb.update(AUDIO_TABLE, values, "_id=?", new String[]{c.getString(0)});
                    values = new ContentValues();
                    values.put("_id", "Offline");
                    values.put(TITLE, "Offline");
                    Cursor c1 = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + OFFLINE + " = ?", new String[]{"1"});
                    values.put(AUDIOS, c1.getCount());
                    c1.close();
                    mAudioDb.insertWithOnConflict("playlist", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (startPlaying) {
                nowPlaying = a;
                mServ.startPlaying();
                startPlaying = false;
            }
            p.dismiss();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            p.dismiss();
            if (mPlayer != null && mPlayer.isPrepared && mPlayer.isPlaying())
                mPlayer.onCompletion(mPlayer);
        }
    }

    public static class DownloadTask extends AsyncTask<String, String, String> {
        String mUrl;
        File mFile;
        PostExecuteListener mPost;

        DownloadTask(String url, File file, PostExecuteListener postExecuteListener) {
            mUrl = url;
            mFile = file;
            mPost = postExecuteListener;
        }

        @SuppressLint("WakelockTimeout")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mWakeLock.isHeld()) mWakeLock.acquire();
            runningDownloadTasks++;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runningDownloadTasks--;
            if (mPost != null) {
                mPost.postExecute(s);
            }
            if (mWakeLock.isHeld() && runningDownloadTasks <= 0) mWakeLock.release();
        }

        @Override
        protected String doInBackground(String... strings) {
            return download(mFile, mUrl);
        }
    }

    interface PostExecuteListener {
        void postExecute(String s);
    }

    public static String download(File mFile, String mUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        if (mFile.exists()) return null;
        try {
            java.net.URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            input = connection.getInputStream();
            output = new FileOutputStream(mFile);

            byte[] data = new byte[4098];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    static String SUBALBS = "sx";
    static String AUDIOS = "xy";
    static String LANG = "lx";
    static String URL_REP_ID = "_x";
    static String LU = "xu";
    static String ALBUM = "xs";
    static String TITLE = "t";
    static String PARENT = "p";
    static String URL = "z";
    static String ARTE = "a";
    static String DATE = "x";
    static String PLACE = "pz";
    static String SIZE = "bz";
    static String SIZE1 = "cz";
    static String ALB = "wx";
    static String REF = "zx";
    static String FAVOURITE = "f";
    static String DISPLAY = "d";
    static String OFFLINE = "o";
    static String VOLUME = "pl";
    static String AUDIO_TABLE = "v";
    static String VIDEO_URL = "de";

    private void createDb(String dbPath, String sName) {
        File sDir = new File(baseDir + "/" + sName + "/s");
        downloadSpChant();
        SQLiteDatabase mDb = (new SQLHelper(mContext, dbPath)).getWritableDatabase();
        SQLiteDatabase mAudioDb = (new SQLHelperA(mContext, dbPath)).getWritableDatabase();
        BufferedReader reader;
        try {
            String line;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sDir, "p"))));
            String[] places = reader.readLine().split("~");
            for (int i = 0; i < places.length; i++) places[i] = decodeUrl(places[i]);
            reader.close();

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sDir, "a"))));
            String args[] = {TITLE, PARENT, URL, ARTE, DATE, SIZE, REF, PLACE, ALB};
            while ((line = reader.readLine()) != null) {
                String vals[] = line.replaceAll("~", " ~ ").split("~");
                ContentValues values = new ContentValues();
                int[] inds = {1, 2, 3, 6};
                for (int i : inds)
                    values.put(args[i], vals[i].trim());
                values.put(args[0], decodeUrl(vals[0].trim()));
                values.put(ALB, " " + decodeUrl(vals[args.length - 1].trim()) + " ");
                values.put(DATE, encodeUrl(vals[4].trim()));
                values.put(SIZE, decode(vals[5].trim()));
                int p = (int) decode(vals[args.length - 2].trim());
                values.put(args[args.length - 2], p <= 0 ? "" : places[p - 1]);
                mAudioDb.insert(AUDIO_TABLE, null, values);
                display("Preparing Database...\n\n" + vals[0], -1);
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sDir, "m"))));
            args = new String[]{TITLE, ARTE, PARENT/*, SUBALBS, AUDIOS*/};
            while ((line = reader.readLine()) != null) {
                String vals[] = line.replaceAll("~", " ~ ").split("~");
                ContentValues values = new ContentValues();
                values.put(args[0], decodeUrl(vals[0].trim()));
                for (int i = 1; i < args.length; i++)
                    values.put(args[i], vals[i].trim());
                mDb.insert(ALB, null, values);
                display("Preparing Database...\n\n" + vals[0], -1);
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sDir, "b"))));
            args = new String[]{PARENT, URL, LU, URL_REP_ID, LANG};
            while ((line = reader.readLine()) != null) {
                String vals[] = line.replaceAll("~", " ~ ").split("~");
                ContentValues values = new ContentValues();
                for (int i = 0; i < args.length; i++)
                    values.put(args[i], vals[i].trim());
                long id = mDb.insert(ALBUM, null, values);
                Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + PARENT + " = ? ", new String[]{encode(id)});
                if (c.moveToFirst()) {
                    ContentValues values1 = new ContentValues();
                    values1.put(LANG, vals[4].trim());
                    do {
                        mAudioDb.update(AUDIO_TABLE, values1, "_id = ?", new String[]{c.getString(0)});
                    } while (c.moveToNext());
                }
                c.close();
                display("Preparing Database...\n\n" + vals[1], -1);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//            set audio counts
        Cursor c = mDb.rawQuery("select * from " + ALB, null);
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(0);
                ContentValues values = new ContentValues();
                Cursor cursor = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB + " like '% " + encode(id).replaceAll("'", "''") + " %'", null);
                int audios = cursor.getCount();
                if (audios <= 0) {
                    mDb.delete(ALB, "_id = ?", new String[]{id + ""});
                } else {
                    values.put(AUDIOS, audios);
                    cursor.close();
                    cursor = mDb.rawQuery("select * from " + ALB + " where " + PARENT + " = ?", new String[]{encode(id)});
                    values.put(SUBALBS, encode(cursor.getCount()));
                    cursor.close();
                    mDb.update(ALB, values, "_id = ?", new String[]{id + ""});
                    display("Counting ... \n" + decodeUrl(c.getString(1)), -1);
                }
            } while (c.moveToNext());
        }
        c.close();
//        set lang based tables
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sDir, "l"))));
            String line = reader.readLine();
            reader.close();
            String[] temp = line.split("~");
            for (int i = 0; i < temp.length; i++) {
                String[] t = temp[i].replaceAll(";", " ; ").split(";");
                switch (i) {
                    case 0:
                        t[0] = "Vaiṣṇava Bhajans";
                        break;
                    case 1:
                        t[0] = "Hare Kṛṣṇa Dhun";
                        break;
                    default:
                        t[0] = decodeUrl(t[0].trim());
                        if (t[0].toLowerCase().contains("translation"))
                            t[0] = "Lectures with " + t[0];
                        else if (!t[0].toLowerCase().contains("ringtone"))
                            t[0] = t[0] + " Lectures";
                }
                Log.e("t[0] = ", t[0]);
                mDb.execSQL("create table hk" + i + " (_id integer primary key, " + TITLE + ", " + ARTE + ", " +
                        PARENT + " default '', " + SUBALBS + " integer default 0, " + AUDIOS + " integer default 0)");
                c = mDb.rawQuery("select * from " + ALB, null);
                if (c.moveToFirst()) {
                    do {
                        long id = c.getLong(0);
                        Log.e("counting 00 ...", c.getString(1));
                        Cursor cursor = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB + " like '% " +
                                encode(id).replaceAll("'", "''") + " %' and " + LANG + " = ?", new String[]{encode(i)});
                        int audios = cursor.getCount();
                        cursor.close();

                        if (audios > 0) {
                            ContentValues values = new ContentValues();
                            String[] args = new String[]{TITLE, ARTE, PARENT/*, SUBALBS, AUDIOS*/};
                            for (String a : args)
                                values.put(a, c.getString(c.getColumnIndex(a)));
                            values.put("_id", id);
                            values.put(AUDIOS, audios);
                            mDb.insert("hk" + i, null, values);
                            display("Counting ... \n" + c.getString(1), -1);
                            Log.e("counting...", c.getString(1));
                            String parent = c.getString(c.getColumnIndex(PARENT));
                            while (!parent.isEmpty()) {
                                Log.e("caught in loop", "--3963--" + parent + ":" + decode(parent));
                                cursor = mDb.rawQuery("select * from hk" + i + " where _id = ?", new String[]{decode(parent) + ""});
                                if (cursor.moveToFirst()) {
                                    parent = cursor.getString(cursor.getColumnIndex(PARENT));
                                    Log.e("--here???---", parent);
                                    cursor.close();
                                } else {
                                    cursor.close();
                                    cursor = mDb.rawQuery("select * from " + ALB + " where _id = ?", new String[]{decode(parent) + ""});
                                    Log.e("ooo--here???---", parent);
                                    if (cursor.moveToFirst()) {
                                        Log.e("counting 2 --00-- ...", cursor.getString(1));
                                        values = new ContentValues();
                                        args = new String[]{TITLE, ARTE, PARENT/*, SUBALBS, AUDIOS*/};
                                        for (String a : args)
                                            values.put(a, cursor.getString(cursor.getColumnIndex(a)));
                                        id = cursor.getLong(0);
                                        values.put("_id", id);
                                        display("Counting ... \n" + cursor.getString(1), -1);
                                        Log.e("counting 2 ...", cursor.getString(1));
                                        parent = cursor.getString(cursor.getColumnIndex(PARENT));
                                        cursor.close();

                                        cursor = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + ALB + " like '% " +
                                                encode(id).replaceAll("'", "''") + " %' and " + LANG + " = ?", new String[]{encode(i)});
                                        audios = cursor.getCount();
                                        cursor.close();
                                        values.put(AUDIOS, audios);
                                        mDb.insert("hk" + i, null, values);
                                    }
                                    else {
                                        cursor.close();
                                        parent = "";
                                    }
                                }
                            }
                        }
                    } while (c.moveToNext());
                }
                c.close();
                c = mDb.rawQuery("select * from hk" + i, null);
                if (c.moveToFirst()) {
                    do {
                        ContentValues values = new ContentValues();
                        Cursor cursor = mDb.rawQuery("select * from hk" + i + " where " + PARENT + "=?", new String[]{encode(c.getLong(0))});
                        values.put(SUBALBS, encode(cursor.getCount()));
                        cursor.close();
                        mDb.update("hk" + i, values, "_id = ?", new String[]{c.getString(0)});
                    } while (c.moveToNext());
                }
                c.close();

                ContentValues values = new ContentValues();
                values.put("_id", i);
                values.put(TITLE, t[0]);
                values.put(PARENT, "");
                c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where " + LANG + "=?", new String[]{encode(i)});
                int audios = c.getCount();
                values.put(AUDIOS, audios);
                if (c.moveToFirst())
                    values.put(ARTE, c.getString(c.getColumnIndex(ARTE)));
                c.close();
                c = mDb.rawQuery("select * from hk" + i, null);
                int albs = c.getCount();
                values.put(SUBALBS, encode(i < 2 && albs < 2 ? 0 : albs));
                c.close();
                if (audios > 0)
                    mDb.insert(LANG, null, values);
                Log.e("2nd -- t[0] = ", t[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        display(getString(R.string.mahamantra), 1500);
        setFeatureDone("db_" + sName);
    }

    private void readFiles() {
        File sourceDir = new File(baseDir + "/" + shortName + "/s");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceDir, "i"))));
            String s[] = reader.readLine().replaceAll("~", " ~ ").split("~");
            findViewById(R.id.web).setVisibility((infoUrl = decodeUrl(s[0].trim())).isEmpty() ? GONE : VISIBLE);
            baseUrl = decodeUrl(decodeUrl(s[1].trim()));
            findViewById(R.id.fb).setVisibility((fb = decodeUrl(s[2].trim())).isEmpty() ? GONE : VISIBLE);
            findViewById(R.id.yt).setVisibility((yt = decodeUrl(s[3].trim())).isEmpty() ? GONE : VISIBLE);
            ytBase = decodeUrl(s[4].trim());
            findViewById(R.id.in).setVisibility((linkedIn = decodeUrl(s[5].trim())).isEmpty() ? GONE : VISIBLE);
            findViewById(R.id.twitter).setVisibility((twit = decodeUrl(s[6].trim())).isEmpty() ? GONE : VISIBLE);
            reader.close();

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceDir, "h"))));
            replacement = reader.readLine().split("~");
            for (int i = 0; i < replacement.length; i++) replacement[i] = decodeUrl(replacement[i]);
            reader.close();

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceDir, "l"))));
            String temp[] = reader.readLine().split("~");
            lang = new String[temp.length][2];
            for (int i = 0; i < temp.length; i++) {
                String t[] = temp[i].replaceAll(";", " ; ").split(";");
                lang[i][0] = decodeUrl(t[0].trim());
                lang[i][1] = decodeUrl(t[1].trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    View.OnTouchListener zoom = new View.OnTouchListener() {
        // We can be in one of these 3 states
        static final int NONE = 0;
        static final int DRAG = 1;
        static final int ZOOM = 2;
        int mode = NONE;

        // Remember some things for zooming
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;
        long t = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
//            dumpEvent(event);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    if (new Date().getTime() - t < 300) setZoomToCenter();
                    t = new Date().getTime();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.sqrt(Math.pow(start.x - event.getX(), 2) + Math.pow(start.y - event.getY(), 2)) < 20 * dp)
                        view.setVisibility(GONE);
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                - start.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }
            view.setImageMatrix(matrix);
            return true;
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    };

    public void shareSong(String id) {
        String pos = "";
        Audio a = null;
        if (id == null || id.isEmpty()) {
            if (nowPlaying == null) return;
            a = nowPlaying;
            if (mPlayer != null) pos = encode(mPlayer.getCurrentPosition());
        } else {
            Cursor c = mAudioDb.rawQuery("select * from " + AUDIO_TABLE + " where _id=?", new String[]{decode(id) + ""});
            if (c.moveToFirst()) {
                a = new Audio(c);
            }
            c.close();
        }
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        String alb = a.mP;
        id = a.id;
        if (!prefs.songSharingSign.contains("@songUrl"))
            prefs.songSharingSign += "\n\n@songUrl";
        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").
                putExtra(Intent.EXTRA_TEXT, prefs.songSharingSign.replace("@songUrl",
                        "https://" + git + "/?l=" + encode(a.lang) + (alb != null && !alb.isEmpty() ? "&q=" + alb : "") + "&a=" + id + "&pos=" + pos) + " "), "Share Link"));
    }

    File getFileFromUrl(String u) {
        u = u.replace("https://vedabase.io/", "");
        u = u.replace("library/", "");
        if (u.isEmpty()) u = "base/";
        u = u.replaceAll(File.separator, "_").substring(0, u.length() - 1) + ".mht";
        return new File(vbDir, u);
    }

    public void showVedabase(String url) {
        File f = getFileFromUrl(url);
        vedabaseContainer.setVisibility(VISIBLE);
        VedabaseWebView.setVisibility(VISIBLE);
        NotesWebView.setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fab_notes)).setImageResource(R.drawable.note);
        if ((prefs.offlineVedabase == 1 || !isNetworkAvailable(mContext)) && f.exists())
            VedabaseWebView.loadUrl("file:///" + f.getAbsolutePath());
        else
            VedabaseWebView.loadUrl(vedaBaseUrl + url);
        mD.collapse();
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams();
        if (p.topMargin == 0) mStatusBar.animate().alpha(0).start();
    }

    public static boolean err = false;

    @SuppressLint("SetJavaScriptEnabled")
    public CustomWebView initWebView(int id, int toolBarId, boolean zoom) {
        final CustomWebView webView = findViewById(id);
        final View mToolbar = findViewById(toolBarId);
        final View rootView = (View) findViewById(id).getParent();
        webView.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAppCachePath(getBaseContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setCacheMode( WebSettings.LOAD_DEFAULT );
        webSettings.setBuiltInZoomControls(zoom);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            @Override
            public void onHideCustomView() {
                ((FrameLayout) getWindow().getDecorView()).removeView(mCustomView);
                mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }

            @Override
            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
                if (mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                mCustomView = paramView;
                mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();
                mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                mCustomView.setBackgroundColor(Color.BLACK);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.setVisibility(View.GONE);
                if (url.startsWith("https://plus") || url.startsWith("https://play") || url.startsWith("market://") || url.startsWith("vnd:youtube") ||
                        url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("sms:") || url.startsWith("twitter") || url.contains("facebook")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if (view.getId() == R.id.veda_web_view && (prefs.offlineVedabase == 1 || !isNetworkAvailable(mContext))) {
                    File f = getFileFromUrl(url);
                    if (f.exists()) {
                        view.loadUrl("file:///" + f.getAbsolutePath());
                        return true;
                    }
                }
                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                err = true;
                TextView myTextView = rootView.findViewById(R.id.textView2);
                myTextView.setText(getString(R.string.connect_error));
            }

            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
                if (!err) {
                    if (view.getId() == R.id.veda_web_view && !url.endsWith(".mht") && !url.contains("#")) {
                        File f = getFileFromUrl(url);
                        if (!f.exists()) {
                            webView.saveWebArchive(f.getAbsolutePath());
//                            offlineVBFiles.add(f.getName());
//                            vbAdapter.notifyDataSetChanged();
                        }
                    }
                    webView.setVisibility(VISIBLE);
                }
            }
        });
        webView.setGestureDetector(new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
                else {
                    try {
                        if (e1.getY() - e2.getY() > 20) {
                            mToolbar.setVisibility(View.GONE);
                            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                            p1.topMargin = 0;
                            webView.setLayoutParams(p1);
                            webView.invalidate();
                            return false;
                        } else if (e2.getY() - e1.getY() > 20) {
                            mToolbar.setVisibility(VISIBLE);
                            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                            if (((ViewGroup.MarginLayoutParams) vedabaseContainer.getLayoutParams()).topMargin != 0)
                                p1.topMargin = dp * 10;
                            webView.setLayoutParams(p1);
                            webView.invalidate();
//                            findViewById(id).
                            /*if (e1.getY() < 500 && e2.getY() - e1.getY() > 400 ) {
                                myWebView.reload();
                            }*/
                            return false;
                        }

                    } catch (Exception e) {
                        webView.invalidate();
                    }
                    return false;
                }
            }
        }));
        return webView;
    }
}
