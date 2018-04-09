package com.rdypda.presenter;

import android.app.AlertDialog;
import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.activity.SbtlActivity;
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
    private int startType=0;
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
                    isValidDevice(result,startType);
                }else if (type==SCAN_TYPE_TM){
                    isValidCode(new QrCodeUtil(result).getTmxh());
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    //验证设备
    public void isValidDevice(final String sbbh, int startType){
        if (sbbh.equals("")){
            view.showMsgDialog("设备编号不能为空！");
            return;
        }
        view.setSbbText("");
        view.setShowProgressDialogEnable(true);
        String sblb="";
        if (startType== SbtlActivity.START_TYPE_SBTL){
            sblb="ZS";
        }else if (startType==SbtlActivity.START_TYPE_SYTOUL|startType==SbtlActivity.START_TYPE_SYTUIL){
            sblb="SY";
        }else if (startType==SbtlActivity.START_TYPE_ZZFL|startType==SbtlActivity.START_TYPE_ZZTL){
            sblb="ZZ";
        }
        String sql=String.format("Call Proc_PDA_Get_DeviceList('%s','%s');",sbbh,sblb);
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    if (array.length()==1){
                        SbtlPresenter.this.sbbh=sbbh;
                        SbtlPresenter.this.sbbh=array.getJSONObject(0).getString("lbm_lbdm");
                        view.setSbbText(array.getJSONObject(0).getString("lbm_lbmc"));
                        getScanList(sbbh);
                        view.showMsgDialog("验证成功！");
                        view.setSbRadioCheck(false);
                    }else if (array.length()>1){
                        String[] sbmc=new String[array.length()];
                        String[] sbdm=new String[array.length()];
                        for (int i=0;i<array.length();i++){
                            sbdm[i]=array.getJSONObject(i).getString("lbm_lbdm");
                            sbmc[i]=array.getJSONObject(i).getString("lbm_lbmc");
                        }
                        view.showQueryList(sbdm,sbmc);
                    } else {
                        SbtlPresenter.this.sbbh="";
                        view.setSbbText("");
                        getScanList(sbbh);
                        view.showMsgDialog("验证失败！");
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

    /**
     * 验证条码
     * @param tmbh 条码编号
     */
    //验证条码
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
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','MTR_TL','%s','%s');",tmbh,sbbh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.setWltmText(tmbh);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    //条码序号
                    String tmbh=array.getJSONObject(0).getString("brp_Sn");
                    //物料代码
                    String ylbh=array.getJSONObject(0).getString("brp_wldm");
                    //品名规格
                    String ylgg=array.getJSONObject(0).getString("brp_pmgg");
                    //条码数量
                    String tmsl=array.getJSONObject(0).getString("brp_Qty");
                    getTlzs(tmbh,ylbh,ylgg,tmsl);
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

    //获取投料总数列表
    /**
     * 获取投料总数
     * @param tmbh 条码编号
     * @param ylbh 原料编号
     * @param ylgg 原料规格
     * @param tmsl 条码数量
     */
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



    /**
     * //投料数量确认
     * @param tmxh 条码序号（条码编号）
     * @param bzsl 下料数量
     * @param tmsl 条码数量
     */
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
            ////
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

    //获取扫描列表
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

    //取消扫描记录
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

    public void setStartType(int startType) {
        this.startType = startType;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }
}
