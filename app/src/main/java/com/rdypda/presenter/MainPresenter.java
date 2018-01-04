package com.rdypda.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rdypda.R;
import com.rdypda.util.PrintUtil;
import com.rdypda.view.activity.GdxqActivity;
import com.rdypda.view.activity.LlddrActivity;
import com.rdypda.view.activity.LoginActivity;
import com.rdypda.view.activity.FlActivity;
import com.rdypda.view.viewinterface.IMainView;

/**
 * Created by DengJf on 2017/12/8.
 */

public class MainPresenter extends BasePresenter{
    private IMainView view;

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

    public void goToLlddr(){
        Intent intent=new Intent(context, LlddrActivity.class);
        context.startActivity(intent);
    }

    public void goToYljs(){
        Intent intent=new Intent(context, FlActivity.class);
        context.startActivity(intent);
    }

    public void goToGdxq(){
        Intent intent=new Intent(context, GdxqActivity.class);
        context.startActivity(intent);
    }
}
