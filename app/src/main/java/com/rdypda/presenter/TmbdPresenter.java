package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.ITmbdView;

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
 * Created by DengJf on 2018/1/26.
 */

public class TmbdPresenter extends BasePresenter {
    private ITmbdView view;
    private ScanUtil scanUtil;
    private String wldm,pmgg,pch,xtmxh,printMsg;

    public TmbdPresenter(Context context,ITmbdView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();;
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                isValidCode(new QrCodeUtil(result).getTmxh());
            }

            @Override
            public void onFail(String error) {

            }
        });
    }


    public void isValidCode(String tmbh){
        String sql=String.format("Call Proc_PDA_IsValidCode('%s','REPRINT','','%s')",tmbh,preferenUtil.getString("userId"));
        view.setShowProgressDialogEnable(true);
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    splitBarcode(array.getJSONObject(0).getString("brp_Sn"),
                            array.getJSONObject(0).getString("brp_Qty"));
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
                view.setShowMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void splitBarcode(String tmxh,String tmsl){
        if (tmxh.equals("")){
            view.setShowMsgDialog("请先扫描条码或者手动录入条码");
            return;
        }
        if (tmsl.equals("")){
            view.setShowMsgDialog("请先扫描条码或者手动录入条码");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format(" Call Proc_PDA_SplitBarcode('%s', '%s', '%s')",tmxh,tmsl,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    JSONArray array=value.getJSONArray("Table2");
                    view.setTmMsg(array.getJSONObject(0).getString("brp_Sn"),
                            array.getJSONObject(0).getString("brp_wldm"),
                            array.getJSONObject(0).getString("brp_Qty"));
                    printMsg=array.getJSONObject(0).getString("brp_QrCode");
                    xtmxh=array.getJSONObject(0).getString("brp_Sn");
                    pch=array.getJSONObject(0).getString("brp_LotNo");
                    wldm=array.getJSONObject(0).getString("brp_wldm");
                    pmgg=array.getJSONObject(0).getString("brp_pmgg");
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
                view.setShowMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

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
            view.setShowMsgDialog("请先获取条码序号");
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
        final PrinterUtil util=new PrinterUtil();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String address=preferenUtil.getString("blueToothAddress");
                util.openPort(address);
                util.printFont("原料编号:"+wldm.trim(),15,55);
                util.printFont("品名规格:"+wlpm_1.trim()+",",15,105);
                util.printFont(wlpm_3.trim()+" ",15,140);
                util.printFont("批次号:"+pch.trim(),15,185);
                util.printFont("条码编号:"+xtmxh.trim(),15,235);
                util.printQRCode(printMsg,340,55);
                util.startPrint();
                Log.e("printMsg",printMsg);
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
                view.setShowMsgDialog("打印出错！");
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
