package com.rdypda.util;

import android.content.Context;
import android.util.Log;

import com.example.tscdll.TSCActivity;
import com.rdypda.model.cache.PreferenUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by DengJf on 2018/1/11.
 */

/**
 * 打印工具
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
            //打印纸宽，高，打印密度（值越大越暗），传感器类型（0，代表垂直传感器，1代表黑色标记传感器。），设置垂直间隙高度的差距，设置间隙/黑色标记的移位距离
            tscActivity.setup(75,43,4,10,0,0,0);
            preferenUtil.setInt("printNum",1);
            Log.e("printNum",0+"");
        }else if (preferenUtil.getInt("printNum")==1){
            tscActivity.setup(75,42,4,10,0,0,0);
            preferenUtil.setInt("printNum",0);
            Log.e("printNum",1+"");
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

    //M代表
    public void printQRCode(String str,int x,int y,int weight){
        tscActivity.sendcommand("QRCODE "+x+","+y+",M,"+weight+",M,0,M1,S2,\"A"+str+"\" \n");
    }

    public void startPrint(){
        tscActivity.printlabel(1,1);
        tscActivity.closeport();
    }

}

