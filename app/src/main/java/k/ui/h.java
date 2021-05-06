package k.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import k.k;

public class h extends HorizontalScrollView {
    SyncedScrollInterface syncedScrollInterface;
    public h(Context context, AttributeSet attrs) {
        super(context, attrs);
        syncedScrollInterface = (SyncedScrollInterface)context;
        ((k)context).syncedH.add(this);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        syncedScrollInterface.scrollAll(l,t);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface SyncedScrollInterface{
        void scrollAll(int x, int y);
    }
}
