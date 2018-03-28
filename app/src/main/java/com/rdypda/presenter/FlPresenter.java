package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.IFlView;

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
 * Created by DengJf on 2018/1/4.
 */

public class FlPresenter extends BasePresenter {
    private IFlView view;
    private ScanUtil scanUtil;
    private String lldh,wldm;


    public FlPresenter(Context context, final IFlView view) {
        super(context);
        this.view=view;
        initScan(context);
        setScanNum(0);
    }

    public void initScan(Context context){
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                isValidCode(new QrCodeUtil(result).getTmxh());
            }

            @Override
            public void onFail(String error) {
                String r=error;
            }
        });
    }

    public void getScanedData(){
        view.setShowProgressEnable(true);
        String sql =String.format("Call Proc_PDA_GetScanList ('LLD','','%s')",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    List<Map<String,String>>data=new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("tmxh",array.getJSONObject(i).getString("scan_tmxh"));
                        map.put("tmsl",array.getJSONObject(i).getString("scan_qty"));
                        map.put("wldm",array.getJSONObject(i).getString("brp_wldm"));
                        data.add(map);
                    }
                    view.refreshReceive(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("数据解析出错",true);
                }finally {
                    view.setShowProgressEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressEnable(false);
                view.setShowMsgDialogEnable(e.getMessage(),true);
            }

            @Override
            public void onComplete() {

            }
        });
    };

    public void deleteData(){
        view.setShowProgressEnable(true);
        String sql=String.format("Call Proc_PDA_CancelScan('LLD', '', '%s')",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                getScanedData();
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressEnable(false);
                view.setShowMsgDialogEnable(e.getMessage(),true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void uploadScanWld(){
        view.setShowProgressEnable(true);
        String sql=String.format("Call Proc_PDA_LLD_Post('%s')",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.refreshReceive(new ArrayList<Map<String, String>>());
                view.setShowProgressEnable(false);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowMsgDialogEnable(e.getMessage(),true);
                view.setShowProgressEnable(false);

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void isValidCode(final String tmxh){
        view.setShowProgressEnable(true);
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','LLD', '%s', '%s')",
                tmxh,lldh,preferenUtil.getString("userId"));
        view.setShowProgressEnable(true);
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    view.setShowProgressEnable(false);
                    JSONArray array=value.getJSONArray("Table2");
                    if (array.length()>0){
                        Map<String,String>map=new HashMap<>();
                        map.put("tmxh",tmxh);
                        map.put("tmsl",array.getJSONObject(0).getString("brp_Qty"));
                        map.put("wldm",array.getJSONObject(0).getString("brp_wldm"));
                        view.addReceiveData(map);
                    }else {
                        view.setShowMsgDialogEnable("条码验证异常",true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowProgressEnable(false);
                    view.setShowMsgDialogEnable("验证失败，请重试",true);
                }finally {
                    view.setShowProgressEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressEnable(false);
                view.setShowMsgDialogEnable(e.getMessage(),true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void closeScan(){
        scanUtil.close();
    }

    public void setLldh(String lldh) {
        this.lldh = lldh;
    }

    public void setWldm(String wldm) {
        this.wldm = wldm;
    }

    public void sendUploadFinishReceiver(){
        Intent intent=new Intent();
        intent.setAction("com.rdypda.UPDATEWLD");
        context.sendBroadcast(intent);
    }

    public void setScanNum(int num){
        preferenUtil.setInt("scanNum",num);
    }
}
