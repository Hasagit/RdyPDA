package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.IPddyView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DengJf on 2018/1/30.
 */

public class PddyPresenter extends BasePresenter{
    private static final String TAG = PddyPresenter.class.getSimpleName();
    private IPddyView view;
    private ScanUtil scanUtil;
    public PddyPresenter(Context context, IPddyView view) {
        super(context);
        this.view=view;
        getKwData();
        /*scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                barcodeQuery(new QrCodeUtil(result).getTmxh());
            }

            @Override
            public void onFail(String error) {

            }
        });*/
    }

    //查询条码
    /*public void barcodeQuery(String tmxh){
        if (tmxh.equals("")){
            view.setShowMsgDialogEnable("请先输入条码序号");
            return;
        }
        view.setTmxxMsg(null);
        view.refreshKcsw(new ArrayList<Map<String, String>>());
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_BarcodeQuery('%s')",tmxh);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
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
                    tmxxMap.put("kcdd",arrayTmxx.getJSONObject(0).getString("stk_stkmc"));
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
                        map.put("dw",arrayTmxx.getJSONObject(0).getString("brp_Unit"));
                        kcswData.add(map);
                    }
                    view.refreshKcsw(kcswData);

                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("Json数据解析出错");
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
    }*/


    public void closeScan(){
        scanUtil.close();
    }

    public void queryWlbh(String wlbh) {
        if (wlbh.equals("")){
            view.setShowMsgDialogEnable("请先输入物料编号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_Get_Item('%s','','')",wlbh);
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    List<Map<String,String>> wlbhData = new ArrayList<>();

                    JSONArray arrayWlxx=value.getJSONArray("Table1");
                    for (int i = 0; i<arrayWlxx.length(); i++){
                        Map<String,String> map = new HashMap<>();
                        JSONObject jsonObject = (JSONObject) arrayWlxx.get(i);
                        //物料编号
                        map.put("itm_wldm",jsonObject.getString("itm_wldm"));
                        //单位
                        map.put("itm_unit",jsonObject.getString("itm_unit"));
                        //物料规格
                        map.put("itm_wlgg",jsonObject.getString("itm_wlgg"));
                        //物料品名
                        map.put("itm_wlpm",jsonObject.getString("itm_wlpm"));
                        //物料英文品名
                        map.put("itm_ywwlpm",jsonObject.getString("itm_ywwlpm"));
                        wlbhData.add(map);
                    }
                    String wldmArr[] = new String[wlbhData.size()];
                    String dwArr[] = new String[wlbhData.size()];
                    String wlpmArr[] = new String[wlbhData.size()];
                    for (int i=0; i<wlbhData.size(); i++){
                        wldmArr[i] = wlbhData.get(i).get("itm_wldm");
                    }
                    view.onQueryWlbhSucceed(wldmArr,wlbhData);
                    view.setShowProgressDialogEnable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("Json数据解析出错");
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

    /**
     * 获取库位
     */
    public void getKwData() {
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
                    List<Map<String,String>> data=new ArrayList<>();
                    List<String>dataMc=new ArrayList<>();
                    dataMc.add("");
                    for (int i=0;i<array.length();i++){
                        Map<String,String> map = new HashMap<>();
                        map.put("stk_ftyId",array.getJSONObject(i).getString("stk_ftyId"));
                        map.put("stk_stkId",array.getJSONObject(i).getString("stk_stkId"));
                        data.add(map);
                        dataMc.add(array.getJSONObject(i).getString("stk_stkmc"));
                    }
                    view.onGetKwdataSucceed(dataMc,data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable(e.getMessage());
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

    /**
     * 获取条码
     * @param wlbh
     * @param scpc
     * @param bzsl
     * @param strDw
     * @param mapKw
     */
    public void getBarCode(String wlbh, String scpc, String bzsl, String strDw, Map<String, String> mapKw) {
        if (wlbh.equals("")){
            view.setShowMsgDialogEnable("请先输入物料编号");
            return;
        }
        if (scpc.equals("")){
            view.setShowMsgDialogEnable("请先输入生产批次");
            return;
        }
        if (bzsl.equals("")){
            view.setShowMsgDialogEnable("请先输入包装数量");
            return;
        }
        if (mapKw == null){
            view.setShowMsgDialogEnable("请先选择原料库位");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_GenQrcode('BRP','SK','','%s','%s','%s','%s','%s','%s','%s','','%s','')",wlbh,scpc,bzsl,strDw,mapKw.get("stk_ftyId"),mapKw.get("stk_stkId"),mapKw.get("stk_stkId"),preferenUtil.getString("usr_yhmc"));
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    String[] item=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg").split(":");
                    if (item.length>1){
                        String[]itemItem=item[1].split(";");
                        if (itemItem.length>1){
                            view.onGetBarCodeSucceed(itemItem[0],itemItem[1]);
                        }else {
                            view.setShowMsgDialogEnable(item[1]);
                        }
                    }else {
                        view.setShowMsgDialogEnable("数据解析出错");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("Json数据解析出错");
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

    public void printEvent(final String qrCode, final String wlpmChinese, final String wlpmEnlight) {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            view.showBlueToothAddressDialog();
            return;
        }
        if (preferenUtil.getString("blueToothAddress").equals("")){
            view.showBlueToothAddressDialog();
            return;
        }
        if (qrCode==null){
            view.setShowMsgDialogEnable("请先获取条码序号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        final PrinterUtil util=new PrinterUtil(context);
        final QrCodeUtil qrCodeUtil = new QrCodeUtil(qrCode);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                    String address=preferenUtil.getString("blueToothAddress");
                    util.openPort(address);
                    util.printFont("原料编号:"+qrCodeUtil.getWlbh(),15,55);
                    util.printFont("品名规格:"+wlpmChinese+",",15,105);
                    util.printFont(wlpmEnlight+" ",15,140);
                    util.printFont("批次号:"+qrCodeUtil.getScpc(),15,185);
                    util.printFont("条码编号:"+qrCodeUtil.getTmxh(),15,235);
                    util.printQRCode(qrCode,340,55,7);
                    util.startPrint();
                    Log.e("printMsg",qrCode);
                    e.onNext("");
                    e.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                //view.showMessage("打印完成");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.setShowMsgDialogEnable("打印出错！");
            }

            @Override
            public void onComplete() {
                view.setShowProgressDialogEnable(false);
            }
        });
    }
}
