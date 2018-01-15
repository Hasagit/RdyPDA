package com.rdypda.model.network;

import android.util.Log;

import com.rdypda.model.network.api.ServiceApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by DengJf on 2017/12/8.
 */

public class WebService {
    public static String URL="http://yun.ruiduoyi.com:8080/Service.asmx/";
    public static Retrofit retrofit;
    public static  ServiceApi serviceApi;

    public static Retrofit getRetrofit(){
        if (retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            return retrofit;
        }else {
            return retrofit;
        }
    }

    public static ServiceApi getServiceApi(){
        if (serviceApi==null){
            serviceApi=getRetrofit().create(ServiceApi.class);
            return serviceApi;
        }else {
            return serviceApi;
        }
    }


    public static Observable<JSONArray>getQuerySqlResult(final String sql){
        return Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(ObservableEmitter<JSONArray> e) throws Exception {
                Response<String>response=getServiceApi().getQuerysqlResult(sql,"nopassword").execute();
                e.onNext(stringToJsonArray(response.body()));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JSONObject>getCompanyList(){
        return Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {
                Response<String>response=getServiceApi().getCompanyList().execute();
                e.onNext(stringToJsonObject(response.body()));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<JSONObject>usrLogon(final String usrCmpId, final String usrId, final String usrPwd){
        return Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {
                Response<String>response=getServiceApi().userLogin(usrCmpId,usrId,usrPwd).execute();
                e.onNext(stringToJsonObject(response.body()));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    //将服务器返回数据转换成JsonArray
    public static JSONArray stringToJsonArray(String result) throws IOException, JSONException {
        List<List<String>>tab_list=new ArrayList<>();
        JSONArray array=new JSONArray();
        List<String>zd_list=new ArrayList<>();
        if (calculate(result,"\n")>200){
            return null;
        }
        String[] str_line=result.split("\n");
        for (int i=0;i<str_line.length;i++){
            if (i>100){
                break;
            }
            //Log.e("result",str_line[i]);
            if (i==3){
                String temp=str_line[i]+" ";
                if (calculate(temp,"\t")>100){
                    return null;
                }
                String[] items=temp.split("\t");
                for(int j=0;j<items.length;j++){
                    if (j>100){
                        break;
                    }
                    if (j+1==items.length){
                        break;
                    }
                    zd_list.add(items[j+1].trim());
                    //Log.e("item",items[j]);
                    //Log.e("test","--------"+j);
                }

            }


            if(i>3&i<str_line.length-1){
                String temp=str_line[i]+" ";
                if (calculate(temp,"\t")>100){
                    return null;
                }
                String[] items=temp.split("\t");
                List<String>tab_item=new ArrayList<>();
                for(int j=0;j<items.length;j++){
                    if (j>100){
                        break;
                    }
                    tab_item.add(items[j].trim());
                    //Log.e("item",items[j]);
                    //Log.e("test","--------"+j);
                }
                tab_list.add(tab_item);
            }
        }
        for (int i=0;i<tab_list.size();i++){
            List<String>item=tab_list.get(i);
            JSONObject jsonObject=new JSONObject();
            for (int j=0;j<item.size();j++){
                jsonObject.put(zd_list.get(j),item.get(j));
            }
            array.put(jsonObject);
        }
        return array;
    }

    public static JSONObject stringToJsonObject(String result) throws JSONException {
        //Log.e("webSevice",result);
        String format_1=result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://zblog.vicp.net/\">","");
        String format_2=format_1.replace("</string>","");
        String format_3=format_2.replace("\\t","    ");
        Log.e("format",format_3);
        return new JSONObject(format_3);
    }



    public static int calculate(String str,String substr){
        if (str.length()>0){
            String temp = str;
            int count = 0;
            int index = temp.indexOf(substr);
            while (index != -1) {
                temp = temp.substring(index + 1);
                index = temp.indexOf(substr);
                count++;
            }
            return count;
        }else {
            return 0;
        }
    }

}
