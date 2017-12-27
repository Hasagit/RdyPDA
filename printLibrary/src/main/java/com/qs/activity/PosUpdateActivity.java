package com.qs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PosApi.OnDeviceUpdateListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qs.demo3506.R;
import com.qs.utils.FileUtils;
import com.qs.utils.ProgressDialogUtils;
import com.qs.wiget.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * POS机固件更新
 * @author huangyifa
 * @date 2015-3-20 下午4:31:32
 * @Description: TODO(用一句话描述该文件做什么)
 * @version V1.0
 */
public class PosUpdateActivity extends Activity {
	private static  int   FILE_SELECT_CODE =  1000;
	private Button mBtnSelectFile;
	private Button mBtnUpdateFonts;
	private Button mBtnReadoutUnprotect = null;

	private ProgressBar mProgressBar = null;
	private TextView mDownloadState = null;

	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE" };

	private PosApi mPosApi ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		initView();
		mPosApi =  App.getInstance().getPosApi();
		mPosApi.setOnDeviceUpdateListener(onDeviceUpdateListener);

		// 获取权限
		verifyStoragePermissions(this);

	}

	public static void verifyStoragePermissions(Activity activity) {

		try {
			// 检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity,
						PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBtnSelectFile=(Button)this.findViewById(R.id.btnSelectFile);
		mBtnUpdateFonts = (Button)this.findViewById(R.id.btnUpdateFonts);
		mBtnReadoutUnprotect = (Button)this.findViewById(R.id.btnReadoutUnprotect);
		mProgressBar = (ProgressBar) this.findViewById(R.id.pb_download);
		mDownloadState=(TextView)this.findViewById(R.id.tv_download_state);
		mDownloadState.setText("当前设备:"+App.getInstance().getCurDevice());
		mBtnSelectFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showFileChooser();
			}
		});

		mBtnUpdateFonts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showUpdatePosFonts();
			}
		});

		mBtnReadoutUnprotect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ProgressDialogUtils.showProgressDialog(PosUpdateActivity.this, getString(R.string.readoutUnprotect));
				new AsyncTask<Integer, Integer, Integer>() {

					@Override
					protected Integer doInBackground(Integer... params) {
						// TODO Auto-generated method stub
						int ret = mPosApi.readoutUnprotect();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int wRet = mPosApi.writeUnportect();
						return ret;
					}

					@Override
					protected void onPostExecute(Integer result) {
						ProgressDialogUtils.dismissProgressDialog();
						if(result== 0 ){
							Toast.makeText(PosUpdateActivity.this, "readoutUnprotect success ", Toast.LENGTH_SHORT).show();
						}
					};
				}.execute();
			}
		});
	}

	private  void showUpdatePosFonts(){
		new AlertDialog.Builder(PosUpdateActivity.this)
				.setTitle(getString(R.string.update_fonts_))
				.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						updateFonts();
					}
				})
				.setNegativeButton(getString(R.string.cancel), null)
				.show();
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FILE_SELECT_CODE &&resultCode == RESULT_OK) {
			// Get the Uri of the selected file
			Uri uri = data.getData();
			String path = FileUtils.getPath(this, uri);
			if(path==null){
				Toast.makeText(PosUpdateActivity.this, "file error", Toast.LENGTH_SHORT).show();
				return ;
			}
			showIsUpdateDialog(path);
		}

	}

	private  void updateFonts(){
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				InputStream is = getResources().openRawResource(R.raw.print2);

				int length;
				try {
					length = is.available();
					byte [] buffer = new byte[length];
					is.read(buffer);
					is.close();
					int ret = mPosApi.updateFonts(buffer, length);
					Log.d("hello","updateFonts ret="+ret);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				mPosApi.resetDev(App.getInstance().getCurDevice());

			}
		}.execute();
	}


	class UpdateThread extends AsyncTask<File, Integer, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//ProgressDialogUtils.showProgressDialog(PosUpdateActivity.this, "正在升级...");
		}
		@Override
		protected Integer doInBackground(File... params) {
			// TODO Auto-generated method stub
			try {

				FileInputStream is = new FileInputStream(params[0]);
				int length = is.available();
				byte [] buffer = new byte[length];
				is.read(buffer);
				is.close();
				int ret = -1;
				//ret = mPosApi.updateDev(buffer,length);
				ret = mPosApi.updateDevice(App.getInstance().getCurDevice(),buffer,length);
				return ret;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("hello", "IOException"+e.getMessage());
				return -1;
			}
		}


		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//ProgressDialogUtils.dismissProgressDialog();
			if(result==0){
				Toast.makeText(PosUpdateActivity.this, getString(R.string.upgrade_success), Toast.LENGTH_SHORT).show();
				//mTestView.setText("");
				//mPosSDK.resetPos();
				//mPosSDK.unInitialize();
			}else{
				Toast.makeText(PosUpdateActivity.this, getString(R.string.upgrade_failed), Toast.LENGTH_SHORT).show();
			}

		}

	}

	private void showIsUpdateDialog(String updateFilePath){
		if(updateFilePath==null||updateFilePath.equals("")) return;
		final File file  =new File(updateFilePath);
		if(file==null|| !file.exists()){
			//Toast.makeText(PosUpdateActivity.this, "没有找到文件", Toast.LENGTH_SHORT).show();
			return;
		}

		new AlertDialog.Builder(PosUpdateActivity.this)
				.setTitle(getString(R.string.update_Pos_firmware))
				.setMessage(getString(R.string.firmware_path)+updateFilePath)
				.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						mPosApi.closeDev();

						new UpdateThread().execute(file);
					}
				})
				.setNegativeButton(getString(R.string.cancel), null)
				.show();
	}


	private OnDeviceUpdateListener  onDeviceUpdateListener = new OnDeviceUpdateListener(){

		@Override
		public void onStart() {

			mDownloadState.setText(getString(R.string.start_writing_program)+"\n");
		}

		@Override
		public void onProgress(int progress) {
			mProgressBar.setProgress(progress);
			mDownloadState.setText("正在更新设备:"+progress+"%");
		}

		@Override
		public void onFinish() {
			mDownloadState.setText("设备固件更新完成");
		}

		@Override
		public void onFailed(int state) {
			switch(state){
				case PosApi.ERR_MCU_CONNECT_FAILED:
					//通信异常
					mDownloadState.setText("更新失败:通信异常");
					break;
				case PosApi.ERR_SYNC_BAUDRATE_FAILED:
					mDownloadState.setText("更新失败:同步波特率失败");
					break;
				case PosApi.ERR_ERASE_FLASH_FAILED:
					mDownloadState.setText("更新失败:擦除存储器失败");
					break;
				case PosApi.ERR_WRITE_FLASH_FAILED:
					mDownloadState.setText("更新失败:写入存储器失败");
					break;
			}

		}
	};


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		ProgressDialogUtils.dismissProgressDialog();
	}



}
