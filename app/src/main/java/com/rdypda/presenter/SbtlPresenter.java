package com.rdypda.presenter;

import android.app.AlertDialog;
import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.ISbtlView;

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
 * Created by DengJf on 2018/3/7.
 */

public class SbtlPresenter extends BasePresenter {
    private ISbtlView view;
    private ScanUtil scanUtil;
    public int SCAN_TYPE_SB=0;
    public int SCAN_TYPE_TM=1;
    private String sbbh="";
    private String wltm="";
    private int type=0;

    public SbtlPresenter(Context context,ISbtlView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                if (type==SCAN_TYPE_SB){
                    isValidDevice(result);
                }else if (type==SCAN_TYPE_TM){
                    isValidCode(new QrCodeUtil(result).getTmxh());
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    public void isValidDevice(final String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("设备编号不能为空！");
            return;
        }
        view.setSbbText("");
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_Get_DeviceList('%s','');",sbbh);
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    if (array.length()>0){
                        SbtlPresenter.this.sbbh=sbbh;
                        view.setSbbText(sbbh);
                        getScanList(sbbh);
                        view.showMsgDialog("设备验证成功！");
                        view.setSbRadioCheck(false);
                    }else {
                        SbtlPresenter.this.sbbh="";
                        view.setSbbText("");
                        getScanList(sbbh);
                        view.showMsgDialog("设备验证失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    SbtlPresenter.this.sbbh="";
                    view.setSbbText("");
                }

            }

            @Override
            public void onError(Throwable e) {
                SbtlPresenter.this.sbbh="";
                view.setSbbText("");
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void isValidCode(final String tmbh){
        if (tmbh.equals("")){
            view.showMsgDialog("物料条码不能为空！");
            return;
        }
        if (sbbh.equals("")){
            view.showMsgDialog("请先验证设备编号！");
            return;
        }
        view.setWltmText("");
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','MTR_TL','%s','%s');",tmbh,sbbh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                wltm=tmbh;
                view.setWltmText(tmbh);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    String tmbh=array.getJSONObject(0).getString("brp_Sn");
                    String ylbh=array.getJSONObject(0).getString("brp_wldm");
                    String ylgg=array.getJSONObject(0).getString("brp_pmgg");
                    String tmsl=array.getJSONObject(0).getString("brp_Qty");
                    getTlzs(tmbh,ylbh,ylgg,tmsl);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showMsgDialog(e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                wltm="";
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getTlzs(final String tmbh, final String ylbh, final String ylgg, final String tmsl){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetScanList ('MTR_TL', '%s', '');",sbbh);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    if (array.length()>0){
                        view.showScanDialog(tmbh,ylbh,ylgg,tmsl,
                                array.getJSONObject(0).getString("tld_tlsl"));
                    }else {
                        view.showScanDialog(tmbh,ylbh,ylgg,tmsl,"0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showScanDialog(tmbh,ylbh,ylgg,tmsl,"0");
                    view.showMsgDialog(e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                view.setShowProgressDialogEnable(false);
                view.showScanDialog(tmbh,ylbh,ylgg,tmsl,"0");
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void tlSure(String tmxh,String bzsl,String tmsl){
        if (bzsl.equals("")){
            view.showMsgDialog("包装数量不能为空！");
            return;
        }
        if (Double.parseDouble(bzsl)>Double.parseDouble(tmsl)){
            view.showMsgDialog("包装数量不能大于条码数量！");
            return;
        }
        if (sbbh.equals("")){
            view.showMsgDialog("设备编号不能为空！");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_QtyUpdate('%s','MTR_TL','%s',%s,'%s')",
                tmxh,sbbh,bzsl,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                getScanList(sbbh);
                view.showMsgDialog("操作成功！");
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

    public void getScanList(String sbbh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetScanList ('MTR_TL', '%s', '');",sbbh);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray arrayZs=value.getJSONArray("Table2");
                    JSONArray arrayScan=value.getJSONArray("Table3");
                    List<Map<String,String>>dataScan=new ArrayList<>();
                    for (int i=0;i<arrayScan.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("tmbh",arrayScan.getJSONObject(i).getString("tll_tmxh"));
                        map.put("tlsl",arrayScan.getJSONObject(i).getString("tll_tlsl"));
                        map.put("ylgg",arrayScan.getJSONObject(i).getString("itm_wlpm"));
                        map.put("jlsj",arrayScan.getJSONObject(i).getString("tll_jlrq"));
                        map.put("zyry",arrayScan.getJSONObject(i).getString("tll_jlrymc"));
                        dataScan.add(map);
                    }
                    List<Map<String,String>>dataZs=new ArrayList<>();
                    for (int i=0;i<arrayZs.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("ylgg",arrayZs.getJSONObject(i).getString("tld_wlpm"));
                        map.put("sbbh",arrayZs.getJSONObject(i).getString("tld_devid"));
                        map.put("zjls",arrayZs.getJSONObject(i).getString("tld_tlsl"));
                        map.put("yjys",arrayZs.getJSONObject(i).getString("tld_sysl"));
                        dataZs.add(map);
                    }
                    view.refreshZsList(dataZs);
                    view.refreshScanList(dataScan);
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void cancelScan(String tmbh, final AlertDialog dialog){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_CancelScan('MTR_TL', '%s','%s');",tmbh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                dialog.dismiss();
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
        if (scanUtil!=null){
            scanUtil.close();
        }
    }

    public void setType(int type) {
        this.type = type;
    }
}
