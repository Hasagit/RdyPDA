package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.util.DownloadUtils;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.MainActivity;
import com.rdypda.view.viewinterface.IFirstView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/*
 * Created by DengJf on 2018/1/12.
 */

public class FirstPresenter extends BasePresenter {
    private static final String TAG = FirstPresenter.class.getSimpleName();
    private IFirstView view;
    private String arrayStr;

    public FirstPresenter(Context context, IFirstView view) {
        super(context);
        this.view = view;
        preferenUtil.setInt("goToWhere", 0);
        WebService.initUrl(preferenUtil);

        //更新
        checkToUpdate();



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(5000);
                    switch (preferenUtil.getInt("goToWhere")){
                        case 0:
                            goToLogin();
                            break;
                        case 1:
                            goToMain();
                            break;
                        case 2:
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //登录请求
    public void login(final String cmp_gsdm, final String usrId, final String usrPwd) {
        WebService.usrLogon(cmp_gsdm, usrId, usrPwd).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")) {
                        //登录成功
                        JSONArray array = value.getJSONArray("usr_mstr");
                        preferenUtil.setString("usr_yhdm", array.getJSONObject(0).getString("usr_yhdm"));
                        preferenUtil.setString("usr_yhmm", array.getJSONObject(0).getString("usr_yhmm"));
                        preferenUtil.setString("usr_yhmc", array.getJSONObject(0).getString("usr_yhmc"));
                        preferenUtil.setString("usr_bmdm", array.getJSONObject(0).getString("usr_bmdm"));
                        preferenUtil.setString("usr_gsdm", array.getJSONObject(0).getString("usr_gsdm"));
                        preferenUtil.setString("usr_flag", array.getJSONObject(0).getString("usr_flag"));
                        preferenUtil.setString("usr_Token", array.getJSONObject(0).getString("usr_Token"));
                        preferenUtil.setString("usr_TokenValid", array.getJSONObject(0).getString("usr_TokenValid"));
                        preferenUtil.setInt("goToWhere", 1);
                        arrayStr = value.getString("Table3");
                    } else {
                        view.showToastMsg(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                        preferenUtil.setInt("goToWhere", 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    preferenUtil.setInt("goToWhere", 0);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showToastMsg(e.getMessage());
                preferenUtil.setInt("goToWhere", 0);
            }

            @Override
            public void onComplete() {

            }
        });


    }

    public void goToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        view.finish();
    }

    public void goToMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("permissionList", arrayStr);
        context.startActivity(intent);
        view.finish();
    }

    //查询服务器App版本
    public void checkToUpdate() {
        String sql = "Call PAD_Get_WebAddr()";
        String token="RDYWEBSERVICEAUTOCALLADMIN";
        WebService.querySqlCommandJson(sql, token).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(final JSONObject value) {
                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    String oldVersion = pi.versionName;
                    JSONArray array = value.getJSONArray("Table1");
                    String newVersion = array.getJSONObject(0).getString("v_WebAppVer").trim();
                    //如果不相等
                    if (haveNewVersion(newVersion,oldVersion)) {
                        String urlStr = array.getJSONObject(0).getString("v_WebAppPath");
                        //2是不做处理
                        preferenUtil.setInt("goToWhere", 2);
                        //显示是否下载
                        view.showDownloadDialog(urlStr);
                    } else {
                        //如果没登录过
                        if (preferenUtil.getString("userId").equals("")) {
                            preferenUtil.setInt("goToWhere", 0);
                        } else {
                            //如果登录过
                            login(preferenUtil.getString("cmp_gsdm"), preferenUtil.getString("userId"), preferenUtil.getString("userPwd"));
                        }
                    }
                } catch (JSONException e) {
                    preferenUtil.setInt("goToWhere", 0);
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    preferenUtil.setInt("goToWhere", 0);
                } finally {
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showMsgDialog(e.getMessage());
                preferenUtil.setInt("goToWhere", 0);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 是否有新版本
     * @param newVersion
     * @param oldVersion
     * @return
     */
    private boolean haveNewVersion(String newVersion, String oldVersion) {
        boolean isHaveNewVersion = false;
        //Log.d(TAG, "haveNewVersion: newVersion"+newVersion);
        //Log.d(TAG, "haveNewVersion: oldVersion"+oldVersion);
        String[] oldVersionArr = oldVersion.split("\\.");
        String[] newVersionArr = newVersion.split("\\.");
        //Log.d(TAG, "haveNewVersion: length"+oldVersionArr.length);
        for (int i=0;i<oldVersionArr.length; i++){
            if((Integer.parseInt(newVersionArr[i])) > (Integer.parseInt(oldVersionArr[i]))){
                isHaveNewVersion = true;
            }else if ((Integer.parseInt(newVersionArr[i])) == (Integer.parseInt(oldVersionArr[i]))){
                continue;
            }else{
                break;
            }
        }
        return isHaveNewVersion;
    }

    //下载App
    public void downloadInstallApk(String urlStr) {
        DownloadUtils downloadUtils = new DownloadUtils(context);
        view.setShowDownloadProgressDialogEnable(true);
        //下载后安装新版本
        downloadUtils.downloadAPK(urlStr, Environment.getExternalStorageDirectory().getPath(), "RdyPDA.apk").subscribe(new Observer<Integer>() {
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
                view.showToastMsg("下载失败");
                goToLogin();
                view.setShowDownloadProgressDialogEnable(false);
            }

            @Override
            public void onComplete() {
                view.setShowDownloadProgressDialogEnable(false);
            }
        });
    }

}
