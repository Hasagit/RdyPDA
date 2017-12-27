package com.rdypda.presenter;

import android.content.Context;
import android.view.View;

import com.rdypda.view.viewinterface.ILlddrView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DengJf on 2017/12/8.
 */

public class LlddrPresenter extends BasePresenter{
    private ILlddrView view;
    private List<Map<String,String>>data;
    public LlddrPresenter(Context context,ILlddrView view) {
        super(context);
        this.view=view;
        refreshListData();
    }

    public void refreshListData(){
        List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<20;i++){
            Map<String,String>map=new HashMap<>();
            map.put("lld","11111"+i+(i+1)+(i+2));
            data.add(map);
        }
        this.data=data;
        view.showList(data);
        view.setRefreshing(false);
    }

    public void queryDataByKey(final String key){
         if (data!=null){
             view.setProgressBarVisibility(View.VISIBLE);
             view.setSwipeVisibility(View.GONE);
             io.reactivex.Observable.create(new ObservableOnSubscribe<List<Map<String,String>>>() {
                 @Override
                 public void subscribe(ObservableEmitter<List<Map<String, String>>> e) throws Exception {
                     List<Map<String,String>>newData=new ArrayList<>();
                     for (int i=0;i<data.size();i++){
                         String lld=data.get(i).get("lld");
                         int time=lld.length()-key.length();
                         for (int j=0;j<time+1;j++){
                             if(key.equals(lld.substring(j,j+key.length()))){
                                 newData.add(data.get(i));
                                 break;
                             }
                         }
                     }
                     e.onNext(newData);
                     e.onComplete();
                 }
             }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Map<String, String>>>() {
                 @Override
                 public void onSubscribe(Disposable d) {

                 }

                 @Override
                 public void onNext(List<Map<String, String>> value) {
                     view.showList(value);
                     view.setSwipeVisibility(View.VISIBLE);
                     view.setProgressBarVisibility(View.GONE);
                 }

                 @Override
                 public void onError(Throwable e) {
                     view.setSwipeVisibility(View.VISIBLE);
                     view.setProgressBarVisibility(View.GONE);
                 }

                 @Override
                 public void onComplete() {

                 }
             });
         }
    }
}
