package com.rdypda.presenter;

import android.content.Context;
import android.view.View;

import com.rdypda.model.network.WebService;
import com.rdypda.view.viewinterface.ILlddrView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DengJf on 2017/12/8.
 */

public class LlddrPresenter extends BasePresenter{
    private ILlddrView view;
    private List<Map<String,String>>data;
    public LlddrPresenter(Context context,ILlddrView view) {
        super(context);
        this.view=view;
        refreshListData();
    }

    public void refreshListData(){
    }

    public void queryDataByKey(final String lldh,String wldm,String ddbh){
         if (lldh.equals("")){
             view.showToast("领料单号不能为空");
             return;
         }
         view.setProgressDialogEnable(true);
         String sql=String.format("Call Proc_PDA_Get_lld('%s','%s','%s')",lldh,wldm,ddbh);
         WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
             @Override
             public void onSubscribe(Disposable d) {

             }

             @Override
             public void onNext(JSONObject value) {
                 try {
                     if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                         JSONArray array=value.getJSONArray("Table1");
                         if (array.length()==0){
                             view.showToast("没有符合查询条件的领料单");
                         }else {
                             List<Map<String,String>>data=new ArrayList<>();
                             for (int i=0;i<array.length();i++){
                                 Map<String,String>map=new HashMap<>();
                                 map.put("djbh",array.getJSONObject(i).getString("llm_djbh"));
                                 map.put("wldm",array.getJSONObject(i).getString("llm_wldm"));
                                 map.put("wlpm",array.getJSONObject(i).getString("itm_wlpm"));
                                 map.put("ywwlpm",array.getJSONObject(i).getString("itm_ywwlpm"));
                                 map.put("ddbh",array.getJSONObject(i).getString("llm_ddbh"));
                                 map.put("czdm",array.getJSONObject(i).getString("llm_czdm"));
                                 map.put("cust",array.getJSONObject(i).getString("llm_cust"));
                                 map.put("ddsl",array.getJSONObject(i).getString("llm_ddsl"));
                                 map.put("tzbh",array.getJSONObject(i).getString("llm_tzbh"));
                                 map.put("stk",array.getJSONObject(i).getString("llm_stk"));
                                 map.put("unit",array.getJSONObject(i).getString("llm_unit"));
                                 map.put("desc",array.getJSONObject(i).getString("llm_desc"));
                                 map.put("gxdm",array.getJSONObject(i).getString("llm_gxdm"));
                                 map.put("rwms",array.getJSONObject(i).getString("llm_rwms"));
                                 map.put("gzzx",array.getJSONObject(i).getString("llm_gzzx"));
                                 data.add(map);
                             }
                             view.showList(data);
                         }
                     }else {
                         view.showToast(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                     }
                     view.setProgressDialogEnable(false);
                 } catch (JSONException e) {
                     e.printStackTrace();
                     view.setProgressDialogEnable(false);
                 }
             }

             @Override
             public void onError(Throwable e) {
                e.printStackTrace();
                view.showToast("查询失败");
             }

             @Override
             public void onComplete() {

             }
         });


    }
}
