package k;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static android.view.View.GONE;
import static k.k.dp;
import static k.k.size;
import static k.m.AppTour;

public class helper {
    static ArrayList<ArrayList<String>> getTextUrls(String text) {
        ArrayList<ArrayList<String>> textUrl = new ArrayList<>();
        ArrayList<String> textLinkUrl = new ArrayList<>();
        ArrayList<String> textString = new ArrayList<>();
        text = text.replaceAll("śrī\\.", "").replaceAll("bhagavad\\.", "");
        String[] temp = text.split("\\.");
        String book = temp[0];
        boolean multipleText = false;
        if (book.startsWith("sb") && temp.length > 1) {
            int canto = Integer.parseInt(temp[1]);
            int chapter = temp.length > 2 ? Integer.parseInt(temp[2]) : 0;
            if (temp.length == 5) {
                multipleText = true;
                int startText = Integer.parseInt(temp[3].trim()), endText = Integer.parseInt(temp[4].trim());
                for (int i = startText; i <= endText; i++) {
                    textLinkUrl.add(book + "/" + canto + "/" + chapter + "/" + i);
                    textString.add("SB Canto " + canto + " Chapter " + chapter + " Text " + i);
                }
            } else if (temp.length == 4) {
                int txt = Integer.parseInt(temp[3]);
                textLinkUrl.add(book + "/" + canto + "/" + chapter + "/" + txt);
                textString.add("SB Canto " + canto + " Chapter " + chapter + " Text " + txt);
            } else if (temp.length == 3) {
                textString.add("SB Canto " + canto + " Chapter " + chapter);
                textLinkUrl.add(book + "/" + canto + "/" + chapter);
            } else if (temp.length == 2) {
                textLinkUrl.add(book + "/" + canto);
                textString.add("SB Canto " + canto);
            }
//            ========================================if not SB
        } else if (book.startsWith("cc") && temp.length > 1) {
            String lila = temp[1];
            int chapter = temp.length > 2 ? Integer.parseInt(temp[2]) : 0;
            if (temp.length == 5) {
                multipleText = true;
                int startText = Integer.parseInt(temp[3].trim()), endText = Integer.parseInt(temp[4].trim());
                for (int i = startText; i <= endText; i++) {
                    textLinkUrl.add(book + "/" + lila + "/" + chapter + "/" + i);
                    textString.add("CC " + lila + " Līlā Chapter " + chapter + " Text " + i);
                }
            } else if (temp.length == 4) {
                int txt = Integer.parseInt(temp[3]);
                textLinkUrl.add(book + "/" + lila + "/" + chapter + "/" + txt);
                textString.add("CC " + lila + " Līlā Chapter " + chapter + " Text " + txt);
            } else if (temp.length == 3) {
                textLinkUrl.add(book + "/" + lila + "/" + chapter);
                textString.add("CC " + lila + " Līlā Chapter " + chapter);
            } else if (temp.length == 2) {
                textLinkUrl.add(book + "/" + lila);
                textString.add("CC " + lila + " Līlā");
            }
        } else if (temp.length > 1) {
            int chapter = Integer.parseInt(temp[1]);
            if (temp.length == 4) {
                multipleText = true;
                int startText = Integer.parseInt(temp[2]), endText = Integer.parseInt(temp[3]);
                for (int i = startText; i <= endText; i++) {
                    textLinkUrl.add(book.replace('_', '/') + "/" + chapter + "/" + i);
                    textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter + " Text " + i);
                }
            } else if (temp.length == 3) {
                int txt = Integer.parseInt(temp[2]);
                textLinkUrl.add(book.replace('_', '/') + "/" + chapter + "/" + txt);
                textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter + " Text " + txt);
            } else if (temp.length == 2) {
                textLinkUrl.add(book.replace('_', '/') + "/" + chapter);
                textString.add(book.replace('_', ' ').toUpperCase() + " Chapter " + chapter);
            }
        } else {
            textLinkUrl.add(book.replace('_', '/').toLowerCase());
            textString.add(book.replace('_', ' ').toUpperCase());
        }
        textUrl.add(textLinkUrl);
        textUrl.add(textString);
        ArrayList<String> label = new ArrayList<>();
        if (multipleText) {
            label.add(book.toUpperCase() + " " + text.substring(text.indexOf('.') + 1, text.lastIndexOf(".")) + " to " + text.substring(text.lastIndexOf(".") + 1));
        } else {
            label.add(book.toUpperCase() + " " + (text.indexOf('.') > -1 ? text.substring(text.indexOf('.') + 1) : ""));
        }
        label.set(0, label.get(0).replaceAll("adi(\\.)?", "Ādī ")
                .replaceAll("madhya(\\.)?", "Madhya ")
                .replaceAll("antya(\\.)?", "Antya "));
        textUrl.add(label);
        return textUrl;
    }

    static String encodeUrl(String str) {
        if (null == str) return "";
        boolean[] key = {true, false, false, false, true, false, true, true, false, false, true};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, j = 0; i < str.length(); i++, j++) {
            if (str.charAt(i) == ' ')
                stringBuilder.append(str.charAt(i));
            else if (key[j % key.length])
                stringBuilder.append((char) (str.charAt(i) - 1));
            else
                stringBuilder.append((char) (str.charAt(i) + 1));
        }
        return stringBuilder.toString();
    }

    static String decodeUrl(String str) {
        boolean[] key = {true, false, false, false, true, false, true, true, false, false, true};
        int keyLength = key.length, strLen = str.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strLen; i++) {
            if (str.charAt(i) == ' ')
                stringBuilder.append(str.charAt(i));
            else if (key[i % keyLength])
                stringBuilder.append((char) (str.charAt(i) + 1));
            else
                stringBuilder.append((char) (str.charAt(i) - 1));
        }
        return stringBuilder.toString();
    }

    private static ArrayList<String> charSet = new ArrayList<>();

    static String encode(long k) {
        if (charSet.isEmpty()) {
            getCharset();
        }
        if (k < 0) return "";
        int base = charSet.size();
        StringBuilder str = new StringBuilder();
        while (k >= base) {
            str.append(charSet.get((int) (k % base)));
            k = k / base;
        }
        str.append(charSet.get((int) (k % base)));
        return str.toString();
    }

    static long decode(String str) {
        if (str.trim().isEmpty()) return -1;
        if (charSet.isEmpty()) {
            getCharset();
        }
        long k = 0, base = charSet.size();
        for (int i = 0; i < str.length(); i++) {
            k += Math.pow(base, i) * (charSet.indexOf(str.charAt(i) + ""));
        }
        return k;
    }

    private static void getCharset() {
        for (int i = 0; i < 26; i++) {
            charSet.add((char) ('a' + i) + "");
        }
        for (int i = 0; i < 10; i++) {
            charSet.add(i + "");
        }
        charSet.add("-");
        charSet.add("'");
    }

    static void hideSystemUI(k c) {
        k.notchH = 0;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) c.mToolBar.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        c.mToolBar.setLayoutParams(params);
        c.mSearchBar.setLayoutParams(params);
        params = (ViewGroup.MarginLayoutParams) c.mFSToolBar.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        c.mFSToolBar.setLayoutParams(params);
        k.notchH = 0;
        ViewGroup.LayoutParams params1 = c.mStatusBar.getLayoutParams();
        params1.height = k.notchH;
        c.mStatusBar.setLayoutParams(params1);
        View decorView = c.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    static void showSystemUI(k c) {
        k.notchH = 0;
        int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            k.notchH = c.getResources().getDimensionPixelSize(resourceId);
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) c.mToolBar.getLayoutParams();
        params.setMargins(0, k.notchH, 0, 0);
        c.mToolBar.setLayoutParams(params);
        c.mSearchBar.setLayoutParams(params);
        params = (ViewGroup.MarginLayoutParams) c.mFSToolBar.getLayoutParams();
        params.setMargins(0, k.notchH, 0, 0);
        c.mFSToolBar.setLayoutParams(params);
        ViewGroup.LayoutParams params1 = c.mStatusBar.getLayoutParams();
        params1.height = k.notchH;
        c.mStatusBar.setLayoutParams(params1);
        View decorView = c.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = Objects.requireNonNull(connectivityManager).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected();
    }

    static String hari(String title) {
        title = title.replaceAll("Gita", "Gītā")
                .replaceAll("(Srimad|Shrimad)", "Śrīmad")
                .replaceAll("Bhagavat(h)?(am|a)?", "Bhāgavatam")
                .replaceAll("Chaitanya", "Caitanya")
                .replaceAll("(?i) Lila", " Līlā")
                .replaceAll("(?i) Adi", " Ādī")
                .replaceAll("(?i)CC A ", "CC Ādī ")
                .replaceAll("(?i)CC M ", "CC Madhya ")
                .replaceAll("Chapter[\\s-]?0?", "Chapter ")
                .replaceAll("Canto[\\s-]?0?", "Canto ")
                .replaceAll("(?i) Ar(a)?ti", " Ārati")
                .replaceAll("(Krishna|Krsna)", "Kṛṣṇa")
                .replaceAll("Vr(i)?ndavan(a)?", "Vṛndāvana")
                .replaceAll("Radha", "Rādhā")
                .replaceAll("Jayapataka", "Jayapatākā")
                .replaceAll("(Rasamrit(a)?)", "Rasāmṛta")
                .replaceAll("(Radheyshyam)", "Rādheyshyam")
                .replaceAll("(Gopinath)", "Gopināth")
                .replaceAll("(Swami)", "Swāmi")
                .replaceAll("(Prabhupada)", "Prabhupāda")
                .replaceAll("vais(h)?nav(a)?", "vaiṣṇava")
                .replaceAll("Vais(h)?nav(a)?", "Vaiṣṇava")
                .replaceAll("vedant(a)?", "vedānta")
                .replaceAll("Sri", "Śrī")
                .replaceAll("(?i)prer(a)?na", "Preraṇa")
                .replaceAll("(?i)chet(a)?na", "Chetana")
                .replaceAll("(?i)is(h)?opanis(h)?ad(a)?", "Īśopaniṣada")
                .replaceAll("%20", " ")
                .replaceAll("C(h)?arit(h)?amr(i)?(tha|ta|t)", "Caritāmṛta").trim();

        if (title.replaceFirst("Śrīmad Bhāgavatam", "").contains("Śrīmad Bhāgavatam"))
            title = title.replaceFirst("Śrīmad Bhāgavatam", "");
        return title;
    }

    static String FeatureId;
    final static int RIGHT = 0, BOTTOM = 1, LEFT = 2, TOP = 3;
    static ValueAnimator swipeAnimator;

    static View showSwipeTip(final Activity c, String m, int direction) {
        final View root = initTip(c, null, m);
        final ImageView pointer = root.findViewById(R.id.pointer), arrow = root.findViewById(R.id.arrow);
        pointer.animate().alpha(1).start();
        arrow.animate().alpha(1).rotation(direction * 90).setDuration(0).start();
        swipeAnimator = new ValueAnimator();
        swipeAnimator.setFloatValues(0, 1);
        swipeAnimator.setInterpolator(new LinearInterpolator());
        switch (direction) {
            case TOP:
                swipeAnimator.addUpdateListener(animation -> {
                    float f = animation.getAnimatedFraction();
                    if (f > .35) {
                        arrow.animate().alpha(0).setDuration(0).start();
                        pointer.animate().alpha(0).setDuration(0).start();
                    } else {
                        arrow.animate().alpha(.8f + f).scaleY(f / 2).scaleX(f).translationY(-f * size.x / 2f).setDuration(0).start();
                        pointer.animate().alpha(.8f + f).translationY(-f * size.y / 2f + (1 - f) * 15 * dp).setDuration(0).start();
                    }
                });
            case RIGHT:
                break;
            case BOTTOM:
                swipeAnimator.addUpdateListener(animation -> {
                    float f = animation.getAnimatedFraction();
                    if (f > .35) {
                        arrow.animate().alpha(0).setDuration(0).start();
                        pointer.animate().alpha(0).setDuration(0).start();
                    } else {
                        arrow.animate().alpha(.8f + f).scaleY(f / 2).scaleX(f).translationY(f * size.x / 2f).setDuration(0).start();
                        pointer.animate().alpha(.8f + f).translationY(f * size.y / 2f + (1 - f) * 15 * dp).setDuration(0).start();
                    }
                });
                break;
            case LEFT:
        }
        swipeAnimator.setDuration(1800);
        swipeAnimator.start();
        swipeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return root;
    }

    private static ValueAnimator rippleAnimator;

    static View showTip(final Activity c, final View v, String m) {
        final View root = initTip(c, v, m);
        return root.findViewById(R.id.f);
    }

    static View initTip(final Activity c, final View v, final String m) {
        if (rippleAnimator != null && rippleAnimator.isRunning()) rippleAnimator.cancel();
        final View root = c.findViewById(R.id.tips_view);
        final ImageView rp = root.findViewById(R.id.rp);
        final View closeTip = root.findViewById(R.id.close_tip);
        final TextView t = root.findViewById(R.id.tip);
        t.animate().alpha(0).scaleX(0).scaleY(0).translationY(0).translationX(0).setDuration(0).start();
        closeTip.animate().alpha(0).scaleX(0).scaleY(0).translationY(0).translationX(0).setDuration(0).start();
        root.setVisibility(View.VISIBLE);
//        set circular gradient overlay on b1 centered at view position -- animate
        final Bitmap output = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(120, 10, 140, 240));
        final int[] location = {size.x / 2, size.y / 2};
        if (v != null) v.getLocationInWindow(location);
        final int width = v == null ? 0 : v.getWidth();
        final int height = v == null ? 0 : v.getHeight();
        final float innerR = (Math.min(width, height)) * 1.2f, r1 = innerR + 100 * dp, r2 = Math.max(size.x, size.y);
        final float cx = location[0] + width / 2f;
        final float cy = location[1] + height / 2f;

        ValueAnimator animator = new ValueAnimator();
        animator.setFloatValues(0, 1);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float f = animation.getAnimatedFraction();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            paint.setStrokeWidth(f * (r1 - innerR));
            canvas.drawCircle(cx, cy, f * (r1 + innerR) / 2, paint);
            paint.setStrokeWidth(f * (r2 - innerR));
            canvas.drawCircle(cx, cy, f * (r2 + innerR) / 2, paint);
            root.setBackground(new BitmapDrawable(c.getResources(), output));
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                paint.setColor(Color.argb(10, 50, 190, 255));
                for (int i = 0; i < 30; i += 2) {
                    paint.setStrokeWidth(i * dp);
                    canvas.drawCircle(cx, cy, innerR + i * dp / 2f, paint);
                    canvas.drawCircle(cx, cy, r1 + i * dp / 2f, paint);
                }
                paint.setColor(Color.rgb(0, 0, 255));
                paint.setStrokeWidth(4 * dp);
                canvas.drawCircle(cx, cy, innerR + 2 * dp, paint);
                root.setBackground(new BitmapDrawable(c.getResources(), output));
                t.setText(m);
                t.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300).start();
                closeTip.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300).start();
                if (v != null) {
                    final ImageButton btn = root.findViewById(R.id.f);
                    v.setDrawingCacheEnabled(true);
                    final Bitmap b = changeBitmapContrastBrightness(Bitmap.createBitmap(v.getDrawingCache()), 1, 40);
                    v.setDrawingCacheEnabled(false);
                    btn.setImageBitmap(b);
                    v.animate().alpha(.1f).start();
                    FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(width, height);
                    btn.setLayoutParams(p);
                    btn.setX(location[0]);
                    btn.setY(location[1]);
                    btn.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();

                    paint.setColor(Color.argb(10, 216, 27, 96));
                    btn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rp.animate().scaleX(1).scaleY(1).translationX(0)
                                    .translationY(0).setDuration(0).start();
                            rp.setImageBitmap(null);
                            if (root.getVisibility() != GONE) {
                                animateRipple();
                                btn.postDelayed(this, 800);
                            }
                        }
                    }, 30);
                }
            }

            private void animateRipple() {
                final Bitmap b2 = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
                final Canvas canvas1 = new Canvas(b2);
                final ImageView rp = root.findViewById(R.id.rp);
                rippleAnimator = new ValueAnimator();
                rippleAnimator.setFloatValues(0, 1000);
                rippleAnimator.setInterpolator(new LinearInterpolator());
                rippleAnimator.setDuration(400);
                rippleAnimator.addUpdateListener(animation -> {
                    float f = 1 - animation.getAnimatedFraction();
                    f = f * f * f;
                    float rr = innerR + 5 * dp;
                    float innerR_ = rr - f * 5 * dp;
                    paint.setStrokeWidth(rr - innerR_);
                    canvas1.drawCircle(cx, cy, (rr + innerR_) / 2, paint);
                    rp.setImageBitmap(b2);
                });
                rippleAnimator.start();
                rippleAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        float scale = r1 / innerR;
                        rp.animate().scaleX(scale).scaleY(scale).translationX((scale - 1) * (b2.getWidth() / 2f - cx))
                                .translationY((scale - 1) * (b2.getHeight() / 2f - cy)).setDuration(400).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        float scale = r2 / innerR;
                        rp.animate().scaleX(scale).scaleY(r2 / innerR).translationX((scale - 1) * (b2.getWidth() / 2f - cx))
                                .translationY((scale - 1) * (b2.getHeight() / 2f - cy)).setDuration(1000).start();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
        closeTip.setOnClickListener(view -> {
            if (swipeAnimator != null) {
                swipeAnimator.cancel();
                swipeAnimator = null;
            }
            if (rippleAnimator != null) {
                rippleAnimator.cancel();
                rippleAnimator = null;
            }
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (v != null) v.animate().alpha(1).start();
            root.findViewById(R.id.arrow).animate().alpha(0).start();
            root.findViewById(R.id.pointer).animate().alpha(0).start();
            setFeatureDone(FeatureId);
            root.setOnTouchListener(null);
            root.setVisibility(GONE);
        });
        root.setOnClickListener(v1 -> {
            if (closeTip.getVisibility() == GONE) return;
            ValueAnimator animator1 = new ValueAnimator();
//                animator1.setInterpolator(new LinearInterpolator());
            animator1.setDuration(1500);
            animator1.setFloatValues(0, 1);
            final int h = closeTip.getMeasuredHeight();
            animator1.addUpdateListener(animation -> {
                float f = 1 - animation.getAnimatedFraction();
                final float scale = 1 - (float) (f * f * Math.pow(Math.sin(5 * Math.PI * f), 2));
                closeTip.animate().scaleY(scale).translationY((scale - 1) / 2 * h).setDuration(0).start();
            });
            animator1.start();
        });
        return root;
    }

    static void deleteDir(File f) {
        if (f == null) return;
        if (f.isDirectory()) for (File f1 : f.listFiles()) deleteDir(f1);
        else f.delete();
    }

    public static boolean shouldShow(String s) {
        Cursor c = AppTour.rawQuery("select * from tour_data where _id = ?", new String[]{s});
        boolean shouldShow = !c.moveToFirst() || c.getInt(1) != 1;
        c.close();
        return shouldShow;
    }

    public static void setFeatureDone(String s) {
        ContentValues v = new ContentValues();
        v.put("_id", s);
        v.put("status", 1);
        AppTour.insertWithOnConflict("tour_data", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    static void reStartTips() {
        Cursor c = AppTour.rawQuery("select * from tour_data", null);
        if(c.move(2))
            do{
                String s = c.getString(0);
                if(!s.startsWith("db_"))
                    AppTour.delete("tour_data", "_id = ?", new String[]{s});
            }while (c.moveToNext());
        c.close();
    }

    private static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
