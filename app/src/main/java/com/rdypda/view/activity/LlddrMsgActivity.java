package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.LlddrMsgPresenter;
import com.rdypda.view.viewinterface.ILlddrMsgView;
import com.rdypda.view.widget.PowerButton;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LlddrMsgActivity extends BaseActivity implements ILlddrMsgView{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.setting_btn)
    PowerButton settingBtn;
    @BindView(R.id.wlbh)
    TextView wlbhText;
    @BindView(R.id.tmpch)
    EditText tmpchEd;
    @BindView(R.id.tmsl)
    EditText tmslEd;
    @BindView(R.id.tmxh)
    TextView tmxhText;
    @BindView(R.id.wfsl)
    TextView wfslText;
    @BindView(R.id.xqsl)
    TextView xqslText;
    private String lldhStr,wlbhStr,tmpchStr,tmslStr,dwStr,gchStr,kcddStr,kwStr,cwStr,wlpmStr,ywwlpmStr,xqslStr,wfslStr;
    private AlertDialog msgDialog;
    private LlddrMsgPresenter presenter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llddr_msg);
        ButterKnife.bind(this);
        initView();
        presenter=new LlddrMsgPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.more_msg));


        msgDialog=new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tip))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msgDialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        lldhStr=getIntent().getStringExtra("lldh");
        wlbhStr=getIntent().getStringExtra("wldm");
        dwStr=getIntent().getStringExtra("dw");
        gchStr=getIntent().getStringExtra("gch");
        kcddStr=getIntent().getStringExtra("kcdd");
        kwStr=getIntent().getStringExtra("kcdd");
        wlpmStr=getIntent().getStringExtra("wlpm");
        ywwlpmStr=getIntent().getStringExtra("ywwlpm");
        xqslStr=getIntent().getStringExtra("xqsl");
        wfslStr=getIntent().getStringExtra("wfsl");
        wlbhText.setText(wlbhStr);
        wfslText.setText(wfslStr);
        xqslText.setText(xqslStr);
    }


    @OnClick({R.id.print_btn,R.id.setting_btn,R.id.get_tm_btn,R.id.fl_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.print_btn:
                presenter.printEven(wlbhStr,wlpmStr,ywwlpmStr,tmxhText.getText().toString(),tmpchEd.getText().toString());
                break;
            case R.id.setting_btn:
                showBlueToothAddressDialog();
                break;
            case R.id.get_tm_btn:
                presenter.getTmxh(tmpchEd.getText().toString(),tmslEd.getText().toString(),lldhStr,
                        wlbhStr,dwStr,gchStr,kcddStr,tmxhText.getText().toString());
                break;
            case R.id.fl_btn:
                presenter.goToFl(tmxhText.getText().toString(),getIntent().getStringExtra("lldh"));
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(String message) {
        msgDialog.setMessage(message);
        msgDialog.show();
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
                    PreferenUtil preferenUtil=new PreferenUtil(LlddrMsgActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }

    @Override
    public void setTmxhText(String tmxh) {
        tmxhText.setText(tmxh);
    }

    @Override
    public void setProgressDialogEnable(String title,boolean enable) {
        if (enable){
            progressDialog.setTitle(title);
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
