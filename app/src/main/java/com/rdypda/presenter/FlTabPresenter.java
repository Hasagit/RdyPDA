package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IFlTabView;

/**
 * Created by DengJf on 2018/1/23.
 */

public class FlTabPresenter extends BasePresenter {
    private IFlTabView view;

    public FlTabPresenter(Context context,IFlTabView view) {
        super(context);
        this.view=view;
    }

}
