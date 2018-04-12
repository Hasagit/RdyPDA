package com.rdypda.util;

import android.content.Context;
import android.util.Log;

import com.example.tscdll.TSCActivity;
import com.rdypda.model.cache.PreferenUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by DengJf on 2018/1/11.
 */

public class PrinterUtil {
    private String Address;
    private TSCActivity tscActivity;
    private Context context;
    private PreferenUtil preferenUtil;
    public PrinterUtil(Context context) {
        tscActivity=new TSCActivity();
        this.context=context;
        preferenUtil=new PreferenUtil(context);
    }

    public void openPort(String address){
        tscActivity.openport(address);
        //41
        if (preferenUtil.getInt("printNum")==0){
            tscActivity.setup(75,43,4,10,0,0,0);
            preferenUtil.setInt("printNum",1);
            Log.e("printNum",0+"");
        }else if (preferenUtil.getInt("printNum")==1){
            tscActivity.setup(75,42,4,10,0,0,0);
            preferenUtil.setInt("printNum",2);
            Log.e("printNum",1+"");
        }else if (preferenUtil.getInt("printNum")==2){
            tscActivity.setup(75,42,4,10,0,0,0);
            preferenUtil.setInt("printNum",0);
            Log.e("printNum",2+"");
        }
        tscActivity.clearbuffer();
    }

    public void printFont(String str,int x,int y) throws UnsupportedEncodingException {
        tscActivity.sendcommand("TEXT "+x+","+y+",\"FONT001\",0,2,2,\"");
        tscActivity.sendcommand(str.getBytes("gb2312"));
        tscActivity.sendcommand("\"\n");
    }

    public void printBarcode(String barcode,int x,int y){
        tscActivity.barcode(x,y,"128",70,1,0,1,1,barcode);
    }

    public void printQRCode(String str,int x,int y,int weight){
        tscActivity.sendcommand("QRCODE "+x+","+y+",M,"+weight+",M,0,M1,S2,\"A"+str+"\" \n");
    }

    public void startPrint(){
        tscActivity.printlabel(1,1);
        tscActivity.closeport();
    }

}

