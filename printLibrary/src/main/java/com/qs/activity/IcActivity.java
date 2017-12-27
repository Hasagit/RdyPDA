package com.qs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.posapi.Conversion;
import android.posapi.PosApi;
import android.posapi.PosApi.OnIcPasmEventListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qs.demo3506.R;
import com.qs.service.ScanService;
import com.qs.wiget.App;

public class IcActivity extends Activity {
	private TextView mTv = null;
	private Button mBtnIcInit = null;

	private Button mBtnClean = null;
	private Button mBtnClose = null;
	private Button mBtnCmd   = null;
	private EditText mEtCmd  = null;
//	private PosApi  mApi  = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ic);
		ScanService.mApi.setOnIcPasmEventListener(onIcPasmEventListener);
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mTv =(TextView)this.findViewById(R.id.tv_text);
		mBtnIcInit = (Button)this.findViewById(R.id.btn_p1init);

		mBtnClose = (Button)this.findViewById(R.id.btn_close);
		mBtnClean = (Button)this.findViewById(R.id.btn_clear);
		mBtnCmd= (Button)this.findViewById(R.id.btnSend);
		mEtCmd= (EditText)this.findViewById(R.id.etContent);

		//reset  ic
		mBtnIcInit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ScanService.mApi.icReset(500);

			}
		});



		mBtnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ScanService.mApi.icClose(500);
			}
		});

		mBtnCmd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str  = mEtCmd.getText().toString().trim();
				if(TextUtils.isEmpty(str))return;
				byte[] mCmd = Conversion.HexString2Bytes(str);
				ScanService.mApi.icCmd(mCmd, mCmd.length);

			}
		});

		mBtnClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTv.setText("");
			}
		});
	}

	OnIcPasmEventListener onIcPasmEventListener = new OnIcPasmEventListener() {

		@Override
		public void onReset(int state, int slot, byte[] atr, int length) {
			// TODO Auto-generated method stub
			if(state==PosApi.COMM_STATUS_SUCCESS){
				mTv.append("IC卡复位成功\n");
				mTv.append("复位数据:"+Conversion.Bytes2HexString(atr)+"\n");
			}else {
				mTv.append("IC卡复位失败\n");
			}
		}

		@Override
		public void onClose(int state, int slot) {
			// TODO Auto-generated method stub
			if(state==PosApi.COMM_STATUS_SUCCESS){
				mTv.append("IC卡关闭成功\n");

			}else {
				mTv.append("IC卡关闭失败\n");
			}
		}

		@Override
		public void onApdu(int state, int slot, byte[] reApdu, int length) {
			// TODO Auto-generated method stub
			if(state==PosApi.COMM_STATUS_SUCCESS){
				if(reApdu!=null){
					//iC APDU 执行成功
					mTv.append("数据回复:"+Conversion.Bytes2HexString(reApdu)+"\n");
				}
			}else {
				//iC 命令 执行失败
				mTv.append("IC 命令执行失败\n");
			}
		}
	};



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(ScanService.mApi!=null){
			ScanService.mApi.removeEventListener(onIcPasmEventListener);
		}
	}
}
