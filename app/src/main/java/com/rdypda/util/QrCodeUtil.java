package com.rdypda.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DengJf on 2018/1/26.
 */

public class QrCodeUtil {
    String qrcode;
    JSONObject object;
    String cpxh;//产品型号
    String scpc;//生产批次
    String tmxh;//条码序号
    String bzsl;//包装数量
    String dw;//单位

    public QrCodeUtil(String qrcode) {
        this.qrcode = qrcode;
        object=new JSONObject();
        String[] items=qrcode.split("\\*");
        for (int i=0;i<items.length;i++){
            if (items[i].length()<2)
                break;
            String key=items[i].substring(0,2);
            String values=items[i].substring(2,items[i].length());
            try {
                object.put(key,values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getWlbh() {
        try {
            return object.getString("PN");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getScpc() {
        try {
            return object.getString("LT");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getTmxh() {
        try {
            return object.getString("BR");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getBzsl() {
        try {
            return object.getString("QY");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getDw() {
        try {
            return object.getString("UT");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
