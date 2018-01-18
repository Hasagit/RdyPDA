package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.rdypda.R;
import com.rdypda.util.PrintUtil;
import com.rdypda.view.activity.GdxqActivity;
import com.rdypda.view.activity.HlActivity;
import com.rdypda.view.activity.LlddrActivity;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.TlActivity;
import com.rdypda.view.activity.UpdateActivity;
import com.rdypda.view.activity.YlzckActivity;
import com.rdypda.view.viewinterface.IMainView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by DengJf on 2017/12/8.
 */

public class MainPresenter extends BasePresenter{
    private IMainView view;
    private String[] permissionList;
    static final public int TMDY=0;
    static final public int FL=1;

    public MainPresenter(Context context,IMainView view) {
        super(context);
        this.view = view;
        view.setUserName("你好，"+preferenUtil.getString("usr_yhmc"));
        if (Build.MODEL.equals(context.getResources().getString(R.string.print_scan_model))){
            PrintUtil util=new PrintUtil(context);
            util.initPost();
        }
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
            //view.showMsgDialog("敬请期待");
            if (isPermission("MTR502D1")){
                Intent intent=new Intent(context, LlddrActivity.class);
                intent.putExtra("type",type);
                context.startActivity(intent);
            }else {
                view.showMsgDialog("你没有权限使用该功能");
            }
        }
    }


    public void goToGdxq(){
        Intent intent=new Intent(context, GdxqActivity.class);
        context.startActivity(intent);
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
        view.showMsgDialog("敬请期待");
    }

    //条码补打
    public void goToTmbd(){
        view.showMsgDialog("敬请期待");
    }

    //库存盘点
    public void goToKcpd(){
        view.showMsgDialog("敬请期待");
    }

    //条码查询
    public void goToTmcx(){
        view.showMsgDialog("敬请期待");
    }

    public void checkToUpdate(){
        /*Intent intent=new Intent(context, UpdateActivity.class);
        context.startActivity(intent);*/
    }
}
