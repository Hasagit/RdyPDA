package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.ITmcxView;

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
 * Created by DengJf on 2018/1/30.
 */

public class TmcxPresenter extends BasePresenter{
    private ITmcxView view;
    private ScanUtil scanUtil;
    public TmcxPresenter(Context context,ITmcxView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                barcodeQuery(new QrCodeUtil(result).getTmxh());
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    public void barcodeQuery(String tmxh){
        if (tmxh.equals("")){
            view.setShowMsgDialogEnable("请先输入条码序号");
            return;
        }
        view.setTmxxMsg(null);
        view.refreshKcsw(new ArrayList<Map<String, String>>());
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_BarcodeQuery('%s')",tmxh);
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    JSONArray arrayTmxx=value.getJSONArray("Table2");
                    JSONArray arrayKcsw=value.getJSONArray("Table3");
                    Map<String,String>tmxxMap=new HashMap<>();
                    tmxxMap.put("wjbh",arrayTmxx.getJSONObject(0).getString("brp_DocNo"));
                    tmxxMap.put("wldm",arrayTmxx.getJSONObject(0).getString("brp_wldm"));
                    tmxxMap.put("ph",arrayTmxx.getJSONObject(0).getString("brp_LotNo"));
                    tmxxMap.put("sl",arrayTmxx.getJSONObject(0).getString("brp_Qty")
                            +arrayTmxx.getJSONObject(0).getString("brp_Unit"));
                    tmxxMap.put("pmgg",arrayTmxx.getJSONObject(0).getString("brp_pmgg"));
                    tmxxMap.put("scrq",arrayTmxx.getJSONObject(0).getString("brp_Prd_Date"));
                    tmxxMap.put("gc",arrayTmxx.getJSONObject(0).getString("brp_FtyId"));
                    tmxxMap.put("kcdd",arrayTmxx.getJSONObject(0).getString("brp_StkId"));
                    tmxxMap.put("dycs",arrayTmxx.getJSONObject(0).getString("brp_dycs"));
                    tmxxMap.put("qrcode",arrayTmxx.getJSONObject(0).getString("brp_QrCode"));
                    view.setTmxxMsg(tmxxMap);
                    List<Map<String,String>>kcswData=new ArrayList<>();
                    for (int i=0;i<arrayKcsw.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("swm",arrayKcsw.getJSONObject(i).getString("trx_yydm"));
                        map.put("swdh",arrayKcsw.getJSONObject(i).getString("trx_swdh"));
                        map.put("rq",arrayKcsw.getJSONObject(i).getString("trx_swrq"));
                        map.put("wldm",arrayKcsw.getJSONObject(i).getString("trx_wldm"));
                        map.put("ph",arrayKcsw.getJSONObject(i).getString("trx_LotNo"));
                        map.put("kcdd",arrayKcsw.getJSONObject(i).getString("trx_stkId"));
                        map.put("kw",arrayKcsw.getJSONObject(i).getString("trx_kwdm"));
                        map.put("cw",arrayKcsw.getJSONObject(i).getString("trx_cwdm"));
                        map.put("swqsl",arrayKcsw.getJSONObject(i).getString("trx_kcsl"));
                        map.put("swsl",arrayKcsw.getJSONObject(i).getString("trx_swsl"));
                        map.put("jysl",arrayKcsw.getJSONObject(i).getString("trx_sysl"));
                        map.put("czry",arrayKcsw.getJSONObject(i).getString("trx_jlrymc"));
                        map.put("czrq",arrayKcsw.getJSONObject(i).getString("trx_jlrq"));
                        kcswData.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    view.setShowProgressDialogEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.setShowMsgDialogEnable(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void closeScan(){
        scanUtil.close();
    }
}
