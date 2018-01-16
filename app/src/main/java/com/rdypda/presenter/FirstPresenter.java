package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;

import com.rdypda.model.network.WebService;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.MainActivity;
import com.rdypda.view.viewinterface.IFirstView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by DengJf on 2018/1/12.
 */

public class FirstPresenter extends BasePresenter{
    private IFirstView view;
    private Observable<JSONObject>observable;
    public FirstPresenter(Context context, IFirstView view) {
        super(context);
        this.view=view;
        preferenUtil.setBoolean("isFirstActivityLogin",false);
        WebService.initUrl(preferenUtil);
        if (preferenUtil.getString("userId").equals("")){
            preferenUtil.setBoolean("isFirstActivityLogin",false);
        }else {
            login(preferenUtil.getString("cmp_gsdm"),preferenUtil.getString("userId"),preferenUtil.getString("userPwd"));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(5000);
                    if (preferenUtil.getBoolean("isFirstActivityLogin")){
                        goToMain();
                    }else {
                        goToLogin();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void login(final String cmp_gsdm, final String usrId, final String usrPwd){
        WebService.usrLogon(cmp_gsdm,usrId,usrPwd).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        JSONArray array=value.getJSONArray("usr_mstr");
                        preferenUtil.setString("usr_yhdm",array.getJSONObject(0).getString("usr_yhdm"));
                        preferenUtil.setString("usr_yhmm",array.getJSONObject(0).getString("usr_yhmm"));
                        preferenUtil.setString("usr_yhmc",array.getJSONObject(0).getString("usr_yhmc"));
                        preferenUtil.setString("usr_bmdm",array.getJSONObject(0).getString("usr_bmdm"));
                        preferenUtil.setString("usr_gsdm",array.getJSONObject(0).getString("usr_gsdm"));
                        preferenUtil.setString("usr_flag",array.getJSONObject(0).getString("usr_flag"));
                        preferenUtil.setString("usr_Token",array.getJSONObject(0).getString("usr_Token"));
                        preferenUtil.setString("usr_TokenValid",array.getJSONObject(0).getString("usr_TokenValid"));
                        preferenUtil.setBoolean("isFirstActivityLogin",true);
                    }else {
                        view.showToastMsg(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                        preferenUtil.setBoolean("isFirstActivityLogin",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    preferenUtil.setBoolean("isFirstActivityLogin",false);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.showToastMsg(e.getMessage());
                preferenUtil.setBoolean("isFirstActivityLogin",false);
            }

            @Override
            public void onComplete() {

            }
        });


    }

    public void goToLogin(){
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        view.finish();
    }

    public void goToMain(){
        Intent intent=new Intent(context, MainActivity.class);
        context.startActivity(intent);
        view.finish();
    }
}
