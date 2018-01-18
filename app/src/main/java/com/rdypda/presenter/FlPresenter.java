package com.rdypda.presenter;

import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
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

        initScan();

    }

    public void initScan(){
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


    public void isValidCode(String qrCode){
        String[] items=qrCode.split("\\*");
        final String tmxh=items[2].substring(1,items[2].length());
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
                        JSONArray array=value.getJSONArray("Table1");
                        String result=array.getJSONObject(0).getString("cRetMsg");
                        String[] items=result.split(":");
                        if (items[0].equals("OK")){
                            //获取物料明细
                            List<Map<String,String>>data=new ArrayList<>();
                            for (int i=0;i<array.length();i++){
                                Map<String,String>map=new HashMap<>();
                                map.put("tmxh",tmxh);
                                map.put("kcdd",array.getJSONObject(i).getString("brp_StkId"));
                                map.put("wldm",array.getJSONObject(i).getString("brp_wldm"));
                               /* map.put("FtyId",array.getJSONObject(i).getString("brp_FtyId"));
                                map.put("StkId",array.getJSONObject(i).getString("brp_StkId"));
                                map.put("wldm",array.getJSONObject(i).getString("brp_wldm"));
                                map.put("DocNo",array.getJSONObject(i).getString("brp_DocNo"));
                                map.put("Qty",array.getJSONObject(i).getString("brp_Qty"));
                                map.put("Unit",array.getJSONObject(i).getString("brp_Unit"));
                                map.put("LotNo",array.getJSONObject(i).getString("brp_LotNo"));
                                map.put("Date",array.getJSONObject(i).getString("brp_Date"));
                                map.put("QrCode",array.getJSONObject(i).getString("brp_QrCode"));
                                map.put("Sn",array.getJSONObject(i).getString("brp_Sn"));
                                map.put("pmgg",array.getJSONObject(i).getString("brp_pmgg"));*/
                                data.add(map);
                            }
                            view.refreshReceive(data);





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
}
