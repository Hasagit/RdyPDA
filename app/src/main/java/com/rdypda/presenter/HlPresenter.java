package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.IHlView;

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
 * Created by DengJf on 2018/1/10.
 */

public class HlPresenter extends BasePresenter {
    private IHlView view;
    private ScanUtil scanUtil;
    private String hljh="";

    public HlPresenter(Context context,IHlView view) {
        super(context);
        this.view=view;
        getSbmc("T03");
        getScanedData();
        initScanUtil();
    }

    public void initScanUtil(){
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                String tmxh=new QrCodeUtil(result).getTmxh();
                isValidCode(tmxh);
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    public void isValidCode(String tmxh){
        if (hljh.equals("")){
            view.showMsgDialog("设备明细不能为空");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_IsValidCode('%s', 'MTR_HL', '', '%s')",
                tmxh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    view.setShowProgressDialogEnable(false);
                    JSONArray array=value.getJSONArray("Table2");
                    view.showTmMsgDialog(hljh,
                            array.getJSONObject(0).getString("brp_Sn"),
                            array.getJSONObject(0).getString("brp_wldm"),
                            array.getJSONObject(0).getString("brp_pmgg"),
                            array.getJSONObject(0).getString("brp_Qty"));
                    getScanedData();
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

    public void getSbmc(String lbdm){
        String sql=String.format("Call Proc_PDA_Get_DeviceList('','%s');",lbdm);
        view.setShowProgressDialogEnable(true);
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    List<String>data=new ArrayList<>();
                    data.add("");
                    for (int i=0;i<array.length();i++){
                        data.add(array.getJSONObject(i).getString("lbm_lbdm"));
                    }
                    view.refreshSbmx(data);
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

    public void getScanedData(){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetScanList ('MTR_HL', '', '%s');",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    List<Map<String,String>>data=new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("lab_1",array.getJSONObject(i).getString("scan_tmxh"));
                        map.put("lab_2",array.getJSONObject(i).getString("scan_qty"));
                        map.put("lab_3",array.getJSONObject(i).getString("brp_wldm"));
                        map.put("lab_4",array.getJSONObject(i).getString("brp_pmgg"));
                        data.add(map);
                    }
                    view.refreshScanedList(data);
                    initHlData(data);

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

    public void delScanedData(String tmxh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_CancelScan('MTR_HL', '%s', '%s');",tmxh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                getScanedData();
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

    public void uploadQty(String tmxh,String qty,String tmsl){
        if (qty.equals("")){
            view.showMsgDialog("请先输入投料数量");
            return;
        }
        if (Double.parseDouble(qty)>Double.parseDouble(tmsl)){
            view.showMsgDialog("投料数量不能大于条码数量");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_HL_QtyUpdate('%s', %s, '%s')",tmxh,qty,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog("操作成功");
                getScanedData();
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

    public void uploadHl(){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_HL_Post('%s','%s');",hljh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    view.showMsgDialog("操作成功\n混料单号:"+
                            value.getJSONArray("Table1")
                                    .getJSONObject(0)
                                    .getString("cNewCode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getScanedData();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showMsgDialog(e.getMessage());
                view.setShowProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void closeScanUtil(){
        scanUtil.close();
    }

    public void setHljh(String hljh) {
        this.hljh = hljh;
    }

    public String getHljh() {
        return hljh;
    }

    public void initHlData(List<Map<String,String>>dataScan){
        List<Map<String,String>>dataSz=new ArrayList<>();
        List<Map<String,String>>dataYl=new ArrayList<>();
        List<Map<String,String>>dataHl=new ArrayList<>();
        for (int i=0;i<dataScan.size();i++){
            String twoNum=dataScan.get(i).get("lab_3").substring(0,2);
            if (twoNum.equals("81")){
                dataYl.add(dataScan.get(i));
            }else if (twoNum.equals("84")){
                dataSz.add(dataScan.get(i));
            }
        }
        for (int i=0;i<dataYl.size();i++){
            Map<String,String>map=new HashMap<>();
            map.put("wlgg",dataYl.get(i).get("lab_4"));
            map.put("wlbh",dataYl.get(i).get("lab_3"));
            double hlsl=0;
            if (dataYl.size()==dataSz.size()){
                hlsl=Double.parseDouble(dataYl.get(i).get("lab_2"))+Double.parseDouble(dataSz.get(i).get("lab_2"));
            }else {
                hlsl=Double.parseDouble(dataYl.get(i).get("lab_2"));
            }
            map.put("hlsl",hlsl+"");
            dataHl.add(map);
        }
        view.refreshHlList(dataHl);


    }
}
