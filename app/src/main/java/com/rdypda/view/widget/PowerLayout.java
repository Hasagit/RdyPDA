package com.rdypda.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by DengJf on 2018/1/2.
 */

public class PowerLayout extends LinearLayout {
    public PowerLayout(Context context) {
        super(context);
    }

    public PowerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_UP:
                setAlpha(1f);
                break;
        }
        return true;
    }
}
