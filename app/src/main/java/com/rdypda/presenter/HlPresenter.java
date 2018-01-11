package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IHlView;

/**
 * Created by DengJf on 2018/1/10.
 */

public class HlPresenter extends BasePresenter {
    private IHlView view;


    public HlPresenter(Context context,IHlView view) {
        super(context);
        this.view=view;
    }
}
