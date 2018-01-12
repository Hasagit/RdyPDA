package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IFirstView;

/**
 * Created by DengJf on 2018/1/12.
 */

public class FirstPresenter extends BasePresenter{
    IFirstView view;
    public FirstPresenter(Context context, IFirstView view) {
        super(context);
        this.view=view;

    }
}
