package com.rdypda.presenter;

import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.activity.WydrckActivity;
import com.rdypda.view.viewinterface.IWydrckView;

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
 * Created by DengJf on 2018/3/15.
 */

public class WydrckPresenter extends BasePresenter {
    public static final int SCAN_TYPE_GDH = 1;
    public static final int SCAN_TYPE_TMBH = 2;
    private static final String TAG = WydrckActivity.class.getSimpleName();
    private String gdh = "";
    private IWydrckView view;
    private ScanUtil scanUtil;
    private String ftyIdAndstkId=";";
    private int startType=0;
    private int scanType = 0;


    public WydrckPresenter(Context context, final IWydrckView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                if (scanType == SCAN_TYPE_TMBH) {
                    String tmbh = new QrCodeUtil(result).getTmxh();
                    view.setTmEd(tmbh);
                    isValidCode(tmbh);
                }else if (scanType == SCAN_TYPE_GDH){
                    String gdh = new QrCodeUtil(result).getTmxh();
                    view.setGdhEd(gdh);
                    isValidGDH(gdh);
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
        getKc();
    }

    public void isValidGDH(final String gdh) {
        if (gdh.equals("")){
            view.showMsgDialog("请先输入工单号");
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

        String sql=String.format("Call Proc_PDA_SoValid('%s');", gdh);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    String result = value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg");
                    if (result.equals("OK")){
                        WydrckPresenter.this.gdh = gdh;
                        view.onQueryGdhSucceed(result);
                        view.showMsgDialog("工单号验证成功");
                    }
                    Log.d(TAG, "onNext: result"+result);
                    //String .put("sl",value.getJSONObject(0).getString("brp_Qty"));
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

    /**
     * 获取库存地点
     */
    public void getKc(){
        view.setShowProgressDialogEnable(true);
        String sql="Call Proc_PDA_GetStkList();";
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
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

    /**
     * 验证条码编号
     * @param tmbh
     */
    public void isValidCode(String tmbh){
        //如果是工单退货，应该先验证工单号
        if (startType == WydrckActivity.START_TYPE_GDTH) {
            if (gdh.equals("")) {
                view.showMsgDialog("请先输入工单号");
                return;
            }
        }
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
        String type = null;
        if (startType== WydrckActivity.START_TYPE_WYDRK){
            type="GDSH";
        }else if (startType == WydrckActivity.START_TYPE_WYDCK){
            type="CHWY";
        }else if (startType == WydrckActivity.START_TYPE_GDTH){
            type = "GDTH";
        }
        String sql = "";
        if (startType == WydrckActivity.START_TYPE_GDTH){
            sql = String.format("Call Proc_PDA_IsValidCode('%s','%s','%s','%s');",
                    tmbh,type,gdh,preferenUtil.getString("userId"));
        }else{
            sql = String.format("Call Proc_PDA_IsValidCode('%s','%s', '%s;%s;%s;' ,'%s');",
                    tmbh,type,kw[0],kw[1],kw[1],preferenUtil.getString("userId"));
        }

        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    Map<String,String>mapScan=new HashMap<>();
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
        String type="";
        if (startType==WydrckActivity.START_TYPE_WYDRK){
            type="GDSH";
        }else {
            type="CHWY";
        }
        String sql=String.format("Call Proc_PDA_CancelScan('%s', '%s', '%s');",type,tmxh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
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

    public void closeScanUtil(){
        scanUtil.close();
    }

    public void setFtyIdAndstkId(String ftyIdAndstkId) {
        this.ftyIdAndstkId = ftyIdAndstkId;
    }

    /**
     * 设置是出库，入库，还是工单退货
     * @param startType
     */
    public void setStartType(int startType) {
        this.startType = startType;
        //如果是工单退货，扫描类型设置为先扫工单号
        if (startType == WydrckActivity.START_TYPE_GDTH){
            startType = SCAN_TYPE_GDH;
        }
    }

    public void setScanTpte(int scanType) {
        this.scanType = scanType;
    }
}
