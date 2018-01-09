package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IWldView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/20.
 */

public class WldPresenter extends BasePresenter{
    private IWldView view;

    public WldPresenter(Context context, IWldView view) {
        super(context);
        this.view = view;
        this.context = context;
        List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<20;i++){
            Map<String,String>map=new HashMap<>();
            map.put("id","11111"+i+""+i+1);
            data.add(map);
        }
        view.refreshWldRecycler(data);
    }



}
