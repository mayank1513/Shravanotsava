package k;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class ImgProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("query - Uri", uri.toString());
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "_data", "mime_type", "_display_name"});
        cursor.addRow(new Object[]{
                0,
                new File(getContext().getCacheDir(), uri.getLastPathSegment() + ".png"),
                "image/png", uri.getLastPathSegment().replaceAll("_", " ")
        });
        return cursor;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.e("openFile", uri.toString());
        return ParcelFileDescriptor.open(new File(getContext().getCacheDir(), uri.getLastPathSegment() + ".png"), ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(Uri uri) {
        return "image/png";
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported");
    }
}
