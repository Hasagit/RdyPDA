package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.view.viewinterface.IWldView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by DengJf on 2017/12/20.
 */

public class WldPresenter extends BasePresenter{
    private IWldView view;

    public WldPresenter(Context context, IWldView view) {
        super(context);
        this.view = view;
        this.context = context;
    }


    public void getLldDet(String djbh,String wldm){
        view.setProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_Get_lld_det('%s','%s')",djbh,wldm);
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        JSONArray array=value.getJSONArray("Table1");
                        List<Map<String,String>>data=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            Map<String,String>map=new HashMap<>();
                            map.put("djbh",array.getJSONObject(i).getString("lld_djbh"));
                            map.put("llm_wldm",array.getJSONObject(i).getString("lld_llm_wldm"));
                            map.put("ftyId",array.getJSONObject(i).getString("lld_ftyId"));
                            map.put("stkId",array.getJSONObject(i).getString("lld_stkId"));
                            map.put("wldm",array.getJSONObject(i).getString("lld_wldm"));
                            map.put("wlpm",array.getJSONObject(i).getString("itm_wlpm"));
                            map.put("ywwlpm",array.getJSONObject(i).getString("itm_ywwlpm"));
                            map.put("qty",array.getJSONObject(i).getString("lld_qty"));
                            map.put("unit",array.getJSONObject(i).getString("lld_unit"));
                            map.put("jlry",array.getJSONObject(i).getString("lld_jlry"));
                            map.put("jlrq",array.getJSONObject(i).getString("lld_jlrq"));
                            map.put("id",array.getJSONObject(i).getString("lld_id"));
                            map.put("ni_qty",array.getJSONObject(i).getString("lld_ni_qty"));
                            data.add(map);
                        }
                        view.refreshWldRecycler(data);
                    }else {
                       view.showToastMsg(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.setProgressDialogEnable(false);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {

            }
        });
    }



}
