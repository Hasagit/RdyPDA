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
         if ((!view.isFinishCheck())&(!view.isUnFinishCheck())){
             view.showToast("至少选中一种状态");
             return;
         }
         String status="1";
         if (view.isUnFinishCheck())status="1";
         if (view.isFinishCheck())status="2";
         if (view.isUnFinishCheck()&view.isFinishCheck())status="3";
         view.setProgressDialogEnable(true);
         String sql=String.format("Call Proc_PDA_Get_lld('%s','%s','%s','',%s)",lldh,wldm,ddbh,status);
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
                                 map.put("xsdh",array.getJSONObject(i).getString("llm_ddbh"));
                                 map.put("klrq",array.getJSONObject(i).getString("llm_ksrq"));
                                 map.put("kcdd",array.getJSONObject(i).getString("stk_id"));
                                 map.put("zt",array.getJSONObject(i).getString("llm_Status"));
                                 map.put("wldm",array.getJSONObject(i).getString("llm_wldm"));
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
                     view.showToast("查询出错！");
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
