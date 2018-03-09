package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.view.viewinterface.ILlddrMsgView;

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
 * Created by DengJf on 2017/12/26.
 */

public class LlddrMsgPresenter extends BasePresenter{
    private ILlddrMsgView view;
    private String printMsg;

    public LlddrMsgPresenter( Context context,ILlddrMsgView view) {
        super(context);
        this.view = view;
        this.context = context;
    }

    public void getTmxh(String tmpch,String tmsl,String lldh,String wlbh,String dw,String gch,String kcdd,String tmbh){
        if (tmpch.equals("")){
            view.showMessage("请先输入条码批次号");
            return;
        }
        if (tmsl.equals("")){
            view.showMessage("请先输入条码数量");
            return;
        }
        view.setProgressDialogEnable("获取中...",true);
        String sql=String.format("Call Proc_GenQrcode('MRP','MR','%s','%s','%s',%s,'%s','%s','%s','%s','','%s','')",
                lldh,wlbh,tmpch,tmsl,dw,gch,kcdd,kcdd,preferenUtil.getString("userId"));
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        JSONArray array=value.getJSONArray("Table1");
                        String result=array.getJSONObject(0).getString("cRetMsg");
                        String[] item0=result.split(":");
                        if (item0[0].equals("OK")){
                            String[] item1=item0[1].split(";");
                            view.setTmxhText(item1[0]);
                            printMsg=item1[1];
                        }else {
                            view.showMessage(item0[1]);
                        }
                    }else {
                        view.showMessage(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                    }
                } catch (JSONException e) {
                    view.showMessage("条码获取失败，请重试");
                    view.setProgressDialogEnable("",false);
                    e.printStackTrace();
                }
                view.setProgressDialogEnable("",false);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showMessage(e.getMessage());
                view.setProgressDialogEnable("",false);
            }

            @Override
            public void onComplete() {

            }
        });
    }



    public void printEven(final String wldm, final String wlpm, final String ywwlpm, final String tmbh, final String tmpch){

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            view.showBlueToothAddressDialog();
            return;
        }

        if (preferenUtil.getString("blueToothAddress").equals("")){
            view.showBlueToothAddressDialog();
            return;
        }
        if (tmpch.equals("")){
            view.showMessage("请先获取条码序号");
            return;
        }
        view.setProgressDialogEnable("打印中...",true);
        final PrinterUtil util=new PrinterUtil();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String address=preferenUtil.getString("blueToothAddress");
                util.openPort(address);
                util.printFont("原料编号:"+wldm.trim(),15,55);
                util.printFont("品名规格:"+wlpm.trim()+",",15,105);
                util.printFont(ywwlpm.trim()+" ",15,140);
                util.printFont("批次号:"+tmpch.trim(),15,185);
                util.printFont("条码编号:"+tmbh.trim(),15,235);
                util.printQRCode(printMsg,320,55,7);
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
                view.setProgressDialogEnable("",false);
                view.showMessage("打印出错！");
            }

            @Override
            public void onComplete() {
                view.setProgressDialogEnable("",false);
            }
        });


    }

}
