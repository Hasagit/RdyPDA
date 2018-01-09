package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.IFlView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/4.
 */

public class FlPresenter extends BasePresenter {
    private IFlView view;
    private ScanUtil scanUtil;
    public FlPresenter(Context context, final IFlView view) {
        super(context);
        this.view=view;
        List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<20;i++){
            Map<String,String>item=new HashMap<>();
            item.put("wld","3333333");
            item.put("person","胡歌");
            item.put("time","2018-1-4");
            item.put("scaned","0");
            item.put("need","200");
            data.add(item);
        }
        view.refreshReceive(data);
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                /*Map<String,String> map=new HashMap<>();
                map.put("id",result);
                view.refreshReceive(map);*/
            }

            @Override
            public void onFail(String error) {

            }
        });
    }
}
