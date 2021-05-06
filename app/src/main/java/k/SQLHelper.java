package k;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static k.k.ALB;
import static k.k.ALBUM;
import static k.k.ARTE;
import static k.k.AUDIOS;
import static k.k.DISPLAY;
import static k.k.LANG;
import static k.k.LU;
import static k.k.PARENT;
import static k.k.SUBALBS;
import static k.k.TITLE;
import static k.k.URL;
import static k.k.URL_REP_ID;

public class SQLHelper extends SQLiteOpenHelper {
    private static int version = 1;
    private String[][] prefs = {
            {"dailyNectarNotification", "integer", "1"},
            {"dailyNectarTime", "integer", "360"},
            {"dailyNectarRingTone", "text", ""},
            {"SPQuoteNotification", "integer", "1"},
            {"spQuoteTime", "integer", "1"},
            {"spQuoteRingTone", "text", ""},
            {"lowerSPChantVolTo", "integer", "50"},
            {"spChantVol", "integer", "100"},
            {"autoHideToolBar", "integer", "1"},
            {"expandToolBar", "integer", "1"},
            {"resizeDrawerHeader", "integer", "1"},
            {"showQuickBall", "integer", "1"},
            {"showLangViz", "integer", "1"},
            {"fixedBackground", "integer", "1"},
            {"onShake", "integer", "0"},
            {"becomingNoisyAction", "integer", "0"},
            {"filterFlags", "integer", "0"},
            {"a", "integer", "195"},
            {"r", "integer", "150"},
            {"g", "integer", "150"},
            {"b", "integer", "160"},
            {"bgUri", "text", ""},
            {"appSharingSign", "text", "Hare Krishna\nFound wonderful @App.\n@appUrl" +
                    "\nPlease install this light weight app and share with your friends."},
            {"songSharingSign", "text", "Hare Krishna\nI am listening to @songUrl."},
            {"nowPlayingId", "text", "1"},
            {"repeat", "integer", "1"},
            {"shuffle", "integer", "0"},
    };
    SQLHelper(Context context, String name) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ALBUM + " (_id integer primary key autoincrement, " + TITLE + ", " + PARENT + " integer default -1" +
                ", " + LANG + " integer default -1, " + URL + ", " + URL_REP_ID + " integer default -1, " + LU + " datetime)");

        db.execSQL("create table " + ALB + " (_id integer primary key autoincrement, " + TITLE + ", " + ARTE + ", " +
                PARENT + ", " + SUBALBS + " integer default 0, " + DISPLAY + " integer default 0, " + AUDIOS + " integer default 0)");

        db.execSQL("create table " + LANG + " (_id integer primary key, " + TITLE + ", " + ARTE + ", " +
                PARENT + ", " + SUBALBS + " integer default 0, " + AUDIOS + " integer default 0)");

        StringBuilder sb = new StringBuilder();
        sb. append("CREATE table prefs (_id integer primary key");
        for (String[]s:prefs){
            String[]s1;
            if(s[1].equals("text"))
                s1 = new String[]{" default '", "'"};
            else
                s1 = new String[]{" default ", ""};
            sb.append(", ").append(s[0]).append(" ").append(s[1]).append(s1[0]).append(s[2]).append(s1[1]);
        }
        sb.append(")");
        db.execSQL(sb.toString());
        ContentValues values = new ContentValues();
        values.put("_id", 1);
        db.insert("prefs", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
