package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.view.viewinterface.ISbxlView;

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
 * Created by DengJf on 2018/3/9.
 */

public class SbxlPresenter extends BasePresenter {
    private ISbxlView view;
    private String date;
    private String hldh="";
    private String ftyIdAndstkId=";";
    private String printMsg="";
    public SbxlPresenter(Context context,ISbxlView view) {
        super(context);
        this.view=view;
        getKc();
        getScrq();
    }

    public void getKc(){
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
                    List<String> data=new ArrayList<>();
                    List<String>dataMc=new ArrayList<>();
                    data.add("");
                    dataMc.add("");
                    for (int i=0;i<array.length();i++){
                        data.add(array.getJSONObject(i).getString("stk_ftyId")+";"+
                                array.getJSONObject(i).getString("stk_stkId"));
                        dataMc.add(array.getJSONObject(i).getString("stk_stkmc"));
                    }
                    view.refreshXlkwSp(data,dataMc);
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

    public void getScanList(String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("请先输入设备编号！");
            return;
        }
        hldh="";
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetScanList ('MTR_TL', '%s', '');",sbbh);
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                if (date==null){
                    view.showMsgDialog("生产日期获取失败,请重新进入");
                    return;
                }
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
                        map.put("hldh",arrayScan.getJSONObject(i).getString("tll_wldm"));
                        hldh=arrayScan.getJSONObject(i).getString("tll_wldm");
                        String lastThreeChar=hldh.substring(hldh.length()-3,hldh.length());
                        if (!lastThreeChar.equals("(H)")){
                            hldh=hldh+"(H)";
                        }
                        dataScan.add(map);
                    }
                    List<Map<String,String>>dataZs=new ArrayList<>();
                    for (int i=0;i<arrayZs.length();i++){
                        Map<String,String>map=new HashMap<>();
                        map.put("ylgg",arrayZs.getJSONObject(i).getString("tld_wlpm"));
                        map.put("yldm",arrayZs.getJSONObject(i).getString("tld_wldm"));
                        map.put("sbbh",arrayZs.getJSONObject(i).getString("tld_devid"));
                        map.put("zjls",arrayZs.getJSONObject(i).getString("tld_tlsl"));
                        map.put("yjys",arrayZs.getJSONObject(i).getString("tld_sysl"));
                        map.put("szgg",arrayZs.getJSONObject(i).getString("tld_szmc"));
                        map.put("scrq",date);
                        map.put("zyry",preferenUtil.getString("usr_yhmc"));
                        map.put("dw",arrayZs.getJSONObject(i).getString("itm_unit"));
                        dataZs.add(map);
                    }
                    view.refreshZsList(dataZs);
                    view.refreshScanList(dataScan);
                } catch (JSONException e) {
                    e.printStackTrace();
                    hldh="";
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
                hldh="";
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
                    view.showMsgDialog("生产日期获取失败");
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

    public void getTmxh(String sbbh, String bzsl, String dw, final TextView tmbhText){
        if (hldh.equals("")){
            view.showMsgDialog("混料单号不能为空");
            return;
        }
        if (bzsl.equals("")){
            view.showMsgDialog("请先输入包装数量");
            return;
        }
        if (bzsl.equals("0")){
            view.showMsgDialog("包装数量必须大于0");
            return;
        }
        if (ftyIdAndstkId.equals(";")|ftyIdAndstkId.equals("")){
            view.showMsgDialog("请先选择退料库位");
            return;
        }
        String[] items=ftyIdAndstkId.split(";");
        if (items.length<2){
            view.showMsgDialog("工厂号和库位解析出错");
            return;
        }
        printMsg="";
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_GenQrcode('BRP','XL','%s','%s','',%s,'%s','%s','%s','%s','','%s','%s');",
                sbbh,hldh,bzsl,dw,items[0],items[1],items[1],preferenUtil.getString("userid"),date);
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    String[] item=value.getJSONArray("Table1").getJSONObject(0).getString("cRetMsg").split(":");
                    if (item.length>1){
                        String[]itemItem=item[1].split(";");
                        if (itemItem.length>1){
                            tmbhText.setText(itemItem[0]);
                            printMsg=itemItem[1];
                        }else {
                            view.showMsgDialog("数据解析出错");
                        }
                    }else {
                        view.showMsgDialog("数据解析出错");
                    }
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

    public void setFtyIdAndstkId(String ftyIdAndstkId) {
        this.ftyIdAndstkId = ftyIdAndstkId;
    }

    public void printEven(final String ylgg,
                          final String szgg, final String zyry,
                          final String bzsl, final String tmbh,
                          final HlbzPresenter.OnPrintListener onPrintListener){

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
                util.printQRCode(printMsg,335,135,5);
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
                view.setShowProgressDialogEnable(false);
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

    public void xlPacking(final String sbbh, final String tmbh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_XL_Packing('%s', '%s', '%s');",sbbh,tmbh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showPackErrorDialog(sbbh,tmbh);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void xlClear(final String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("请先输入设备编号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_XL_Clear('%s', '%s');",sbbh,preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog("操作成功");
                getScanList(sbbh);
            }

            @Override
            public void onError(Throwable e) {
                view.setShowProgressDialogEnable(false);
                e.printStackTrace();
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
