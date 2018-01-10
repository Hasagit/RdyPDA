package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IYljsView;

/**
 * Created by DengJf on 2018/1/9.
 */

public class YlzckPresenter extends BasePresenter {
    private IYljsView view;
    public YlzckPresenter(Context context, IYljsView view) {
        super(context);
        this.view=view;
    }
}
