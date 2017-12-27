package com.qs.activity;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PosApi.OnCommEventListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.qs.adapter.TestInfoAdapter;
import com.qs.bean.TestInfo;
import com.qs.demo3506.R;
import com.qs.service.ScanService;
import com.qs.utils.DialogUtils;
import com.qs.utils.ProgressDialogUtils;
import com.qs.wiget.App;
import com.qs.wiget.CustomDialog;

public class MainActivity extends Activity {
	private GridView mGridView = null;
	private List<TestInfo> testInfos = null;
	private TestInfoAdapter  mAdapter = null;
	private PosApi  mPosSDK;
	private boolean isSwipCard = false;

	public static final int  POS_IDCARD = -4 ;
	public static final int  POS_FAN_CONTROL = -100;
	public static final byte GPIO_PRINT_LED = (byte)0x23;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		initViews();
		initData();
		setListener();

//		mPosSDK = App.getInstance().getPosApi();
//		mPosSDK.setOnComEventListener(mCommEventListener);

		mGridView.setNumColumns(3);
//		mPosSDK.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01);


		mPosSDK = App.getInstance().getPosApi();
		//初始化接口时回调
		mPosSDK.setOnComEventListener(mCommEventListener);
		//获取状态时回调
		mPosSDK.setOnDeviceStateListener(onDeviceStateListener);

		Intent newIntent = new Intent(MainActivity.this, ScanService.class);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(newIntent);

	}

	private void initData() {
		// TODO Auto-generated method stub

		testInfos = new ArrayList<TestInfo>();
		TestInfo  info = null;
		info = new TestInfo(getString(R.string.reset),R.drawable.ic_rest,PosApi.POS_INIT);
		testInfos.add(info);

		info = new TestInfo(getString(R.string.state),R.drawable.ic_state,PosApi.POS_GET_STATUS);
		testInfos.add(info);



		info = new TestInfo(getString(R.string.ic_card),R.drawable.ic_ic,PosApi.POS_IC_RESET);
		testInfos.add(info);


		info = new TestInfo(getString(R.string.psam_card),R.drawable.ic_psam,PosApi.POS_PSAM_RESET);
		testInfos.add(info);



		info = new TestInfo(getString(R.string.print),R.drawable.ic_printimage,PosApi.POS_PRINT_PICTURE);
		testInfos.add(info);

//		info = new TestInfo(getString(R.string.serial_port),R.drawable.ic_serial,PosApi.POS_EXPAND_SERIAL1);
//		testInfos.add(info);

		info = new TestInfo("SCAN",R.drawable.ic_barcode,PosApi.POS_BARCODE_INIT);
		testInfos.add(info);

		info = new TestInfo("NFC",R.drawable.ic_nfc,-2);
		testInfos.add(info);

		info = new TestInfo(getString(R.string.update),R.drawable.ic_update,-3);
		testInfos.add(info);

//		info = new TestInfo(getString(R.string.ex_io),R.drawable.ic_io,PosApi.POS_GPIO);
//		testInfos.add(info);

//		if(Build.MODEL.equalsIgnoreCase("3506")){
//		}

		mAdapter = new TestInfoAdapter(MainActivity.this, testInfos);
		mGridView.setAdapter(mAdapter);
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mGridView = (GridView)this.findViewById(R.id.mGridView);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				TestInfo info = (TestInfo) parent.getAdapter().getItem(position);
				switch(info.getCmd()){
					case PosApi.POS_INIT:
						if (Build.MODEL.equalsIgnoreCase("3508")||Build.MODEL.equalsIgnoreCase("403")) {
							mPosSDK.initPosDev("ima35s09");
							App.getInstance().setCurDevice("ima35s09");
						} else {
							mPosSDK.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01);
							App.getInstance().setCurDevice(PosApi.PRODUCT_MODEL_IMA80M01);
						}
						break;
					case PosApi.POS_GET_STATUS:
						//正在获取pos状态
						showTips(getString(R.string.getting_pos_status));
						App.getInstance().getPosApi().getDevStatus();
						break;
					case PosApi.POS_SWIPING_CARD:
						break;
					case PosApi.POS_IC_RESET:
						startActivity(new Intent(MainActivity.this, IcActivity.class));
						break;
					case PosApi.POS_PSAM_RESET:
						startActivity(new Intent(MainActivity.this, PsamActivity.class));
						break;
					case PosApi.POS_PRINT_PICTURE:
						startActivity(new Intent(MainActivity.this, PrintActivity.class));
						break;
					case PosApi.POS_EXPAND_SERIAL1:
						break;
					case PosApi.POS_LED:
						break;
					case -1:
						processGpioControl();
						break;
					case -2:
						startActivity(new Intent(MainActivity.this, NfcActivity.class));
						break;
					case -3:
						startActivity(new Intent(MainActivity.this, PosUpdateActivity.class));
						break;
					case PosApi.POS_BARCODE_INIT:
						startActivity(new Intent(MainActivity.this, ScanActivity.class));
						break;
				}
			}
		});
	}

	private void processGpioControl(){
		String[] states = new String[]{"开","关"};
		new AlertDialog.Builder(this).setTitle("设置PS/2 电源").setItems(states, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
					case 0:
						ScanService.mApi.gpioControl((byte)0x20, 0, 1);
						break;
					case 1:
						ScanService.mApi.gpioControl((byte)0x20, 0, 0);
						break;
				}
			}
		}).show();
	}

	OnCommEventListener mCommEventListener = new OnCommEventListener() {
		@Override
		public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
			// TODO Auto-generated method stub
			switch(cmdFlag){
				case PosApi.POS_INIT:
					if(state==PosApi.COMM_STATUS_SUCCESS){
						Toast.makeText(getApplicationContext(), "设备初始化成功", Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getApplicationContext(), "设备初始化失败", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	};

	private PosApi.OnDeviceStateListener onDeviceStateListener = new PosApi.OnDeviceStateListener() {
		/**
		 * @param state 0-获取状态成功  1-获取状态失败
		 * @param version 设备固件版本
		 * @param serialNo 设备序列号
		 * @param psam1 psam1 状态   0-正常   1-无卡   2-卡错误
		 * @param psam2 psam2 状态   0-正常   1-无卡   2-卡错误
		 * @param ic IC卡 状态   0-正常   1-无卡   2-卡错误
		 * @param swipcard 磁卡状态 o-正常  1-故障
		 * @param printer 打印机状态 0-正常  1-缺纸
		 */
		public void OnGetState(int state,String version,String serialNo,int psam1,int psam2,int ic,int swipcard,int printer){
			ProgressDialogUtils.dismissProgressDialog();
			if(state == PosApi.COMM_STATUS_SUCCESS){

				StringBuilder sb = new StringBuilder();
				String mPsam1 = null;
				switch(psam1){
					case 0:
						mPsam1=getString(R.string.state_normal);
						break;
					case 1:
						mPsam1=getString(R.string.state_no_card);
						break;
					case 2:
						mPsam1=getString(R.string.state_card_error);
						break;
				}

				String mPsam2 = null;
				switch(psam2){
					case 0:
						mPsam2=getString(R.string.state_normal);
						break;
					case 1:
						mPsam2=getString(R.string.state_no_card);
						break;
					case 2:
						mPsam2=getString(R.string.state_card_error);
						break;
				}

				String mIc = null;
				switch(ic){
					case 0:
						mIc=getString(R.string.state_normal);
						break;
					case 1:
						mIc=getString(R.string.state_no_card);
						break;
					case 2:
						mIc=getString(R.string.state_card_error);
						break;
				}

				String magnetic_card = null;
				switch(swipcard){
					case 0:
						magnetic_card=getString(R.string.state_normal);
						break;
					case 1:
						magnetic_card=getString(R.string.state_fault);
						break;

				}

				String mPrinter = null;
				switch(printer){
					case 0:
						mPrinter=getString(R.string.state_normal);
						break;
					case 1:
						mPrinter=getString(R.string.state_no_paper);
						break;

				}

				sb.append(/*getString(R.string.pos_status)+"\n "
							+*/
						getString(R.string.psam1_)+mPsam1+"\n" //pasm1
								+getString(R.string.psam2)+mPsam2+"\n" //pasm2
								+getString(R.string.ic_card)+mIc+"\n" //card
								+getString(R.string.magnetic_card)+magnetic_card+"\n" //磁条卡
								+getString(R.string.printer)+mPrinter+"\n" //打印机
				);

				sb.append(getString(R.string.pos_serial_no)+serialNo+"\n");
				sb.append(getString(R.string.pos_firmware_version)+version);
				DialogUtils.showTipDialog(MainActivity.this,sb.toString());

			}
			else
			{
				// 获取状态失败
				DialogUtils.showTipDialog(MainActivity.this,getString(R.string.get_pos_status_failed));
			}
		}
	};

	public void showAlertDialog(String msg) {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(msg);
		builder.setTitle(getString(R.string.tips));
		builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置你的操作事项
			}
		});

		builder.setNegativeButton(getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	private void showTips(String msg){
		ProgressDialogUtils.showProgressDialog(MainActivity.this, msg);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		//这里一定要做双重判断，不然会出现两个对话框
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if(isSwipCard){
				Toast.makeText(this, "取消刷卡", Toast.LENGTH_SHORT).show();
				ScanService.mApi.sendKeyCmd(0x91);
			}else{
				this.finish();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		IntentFilter filter  = new IntentFilter();
//		filter.addAction(PosApi.ACTION_POS_KEY_EVENT);
//		filter.addAction(PosApi.ACTION_POS_COMM_STATUS);
//		filter.addAction(PosApi.ACTION_POS_GET_STATUS);
//		filter.addAction(PosApi.ACTION_POS_SWIPING_CARD);
//		registerReceiver(onDeviceStateListener, filter);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);//通过程序改变屏幕显示的方向
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		unregisterReceiver(onDeviceStateListener);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//unregisterReceiver(mPosSDKReceiver);
		ProgressDialogUtils.dismissProgressDialog();
//		if(mPosSDK!=null){
//			mPosSDK.closeDev();
//		}
	}

}

