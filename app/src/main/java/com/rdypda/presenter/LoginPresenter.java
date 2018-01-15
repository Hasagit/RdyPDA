package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rdypda.model.network.WebService;
import com.rdypda.view.activity.MainActivity;
import com.rdypda.view.viewinterface.ILoginView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by DengJf on 2018/1/12.
 */

public class LoginPresenter extends BasePresenter {
    private ILoginView view;
    private List<Map<String,String>>data;
    private boolean isRemember=true;
    public LoginPresenter(Context context, final ILoginView view) {
        super(context);
        this.view=view;
        if (!preferenUtil.getString("userId").equals("")){
            view.setDefaultUser(preferenUtil.getString("userId"),preferenUtil.getString("userPwd"));
            preferenUtil.setString("usr_yhdm","");
            preferenUtil.setString("usr_yhmm","");
            preferenUtil.setString("usr_yhmc","");
            preferenUtil.setString("usr_bmdm","");
            preferenUtil.setString("usr_gsdm","");
            preferenUtil.setString("usr_flag","");
            preferenUtil.setString("usr_Token","");
            preferenUtil.setString("usr_TokenValid","");
        }
        WebService.getCompanyList().subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        JSONArray array=value.getJSONArray("cmp_mstr");
                        data=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            Map<String,String>item=new HashMap<>();
                            item.put("cmp_gsdm",array.getJSONObject(i).getString("cmp_gsdm"));
                            item.put("cmp_gsmc",array.getJSONObject(i).getString("cmp_gsmc"));
                            data.add(item);
                        }
                        view.showFactoryList(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }



    public void login(final int usrCmpIdPosition, final String usrId, final String usrPwd){
        view.setUserIdErrorEnable(false);
        view.setPwdErrorEnable(false);
        if (usrId.equals("")){
            view.setUserIdErrorEnable(true);
            view.setUserIdError("请先输入账号");
            return;
        }
        if (usrPwd.equals("")){
            view.setPwdErrorEnable(true);
            view.setPwdError("请先输入密码");
            return;
        }
        if (usrCmpIdPosition==-1){
            view.showToastMsg("公司信息加载失败，请重启软件");
            return;
        }
        if (data==null){
            view.showToastMsg("公司信息加载失败，请重启软件");
            return;
        }
        view.setShowProgressDialogEnable(true);
        WebService.usrLogon(data.get(usrCmpIdPosition).get("cmp_gsdm"),usrId,usrPwd).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                try {
                    if (value.getJSONArray("Table0").getJSONObject(0).getString("cStatus").equals("SUCCESS")){
                        JSONArray array=value.getJSONArray("usr_mstr");
                        Intent intent=new Intent(context, MainActivity.class);
                        preferenUtil.setString("usr_yhdm",array.getJSONObject(0).getString("usr_yhdm"));
                        preferenUtil.setString("usr_yhmm",array.getJSONObject(0).getString("usr_yhmm"));
                        preferenUtil.setString("usr_yhmc",array.getJSONObject(0).getString("usr_yhmc"));
                        preferenUtil.setString("usr_bmdm",array.getJSONObject(0).getString("usr_bmdm"));
                        preferenUtil.setString("usr_gsdm",array.getJSONObject(0).getString("usr_gsdm"));
                        preferenUtil.setString("usr_flag",array.getJSONObject(0).getString("usr_flag"));
                        preferenUtil.setString("usr_Token",array.getJSONObject(0).getString("usr_Token"));
                        preferenUtil.setString("usr_TokenValid",array.getJSONObject(0).getString("usr_TokenValid"));
                        if (isRemember){
                            preferenUtil.setString("userId",usrId);
                            preferenUtil.setString("userPwd",usrPwd);
                            preferenUtil.setString("cmp_gsdm",data.get(usrCmpIdPosition).get("cmp_gsdm"));
                        }else {
                            preferenUtil.setString("userId","");
                            preferenUtil.setString("userPwd","");
                            preferenUtil.setString("cmp_gsdm","");
                        }
                        //preferenUtil.setString("cmp_gsdm",data.get(usrCmpIdPosition).get("cmp_gsdm"));
                        context.startActivity(intent);
                        view.finish();
                    }else {
                        view.showToastMsg(value.getJSONArray("Table0").getJSONObject(0).getString("cMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                view.setShowProgressDialogEnable(false);
                view.showToastMsg(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });


    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }
}
