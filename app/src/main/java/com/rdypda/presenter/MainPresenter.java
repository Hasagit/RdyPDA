package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.rdypda.R;
import com.rdypda.model.network.WebService;
import com.rdypda.util.DownloadUtils;
import com.rdypda.util.DownloadUtilsII;
import com.rdypda.util.PrintUtil;
import com.rdypda.view.activity.GdxqActivity;
import com.rdypda.view.activity.LlddrActivity;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.TmbdActivity;
import com.rdypda.view.activity.TmcfActivity;
import com.rdypda.view.activity.TmcxActivity;
import com.rdypda.view.viewinterface.IMainView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if (Build.MODEL.equals(context.getResources().getString(R.string.print_scan_model))){
            PrintUtil util=new PrintUtil(context);
            util.initPost();
        }
        autoUpdate();
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


    public void goToYljs(){
        if (isPermission("MTR511D1")){
            Intent intent=new Intent(context, LlddrActivity.class);
            intent.putExtra("type",YLJS);
            context.startActivity(intent);
        }else {
            view.showMsgDialog("你没有权限使用该功能");
        }
    }


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
        view.showMsgDialog("敬请期待");
        /*Intent intent=new Intent(context, HlActivity.class);
        context.startActivity(intent);*/
    }

    //按料单发料
    public void goToAldfl(){
        view.showMsgDialog("敬请期待");
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
        DownloadUtilsII downloadUtils=new DownloadUtilsII(context);
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
        },0,1800*1000);
    }


    public void closeTimer(){
        timer.cancel();
        timer=null;
    }

}
