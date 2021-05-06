package k.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import k.R;
import k.k;

import static k.k.dp;
import static k.k.size;
import static k.k.wOffset;

public class d extends RelativeLayout implements View.OnTouchListener {
    private static float CLICK_DRAG_TOLERANCE = 10;
    final public static int Horizontal = 1, Vertical = 2;
    private float downX, downY, dX, drawerWidth, alphaMax = 1, sensitivity, directionSensitivity;
    private int scrollDirection = 0, direction, animDuration = 300, arteId;
    private k mContext;
    private View bg, arte;
    private Drawable shadow;

    @SuppressLint("RtlHardcoded")
    public d(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (k) context;
        CLICK_DRAG_TOLERANCE = 5 * dp;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.d, 0, 0);
        drawerWidth = 300 * dp;
        Drawable background;
        try {
            drawerWidth = a.getDimension(R.styleable.d_android_layout_width, 300 * dp);
            int g = a.getInteger(R.styleable.d_android_layout_gravity, Gravity.START);
            direction = a.getBoolean(R.styleable.d_onRightEdge, false) ? -1 :
                    (g & Gravity.END) == Gravity.END || (g & Gravity.RIGHT) == Gravity.RIGHT ? -1 : 1;
            background = a.getDrawable(R.styleable.d_android_background);
            shadow = a.getDrawable(R.styleable.d_shadowing_background);
            arteId = a.getResourceId(R.styleable.d_animated_arte, -1);
            sensitivity = a.getFloat(R.styleable.d_sensitivity, 1);
            directionSensitivity = a.getFloat(R.styleable.d_direction_sensitivity, 1);
            if (sensitivity == 0) sensitivity = 1;
            if (directionSensitivity == 0) directionSensitivity = 1;
        } finally {
            a.recycle();
        }
        if (Math.abs(getPaddingRight() - getPaddingLeft()) < 20 * dp) {
            if (direction == 1)
                setPadding(getPaddingLeft(), getPaddingTop(), (int) (getPaddingRight() + size.x - drawerWidth), getPaddingBottom());
            else
                setPadding((int) (getPaddingLeft() + size.x - drawerWidth), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        } else {
            if (direction == 1)
                drawerWidth = size.x - getPaddingRight();
            else
                drawerWidth = size.x - getPaddingLeft();
        }

        View view = new View(context); //added to make sure viewport is filled
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (3000 * dp)));
        view.setBackgroundColor(Color.rgb(240, 240, 240));
        if (background != null)
            view.setBackground(background);

        setBackgroundColor(Color.argb(0, 0, 0, 0));
        addView(view, 0);

        setOnTouchListener(this);
        collapse();
        post(() -> {
//                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = -1;
            params.width = -1;
            setLayoutParams(params);
            if (arteId >= 0) arte = findViewById(arteId);
        });
    }

    public void setPad() {
        if (direction == 1)
            setPadding(getPaddingLeft(), getPaddingTop(), (int) (size.x - 2 * wOffset - drawerWidth), getPaddingBottom());
        else
            setPadding((int) (size.x - drawerWidth - 2 * wOffset), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        collapse();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            scrollDirection = 0;
            downX = ev.getRawX();
            downY = ev.getRawY();
            dX = this.getX() - downX;
            peep();
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (scrollDirection == 0) {
                scrollDirection = Math.abs(ev.getRawX() - downX) > CLICK_DRAG_TOLERANCE / directionSensitivity ? Horizontal
                        : Math.abs(ev.getRawY() - downY) > CLICK_DRAG_TOLERANCE / directionSensitivity ? Vertical : 0;
            } else if (scrollDirection == Horizontal) {
                return onTouch(this, ev);
            }
        } else if (action == MotionEvent.ACTION_UP && scrollDirection != 0 && scrollDirection != Vertical)
            return onTouch(this, ev);
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        int action = ev.getAction();
        if (bg == null) {
            View view1 = new View(mContext);
            view1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            view1.setBackgroundColor(Color.argb(150, 0, 0, 0));
            if (shadow != null)
                view1.setBackground(shadow);
            view1.setAlpha(0);
            ViewGroup parent = (ViewGroup) getParent();
            parent.addView(view1, parent.indexOfChild(this));
            bg = view1;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            downX = ev.getRawX();
            dX = this.getX() - downX;
            peep();
        } else if (action == MotionEvent.ACTION_MOVE) {
            float newX = ev.getRawX() + dX;
            newX = direction * Math.min(0, direction * newX);
            if (arte != null) {
                if (direction == 1)
                    arte.setPadding((int) -newX, 0, 0, 0);
                else
                    arte.setPadding(0, 0, (int) newX, 0);
            }
//            view.setLayoutParams(new FrameLayout.LayoutParams((int) (size.x + direction*newX), ViewGroup.LayoutParams.MATCH_PARENT));
            view.animate().x(newX).setDuration(0).start();
            if (bg != null)
                bg.animate().alpha(alphaMax + direction * newX / drawerWidth).setDuration(0).start();
        } else if (action == MotionEvent.ACTION_UP) {
            float upRawX = ev.getRawX();
            if (direction == 1 ? downX > drawerWidth : downX < getWidth() - drawerWidth) collapse();
            else if (direction * (upRawX - downX) > 5 * CLICK_DRAG_TOLERANCE / sensitivity)
                expand();
            else if (direction * (downX - upRawX) > 5 * CLICK_DRAG_TOLERANCE / sensitivity)
                collapse();
            else if (Math.abs(this.getX()) > drawerWidth - 30 * dp) collapse();
            else expand();
        }
        return true;
    }

    public void collapse() {
        if (bg != null) bg.animate().alpha(0).setDuration(animDuration).start();
        if (direction == -1)
            this.animate().x(size.x - 10 * dp - 1.8f * wOffset).setDuration(animDuration).start();
        else {
            this.animate().x(-size.x + 20 * dp + 2 * wOffset).setDuration(animDuration).start();
        }
        setArteAnimation();
        if (mContext.dMenu != null)
            mContext.dMenu.setAdapter(mContext.dMenuAdapter);
    }

    public void disableDrawer() {
        this.animate().x(-direction * this.getWidth()).setDuration(0).start();
        if (bg != null) bg.animate().alpha(0).setDuration(0).start();
    }

    private void peep() {
        if (this.getX() != 0) {
            float newX = direction * (-drawerWidth + 20 * dp);
            this.animate().x(newX).setDuration(animDuration).start();
            if (bg != null)
                bg.animate().alpha(alphaMax + direction * newX / drawerWidth).setDuration(animDuration).start();
            dX += direction * (getWidth() - drawerWidth + 20 * dp);
            if (arte != null) {
                if (direction == 1)
                    arte.setPadding((int) -newX, 0, 0, 0);
                else
                    arte.setPadding(0, 0, (int) newX, 0);
            }
        }
    }

    public void expand() {
        this.animate().x(0).setDuration(animDuration).start();
        if (bg != null) bg.animate().alpha(alphaMax).setDuration(animDuration).start();
        setArteAnimation();
    }

    private void setArteAnimation() {
        if (arte != null) {
            ValueAnimator animator = new ValueAnimator();
            animator.setDuration(animDuration);
            animator.setFloatValues(0, 1);
            if (direction == 1) {
                animator.addUpdateListener(animation -> arte.setPadding((int) Math.abs(getX()), 0, 0, 0));
            } else {
                animator.addUpdateListener(animation -> arte.setPadding(0, 0, (int) Math.abs(getX()), 0));
            }
            animator.start();
        }
    }

    public boolean isCollapsed() {
        return this.getX() != 0;
    }
}