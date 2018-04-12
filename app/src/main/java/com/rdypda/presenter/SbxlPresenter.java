package com.rdypda.presenter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.rdypda.model.network.WebService;
import com.rdypda.util.PrinterUtil;
import com.rdypda.util.ScanUtil;
import com.rdypda.view.activity.SbtlActivity;
import com.rdypda.view.activity.SbxlActivity;
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
    private ScanUtil scanUtil;
    private int startType=0;
    private String hldh="";//当混料单只有一条时，混料单号用此参数
    private String hldhs="";//当混料单有多条时，混料单号用此参数
    private String ftyIdAndstkId=";";
    private String printMsg="";
    private String sbbh;
    public int HLS=0,HL=1;


    public SbxlPresenter(Context context,ISbxlView view) {
        super(context);
        this.view=view;
        scanUtil=new ScanUtil(context);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                if (startType==SbxlActivity.START_TYPE_ZZTL|startType==SbxlActivity.START_TYPE_SYTL){
                    isValidDevice(result);
                }else if (startType==SbxlActivity.START_TYPE_SBXL){
                    getScanList(result);
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
        getKc();
        getScrq();
    }

    public void isValidDevice(final String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("设备编号不能为空！");
            return;
        }
        view.setSbEditText("");
        view.setShowProgressDialogEnable(true);
        String sblb="";
        if (startType== SbxlActivity.START_TYPE_SYTL){
            sblb="SY";
        }else if (startType==SbxlActivity.START_TYPE_ZZTL){
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
                        SbxlPresenter.this.sbbh=sbbh;
                        SbxlPresenter.this.sbbh=array.getJSONObject(0).getString("lbm_lbdm");
                        view.setSbEditText(array.getJSONObject(0).getString("lbm_lbmc"));
                        getScanList(sbbh);
                        view.showMsgDialog("验证成功！");

                    }else if (array.length()>1){
                        String[] sbmc=new String[array.length()];
                        String[] sbdm=new String[array.length()];
                        for (int i=0;i<array.length();i++){
                            sbdm[i]=array.getJSONObject(i).getString("lbm_lbdm");
                            sbmc[i]=array.getJSONObject(i).getString("lbm_lbmc");
                        }
                        view.showQueryList(sbdm,sbmc);
                    } else {
                        SbxlPresenter.this.sbbh="";
                        view.setSbEditText("");
                        getScanList(SbxlPresenter.this.sbbh);
                        view.showMsgDialog("验证失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    SbxlPresenter.this.sbbh="";
                    view.setSbEditText("");
                }

            }

            @Override
            public void onError(Throwable e) {
                SbxlPresenter.this.sbbh="";
                view.setSbEditText("");
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    //获取退料库位
    public void getKc(){
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

    //获取扫描列表
    public void getScanList(final String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("请先输入设备编号！");
            return;
        }
        hldh="";
        hldhs="";
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_GetScanList ('MTR_TL', '%s', '');",sbbh);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.setSbEditText(sbbh);
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
                        dataScan.add(map);
                    }
                    List<Map<String,String>>dataZs=new ArrayList<>();
                    String hl="";
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
                        if (i==0){
                            hl=arrayZs.getJSONObject(i).getString("tld_wldm");
                            hldh=arrayZs.getJSONObject(i).getString("tld_wldm");
                        }else {
                            hl=hl+","+arrayZs .getJSONObject(i).getString("tld_wldm");
                        }
                        dataZs.add(map);
                    }
                    if (dataZs.size()==1){
                        getHldhs(hldh);
                    }else if (dataZs.size()>1){
                        getHldhs(hl);
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

    //获取生产日期
    public void getScrq(){
        view.setShowProgressDialogEnable(true);
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String sql=String.format("Call Proc_check_Prod_Day('%s');",dateString);
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
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

    //获取条码序号
    public void getTmxh(String sbbh, String bzsl, String dw, final TextView tmbhText,int type){
        String hldhed="";
        if (type==HL){
            hldhed=hldh;
            if (hldh.equals("")){
                view.showMsgDialog("混料单号不能为空");
                return;
            }
        }else if (type==HLS){
            hldhed=hldhs;
            if (hldhs.equals("")){
                view.showMsgDialog("混料单号不能为空");
                return;
            }
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
                sbbh,hldhed,bzsl,dw,items[0],items[1],items[1],preferenUtil.getString("userid"),date);
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
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

    //打印事件
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
        final PrinterUtil util=new PrinterUtil(context);
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

    //获取混料单号
    public void getHldhs(String hldh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_GenHlWldm2('%s','%s');",
                hldh,preferenUtil.getString("userId"));
        WebService.querySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    hldhs=value.getJSONArray("Table1").getJSONObject(0).getString("cRetStr");
                    view.setShowProgressDialogEnable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.showMsgDialog(e.getMessage());
                    view.setShowProgressDialogEnable(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showMsgDialog(e.getMessage());
                view.setShowProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //下料提交确认
    public void xlPacking(final String sbbh, final String tmbh){
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_XL_Packing('%s', '%s', '%s');",sbbh,tmbh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                getScanList(sbbh);

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.setShowProgressDialogEnable(false);
                view.showToastMsg(e.getMessage());
                view.showPackErrorDialog(sbbh,tmbh);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //清料
    public void xlClear(final String sbbh){
        if (sbbh.equals("")){
            view.showMsgDialog("请先输入设备编号");
            return;
        }
        view.setShowProgressDialogEnable(true);
        String sql=String.format("Call Proc_PDA_XL_Clear('%s', '%s');",sbbh,preferenUtil.getString("userId"));
        WebService.doQuerySqlCommandResultJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.showMsgDialog("操作成功");
                view.refreshZsList(new ArrayList<Map<String, String>>());
                view.refreshScanList(new ArrayList<Map<String, String>>());
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

    public void closeScanUtil(){
        if (scanUtil!=null){
            scanUtil.close();
        }
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
        getScanList(sbbh);
    }

    public void setStartType(int startType) {
        this.startType = startType;
    }

    public String getSbbh() {
        return sbbh;
    }
}
