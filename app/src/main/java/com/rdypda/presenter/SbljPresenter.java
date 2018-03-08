package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.ISbljView;
import com.rdypda.view.widget.PowerButton;

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
 * Created by DengJf on 2018/3/8.
 */

public class SbljPresenter extends BasePresenter{
    private ISbljView view;
    private ScanUtil scanUtil;
    private int type=0;
    public int SCAN_TYPE_JT=0;
    public int SCAN_TYPE_KL=1;
    public SbljPresenter(Context context, final ISbljView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                if (type==SCAN_TYPE_JT){
                    view.setJtKlText(result,view.getklbhText());
                    getConnectedDevice(result);
                }else if (type==SCAN_TYPE_KL){
                    if (view.getJtbhText().equals("")){
                        view.showMsgDialog("请先输入机台编号");
                        return;
                    }
                    view.setJtKlText(view.getJtbhText(),view.getklbhText());
                    connectDevice(view.getJtbhText(),result);
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
        getConnectedDevice("");
    }

    public void getConnectedDevice(String sbbh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_Get_JtmList('%s');",sbbh);
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    List<Map<String,String>>data=new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("jtbh",array.getJSONObject(i).getString("jtm_jtbh"));
                        map.put("jtmc",array.getJSONObject(i).getString("jtm_jtmc"));
                        map.put("ybdkl",array.getJSONObject(i).getString("jtm_devlist"));
                        data.add(map);
                    }
                    view.refreshSblj(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showMsgDialog(e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void connectDevice(final String jtbh, String klbh){
        if (jtbh.equals("")){
            view.showMsgDialog("请先输入机台编号");
            return;
        }
        if (klbh.equals("")){
            view.showMsgDialog("请先输入烤炉编号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_Jtm_Dev_Binding('%s', '%s', '%s');",jtbh,klbh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog("操作成功！");
                getConnectedDevice(jtbh);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void setType(int type) {
        this.type = type;
    }

}
