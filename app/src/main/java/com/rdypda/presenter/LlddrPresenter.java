package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.rdypda.model.network.WebService;
import com.rdypda.view.activity.FlTabActivity;
import com.rdypda.view.activity.WldActivity;
import com.rdypda.view.activity.YljsflActivity;
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

    public void queryDataByKey(final String lldh,String wldm,String ddbh,int starType){
         if ((!view.isFinishCheck())&(!view.isUnFinishCheck())){
             view.showToast("至少选中一种状态");
             return;
         }
         String status="1";
         if (view.isUnFinishCheck())status="1";
         if (view.isFinishCheck())status="2";
         if (view.isUnFinishCheck()&view.isFinishCheck())status="3";
         view.setProgressDialogEnable(true);
        String sql="";
         if (starType==MainPresenter.YLJS|starType==MainPresenter.YLTL){
             sql=String.format("Call Proc_PDA_Get_lld2('%s','%s','%s','',%s)",lldh,wldm,ddbh,status);
         }else {
             sql=String.format("Call Proc_PDA_Get_lld('%s','%s','%s','',%s)",lldh,wldm,ddbh,status);
         }
         WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
             @Override
             public void onSubscribe(Disposable d) {

             }

             @Override
             public void onNext(JSONObject value) {
                 try {
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
                             map.put("isCheck","0");
                             data.add(map);
                         }
                         view.showList(data);
                     }
                     view.setProgressDialogEnable(false);
                 } catch (JSONException e) {
                     e.printStackTrace();
                     view.showToast("查询出错！");
                 }finally {
                     view.setProgressDialogEnable(false);
                 }
             }

             @Override
             public void onError(Throwable e) {
                e.printStackTrace();
                view.showToast(e.getMessage());
                view.setProgressDialogEnable(false);
             }

             @Override
             public void onComplete() {

             }
         });


    }

    public void sureEvent(List<Map<String,String>>data,int startType){
        String lldhs="";
        if (!(startType==MainPresenter.YLTL|startType==MainPresenter.YLJS)){
            if (data.size()==0){
                view.showToast("请先选择一个或多个生产单号进行操作");
                return;
            }
            String kcdd=data.get(0).get("kcdd");
            boolean isKcddSame=true;
            for (int i=0;i<data.size();i++){
                if (!kcdd.equals(data.get(i).get("kcdd"))){
                    isKcddSame=false;
                }
                if (i+1!=data.size()){
                    lldhs=lldhs+data.get(i).get("djbh")+",";
                }else {
                    lldhs=lldhs+data.get(i).get("djbh");
                }
            }
            if (!isKcddSame){
                view.showToast("所选生产单号库存地点必须一致");
                return;
            }
        }

        if (startType== MainPresenter.TMDY){
            Intent intent=new Intent(context,WldActivity.class);
            intent.putExtra("djbh",lldhs);
            intent.putExtra("wldm","");
            intent.putExtra("startType",WldActivity.START_TYPE_LLD);
            context.startActivity(intent);
        }else if (startType== MainPresenter.FL){
            Intent intent=new Intent(context,FlTabActivity.class);
            intent.putExtra("djbh",lldhs);
            intent.putExtra("wldm","");
            context.startActivity(intent);
        }else if (startType== MainPresenter.YLTL){
            Intent intent=new Intent(context,YljsflActivity.class);
            intent.putExtra("djbh","");
            intent.putExtra("wldm","");
            intent.putExtra("startType",MainPresenter.YLTL);
            context.startActivity(intent);
        }else if (startType== MainPresenter.YLJS){
            Intent intent=new Intent(context,YljsflActivity.class);
            intent.putExtra("djbh","");
            intent.putExtra("wldm","");
            intent.putExtra("startType",MainPresenter.YLJS);
            context.startActivity(intent);
        }


    }

    public void getScanedData(){
        view.setProgressDialogEnable(true);
        String sql =String.format("Call Proc_PDA_GetScanList ('LLD','','%s')",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    if (array.length()>0){
                        Intent intent=new Intent(context,FlTabActivity.class);
                        intent.putExtra("djbh",array.getJSONObject(0).getString("scan_djbh"));
                        intent.putExtra("wldm","");
                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showToast(e.getMessage());
                }finally {
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setProgressDialogEnable(false);
                view.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    };
}
