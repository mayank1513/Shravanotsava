package k.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Objects;

import static k.k.dp;

public class g extends GridView {
    private ImageView mDragView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    //    At which position is the item currently being dragged. Note that this takes in to account header items.
    private int mDragPos;
    //     * At which position was the item being dragged originally
    private int mSrcDragPos;
    private int mDragPointX; // at what x offset inside the item did the user grab it
    private int mDragPointY; // at what y offset inside the item did the user grab it
    private int mXOffset; // the difference between screen coordinates and coordinates in this view
    private int mYOffset; // the difference between screen coordinates and coordinates in this view
    private DropListener mDropListener;
    private int mUpperBound;
    private int mLowerBound;
    private int mHeight;
    private Rect mTempRect = new Rect();
    private Bitmap mDragBitmap;
    private final int mTouchSlop;
    private int mItemHeightNormal;
    private int mItemHeightExpanded;
    private int mItemHeightHalf;
    float mDragDistance, mDownY;
    ToolBarListener mToolBarListener;

    public g(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDragDistance = dp *60;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mToolBarListener!=null && ev.getAction() == MotionEvent.ACTION_DOWN)
            mDownY = ev.getRawY();
        if (mDropListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemNum = pointToPosition(x, y);
                if (itemNum == AdapterView.INVALID_POSITION) return super.onInterceptTouchEvent(ev);
                View item = getChildAt(itemNum - getFirstVisiblePosition());
                mItemHeightNormal = item.getMeasuredHeight();
                mItemHeightHalf = mItemHeightNormal / 2;
                mItemHeightExpanded = 3 * mItemHeightHalf;
                mDragPointX = x - item.getLeft();
                mDragPointY = y - item.getTop();
                mXOffset = ((int) ev.getRawX()) - x;
                mYOffset = ((int) ev.getRawY()) - y;
                // The left side of the item is the grabber for dragging the item
                if (x < mDragDistance) {
                    item.setDrawingCacheEnabled(true);
                    // Create a copy of the drawing cache so that it does not get recycled
                    // by the framework when the list tries to clean up memory
                    Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
                    startDragging(bitmap, x, y);
                    mDragPos = itemNum;
                    mSrcDragPos = mDragPos;
                    mHeight = getHeight();
                    int touchSlop = mTouchSlop;
                    mUpperBound = Math.min(y - touchSlop, mHeight / 3);
                    mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
                    return false;
                }
                stopDragging();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
     * pointToPosition() doesn't consider invisible views, but we
     * need to, so implement a slightly different version.
     */
    private int myPointToPosition(int x, int y) {
        if (y < 0) {
            // when dragging off the top of the screen, calculate position
            // by going back from a visible item
            int pos = myPointToPosition(x, y + mItemHeightNormal);
            if (pos > 0) {
                return pos - 1;
            }
        }

        Rect frame = mTempRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return INVALID_POSITION;
    }

    private int getItemForPosition(int y) {
        int adjustedY = y - mDragPointY - mItemHeightHalf;
        int pos = myPointToPosition(0, adjustedY);
        if (pos >= 0) {
            if (pos <= mSrcDragPos) {
                pos += 1;
            }
        } else if (adjustedY < 0) {
            // this shouldn't happen anymore now that myPointToPosition deals
            // with this situation
            pos = 0;
        }
        return pos;
    }

    private void adjustScrollBounds(int y) {
        if (y >= mHeight / 3) {
            mUpperBound = mHeight / 3;
        }
        if (y <= mHeight * 2 / 3) {
            mLowerBound = mHeight * 2 / 3;
        }
    }

    /*
     * Restore size and visibility for all listItems
     */
    private void unExpandViews() {
        for (int i = 0;; i++) {
            View v = getChildAt(i);
            if (v == null) {
                try {
                    layoutChildren(); // force children to be recreated where needed
                    v = getChildAt(i);
                } catch (IllegalStateException ex) {
                    // layoutChildren throws this sometimes, presumably because we're
                    // in the process of being torn down but are still getting touch
                    // events
                }
                if (v == null) {
                    return;
                }
            }
            v.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            v.setVisibility(View.VISIBLE);
        }
    }

    /* Adjust visibility and size to make it appear as though
     * an item is being dragged around and other items are making
     * room for it:
     * If dropping the item would result in it still being in the
     * same place, then make the dragged list-item's size normal,
     * but make the item invisible.
     * Otherwise, if the dragged list-item is still on screen, make
     * it as small as possible and expand the item below the insert
     * point.
     * If the dragged item is not on screen, only expand the item
     * below the current insert-point.
     */
    private void doExpansion() {
        int childnum = mDragPos - getFirstVisiblePosition();
        if (mDragPos > mSrcDragPos) {
            childnum++;
        }
        childnum--;
        int numheaders = 0;

        View first = getChildAt(mSrcDragPos - getFirstVisiblePosition());
        for (int i = 0;; i++) {
            View vv = getChildAt(i);
            if (vv == null) {
                break;
            }

            int height = mItemHeightNormal;
            int visibility = View.VISIBLE;
            if (mDragPos < numheaders && i == numheaders) {
                // dragging on top of the header item, so adjust the item below
                // instead
                if (vv.equals(first)) {
                    visibility = View.INVISIBLE;
                } else {
                    height = mItemHeightExpanded;
                }
            } else if (vv.equals(first)) {
                // processing the item that is being dragged
                if (mDragPos == mSrcDragPos || getPositionForView(vv) == getCount() - 1) {
                    // hovering over the original location
                    visibility = View.INVISIBLE;
                } else {
                    // not hovering over it
                    // Ideally the item would be completely gone, but neither
                    // setting its size to 0 nor settings visibility to GONE
                    // has the desired effect.
                    height = 1;
                }
            } else if (i == childnum) {
                if (mDragPos >= numheaders && mDragPos < getCount() - 1) {
                    height = mItemHeightExpanded;
                }
            }
            ViewGroup.LayoutParams params = vv.getLayoutParams();
            params.height = height;
            vv.setLayoutParams(params);
            vv.setVisibility(visibility);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if ((mDropListener != null && mDragView != null)) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Rect r = mTempRect;
                    mDragView.getDrawingRect(r);
                    stopDragging();
                    if (mDropListener != null && mDragPos >= 0 && mDragPos < getCount()) {
                        mDropListener.drop(mSrcDragPos, mDragPos);
                    }
                    unExpandViews();
                    break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    dragView(x, y);
                    int itemnum = getItemForPosition(y);
                    if (itemnum >= 0) {
                        if (action == MotionEvent.ACTION_DOWN || itemnum != mDragPos) {
                            mDragPos = itemnum;
                            doExpansion();
                        }
                        int speed = 0;
                        adjustScrollBounds(y);
                        if (y > mLowerBound) {
                            // scroll the list up a bit
                            if (getLastVisiblePosition() < getCount() - 1) {
                                speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
                            } else {
                                speed = 1;
                            }
                        } else if (y < mUpperBound) {
                            // scroll the list down a bit
                            speed = y < mUpperBound / 2 ? -16 : -4;
                            if (getFirstVisiblePosition() == 0
                                    && getChildAt(0).getTop() >= getPaddingTop()) {
                                // if we're already at the top, don't try to scroll, because
                                // it causes the framework to do some extra drawing that messes
                                // up our animation
                                speed = 0;
                            }
                        }
                        if (speed != 0) {
                            smoothScrollBy(speed, 30);
                        }
                    }
                    break;
            }
            return true;
        } else if(mToolBarListener!=null){
            switch (action){
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mToolBarListener.setTabHeaders((int) (ev.getRawY()-mDownY), true);
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if(getChildCount() > 0 && getFirstVisiblePosition()==0 && getChildAt(0).getY() >= -35* dp){
                        if(!mToolBarListener.setToolBarHeight((int) (ev.getRawY()-mDownY)))
                            smoothScrollToPosition(0);
                    } else {
                        if(getChildCount() > 0 && ev.getRawY()<mDownY)
                            mToolBarListener.setToolBarHeight((int) (ev.getRawY()-mDownY));
                        mToolBarListener.setTabHeaders((int) (ev.getRawY()-mDownY), false);
                    }
                    mDownY = ev.getRawY();
            }
        }
        return super.onTouchEvent(ev);
    }

    private void startDragging(Bitmap bm, int x, int y) {
        stopDragging();

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        mWindowParams.x = x - mDragPointX + mXOffset;
        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowParams.alpha = 0.5f;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setPadding(0, 0, 0, 0);
        v.setImageBitmap(bm);
        mDragBitmap = bm;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Objects.requireNonNull(mWindowManager).addView(v, mWindowParams);
        mDragView = v;
    }

    private void dragView(int x, int y) {
        mWindowParams.x = x/2;
        mWindowParams.y = y - mDragPointY + mYOffset;
        mWindowManager.updateViewLayout(mDragView, mWindowParams);
    }

    private void stopDragging() {
        if (mDragView != null) {
            mDragView.setVisibility(GONE);
            WindowManager wm =
                    (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Objects.requireNonNull(wm).removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
        if (mDragBitmap != null) {
            mDragBitmap.recycle();
            mDragBitmap = null;
        }
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }
    public void setToolBarListener(ToolBarListener mToolBarListener) {
        this.mToolBarListener = mToolBarListener;
    }
    public interface DropListener { void drop(int from, int to); }
    public interface ToolBarListener{
        void setTabHeaders(int dh, boolean actionUp);
        boolean setToolBarHeight(int dh);
    }
}
