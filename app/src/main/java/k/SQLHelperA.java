package k;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static k.k.ALB;
import static k.k.ARTE;
import static k.k.AUDIO_TABLE;
import static k.k.DATE;
import static k.k.FAVOURITE;
import static k.k.LANG;
import static k.k.OFFLINE;
import static k.k.PARENT;
import static k.k.PLACE;
import static k.k.REF;
import static k.k.SIZE;
import static k.k.SIZE1;
import static k.k.TITLE;
import static k.k.URL;
import static k.k.VIDEO_URL;
import static k.k.VOLUME;

public class SQLHelperA extends SQLiteOpenHelper {
    private static int version = 1;
    SQLHelperA(Context context, String name) {
        super(context, name + "_k", null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AUDIO_TABLE + " (_id integer primary key autoincrement, " + TITLE + ", "
                + PARENT + " integer, " + ARTE + ", " + URL + ", " + DATE + " datetime, " + PLACE + ", " + VIDEO_URL + ", "
                + SIZE + " integer, " + SIZE1 + " integer, " + LANG + " integer default -1, " + ALB + " default ' ', "
                + REF + ", " + FAVOURITE + " integer default 0, " + OFFLINE + " integer default 0, " + VOLUME + " integer default 100)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
