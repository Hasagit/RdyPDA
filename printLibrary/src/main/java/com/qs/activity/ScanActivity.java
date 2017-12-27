package com.qs.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qs.demo3506.R;
import com.qs.service.ScanService;

public class ScanActivity extends Activity {

    private Button mBtnOpen = null;
    private Button mBtnClose = null;
    private Button mBtnScan = null;
    private EditText mTv = null;

//    private PosApi mApi = null;

    private byte mGpioPower = 0x1E ;//PB14
    private byte mGpioTrig = 0x29 ;//PC9

    private int mCurSerialNo = 3; //usart3
    private int mBaudrate = 4; //9600

//    private ScanBroadcastReceiver scanBroadcastReceiver;

//    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initViews();
    }

    private void initViews(){
        mBtnOpen = (Button)this.findViewById(R.id.btn_open);
        mBtnClose = (Button)this.findViewById(R.id.btn_close);
        mBtnScan = (Button)this.findViewById(R.id.btn_scan);
        mTv = (EditText)this.findViewById(R.id.tv);

        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDevice();
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                closeDevice();
            	mTv.setText("");
            }
        });
        
        mBtnScan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                    	ScanService.mApi.gpioControl(mGpioTrig, 0, 0);
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                    	ScanService.mApi.gpioControl(mGpioTrig, 0, 1);
                        break;
                    }

                    default:

                        break;
                }
                return false;
            }
        });


    }
    
    private void openDevice(){
    	
    	ScanService.mApi.gpioControl(mGpioPower,0,1);

    	ScanService.mApi.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1);
        
    }

    private void closeDevice(){
        //close power
    	ScanService.mApi.gpioControl(mGpioPower,0,0);
    	ScanService.mApi.extendSerialClose(mCurSerialNo);
    }


}
