package com.rdypda.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DengJf on 2018/3/6.
 */

public class HlQrCodeUtil {
    private String qrCode;
    private JSONObject jsonObject;
    private String wldm;
    private String ph;
    private String tmxh;
    private String sl;
    private String dw;

    public HlQrCodeUtil(String qrCode) {
        this.qrCode = qrCode;
        jsonObject=new JSONObject();
        String[] items=qrCode.split("\\*");
        for (int i=0;i<items.length;i++){
            if (items[i].length()<3){
                break;
            }
            String key=items[i].substring(0,2);
            String values=items[i].substring(2,items[i].length());
            try {
                jsonObject.put(key,values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getWldm() {
        try {
            wldm=jsonObject.getString("PN");
        } catch (JSONException e) {
            e.printStackTrace();
            wldm="";
        }
        return wldm;
    }

    public String getPh() {
        try {
            ph=jsonObject.getString("LT");
        } catch (JSONException e) {
            e.printStackTrace();
            ph="";
        }
        return ph;
    }

    public String getTmxh() {
        try {
            tmxh=jsonObject.getString("BR");
        } catch (JSONException e) {
            e.printStackTrace();
            tmxh="";
        }
        return tmxh;
    }

    public String getSl() {
        try {
            sl=jsonObject.getString("QY");
        } catch (JSONException e) {
            e.printStackTrace();
            sl="";
        }
        return sl;
    }

    public String getDw() {
        try {
            dw=jsonObject.getString("UT");
        } catch (JSONException e) {
            e.printStackTrace();
            dw="";
        }
        return dw;
    }
}
