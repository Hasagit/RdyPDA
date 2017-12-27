package com.rdypda.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by DengJf on 2017/12/21.
 */

public class MyRecyclerView extends RecyclerView {
    private boolean onTouchEnable=true;
    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        return onTouchEnable;
    }

    public void setOnTouchEnable(boolean onTouchEnable) {
        this.onTouchEnable = onTouchEnable;
    }
}
