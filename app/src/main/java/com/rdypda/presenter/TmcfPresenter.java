package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.ITmcfView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DengJf on 2018/1/25.
 */

public class TmcfPresenter extends BasePresenter {
    private ITmcfView view;
    private ScanUtil scanUtil;
    private String printMsg,wldm,pmgg,pch,xtmxh,szgg,ylgg,bzsl,date,zyry;


    public TmcfPresenter(Context context,ITmcfView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                isValidCode(new QrCodeUtil(result).getTmxh(),preferenUtil.getString("userId"));
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    //条码验证
    public void isValidCode(final String tmxh, String userId){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','SPLIT','','%s')",tmxh,userId);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    view.setOldCodeMsg(array.getJSONObject(0).getString("brp_Qty"),
                            array.getJSONObject(0).getString("brp_Sn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    view.setShowProgressDialogEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                view.setShowMsgDialogEnable(e.getMessage(),true);
                view.setShowProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //获取拆分条码序号

    /**
     *
     * @param tmxh 条码号
     * @param ytmsl 原条码数量
     * @param xtmsl 拆分数量
     */
    public void getTmxh(final String tmxh, String ytmsl, final String xtmsl){
        if (tmxh.equals("")){
            view.setShowMsgDialogEnable("请先扫描拆分条码",true);
            return;
        }
        if (ytmsl.equals("")){
            view.setShowMsgDialogEnable("请先扫描拆分条码",true);
            return;
        }
        if (xtmsl.equals("")){
            view.setShowMsgDialogEnable("请先输入拆分数量",true);
            return;
        }
        final double cf_1=Double.parseDouble(ytmsl)-Double.parseDouble(xtmsl);
        if (cf_1<=0){
            view.setShowMsgDialogEnable("拆分数量不能大于原条码数量",true);
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_SplitBarcode('%s', '%s', '%s')",tmxh,cf_1+","+xtmsl,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            //
            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    view.setNewCodeMsg(cf_1+","+xtmsl,array.getJSONObject(0).getString("brp_Sn"));
                    printMsg=array.getJSONObject(0).getString("brp_QrCode");
                    //新条码序号
                    xtmxh=array.getJSONObject(0).getString("brp_Sn");
                    pch=array.getJSONObject(0).getString("brp_LotNo");
                    wldm=array.getJSONObject(0).getString("brp_wldm");
                    //品名规格
                    pmgg=array.getJSONObject(0).getString("brp_pmgg");

                    szgg=array.getJSONObject(0).getString("sz_wlgg");
                    ylgg=array.getJSONObject(0).getString("wl_wlgg");
                    date=array.getJSONObject(0).getString("brp_Prd_Date");
                    bzsl=array.getJSONObject(0).getString("brp_Qty")+array.getJSONObject(0).getString("brp_Unit");
                    zyry=array.getJSONObject(0).getString("brp_Rec_Name");
                    printEven();
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.setShowMsgDialogEnable("数据解析出错",true);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.setShowMsgDialogEnable(e.getMessage(),true);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //打印事件
    public void printEven(){
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            view.showBlueToothAddressDialog();
            return;
        }

        if (preferenUtil.getString("blueToothAddress").equals("")){
            view.showBlueToothAddressDialog();
            return;
        }
        if (printMsg==null){
            view.setShowMsgDialogEnable("请先获取条码序号",true);
            return;
        }
        String[] pmggItem=pmgg.split(",");
        final String wlpm_1=pmggItem[0];
        String wlpm_2="";
        for (int i=1;i<pmggItem.length;i++){
            wlpm_2=wlpm_2+pmggItem[i]+"\t";
        }
        final String wlpm_3=wlpm_2;
        view.setShowProgressDialogEnable(true);
        final PrinterUtil util=new PrinterUtil(context);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (xtmxh.substring(0,2).equals("HL")){
                    String address=preferenUtil.getString("blueToothAddress");
                    util.openPort(address);
                    util.printFont("原料规格:"+ylgg.trim(),15,55);
                    util.printFont("色种规格:"+szgg.trim()+",",15,100);
                    util.printFont("作业人员:"+zyry.trim(),15,145);
                    util.printFont("生产日期:"+date.trim(),15,190);
                    util.printFont("包装数量:"+bzsl.trim(),15,235);
                    util.printFont("条码编号:"+xtmxh.trim(),15,280);
                    util.printQRCode(printMsg,335,135,5);
                    util.startPrint();
                    Log.e("printMsg",printMsg);
                    e.onNext("");
                    e.onComplete();
                }else {
                    String address=preferenUtil.getString("blueToothAddress");
                    util.openPort(address);
                    util.printFont("原料编号:"+wldm.trim(),15,55);
                    util.printFont("品名规格:"+wlpm_1.trim()+",",15,105);
                    util.printFont(wlpm_3.trim()+" ",15,140);
                    util.printFont("批次号:"+pch.trim(),15,185);
                    util.printFont("条码编号:"+xtmxh.trim(),15,235);
                    util.printQRCode(printMsg,340,55,6);
                    util.startPrint();
                    Log.e("printMsg",printMsg);
                    e.onNext("");
                    e.onComplete();
                }
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
                view.setShowMsgDialogEnable("打印出错！",true);
            }

            @Override
            public void onComplete() {
                view.setShowProgressDialogEnable(false);
            }
        });


    }

    public void closeScan(){
        scanUtil.close();
    }

}
