package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.activity.WydrckActivity;
import com.rdypda.view.viewinterface.IYkView;

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
 * Created by DengJf on 2018/3/16.
 */

public class YkPresenter extends BasePresenter {
    private IYkView view;
    private ScanUtil scanUtil;
    private String ftyIdAndstkId=";";

    public YkPresenter(Context context, final IYkView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                String tmbh=new QrCodeUtil(result).getTmxh();
                view.setTmEd(tmbh);
                isValidCode(tmbh);
            }

            @Override
            public void onFail(String error) {

            }
        });
        getKc();
    }

    public void getKc(){
        view.setShowProgressDialogEnable(true);
        String sql="Call Proc_PDA_GetStkList();";
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    List<String> data=new ArrayList<>();
                    List<String>dataMc=new ArrayList<>();
                    data.add(";");
                    dataMc.add("");
                    for (int i=0;i<array.length();i++){
                        data.add(array.getJSONObject(i).getString("stk_ftyId")+";"+
                                array.getJSONObject(i).getString("stk_stkId"));
                        dataMc.add(array.getJSONObject(i).getString("stk_stkmc"));
                    }
                    view.refreshJskwSp(dataMc,data);
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

    public void isValidCode(String tmbh){
        if (tmbh.equals("")){
            view.showMsgDialog("请先输入条码编号");
            return;
        }
        if (ftyIdAndstkId.equals(";")){
            view.showMsgDialog("请先选择接收库位");
            return;
        }
        String[]kw=ftyIdAndstkId.split(";");
        if (kw.length<2){
            view.showMsgDialog("接收库位解析失败");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String type;
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','WYYK', '%s;%s;%s; ', '%s');",
                tmbh,kw[0],kw[1],kw[1],preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    Map<String,String> mapScan=new HashMap<>();
                    Map<String,String>mapZs=new HashMap<>();
                    mapScan.put("tmbh",array.getJSONObject(0).getString("brp_Sn"));
                    mapScan.put("sl",array.getJSONObject(0).getString("brp_Qty"));
                    mapScan.put("wlbh",array.getJSONObject(0).getString("brp_wldm"));
                    mapZs.put("sl",array.getJSONObject(0).getString("brp_Qty"));
                    mapZs.put("wlbh",array.getJSONObject(0).getString("brp_wldm"));
                    mapZs.put("wlgg",array.getJSONObject(0).getString("brp_pmgg"));
                    view.addScanData(mapScan);
                    view.addZsData(mapZs);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showMsgDialog(e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
                e.printStackTrace();

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void cancelScan(final String tmxh, final String wlbh, final String tmsl){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_CancelScan('WYYK', '%s', '%s');",tmxh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.removeScanData(tmxh);
                view.removeZsData(wlbh,tmsl);
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

    public void setFtyIdAndstkId(String ftyIdAndstkId) {
        this.ftyIdAndstkId = ftyIdAndstkId;
    }

    public void closeScanUtil(){
        scanUtil.close();
    }
}
