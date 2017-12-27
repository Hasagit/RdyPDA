package com.rdypda.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.posapi.PosApi;
import android.posapi.PosApi.OnDeviceStateListener;
import android.posapi.PrintQueue;
import android.widget.Toast;

import com.qs.activity.MainActivity;
import com.qs.service.ScanService;
import com.qs.utils.BarcodeCreater;
import com.qs.utils.BitmapTools;
import com.qs.wiget.App;

import java.io.UnsupportedEncodingException;

/**
 * Created by DengJf on 2017/12/25.
 */

public class PrintUtil {
    private PrintQueue mPrintQueue = null;
    private Context context;
    private MediaPlayer player;
    private PosApi  mPosSDK;


    public PrintUtil(final Context context) {
        this.context = context;
        //获取状态时回调
        //mPosSDK.setOnDeviceStateListener(onDeviceStateListener);

        player = MediaPlayer.create(context, com.qs.demo3506.R.raw.beep);
        /*IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
        context.registerReceiver(receiver, mFilter);*/
        mPosSDK = PosApi.getInstance(context);
    }

    public void initPost(){
        //初始化接口时回调
        mPosSDK.setOnComEventListener(new PosApi.OnCommEventListener() {
            @Override
            public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
                // TODO Auto-generated method stub
                switch(cmdFlag){
                    case PosApi.POS_INIT:
                        if(state==PosApi.COMM_STATUS_SUCCESS){
                            //Toast.makeText(context, "设备初始化成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "设备初始化失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        mPosSDK.setOnDeviceStateListener(new OnDeviceStateListener(){
            @Override
            public void OnGetState(int i, String s, String s1, int i1, int i2, int i3, int i4, int i5) {
                switch (i1){
                    case 0:
                        //正常
                        break;
                    case 1:
                        Toast.makeText(context,"psam1无卡",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context,"psam1卡错误",Toast.LENGTH_SHORT).show();
                        break;
                }

                switch (i2){
                    case 0:
                        //正常
                        break;
                    case 1:
                        Toast.makeText(context,"psam2无卡",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context,"psam2卡错误",Toast.LENGTH_SHORT).show();
                        break;
                }

                switch (i3){
                    case 0:
                        //正常
                        break;
                    case 1:
                        Toast.makeText(context,"IC无卡",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context,"IC卡错误",Toast.LENGTH_SHORT).show();
                        break;
                }


                switch (i4){
                    case 0:
                        //正常
                        break;
                    case 1:
                        Toast.makeText(context,"磁条卡无卡",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context,"磁条卡卡错误",Toast.LENGTH_SHORT).show();
                        break;
                }

                switch (i5){
                    case 0:
                        //正常
                        break;
                    case 1:
                        Toast.makeText(context,"打印机正常",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        Intent newIntent = new Intent(context, ScanService.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(newIntent);
    }

    public void initPrintQueue(){
        mPrintQueue = new PrintQueue( context,mPosSDK);
        mPrintQueue.init();
    }

    public PrintQueue getPrintQueue() {
        return mPrintQueue;
    }

    public void printBarcode(String barcode){
        Bitmap bitmap=BarcodeCreater.creatBarcode(context,barcode,300,100,true,1);

        int mLeft = 0;
        byte[] printData = BitmapTools.bitmap2PrinterBytes(bitmap);
        int concentration = 60;

        mPrintQueue.addBmp(concentration, mLeft, bitmap.getWidth(),
                bitmap.getHeight(), printData);
        bitmap.recycle();
    }

    public void printQRcode(String qrcode){
        Bitmap bitmap=BarcodeCreater.encode2dAsBitmap(qrcode,300,300,2);

        int mLeft = 0;
        byte[] printData = BitmapTools.bitmap2PrinterBytes(bitmap);
        int concentration = 60;

        mPrintQueue.addBmp(concentration, mLeft, bitmap.getWidth(),
                bitmap.getHeight(), printData);
        bitmap.recycle();
    }

    public void printText(String text){
        PrintQueue.TextData data=mPrintQueue.new TextData();
        data.addText(text);
        mPrintQueue.addText(80, data);
    }

    public void printStart(PrintQueue.OnPrintListener listener){
        mPrintQueue.setOnPrintListener(listener);
        mPrintQueue.printStart();
    }

    public void closePrint(){
        if (mPrintQueue!=null){
            mPrintQueue.close();
        }
    }


}
