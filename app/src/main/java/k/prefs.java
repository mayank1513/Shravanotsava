package k;

import android.database.Cursor;

class prefs {
//    UI preferences
    boolean autoHideToolBar, expandToolBar, resizeDrawerHeader, showLangViz, shuffle = false;
    int dailyNectarNotification = 1, dailyNectarTime = 360, SPQuoteNotification = 1, onShake, spChantVol,
            showQuickBall, spQuoteTime = 360, becomingNoisyAction, a, r, g, b, filterFlags, fixedBackground, repeat = 1; //---
    String dailyNectarRingTone, spQuoteRingTone, appSharingSign, songSharingSign, bgUri, nowPlayingId;
    float lowerSPChantVolTo, backgroundVolume = 0;
    int offlineVedabase = 1;

    prefs(Cursor c) {
//        if(karanotsava){
        lowerSPChantVolTo = c.getInt(c.getColumnIndex("lowerSPChantVolTo"));
        spChantVol = c.getInt(c.getColumnIndex("spChantVol"));
        dailyNectarRingTone = c.getString(c.getColumnIndex("dailyNectarRingTone"));
        spQuoteRingTone = c.getString(c.getColumnIndex("spQuoteRingTone"));
        dailyNectarNotification = c.getInt(c.getColumnIndex("dailyNectarNotification"));
        dailyNectarTime = c.getInt(c.getColumnIndex("dailyNectarTime"));
        SPQuoteNotification = c.getInt(c.getColumnIndex("SPQuoteNotification"));
        spQuoteTime = c.getInt(c.getColumnIndex("spQuoteTime"));
        autoHideToolBar = c.getInt(c.getColumnIndex("autoHideToolBar"))==1;
        expandToolBar = c.getInt(c.getColumnIndex("expandToolBar"))==1;
        resizeDrawerHeader = c.getInt(c.getColumnIndex("resizeDrawerHeader"))==1;
        onShake = c.getInt(c.getColumnIndex("onShake"));
        onShake = 0;
        showQuickBall = c.getInt(c.getColumnIndex("showQuickBall"));
        showLangViz = c.getInt(c.getColumnIndex("showLangViz"))==1;
        becomingNoisyAction = c.getInt(c.getColumnIndex("becomingNoisyAction"));
        a = c.getInt(c.getColumnIndex("a"));
        r = c.getInt(c.getColumnIndex("r"));
        g = c.getInt(c.getColumnIndex("g"));
        b = c.getInt(c.getColumnIndex("b"));
        filterFlags = c.getInt(c.getColumnIndex("filterFlags"));
        fixedBackground = c.getInt(c.getColumnIndex("fixedBackground"));
        appSharingSign = c.getString(c.getColumnIndex("appSharingSign"));
        songSharingSign = c.getString(c.getColumnIndex("songSharingSign"));
        bgUri = c.getString(c.getColumnIndex("bgUri"));
        nowPlayingId = c.getString(c.getColumnIndex("nowPlayingId"));
        repeat = c.getInt(c.getColumnIndex("repeat"));
        shuffle = c.getInt(c.getColumnIndex("shuffle"))==1;
    }
}