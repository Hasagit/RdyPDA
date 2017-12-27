package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.cache.PreferenUtil;

/**
 * Created by DengJf on 2017/12/8.
 */

public class BasePresenter {
    protected PreferenUtil preferenUtil;
    protected  Context context;
    public BasePresenter(Context context) {
        this.context=context;
        preferenUtil=new PreferenUtil(context);
    }
}
