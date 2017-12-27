package com.rdypda.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by DengJf on 2017/12/21.
 */

public class PowerButton extends android.support.v7.widget.AppCompatButton {
    public PowerButton(Context context) {
        super(context);
    }

    public PowerButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
