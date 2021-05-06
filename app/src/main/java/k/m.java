package k;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Objects;

import k.ui.g;

import static android.view.View.GONE;
import static k.helper.BOTTOM;
import static k.helper.FeatureId;
import static k.helper.decode;
import static k.helper.deleteDir;
import static k.helper.encode;
import static k.helper.encodeUrl;
import static k.helper.shouldShow;
import static k.helper.showSwipeTip;
import static k.helper.showTip;
import static k.k.ARTE;
import static k.k.PARENT;
import static k.k.TITLE;
import static k.k.URL;
import static k.k.arteDir;
import static k.k.download;
import static k.k.dp;
import static k.k.logoDir;
import static k.k.lyDir;
import static k.k.publicBaseDir;
import static k.k.rawBase;
import static k.k.size;
import static k.k.spQuoteDir;
import static k.k.vbDir;

public class m extends Activity implements ds.CallBacks {
    static String[] apps = {"sp", "rns", "rgs", "lks", "bsrm", "mvg", "bcs", "bnds", "bps", "bsds", "ds", "gkg",
            "rps", "kkp", "dnp"},
            appNames = {"A C Bhakti Vedānta Swāmi", "Rādhānāth Swāmi", "Rādhā Govind Dās Goswāmi Mahārāj", "Lokanāth Swāmi", "Bhakti Rasāmrita Swāmi", "Mahavishnu Goswāmi",
                    "Bhakti Chāru Swāmi", "Badarinārāyana Dās Goswāmi", "Bhakti Prema Swāmi", "Bhakti Swarup Dāmodara Swāmi",
                    "Devāmrita Swāmi", "Gopāl Krishna Goswāmi", "Romapāda Swāmi", "Kundal Krishna Prabhu", "Devakinandan Prabhu"},
            appGit, appPrefixs = {"His Divine Grace", "", "", "", "", "", "", "", "", "", "", "", "His Grace", "His Grace"},
            pkgs = {"com.mayank.srilaprabhupadavani", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    final static String animRawBase = "https://raw.githubusercontent.com/shravanotsava/shravanotsava/master/a";
    static File animDirLandScape, animDirPortrait, baseDir;
    int[] scenes = {132, 148, 504, 1079, 1585, 2614, 3799, 4109};
    static boolean launching = true, infoVisible = false;
    View title, btns, tu, closeTip;
    ImageView anim, bg;
    g grid;
    ViewGroup.MarginLayoutParams p;
    ValueAnimator animator, bgAnimator;
    int maxMargin;
    public static SQLiteDatabase AppTour, lyDb;
    public static boolean showTips = true, acceptTerms = false, restartTips = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appGit = new String[]{getString(R.string.sp), getString(R.string.rns),  getString(R.string.rgs), getString(R.string.lks),
                getString(R.string.bsrm), getString(R.string.mvg), getString(R.string.bcs), getString(R.string.bnds),
                getString(R.string.bps), getString(R.string.bsds), getString(R.string.ds), getString(R.string.gkg),
                getString(R.string.rps), getString(R.string.kkp), getString(R.string.dnp)};
        AppTour = new TourData("app_tour").getWritableDatabase();
        getFrames();
        setContentView(R.layout.h1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setViews();
    }

    private void getFrames() {
        k.dp = getResources().getDimensionPixelSize(R.dimen.dp1);
        new Thread(() -> {
            k.mWakeLock = ((PowerManager) Objects.requireNonNull(getSystemService(Context.POWER_SERVICE))).
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            baseDir = getExternalFilesDir("base");
            animDirLandScape = new File(baseDir + "/anim/l");
            if (!animDirLandScape.exists()) animDirLandScape.mkdirs();
//        animDirPortrait = new File(filesDir + "/anim/p");
//        if(!animDirPortrait.exists()) animDirPortrait.mkdirs();
            k.logoDir = new File(baseDir + "/apps");
            if (!logoDir.exists()) logoDir.mkdirs();
            download(new File(logoDir, "qr_" + BuildConfig.QR), "https://f-qr.github.io/r/3/" + BuildConfig.QR + ".webp");
            if (animDirLandScape.listFiles() == null || animDirLandScape.listFiles().length < 4100)
                ds.setUp(m.this);
        }).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews() {
        anim = findViewById(R.id.anim);
        bg = findViewById(R.id.bg);
        k.size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        ViewGroup.LayoutParams p = anim.getLayoutParams();
        if (size.y / size.x <= 9 / 16) {
            p.height = size.y;
            p.width = Math.min(p.height * 2, size.x);
        } else {
            p.width = size.x;
            p.height = Math.min(12 * p.width / 10, size.y);
        }
        anim.setLayoutParams(p);
        title = findViewById(R.id.title);
        tu = findViewById(R.id.tu);
        closeTip = findViewById(R.id.close_tip);
        btns = findViewById(R.id.btns);
        final View view = findViewById(R.id.r), v1 = findViewById(R.id.h);
        final int duration = 500;
        if (launching) {
            ValueAnimator animator = new ValueAnimator();
            animator.addUpdateListener(animation -> {
                int v = (int) animation.getAnimatedValue();
                if (v < 156) {
                    view.animate().scaleX(v / 150f).scaleY(v / 150f).alpha(v / 240f).setDuration(0).start();
                } else if (v < 550) {
                    view.animate().scaleX(1.04f + (v - 156) / 8000f).scaleY(1.04f + (v - 156) / 8000f).alpha(.65f + (v - 156) / 1200f).setDuration(0).start();
                } else if (v <= 680) {
                    view.animate().scaleX(1.09f + (v - 550) / 2200f).scaleY(1.09f + (v - 550) / 2200f).alpha(1 - (v - 550) / 130f).setDuration(0).start();
                    v1.animate().scaleX(1.1f - (v - 550) / 1300f).scaleY(1.1f - (v - 550) / 1300f).alpha(.05f + (v - 550) / 130f).setDuration(0).start();
                } else {
                    view.setVisibility(GONE);
                    v1.animate().scaleY(1).scaleX(1).alpha(1).setDuration(100).start();
                }
            });
            animator.setInterpolator(new LinearInterpolator());
            animator.setIntValues(0, 700);
            animator.setDuration(2500);
            animator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    startAnim();
//                    AppTour = new TourData().getWritableDatabase();
//                    AppTour.deleteDatabase(new File(AppTour.getPath()));
                    Cursor c = AppTour.rawQuery("select * from " + AppTourData + " where _id = ?", new String[]{AppIntro});
                    if (!c.moveToFirst() || c.getInt(1) != BuildConfig.VERSION_CODE || !checkPermissions(false)) {
                        showInfo();
                    }
                    c.close();
                    createDirs();
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
            launching = false;
        } else {
            startAnim();
            view.setVisibility(GONE);
            v1.animate().alpha(1).setDuration(duration).start();
            if (infoVisible) showInfo();
            else if (getPackageName().contains("shravanotsava"))
                showMTip();
        }
        handleIntent(getIntent());
        bg.setOnTouchListener(new View.OnTouchListener() {
            float prevY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float downY = event.getRawY();
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                    mToolBarListener.setToolBarHeight((int) (downY - prevY));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mToolBarListener.setTabHeaders((int) (downY - prevY), true);
                prevY = downY;
                return true;
            }
        });
    }

    private void showInfo() {
        infoVisible = true;
        Cursor c = AppTour.rawQuery("select * from " + AppTourData + " where _id = ?", new String[]{ShowTipsTag});
        if (c.moveToFirst()) showTips = c.getInt(1) == 1;
        c.close();
        c = AppTour.rawQuery("select * from " + AppTourData + " where _id = ?", new String[]{TermsTag});
        if (c.moveToFirst()) acceptTerms = c.getInt(1) == 1;
        c.close();
        c = AppTour.rawQuery("select * from " + AppTourData + " where _id = ?", new String[]{"swipe_to_hide"});
        if (c.moveToFirst()) findViewById(R.id.restartTips).setVisibility(View.VISIBLE);
        c.close();
        ((Switch) findViewById(R.id.screenTips)).setChecked(showTips);
        ((CheckBox) findViewById(R.id.accept_terms)).setChecked(acceptTerms);

        findViewById(R.id.up_indicator).setVisibility(GONE);
        ImageView bg = findViewById(R.id.bg), anim = findViewById(R.id.anim);
        int duration = 500;
        getWindowManager().getDefaultDisplay().getRealSize(size);
        final View v0 = findViewById(R.id.first_launch), v1 = findViewById(R.id.tag_line), v2 = findViewById(R.id.short_intro),
                v3 = findViewById(R.id.board), v4 = findViewById(R.id.tips), v5 = findViewById(R.id.permissions),
                v6 = findViewById(R.id.terms);
        ((TextView) findViewById(R.id.terms_)).setMovementMethod(LinkMovementMethod.getInstance());
        v1.animate().translationX(-size.x).setDuration(0).start();
        v2.animate().translationX(size.x).setDuration(0).start();
        v4.animate().translationY(size.y).setDuration(0).start();
        v5.animate().translationY(size.y).setDuration(0).start();
        v6.animate().translationY(size.y).setDuration(0).start();
        v3.animate().alpha(0).scaleY(.1f).scaleX(.1f).setDuration(0).start();
        firstLaunchAnim(duration);
        grid.setVisibility(GONE);
        btns.animate().alpha(0);
        bgAnimator.pause();
        bg.setImageResource(R.drawable.sp);
        anim.animate().alpha(0);
        v0.setVisibility(View.VISIBLE);
        v0.animate().alpha(1).setDuration(duration).start();
        v1.animate().translationX(0).setDuration(duration).start();
        v2.animate().translationX(0).setDuration(duration).start();
        v4.animate().translationY(0).setDuration(duration).start();
        v5.animate().translationY(0).setDuration(duration + 100).start();
        v6.animate().translationY(0).setDuration(duration + 100).start();
        v3.animate().alpha(1).scaleY(1).scaleX(1).setDuration(duration).start();
        findViewById(R.id.first_launch).animate().alpha(1).scaleX(1).scaleY(1).setDuration(300).start();
        if (checkPermissions(false)) {
            v5.setVisibility(GONE);
            findViewById(R.id.start).setEnabled(true);
        } else {
            findViewById(R.id.start).setEnabled(false);
            v5.setVisibility(View.VISIBLE);
        }
        setAcceptTerms();
    }

    private void firstLaunchAnim(int duration) {
        final ImageView v = findViewById(R.id.ic);
        final ViewGroup.LayoutParams p = v.getLayoutParams();
        v.setVisibility(View.VISIBLE);
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) title.getLayoutParams();
        final int h = duration < 500 ? -90 * dp / 5 : 0;
        ValueAnimator animator1 = new ValueAnimator();
        animator1.setInterpolator(new LinearInterpolator());
        if (duration < 500)
            animator1.setIntValues(90 * dp, 0);
        else
            animator1.setIntValues(0, 90 * dp);
        animator1.addUpdateListener(animation -> {
            p.height = (int) animation.getAnimatedValue();
            p.width = p.height;
            v.setLayoutParams(p);
            params.topMargin = h + p.height / 5;
            title.setLayoutParams(params);
            final float scale = p.height / 90f / dp;
            findViewById(R.id.welcome).animate().alpha(scale).scaleY(scale / 1.4f).setDuration(0).start();
        });
        animator1.setDuration(duration);
        animator1.start();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.first_launch).getVisibility() == View.VISIBLE && checkPermissions(false)) {
            if (acceptTerms)
                findViewById(R.id.start).performClick();
            else
                Toast.makeText(this, getString(R.string.accept_to_continue), Toast.LENGTH_LONG).show();
        } else if (findViewById(R.id.tips_view).getVisibility() == View.VISIBLE)
            closeTip.performClick();
        else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (bgAnimator != null) bgAnimator.pause();
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgAnimator != null) bgAnimator.resume();
        if (checkPermissions(false)) {
            findViewById(R.id.permissions).setVisibility(GONE);
            findViewById(R.id.start).setEnabled(true);
        } else if (grid != null)
            showInfo();
    }

    public void startAnim() {
        int n = animDirLandScape.listFiles() == null ? 0 : animDirLandScape.listFiles().length;
        for (int i = scenes.length - 1; i >= 0; i--) {
            if (n >= scenes[i]) {
                n = scenes[i];
                break;
            }
        }
        bgAnimator = new ValueAnimator();
        bgAnimator.setIntValues(1, n);
        bgAnimator.setInterpolator(new LinearInterpolator());
        bgAnimator.setDuration(n * 66);
        final int finalN = n;
        runOnUiThread(() -> {
            bgAnimator.addUpdateListener(animation -> {
                if (finalN < 132) return;
                int i = (int) animation.getAnimatedValue();
                File f = new File(animDirLandScape, "a" + i);
                if (f.exists()) {
                    Uri uri = Uri.fromFile(f);
                    anim.setImageURI(uri);
                    bg.setImageURI(uri);
                } else {
                    new k.DownloadTask(animRawBase + (i / 500) + "/f" + String.format("%04d", i) + ".webp",
                            new File(animDirLandScape, "a" + i), null).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "");
                }
            });
            bgAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            try {
                if (finalN > 0) bgAnimator.start();
            } catch (Exception ignored) {
            }
            grid = findViewById(R.id.list);
            if (getPackageName().contains("shravanotsava")) {
                grid.setNumColumns(size.x / dp / 100);
                grid.setAdapter(new Adapter());
                p = (ViewGroup.MarginLayoutParams) grid.getLayoutParams();
                animator = new ValueAnimator();
                title.post(() -> maxMargin = (int) (size.y - title.getMeasuredHeight() - title.getY() - (size.y - btns.getY())));
                grid.setToolBarListener(mToolBarListener);
            } else {
                findViewById(R.id.launchApp).setVisibility(View.VISIBLE);
//                String p = getPackageName();
//                for (int i1 = 0; i1 < apps.length; i1++) {
//                    if (p.contains(apps[i1])) {
//                        ((TextView) findViewById(R.id.title)).setText(appNames[i1]);
//                        break;
//                    }
//                }
            }
        });
    }

    g.ToolBarListener mToolBarListener = new g.ToolBarListener() {
        @Override
        public void setTabHeaders(int dh, boolean actionUp) {
            if (actionUp) {
                animator.cancel();
                int h = p.topMargin - prevH < 0 || p.topMargin < 10 * dp ? dp : maxMargin;
                setGridMargin(h);
            }
        }

        @Override
        public boolean setToolBarHeight(int dh) {
            int h = p.topMargin + dh;
            h = h < maxMargin ? h : maxMargin;
            p.topMargin = h;
            long tt = new Date().getTime();
            if (tt >= t + 200) {
                prevH = h;
                t = tt;
            }
            grid.setLayoutParams(p);
            float f = 1f * (maxMargin - h) / maxMargin;
            grid.animate().alpha(f + .01f).setDuration(0).start();
            title.animate().alpha(f + .02f).setDuration(0).start();
            tu.animate().alpha(f + .02f).setDuration(0).start();
            btns.animate().alpha(f + .2f).setDuration(0).start();
            grid.setBackgroundColor(Color.argb((int) ((1 - f) * 255), 230, 230, 230));
            return false;
        }
    };
    int prevH = 0;
    long t = 0;

    private void setGridMargin(int h) {
        animator.setDuration(200);
        animator.setIntValues(p.topMargin, h);
        animator.addUpdateListener(animation -> {
            Integer h1 = (Integer) animation.getAnimatedValue();
            p.topMargin = h1;
            grid.setLayoutParams(p);
            float f = 1f * (maxMargin - h1) / maxMargin;
            grid.animate().alpha(f + .05f).setDuration(0).start();
            title.animate().alpha(f + .2f).setDuration(0).start();
            tu.animate().alpha(f + .2f).setDuration(0).start();
            btns.animate().alpha(f + .5f).setDuration(0).start();
            grid.setBackgroundColor(Color.argb((int) ((1 - f) * 255), 230, 230, 230));
        });
        animator.start();
        findViewById(R.id.app_drawer).setVisibility(h == maxMargin ? View.VISIBLE : GONE);
        findViewById(R.id.up_indicator).animate().scaleY(h == maxMargin ? .5f : -.5f);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.launchApp) {
            String p = getPackageName();
            p = p.replace("radhanathswami", "rns").replace("lokanathswami", "lks");
            for (int i1 = 0; i1 < apps.length; i1++) {
                if (p.contains(apps[i1])) {
                    launchApp(i1, null);
                    break;
                }
            }
        } else if (i == R.id.app_drawer) {
            setGridMargin(dp);
        } else if (i == R.id.rate_app) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        } else if (i == R.id.more_apps) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/dev?id=5009060970068759882")));
        } else if (i == R.id.share_app) {
            startActivity(new Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) +
                            "\n-Festival of Hearing\n\nhttp://play.google.com/store/apps/details?id=" + getPackageName() + " "));
        } else if (i == R.id.info) {
            showInfo();
        } else if (i == R.id.restartTips) {
            restartTips = !restartTips;
            ((CheckBox) v).setChecked(restartTips);
        } else if (i == R.id.start) {
            infoVisible = false;
            if (restartTips) helper.reStartTips();
            ContentValues values = new ContentValues();
            values.put("_id", AppIntro);
            values.put("status", BuildConfig.VERSION_CODE);
            AppTour.insertWithOnConflict(AppTourData, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            findViewById(R.id.first_launch).animate().alpha(0).scaleX(0).scaleY(0).setDuration(300).start();
            firstLaunchAnim(300);
            new Handler().postDelayed(() -> {
                grid.animate().alpha(0).scaleY(0).scaleX(0).setDuration(0).start();
                grid.setVisibility(View.VISIBLE);
                grid.animate().alpha(1).scaleY(1).scaleX(1).setDuration(300).start();
                btns.animate().alpha(1).setDuration(300).start();
                bgAnimator.resume();
                findViewById(R.id.anim).animate().alpha(1).setDuration(300).start();
                findViewById(R.id.first_launch).setVisibility(GONE);
                findViewById(R.id.up_indicator).setVisibility(View.VISIBLE);
                checkPermissions(true);
                if (getPackageName().contains("shravanotsava"))
                    showMTip();
            }, 300);
        } else if (i == R.id.screenTips) {
            showTips = !showTips;
            ((Switch) v).setChecked(showTips);
            ContentValues values = new ContentValues();
            values.put("status", showTips ? 1 : 0);
            AppTour.update(AppTourData, values, "_id = ?", new String[]{ShowTipsTag});
        } else if (i == R.id.accept_terms) {
            acceptTerms = !acceptTerms;
            ContentValues values = new ContentValues();
            values.put("status", acceptTerms ? 1 : 0);
            AppTour.update(AppTourData, values, "_id = ?", new String[]{TermsTag});
            setAcceptTerms();
            ((CheckBox) v).setChecked(acceptTerms);
        } else if (i == R.id.request_permission) {
            if (nRequested > 1)
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", getPackageName(), null))
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
            else
                checkPermissions(true);
            if (permissionDialog != null && permissionDialog.isShowing())
                permissionDialog.dismiss();
        }
    }

    private void setAcceptTerms() {
        findViewById(R.id.screenTips).setEnabled(acceptTerms);
        findViewById(R.id.request_permission).setEnabled(acceptTerms);
        findViewById(R.id.start).setEnabled(acceptTerms);
        if (acceptTerms) {
            findViewById(R.id.tips).setAlpha(1);
            findViewById(R.id.permissions).setAlpha(1);
        } else {
            findViewById(R.id.tips).setAlpha(.5f);
            findViewById(R.id.permissions).setAlpha(.5f);
        }
    }

    private void showMTip() {
        if (showTips) {
            new Handler().postDelayed(() -> {
                FeatureId = "swipe_to_hide";
                if (shouldShow(FeatureId)) {
                    showSwipeTip(m.this, "Swipe down to hide apps.", BOTTOM)
                            .setOnTouchListener((view, event) -> {
                                findViewById(R.id.bg).dispatchTouchEvent(event);
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    view.postDelayed(() -> {
                                        if (p.topMargin > 50 * dp) {
                                            closeTip.performClick();
                                            FeatureId = "swipe_or_tap_to_reveal";
                                            showSwipeTip(m.this, "", helper.TOP).setOnTouchListener((v, event1) -> {
                                                findViewById(R.id.bg).dispatchTouchEvent(event1);
                                                if (event1.getAction() == MotionEvent.ACTION_UP)
                                                    v.postDelayed(() -> {
                                                        if (p.topMargin < 10 * dp) {
                                                            closeTip.performClick();
                                                            showLibTip();
                                                        }
                                                    }, 350);
                                                return true;
                                            });
                                            showTip(m.this, findViewById(R.id.app_drawer), "Tap on App Drawer or swipe up to reveal apps.")
                                                    .setOnClickListener(v -> {
                                                        findViewById(R.id.app_drawer).performClick();
                                                        closeTip.performClick();
                                                        showLibTip();
                                                    });
                                        }
                                    }, 350);
                                }
                                return true;
                            });
                } else showLibTip();
            }, 800);
        }
    }

    private void showLibTip() {
        FeatureId = "tap_to_library";
        if (shouldShow(FeatureId)) {
            new Handler().postDelayed(() -> showTip(m.this, grid.getChildAt(0), "Tap icon to open library.\n\nDatabase will be created when you launch the app for first time. This may take some time.")
                    .setOnClickListener(v -> {
                        launchApp(0, null);
                        closeTip.performClick();
                    }), 1000);
        }
    }

    public class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.length;
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
            final String shortN = apps[position];
            final File f = new File(logoDir, shortN);
            if (f.exists()) holder.logo.setImageURI(Uri.fromFile(f));
            else new k.DownloadTask(animRawBase + "pps/" + shortN + ".webp",
                    new File(logoDir, shortN), s -> holder.logo.setImageURI(Uri.fromFile(f))).execute("");
            holder.name.setText(appNames[position]);
            holder.logo.setOnClickListener(v -> launchApp(position, null));
            holder.logo.setOnLongClickListener(v -> {
                new AlertDialog.Builder(m.this).setMessage("This is Advanced Feature.\n\nDelete database and source files?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteDb(shortN)).setNegativeButton("Cancel", null).show();
                return true;
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

    private void deleteDb(String shortN) {
        File f = new File(baseDir + "/" + shortN + "/s");
        for (File f1 : f.listFiles()) f1.delete();
        deleteDir(new File(getExternalFilesDir("gopinath") + "/" + shortN));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(final Intent intent) {
        if (intent == null) return;
        if (null != intent.getAction() && intent.getAction().equals("shravanotsava.delete")) {
            new AlertDialog.Builder(m.this).setMessage("This is Advanced Feature.\n\nDelete database and source files?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteDb(intent.getStringExtra("shortName"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }).setNegativeButton("Cancel", null).show();
        }
        if (intent.hasExtra("s")) {
            k.shortName = intent.getStringExtra("s");
            for (int i = 0; i < apps.length; i++)
                if (apps[i].equals(k.shortName)) {
                    launchApp(i, null);
                    break;
                }
        } else if (null != intent.getData()) {
            String s = intent.getData().toString();
            for (int i = 0; i < appGit.length; i++)
                if (s.contains(appGit[i])) {
                    launchApp(i, intent);
                    break;
                }
        }
    }

    private void launchApp(int position, Intent intent) {
        k.pkg = pkgs[position].isEmpty() ? "3cCIX07" : pkgs[position];
        k.appName = position == 0 ? "Śrīla Prabhupāda Vāni" : appNames[position];
        if (k.appName.matches("(?i).*sw[a,ā]mi.*")) {
            k.appName = k.appName.replaceFirst("(?i)sw[a,ā]mi", "Swāmi");
            k.appName = k.appName.substring(0, k.appName.indexOf("Swāmi") + 5);
        }
        k.shortName = apps[position];
        k.appInd = position;
        k.git = appGit[position];
        File sourceDir = new File(baseDir + "/" + k.shortName + "/s");
        if (!sourceDir.exists()) sourceDir.mkdirs();
        String[] s = {"a", "b", "m", "l", "h", "p", "i"};
        int i = 0;
        for (; i < s.length; i++) if (!new File(sourceDir, s[i]).exists()) break;
        if (i == s.length) {
            boolean allOk = true;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceDir, "i"))));
                String strs[] = reader.readLine().replaceAll("~", " ~ ").split("~");
                i = 0;
                while (i < s.length - 1) {
                    if (new File(sourceDir, s[i]).length() < decode(strs[7 + i].trim()) - 50) {
                        new File(sourceDir, s[i]).delete();
                        new k.DownloadTask("https://raw.githubusercontent.com/" + appGit[position]
                                .replace(".github.io", "") + "/exp/master/" + s[i], new File(sourceDir, s[i]),
                                null).execute("");
                        allOk = false;
                    }
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (allOk) {
                Intent intent1 = new Intent(m.this, k.class);
                if (null != intent) intent1.setData(intent.getData());
                k.started = false;
                startActivity(intent1);
            } else {
                Toast.makeText(m.this, "Fetching Data..." + k.shortName, Toast.LENGTH_LONG).show();
            }
        } else {
            for (String s1 : s)
                new k.DownloadTask("https://raw.githubusercontent.com/" + appGit[position].replace(".github.io", "") + "/exp/master/" + s1, new File(sourceDir, s1), null).execute("");
            Toast.makeText(m.this, "Fetching Data...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkPermissions(boolean requestPermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (requestPermission)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        } else return true;
    }

    int nRequested = 0;
    AlertDialog permissionDialog;

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nRequested++;
        if (checkPermissions(false)) {
            findViewById(R.id.permissions).setVisibility(GONE);
            findViewById(R.id.start).setEnabled(true);
            createDirs();
        } else {
            findViewById(R.id.start).setEnabled(false);
            findViewById(R.id.permissions).setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                permissionDialog = new AlertDialog.Builder(m.this).setView(R.layout.req_permission)
                        .setNegativeButton("Close App", (dialog, which) -> finish()).setCancelable(false).show();
            } else {
                permissionDialog = new AlertDialog.Builder(m.this).setMessage(R.string.req_permission_info)
                        .setPositiveButton(R.string.grant_permission, (dialog, which) -> checkPermissions(true))
                        .setNegativeButton("Close App", (dialog, which) -> finish()).setCancelable(false).show();
            }
        }
    }

    private void createDirs() {
        if (!checkPermissions(false)) return;
        k.spQuoteDir = new File(baseDir + "/sp/q");
        if (!spQuoteDir.exists()) spQuoteDir.mkdirs();
        k.vbDir = new File(baseDir + "/sp/vb");
        if (!vbDir.exists()) vbDir.mkdirs();
        k.publicBaseDir = getExternalFilesDir("shravanotsava");
        arteDir = new File(baseDir + "/shravanotsava/_xz/a");
        if (!arteDir.exists()) arteDir.mkdirs();
        k.lyDir = new File(baseDir + "/shravanotsava/_xz/l");
        if (!lyDir.exists()) lyDir.mkdirs();
        lyDb = new TourData("l").getWritableDatabase();
        new Thread(() -> {
            download(new File(lyDir, "lyxid"), rawBase + "shravanotsava/l/master/ly_ind");
            download(new File(lyDir, "layid"), rawBase + "shravanotsava/l/master/lya_ind");
            lyDb = new TourData("l").getWritableDatabase();
            Cursor c = lyDb.rawQuery("select * from " + TITLE, null);
            if (!c.moveToFirst()) {
                try {
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lyDir, "lyxid"))));
                    while ((line = reader.readLine()) != null) {
                        String[] a = line.replaceAll("~", " ~ ").split("~");
                        ContentValues values = new ContentValues();
                        values.put(TITLE, a[0].trim());
                        values.put(PARENT, a[1].trim());
                        values.put(officialName, a[2].trim());
                        values.put(book, a[3].trim());
                        values.put(URL, a[4].trim());
                        values.put(ARTE, a[5].trim());
                        lyDb.insert(TITLE, null, values);
                    }
                    reader.close();
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lyDir, "layid"))));
                    while ((line = reader.readLine()) != null) {
                        String[] a = line.replaceAll("~", " ~ ").split("~");
                        ContentValues values = new ContentValues();
                        values.put(TITLE, a[0].trim());
                        values.put(ARTE, a[1].trim());
                        values.put(URL, a[2].trim());
                        long lyId = lyDb.insert(PARENT, null, values);
                        Cursor c1 = lyDb.rawQuery("select * from " + TITLE + " where " + PARENT + " = ?", new String[]{encodeUrl(lyId + "")});
                        values.put("c", encode(c1.getCount()));
                        lyDb.update(PARENT, values, "_id = ?", new String[]{lyId + ""});
                        c1.close();
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }).start();
    }

    final public static String AppTourData = "tour_data", AppIntro = "app_intro",
            ShowTipsTag = "show_tips", TermsTag = "accept_terms";
    final static String officialName = "lo", book = "lb";

    public class TourData extends SQLiteOpenHelper {
        String s;

        TourData(String s) {
            super(m.this, s.equals("app_tour") ? s : lyDir + "/d/" + s, null, 1);
            this.s = s;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (s.equals("app_tour")) {
                initTourData(db);
            } else if (s.equals("l")) {
                db.execSQL("create table " + TITLE + " (_id integer primary key autoincrement, " + TITLE + "," + PARENT + " integer, " +
                        officialName + ", " + book + ", " + URL + ", " + ARTE + ")");
                db.execSQL("create table " + PARENT + " (_id integer primary key autoincrement, " + TITLE + ", " + ARTE + "," + URL + ", c)");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    static void initTourData(SQLiteDatabase db) {
        db.execSQL("create table " + AppTourData + " (_id text primary key, status integer default 0)");
        ContentValues values = new ContentValues();
        values.put("_id", ShowTipsTag);
        values.put("status", 1);
        db.insert(AppTourData, null, values);

        values = new ContentValues();
        values.put("_id", TermsTag);
        values.put("status", 0);
        db.insert(AppTourData, null, values);
    }
}
