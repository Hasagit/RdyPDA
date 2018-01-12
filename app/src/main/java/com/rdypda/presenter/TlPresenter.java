package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.ITlView;

/**
 * Created by DengJf on 2018/1/11.
 */

public class TlPresenter extends  BasePresenter {
    private ITlView view;
    public TlPresenter(Context context,ITlView view) {
        super(context);
        this.view=view;
    }
}
