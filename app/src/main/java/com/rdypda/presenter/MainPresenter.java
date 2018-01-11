package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rdypda.R;
import com.rdypda.util.PrintUtil;
import com.rdypda.view.activity.GdxqActivity;
import com.rdypda.view.activity.HlActivity;
import com.rdypda.view.activity.LlddrActivity;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.YlzckActivity;
import com.rdypda.view.viewinterface.IMainView;

/**
 * Created by DengJf on 2017/12/8.
 */

public class MainPresenter extends BasePresenter{
    private IMainView view;
    static final public int TMDY=0;
    static final public int FL=1;

    public MainPresenter(Context context,IMainView view) {
        super(context);
        this.view = view;
        if (Build.MODEL.equals(context.getResources().getString(R.string.print_scan_model))){
            PrintUtil util=new PrintUtil(context);
            util.initPost();
        }
    }


    public void goToLogin(){
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void goToLlddr(int type){
        Intent intent=new Intent(context, LlddrActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }


    public void goToGdxq(){
        Intent intent=new Intent(context, GdxqActivity.class);
        context.startActivity(intent);
    }

    public void goToYlzck(int type) {
        Intent intent = new Intent(context, YlzckActivity.class);
        intent.putExtra("startType",type);
        context.startActivity(intent);
    }

    public void goToHl(){
        Intent intent=new Intent(context, HlActivity.class);
        context.startActivity(intent);
    }
}
