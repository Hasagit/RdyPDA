package com.qs.service;

import java.io.UnsupportedEncodingException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.posapi.PosApi;
import android.util.Log;
import android.widget.Toast;

import com.qs.demo3506.R;
import com.qs.wiget.App;

public class ScanService extends Service {

	private boolean isOpen = false;
	private int mComFd = -1;
	private final static int SHOW_RECV_DATA = 1;
	// public static ServiceBeepManager beepManager;

	public static PosApi mApi = null;

	private static byte mGpioPower = 0x1E;// PB14
	private static byte mGpioTrig = 0x29;// PC9

	private static int mCurSerialNo = 3; // usart3
	private static int mBaudrate = 4; // 9600

	private ScanBroadcastReceiver scanBroadcastReceiver;

	MediaPlayer player;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mApi = App.getInstance().getPosApi();
		
		
//
//		if (Build.MODEL.equalsIgnoreCase("3508")||Build.MODEL.equalsIgnoreCase("403")) {
//			mApi.initPosDev("ima35s09");
//		} else {
//			mApi.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01);
//		}

		initGPIO();

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
		registerReceiver(receiver_, mFilter);

		scanBroadcastReceiver = new ScanBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ismart.intent.scandown");
		this.registerReceiver(scanBroadcastReceiver, intentFilter);

		player = MediaPlayer.create(getApplicationContext(), R.raw.beep);

		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	BroadcastReceiver receiver_ = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equalsIgnoreCase(PosApi.ACTION_POS_COMM_STATUS)) {
				int cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1);
				byte[] buffer = intent
						.getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER);
				switch (cmdFlag) {
				case PosApi.POS_EXPAND_SERIAL_INIT:
					break;
				case PosApi.POS_EXPAND_SERIAL3:
					if (buffer == null)
						return;
					player.start();
					try {
						String str = new String(buffer, "GBK");
						Log.e("ScanStr", "-----:" + str.trim());
						Intent intentBroadcast = new Intent();
						Intent intentBroadcast1 = new Intent();
						intentBroadcast.setAction("com.qs.scancode");
						intentBroadcast1.setAction("com.zkc.scancode");
						intentBroadcast.putExtra("code", str.trim());
						intentBroadcast1.putExtra("code", str.trim());
						sendBroadcast(intentBroadcast);
						sendBroadcast(intentBroadcast1);
						isScan = false;
						ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
						handler.removeCallbacks(run);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				buffer = null;
			}
		}
	};

	public String toGBK(String str) throws UnsupportedEncodingException {
		return this.changeCharset(str, "GBK");
	}

	/**
	 * 字符串编码转换的实现方法
	 * 
	 * @param str
	 *            待转换编码的字符串
	 * @param newCharset
	 *            目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String changeCharset(String str, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = str.getBytes(newCharset);
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	static boolean isIscanScan = false;

	public static void openScan() {
		ScanService.mApi.gpioControl(mGpioTrig, 0, 0);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
	}

	private static void openDevice() {
		// open power
		mApi.gpioControl(mGpioPower, 0, 1);

		mApi.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1);
	}

	// private static void closeDevice() {
	// // open power
	// mApi.gpioControl(mGpioPower, 0, 0);
	// mApi.extendSerialClose(mCurSerialNo);
	// }

	private void initGPIO() {
		// TODO Auto-generated method stub

//		openDevice();

		/*Toast.makeText(getApplicationContext(), "扫描服务初始化", Toast.LENGTH_SHORT)
				.show();*/

		if (mComFd > 0) {
			isOpen = true;
			// readData();
		} else {
			isOpen = false;
		}
	}

	
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				openDevice();
			}
		}, 1000);
	}

	Vibrator vibrator;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mApi.closeDev();
		super.onDestroy();
	}

	boolean isScan = false;

	class ScanBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!isScan) {
				ScanService.mApi.gpioControl(mGpioTrig, 0, 0);
				isScan = true;
				handler.removeCallbacks(run);
				handler.postDelayed(run, 3000);
			} else {
				ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
				ScanService.mApi.gpioControl(mGpioTrig, 0, 0);
				isScan = true;
				handler.removeCallbacks(run);
				handler.postDelayed(run, 3000);
			}
		}
	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
			isScan = false;
		}
	};

}
