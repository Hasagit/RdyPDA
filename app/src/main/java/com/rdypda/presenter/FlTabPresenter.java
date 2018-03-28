package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.model.network.WebService;
import com.rdypda.view.viewinterface.IFlTabView;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by DengJf on 2018/1/23.
 */

public class FlTabPresenter extends BasePresenter {
    private IFlTabView view;

    public FlTabPresenter(Context context,IFlTabView view) {
        super(context);
        this.view=view;
    }

    public void cancelScaned(){
        view.setShowProgressDialogEnable(true);
        String sql=String.format(" Call Proc_PDA_CancelScan('LLD', '', '%s');",preferenUtil.getString("userId"));
        WebService.getQuerySqlCommandJson(sql,preferenUtil.getString("usr_Token")).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject value) {
                view.setShowProgressDialogEnable(false);
                view.finish();
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

    public int getScanNum(){
        return preferenUtil.getInt("scanNum");
    }

}
