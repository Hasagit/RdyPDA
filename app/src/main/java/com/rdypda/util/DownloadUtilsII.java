package com.rdypda.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.Observable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DengJf on 2018/1/22.
 */

public class DownloadUtilsII {
    private Context mContext;

    public DownloadUtilsII(Context mContext) {
        this.mContext = mContext;
    }

    public io.reactivex.Observable<Integer> downloadAPK(final String url_str, final String filePath, final String fileName){
        return io.reactivex.Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                URL url= null;
                File file=new File(filePath);
                if (!file.exists()){
                    file.mkdir();
                }
                url = new URL(url_str);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.connect();
                double fileSize=urlConnection.getContentLength()/1024;
                Log.e("getLastModified()",urlConnection.getLastModified()+"");
                InputStream in=urlConnection.getInputStream();
                OutputStream out=new FileOutputStream(filePath+"/"+fileName,false);
                byte[] buff=new byte[1024];
                int downloadSize=0;
                int size;
                while ((size = in.read(buff)) != -1) {
                    downloadSize++;
                    if (downloadSize%100==0){
                        int progress=(int) (downloadSize/fileSize*100);
                        if (progress<=100)
                        e.onNext(progress);
                        Log.e("download",downloadSize/fileSize+"");
                    }
                    out.write(buff, 0, size);
                }
                installAPK(filePath+"/"+fileName);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //下载到本地后执行安装
    private void installAPK(String filePath) {
        //获取下载文件的Uri
        Uri downloadFileUri = Uri.fromFile(new File(filePath));
        if (downloadFileUri != null) {
            Intent intent= new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
