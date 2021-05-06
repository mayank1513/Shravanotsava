package k.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import k.R;
import k.k;

import static k.k.day;
import static k.k.month;
import static k.k.notchH;
import static k.k.size;

import static k.k.dp;
import static k.k.tBarH;

public class t extends RelativeLayout implements View.OnTouchListener {
    public static float CLICK_DRAG_TOLERANCE = 10;
    View mDivider, mContainer;
    public LinearLayout mTabs;
    Button mTab, mNextTab;
    public int scrollDirection = 0, mTabInd = 0, mNextTabInd;
    private float downX, downY, dX, scale = 1, absScale = 1;
    final public static int Horizontal = 1, Vertical = 2, duration = 250;
    k mContext;

    public t(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (k) context;
        setOnTouchListener(this);
        CLICK_DRAG_TOLERANCE = 20* dp;
        addView(inflate(getContext(), R.layout.c,null));
        mDivider = findViewById(R.id.divider);
        mContainer = findViewById(R.id.container);
        mTabs = findViewById(R.id.tabs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev){
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            scrollDirection = 0;
            downX = ev.getRawX();
            downY = ev.getRawY();
            dX = this.getX() - downX;
            mTab = (Button) mTabs.getChildAt(mTabInd);
        } else if(action == MotionEvent.ACTION_MOVE){
            if(scrollDirection == 0) {
                scrollDirection = Math.abs(ev.getRawX() - downX) > CLICK_DRAG_TOLERANCE ? Horizontal
                        : Math.abs(ev.getRawY() - downY) > CLICK_DRAG_TOLERANCE ? Vertical : 0;
            } else if(scrollDirection == Horizontal){
                mDivider.animate().setDuration(0).alpha(1).start();
                return onTouch(this, ev);
            }
        } else if(action == MotionEvent.ACTION_UP && scrollDirection != 0 && scrollDirection != Vertical)
            return onTouch(this, ev);
        else if(month>=0 && day>1 && ev.getY()>notchH + tBarH) mContext.setZoomToCenter();
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            mDivider.animate().setDuration(0).alpha(1).start();
            downX = ev.getRawX();
            dX = this.getX() - downX;
            mTab = (Button) mTabs.getChildAt(mTabInd);
        } else {
            if (action == MotionEvent.ACTION_MOVE){
                float newX = ev.getRawX() + dX;
                scale = newX/size.x;
                absScale = Math.abs(scale);
                if(newX - dX > downX) mNextTabInd = mTabInd <= 0 ? 0 : mTabInd - 1;
                else mNextTabInd = mTabInd < mTabs.getChildCount()-1 ? mTabInd + 1 : mTabInd;
                mNextTab = (Button) mTabs.getChildAt(mNextTabInd);
                if(mTab!=null && mNextTab!=null) {
                    int w = mTab.getMeasuredWidth();
                    int width = (int) (w + absScale * (mNextTab.getMeasuredWidth() - w));
                    mDivider.setLayoutParams(new FrameLayout.LayoutParams(width, 5* dp));
                    float x = mTab.getX() + absScale * (mNextTab.getX() - mTab.getX());
                    mDivider.setX(x);
                    mContext.syncedH.get(0).smoothScrollTo((int) (x-size.x/2 + width/2), 0);
                }
                mContainer.animate().rotationY(90*scale).scaleX(1- absScale).scaleY(1- absScale).alpha(0.7f- absScale)
                        .translationX(scale* size.x).setDuration(0).start();
                mContext.mActionButton.animate().scaleX(1- absScale).scaleY(1- absScale).alpha(0.7f- absScale).setDuration(0).start();
            } else {
                if(Math.abs(ev.getRawX() - downX) < 2*CLICK_DRAG_TOLERANCE)
                    mNextTabInd = mTabInd;
                goToTab(mNextTabInd);
            }
        }
        return true;
    }

    public void goToTab(int mNextTabInd) {
        if(mNextTabInd == -1) mNextTabInd = mTabInd;
        mNextTab = (Button) mTabs.getChildAt(mNextTabInd);
//        TODO: Something strange here -- deleting below line will create error some times
        if(mNextTab == null) return;
        mDivider.animate().translationX(mNextTab.getX()).alpha(mContext.fullScreen?.1f:1).setDuration(duration).start();
        if(mNextTabInd != mTabInd) {
            mContainer.animate().rotationY(90*scale/absScale).scaleX(0).scaleY(0).alpha(0.3f)
                .translationX(scale*size.x/absScale).setDuration(duration).start();
            mContext.mActionButton.animate().scaleX(0).scaleY(0).alpha(0.3f).setDuration(duration).start();
        } else {
            mContainer.animate().rotationY(0).scaleX(1).scaleY(1).alpha(1)
                .translationX(0).setDuration(duration).start();
            mContext.mActionButton.animate().scaleX(1).scaleY(1).alpha(1).setDuration(duration).start();
        }
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(duration);
        animator.setIntValues(mDivider.getMeasuredWidth(), mNextTab.getMeasuredWidth());
        animator.addUpdateListener(animation -> {
            mDivider.setLayoutParams(new FrameLayout.LayoutParams((Integer) animation.getAnimatedValue(), 5* dp));
            mContext.syncedH.get(0).scrollTo((int) mDivider.getX()-size.x/2 + mDivider.getMeasuredWidth()/2, 0);
        });
        final int finalMNextTabInd = mNextTabInd;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mContext.setTab(finalMNextTabInd);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(finalMNextTabInd != mTabInd) {
                    mContainer.animate().rotationY(-90*scale/absScale)
                            .translationX(-scale*size.x/absScale).setDuration(0).start();
                }
                mContainer.animate().rotationY(0).scaleX(1).scaleY(1).alpha(1)
                        .translationX(0).setDuration(duration).start();
                mContext.mActionButton.animate().scaleX(1).scaleY(1).alpha(1).setDuration(duration).start();
                View t = mTabs.getChildAt(mTabInd);
                if(null!=t) t.setAlpha(.7f);
                mTabInd = finalMNextTabInd;
                mTabs.getChildAt(mTabInd).setAlpha(1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.start();
    }

    public void setTabs(ArrayList<String> tabs, final int tab){
        mTabs.removeAllViews();
        for(String t:tabs){
            Button b = new Button(mContext);
            b.setId(mTabs.getChildCount());
            b.setText(t);
            b.setAlpha(.7f);
            b.setSingleLine();
            b.setPadding(10*dp, 3* dp, 10* dp, 3*dp);
            b.setTextColor(Color.rgb(255,255,255));
            b.setBackgroundColor(Color.argb(0,0,0,0));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            p.weight = 1;
            b.setLayoutParams(p);
            b.setOnClickListener(v -> goToTab(v.getId()));
            mTabs.addView(b);
        }
        findViewById(R.id.dividerContainer).setLayoutParams(new FrameLayout.LayoutParams(mTabs.getMeasuredWidth(), 3*dp));
        mTabs.post(() -> {
            mDivider = findViewById(R.id.divider);
            goToTab(tab);
        });
    }
}
