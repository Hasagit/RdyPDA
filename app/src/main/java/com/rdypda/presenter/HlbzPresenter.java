package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.view.viewinterface.IHlbzView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Created by DengJf on 2018/3/5.
 */

public class HlbzPresenter extends BasePresenter {
    private IHlbzView view;
    private String hljh="";
    private String date="";

    public HlbzPresenter(Context context,IHlbzView view) {
        super(context);
        this.view=view;
        getSbmc("T03");
        getScrq();
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

    public void getScrq(){
        view.setShowProgressDialogEnable(true);
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String sql=String.format("Call Proc_check_Prod_Day('%s');",dateString);
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    date=value.getJSONArray("Table1").getJSONObject(0).getString("cRetDay");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(true);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void getHlqd(){
        if (hljh.equals("")){
            view.showMsgDialog("请先选择混料机号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetHlmList('%s','%s');",hljh,preferenUtil.getString("userId"));
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    List<Map<String,String>>data=new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("ylgg",array.getJSONObject(i).getString("wl_wlgg"));
                        map.put("szgg",array.getJSONObject(i).getString("sz_wlgg"));
                        map.put("dbzsl",array.getJSONObject(i).getString("hlm_osqty"));
                        map.put("hldh",array.getJSONObject(i).getString("hlm_djbh"));
                        map.put("zyry",array.getJSONObject(i).getString("hlm_jlrymc"));
                        map.put("hljh",array.getJSONObject(i).getString("hlm_jtbh"));
                        map.put("hlzl",array.getJSONObject(i).getString("hlm_qty"));
                        map.put("ybzsl",array.getJSONObject(i).getString("hlm_bz_qty"));
                        data.add(map);
                    }
                    view.refreshBzList(data);
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

    public void getKc(final Map<String,String>map){
        view.setShowProgressDialogEnable(true);
        String sql="Call Proc_PDA_GetStkList();";
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
                    for (int i=0;i<array.length();i++){
                        data.add(array.getJSONObject(i).getString("stk_ftyId")+","+
                                array.getJSONObject(i).getString("stk_stkId")+","+
                                array.getJSONObject(i).getString("stk_stkmc")
                        );
                    }
                    view.showKcDialog(map,data,preferenUtil.getString("usr_gsdm"));
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

    public void getTmxh(String hldh, String bzsl, String gsdm,
                        String kcdd, final TextView tmxh, final TextView qrcode){
        if (bzsl.equals("")){
            view.showMsgDialog("请先输入包装数量");
            return;
        }
        if (date.equals("")){
            view.showMsgDialog("获取日期失败，请重试");
            getScrq();
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_GenQrcode3('BRP','HL','%s',%s,'%s','%s','%s','%s','','%s');",
                hldh,bzsl,date,gsdm,kcdd,kcdd,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    JSONArray array=value.getJSONArray("Table1");
                    String result=array.getJSONObject(0).getString("cRetMsg");
                    String[] item=result.split(":");
                    String[] code;
                    if (item.length>0){
                        code=item[1].split(";");
                        if (code.length>0){
                            tmxh.setText(code[0]);
                            qrcode.setText(code[1]);
                        }else {
                            view.showMsgDialog("数据解析出错\n"+result);
                        }
                    }else {
                        view.showMsgDialog("数据解析出错\n"+result);
                    }
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

    public void printEven(final String printMsg, final String ylgg,
                          final String szgg, final String zyry,
                          final String bzsl, final String tmbh,
                          final OnPrintListener onPrintListener){

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            view.showBlueToothAddressDialog();
            return;
        }

        if (preferenUtil.getString("blueToothAddress").equals("")){
            view.showBlueToothAddressDialog();
            return;
        }
        if (tmbh.equals("")){
            view.showMsgDialog("请先获取条码序号");
            return;
        }
        if (bzsl.equals("")){
            view.showMsgDialog("请先输入包装数量");
            return;
        }
        view.setShowProgressDialogEnable(true);
        final PrinterUtil util=new PrinterUtil();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String address=preferenUtil.getString("blueToothAddress");
                util.openPort(address);
                util.printFont("原料规格:"+ylgg.trim(),15,55);
                util.printFont("色种规格:"+szgg.trim()+",",15,100);
                util.printFont("作业人员:"+zyry.trim(),15,145);
                util.printFont("生产日期:"+date.trim(),15,190);
                util.printFont("包装数量:"+bzsl.trim(),15,235);
                util.printFont("条码编号:"+tmbh.trim(),15,280);
                util.printQRCode(printMsg,335,55,6);
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
                onPrintListener.onFinish();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog("打印出错！");
            }

            @Override
            public void onComplete() {
                view.setShowProgressDialogEnable(false);
            }
        });


    }

    public void hlPacking(final String hldh, final String tmbh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_HL_Packing('%s','%s','%s');",hldh,tmbh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                getHlqd();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgToast(e.getMessage());
                view.showReloadHlPackingDialog(hldh,tmbh);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void setHljh(String hljh) {
        this.hljh = hljh;
    }

    public interface OnPrintListener{
        void onFinish();
    }
}
