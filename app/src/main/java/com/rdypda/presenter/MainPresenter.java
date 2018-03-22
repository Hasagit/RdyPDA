package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.rdypda.R;
import com.rdypda.model.network.WebService;
import com.rdypda.util.DownloadUtils;
import com.rdypda.view.activity.HlActivity;
import com.rdypda.view.activity.HlbzActivity;
import com.rdypda.view.activity.LlddrActivity;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.SbljActivity;
import com.rdypda.view.activity.SbtlActivity;
import com.rdypda.view.activity.SbxlActivity;
import com.rdypda.view.activity.TmbdActivity;
import com.rdypda.view.activity.TmcfActivity;
import com.rdypda.view.activity.TmcxActivity;
import com.rdypda.view.activity.WydrckActivity;
import com.rdypda.view.activity.YkActivity;
import com.rdypda.view.viewinterface.IMainView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by DengJf on 2017/12/8.
 */

public class MainPresenter extends BasePresenter{
    private IMainView view;
    private String[] permissionList;
    static final public int TMDY=0;
    static final public int FL=1;
    static final public int YLTL=2;
    static final public int YLJS=3;
    private Timer timer;

    public MainPresenter(Context context,IMainView view) {
        super(context);
        this.view = view;
        view.setUserName("你好，"+preferenUtil.getString("usr_yhmc"));
        autoUpdate();
        initExpandableListView();
    }

    public void initPermissionList(String arrayStr){
        try {
            JSONArray permissionArray=new JSONArray(arrayStr);
            permissionList=permissionArray.getJSONObject(0).getString("cPrgList").split(";");
            //Log.e("permissionList",permissionList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initExpandableListView(){
        List<String> groupTitles=new ArrayList<>();
        final List<List<String>> titles=new ArrayList<>();
        List<List<Integer>> imgs=new ArrayList<>();
        groupTitles.add("原料仓发料");
        groupTitles.add("原料组仓库");
        groupTitles.add("注塑生产与投料");
        groupTitles.add("注塑产品管理");
        groupTitles.add("组装发料退料");
        groupTitles.add("条码管理");
        groupTitles.add("原料混料");

        //原料仓发料
        List<String>ylcflArray=new ArrayList<>();
        ylcflArray.add("发料条码打印");
        ylcflArray.add("发料");
        List<Integer>ylcflResources=new ArrayList<>();
        ylcflResources.add(R.drawable.print_icon);
        ylcflResources.add(R.drawable.fl_icon);
        imgs.add(ylcflResources);

        //原料组仓库
        List<String>ylzckArray=new ArrayList<>();
        ylzckArray.add("原料接收");
        ylzckArray.add("原料退料");
        titles.add(ylzckArray);
        List<Integer>ylzckResources=new ArrayList<>();
        ylzckResources.add(R.drawable.yljs_icon);
        ylzckResources.add(R.drawable.yltl_icon);
        imgs.add(ylzckResources);

        //注塑生产与投料
        List<String>zsscytlArray=new ArrayList<>();
        zsscytlArray.add("混料");
        zsscytlArray.add("按料单发料");
        zsscytlArray.add("退料");
        zsscytlArray.add("工单退料到原料组");
        titles.add(zsscytlArray);
        List<Integer>zsscytlResources=new ArrayList<>();
        zsscytlResources.add(R.drawable.hl_icon);
        zsscytlResources.add(R.drawable.adfl_icon);
        zsscytlResources.add(R.drawable.tl_icon);
        zsscytlResources.add(R.drawable.gdtldylz_icon);
        imgs.add(zsscytlResources);

        //注塑产品管理
        List<String>zscpglArray=new ArrayList<>();
        zscpglArray.add("产品扫描入库");
        zscpglArray.add("移库领料");
        titles.add(zscpglArray);
        List<Integer>zscpglResources=new ArrayList<>();
        zscpglResources.add(R.drawable.cpsmrk_icon);
        zscpglResources.add(R.drawable.ykll_icon);
        imgs.add(zscpglResources);

        //组装发料退料
        List<String>zzfltlArray=new ArrayList<>();
        zzfltlArray.add("按单发料");
        zzfltlArray.add("按单退料");
        zzfltlArray.add("移库退料到仓库");
        titles.add(zzfltlArray);
        List<Integer>zzfltlResources=new ArrayList<>();
        zzfltlResources.add(R.drawable.adfl2_icon);
        zzfltlResources.add(R.drawable.adtl_icon);
        zzfltlResources.add(R.drawable.yklldck_icon);
        imgs.add(zzfltlResources);

        //条码管理
        List<String>tmglArray=new ArrayList<>();
        tmglArray.add("条码拆分");
        tmglArray.add("条码补打");
        tmglArray.add("库存盘点");
        tmglArray.add("条码查询");
        titles.add(tmglArray);
        List<Integer>tmglResources=new ArrayList<>();
        tmglResources.add(R.drawable.tmcf_icon);
        tmglResources.add(R.drawable.tmbd_icon);
        tmglResources.add(R.drawable.kcpd_icon);
        tmglResources.add(R.drawable.tmcx_icon);
        imgs.add(tmglResources);

        //原料混料
        List<String>ylhlArray=new ArrayList<>();
        ylhlArray.add("混料包装");
        ylhlArray.add("设备投料");
        ylhlArray.add("设备连接");
        ylhlArray.add("设备下料");
        ylhlArray.add("无源单入库");
        ylhlArray.add("无源单出库");
        ylhlArray.add("移库");
        titles.add(ylhlArray);
        List<Integer>ylhlResources=new ArrayList<>();
        ylhlResources.add(R.drawable.hlbz_icon);
        ylhlResources.add(R.drawable.ltjl_icon);
        ylhlResources.add(R.drawable.sblj_icon);
        ylhlResources.add(R.drawable.sbxl_icon);
        ylhlResources.add(R.drawable.wydrk_icon);
        ylhlResources.add(R.drawable.wydck_icon);
        ylhlResources.add(R.drawable.yk_icon);
        imgs.add(ylhlResources);



        view.refreshExpandableListVie(groupTitles, titles, imgs, new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String check=titles.get(groupPosition).get(childPosition);
                switch (titles.get(groupPosition).get(childPosition)){
                    case "发料条码打印":
                        goToLlddr(MainPresenter.TMDY);
                        break;
                    case "发料":
                        goToLlddr(MainPresenter.FL);
                        break;
                    case "原料接收":
                        goToYljs();
                        break;
                    case "原料退料":
                        goToYltl();
                        break;
                    case "按料单发料":
                        goToAldfl();
                        break;
                    case "混料":
                        goToHl();
                        break;
                    case "条码拆分":
                        goToTmcf();
                        break;
                    case "条码补打":
                        goToTmbd();
                        break;
                    case "库存盘点":
                        goToKcpd();
                        break;
                    case "条码查询":
                        goToTmcx();
                        break;
                    case "混料包装":
                        goTohlbz();
                        break;
                    case "设备投料":
                        goToSbtl();
                        break;
                    case "设备连接":
                        goToSblj();
                        break;
                    case "设备下料":
                        goTosbxl();
                        break;
                    case "无源单入库":
                        goToWydrk();
                        break;
                    case "无源单出库":
                        goToWydck();
                        break;
                    case "移库":
                        goToYk();
                        break;
                }
                return true;
            }
        });
    }

    public boolean isPermission(String functionCode){
        boolean isExist=false;
        for (int i=0;i<permissionList.length;i++){
            if (permissionList[i].equals(functionCode)){
                isExist=true;
            }
        }
        return isExist;
    }

    public void goToLogin(){
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        view.finish();
    }

    //条码打印 or 发料
    public void goToLlddr(int type){
        if (type==MainPresenter.TMDY){
            if (isPermission("MTR502D1")){
                Intent intent=new Intent(context, LlddrActivity.class);
                intent.putExtra("type",type);
                context.startActivity(intent);
            }else {
                view.showMsgDialog("你没有权限使用该功能");
            }
        }else if (type==MainPresenter.FL){
            if (isPermission("MTR502D1")){
                Intent intent=new Intent(context, LlddrActivity.class);
                intent.putExtra("type",type);
                context.startActivity(intent);
            }else {
                view.showMsgDialog("你没有权限使用该功能");
            }
        }
    }

    //原料接收
    public void goToYljs(){
        if (isPermission("MTR511D1")){
            Intent intent=new Intent(context, LlddrActivity.class);
            intent.putExtra("type",YLJS);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //原料退料
    public void goToYltl(){
        if (isPermission("MTR512D1")){
            Intent intent=new Intent(context, LlddrActivity.class);
            intent.putExtra("type",YLTL);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //原料组仓库
    public void goToYlzck(int type) {
        view.showMsgDialog("敬请期待");
        /*Intent intent = new Intent(context, YlzckActivity.class);
        intent.putExtra("startType",type);
        context.startActivity(intent);*/
    }

    //混料
    public void goToHl(){
        if (isPermission("MOM503D1")){
            Intent intent=new Intent(context, HlActivity.class);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //按料单发料
    public void goToAldfl(){
        view.showMsgDialog("敬请期待");
        /*String sql="Insert Into kcm_mstr (kcm_ftyid, kcm_stkid, kcm_kwdm, kcm_cwdm, kcm_ph, kcm_wldm, kcm_kcsl, kcm_wfpl, kcm_jlrq, kcm_jlry)\n" +
                "            Select '333', 'WHS', 'WHS', '', '', '81010156-000', 10, 10, Now(), 'ADMIN'\n" +
                "            On Duplicate Key Update kcm_kcsl = kcm_kcsl + 10, kcm_wfpl = kcm_wfpl + 10, kcm_ggrq = Now(), kcm_ggry = 'ADMIN';";
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });*/
    }

    //退料
    public void goToTl(){
        view.showMsgDialog("敬请期待");
        /*Intent intent=new Intent(context, TlActivity.class);
        context.startActivity(intent);*/
    }

    //按单发料
    public void goToAdfl(){
        view.showMsgDialog("敬请期待");
    }

    //按单退料
    public void goToAdtl(){
        view.showMsgDialog("敬请期待");
    }

    //移库退料到仓库
    public void goToYktldck(){
        view.showMsgDialog("敬请期待");
    }

    //条码拆分
    public void goToTmcf(){
        if (isPermission("STK501D1")){
            Intent intent=new Intent(context, TmcfActivity.class);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //条码补打
    public void goToTmbd(){
        if (isPermission("STK502D1")){
            Intent intent=new Intent(context, TmbdActivity.class);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //库存盘点
    public void goToKcpd(){
        view.showMsgDialog("敬请期待");
    }

    //条码查询
    public void goToTmcx(){
        if (isPermission("STK504D1")){
            Intent intent=new Intent(context, TmcxActivity.class);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }

    //混料包装
    public void goTohlbz(){
        Intent intent=new Intent(context, HlbzActivity.class);
        context.startActivity(intent);
    }

    //设备投料
    public void goToSbtl(){
        Intent intent=new Intent(context, SbtlActivity.class);
        context.startActivity(intent);
    }

    //设备连接
    public void goToSblj(){
        Intent intent=new Intent(context, SbljActivity.class);
        context.startActivity(intent);
    }

    //设备下料
    public void goTosbxl(){
        Intent intent=new Intent(context, SbxlActivity.class);
        context.startActivity(intent);
    }

    //无源单入库
    public void goToWydrk(){
        Intent intent=new Intent(context, WydrckActivity.class);
        intent.putExtra("startType",WydrckActivity.START_TYPE_WYDRK);
        context.startActivity(intent);
    }

    //无源单出库
    public void goToWydck(){
        Intent intent=new Intent(context, WydrckActivity.class);
        intent.putExtra("startType",WydrckActivity.START_TYPE_WYDCK);
        context.startActivity(intent);
    }

    //移库
    public void goToYk(){
        Intent intent=new Intent(context, YkActivity.class);
        context.startActivity(intent);
    }

    public void checkToUpdate(final boolean isAuto){
        String sql="Call PAD_Get_WebAddr()";
        if (!isAuto){
            view.setShowProgressDialogEnable(true);
        }
        WebService.querySqlCommandJosn(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(final JSONObject value) {
                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    String versionName = pi.versionName;
                    JSONArray array=value.getJSONArray("Table1");
                    String appVer=array.getJSONObject(0).getString("v_WebAppVer").trim();
                    if (!appVer.equals(versionName)){
                        String urlStr=array.getJSONObject(0).getString("v_WebAppPath");
                        //String urlStr="http://imtt.dd.qq.com/16891/F782CC27B9CE90B89CD494DC95098B7F.apk?fsname=com.qiyi.video_9.0.0_81010.apk&csr=2097&_track_d99957f7=88ee35aa-5ea6-47b8-a8b0-c95f0e025ca9";
                        view.showDownloadDialog(urlStr);
                    }else {
                        if (!isAuto){
                            view.showMsgDialog("当前已是最新版本");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    view.setShowProgressDialogEnable(false);
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

    public void downloadInstallApk(String urlStr){
        DownloadUtils downloadUtils=new DownloadUtils(context);
        view.setShowDownloadProgressDialogEnable(true);
        downloadUtils.downloadAPK(urlStr,Environment.getExternalStorageDirectory().getPath(),"RdyPDA.apk").subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                view.setProgressDownloadProgressDialog(value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showMsgDialog("下载失败");
                view.setShowDownloadProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {
                view.setShowDownloadProgressDialogEnable(false);
            }
        });
    }

    public void autoUpdate(){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("autoUpdate","2");
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        checkToUpdate(true);
                        Log.e("autoUpdate","1");
                        e.onComplete();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        },1800*1000,1800*1000);
    }

    public void closeTimer(){
        timer.cancel();
        timer=null;
    }

}
