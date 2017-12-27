package com.qs.wiget;

import android.app.Application;
import android.os.Build;
import android.posapi.PosApi;
import android.util.Log;

import com.qs.demo3506.R;


public class App extends Application{

	public static final String DEVICE_MODEL_IMA3511 = "iMA3511";
	public static final String DEVICE_MODEL_IMA3507 = "iMA3507";
	public static final String DEVICE_MODEL_SK_16 = "SK-16";
	public static final String DEVICE_MODEL_IMA128   = "X8";
	public static final String DEVICE_MODEL_IMA80M01   = "br6580_we_emmc_m";
	public static final String DEVICE_MODEL_IMA3512   = "iMA3512";
	public static final String DEVICE_MODEL_A380LTE   = "iMA35S05";
	public static final String DEVICE_MODEL_IMA35S09   = "3508";

	private String mCurDev = "";

	static App instance = null;
	//PosSDK mSDK = null;
	PosApi mPosApi = null;
	public App(){
		super.onCreate();
		instance = this;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (Build.MODEL.equals("PDA3505")){
			//mDb = Database.getInstance(this);
			Log.v("hello", "APP onCreate~~");
			//mSDK = PosSDK.getInstance(this);
			mPosApi = PosApi.getInstance(this);

			if (Build.MODEL.equalsIgnoreCase("3508")||Build.MODEL.equalsIgnoreCase("403")) {
				mPosApi.initPosDev("ima35s09");
				setCurDevice("ima35s09");
			} else {
				mPosApi.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01);
				setCurDevice(PosApi.PRODUCT_MODEL_IMA80M01);
			}
		};

	}

	public static  App getInstance(){
		if(instance==null){
			instance =new App();
		}
		return instance;
	}


	public String getCurDevice() {
		return mCurDev;
	}

	public void setCurDevice(String mCurDev) {
		this.mCurDev = mCurDev;
	}

	public PosApi getPosApi(){
		return mPosApi;
	}



}
