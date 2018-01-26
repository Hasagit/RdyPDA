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
    }

    public void initScan(Context context){
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                isValidCode(result);
            }

            @Override
            public void onFail(String error) {
                String r=error;
            }
        });
    }

    public void getScanedData(String lldh, final String wldm){
        view.setShowProgressEnable(true);
        String sql =String.format("Call Proc_PDA_GetScanList ('LLD','%s','%s')",lldh+";"+wldm,preferenUtil.getString("userId"));
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        String cRetMsg=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg");
                        String[] item=cRetMsg.split(":");
                        if (item[0].equals("OK")){
                            JSONArray array=value.getJSONArray("Table2");
                            List<Map<String,String>>data=new ArrayList<>();
                            for (int i=0;i<array.length();i++){
                                Map<String,String>map=new HashMap<>();
                                map.put("tmxh",array.getJSONObject(i).getString("scan_tmxh"));
                                map.put("tmsl",array.getJSONObject(i).getString("scan_qty"));
                                map.put("wldm",wldm);
                                data.add(map);
                            }
                            view.refreshReceive(data);
                        }else {
                            view.setShowMsgDialogEnable(item[1],true);
                        }
                    }else {
                        view.setShowMsgDialogEnable(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"),true);
                    }
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
            }

            @Override
            public void onComplete() {

            }
        });
    };

    public void deleteData(final String lldh, final String wldm){
        view.setShowProgressEnable(true);
        String sql=String.format("Call Proc_PDA_CancelScan('LLD', '%s', '%s')",lldh+";"+wldm,preferenUtil.getString("userId"));
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        String cRetMsg=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg");
                        String[] item=cRetMsg.split(":");
                        if (item[0].equals("OK")){
                            getScanedData(lldh,wldm);
                        }else {
                            view.setShowMsgDialogEnable(item[1],true);
                        }
                    }else {
                        view.setShowMsgDialogEnable(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"),true);
                    }
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
                view.setShowMsgDialogEnable("服务器异常",true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void uploadScanWld(List<Map<String,String>>data){
        view.setShowProgressEnable(true);
        WebService.uploadScanWld(data,lldh,preferenUtil.getString("userId"),preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        String[] items=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg").split(":");
                        if (items[0].equals("OK")){
                            Map<String,String>map=new HashMap<>();
                            JSONObject object=value.getJSONObject("Table2");
                            map.put("wldm",object.getString("wldm"));
                            map.put("tmxh",object.getString("tmxh"));
                            map.put("tmsl",object.getString("tmsl"));
                            view.removeReceiveData(map);
                        }else {
                            view.setShowMsgDialogEnable(items[1],true);
                        }
                    }else {
                        view.setShowMsgDialogEnable(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"),true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("数据解析出错",true);
                    view.setShowProgressEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                view.setShowMsgDialogEnable("数据解析出错",true);
                view.setShowProgressEnable(false);
            }

            @Override
            public void onComplete() {
                view.setShowProgressEnable(false);
                sendUploadFinishReceiver();
            }
        });
    }

    public void isValidCode(String qrCode){
        view.setShowProgressEnable(true);
        QrCodeUtil qrCodeUtil=new QrCodeUtil(qrCode);
        final String tmxh=qrCodeUtil.getTmxh();
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','LLD', '%s', '%s')",
                tmxh,lldh+";"+wldm,preferenUtil.getString("userId"));
        view.setShowProgressEnable(true);
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    view.setShowProgressEnable(false);
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        String result=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg");
                        String[] items=result.split(":");
                        if (items[0].equals("OK")){
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
                        }else {
                            view.setShowMsgDialogEnable(items[1],true);
                        }
                    }else {
                        view.setShowMsgDialogEnable(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"),true);
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
                view.setShowMsgDialogEnable("验证失败，请重试",true);
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
}
