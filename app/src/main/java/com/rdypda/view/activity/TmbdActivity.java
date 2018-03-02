package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.TmbdPresenter;
import com.rdypda.view.viewinterface.ITmbdView;
import com.rdypda.view.widget.PowerButton;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TmbdActivity extends BaseActivity implements ITmbdView {
    @BindView(R.id.tmbh)
    TextView tmxhText;
    @BindView(R.id.wlbh)
    TextView wlbhText;
    @BindView(R.id.tmsl)
    TextView tmslText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private TmbdPresenter presenter;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmbd);
        ButterKnife.bind(this);
        initView();
        presenter=new TmbdPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("条码补打");
        actionBar.setDisplayHomeAsUpEnabled(true);

        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("请稍后...");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                showAddDialog();
                break;
            case R.id.setting:
                showBlueToothAddressDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tmbd_menu, menu);
        return true;
    }

    public void showAddDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_tm,null);
            final AlertDialog deleteDialog=new AlertDialog.Builder(this).setView(view).create();
            deleteDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            PowerButton delBtn=(PowerButton)view.findViewById(R.id.sure_btn);
            PowerButton cancelBtn=(PowerButton) view.findViewById(R.id.cancel_btn);
            final TextInputEditText tmEd=(TextInputEditText)view.findViewById(R.id.tm_ed);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmxh=tmEd.getText().toString();
                    if (tmxh.equals("")){
                        Toast.makeText(TmbdActivity.this,"条码序号不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        presenter.isValidCode(tmxh);
                        deleteDialog.dismiss();
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.show();
        }
    }


    @Override
    public void setShowProgressDialogEnable(boolean enable) {
        if(enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void setShowMsgDialog(String msg) {
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    public void setTmMsg(String tmxh, String wlbh, String tmsl) {
        tmxhText.setText(tmxh);
        wlbhText.setText(wlbh);
        tmslText.setText(tmsl);
    }

    @Override
    public void showBlueToothAddressDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this,3);
        builder.setTitle("请选择蓝牙设备");
        builder.setPositiveButton("找不到设备？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this,3);
            dialog.setTitle("提示");
            dialog.setMessage("是否打开蓝牙");
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        }else {
            Set<BluetoothDevice> devicesSet = adapter.getBondedDevices();
            Object[] devices=devicesSet.toArray();
            final String[] itemName=new String[devices.length];
            final String[] itemAddress=new String[devices.length];
            for(int i=0;i<devices.length;i++){
                BluetoothDevice device=(BluetoothDevice)devices[i];
                itemName[i]=device.getName();
                itemAddress[i]=device.getAddress();
            }
            builder.setItems(itemName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenUtil preferenUtil=new PreferenUtil(TmbdActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @OnClick({R.id.tmbd_btn})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.tmbd_btn:
                presenter.printEven();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.closeScan();
        super.onDestroy();
    }
}
