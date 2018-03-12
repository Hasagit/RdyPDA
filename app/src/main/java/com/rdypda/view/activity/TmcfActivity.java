package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.TmcfPresenter;
import com.rdypda.view.viewinterface.ITmcfView;
import com.rdypda.view.widget.PowerButton;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TmcfActivity extends BaseActivity implements ITmcfView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cftm)
    TextView cftmText;
    @BindView(R.id.ytms)
    TextView ytmsText;
    @BindView(R.id.cftms)
    EditText cftmsText;
    @BindView(R.id.xtmxh)
    TextView xtmxhText;
    @BindView(R.id.cfmx)
    TextView cfmxText;

    private TmcfPresenter presenter;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmcf);
        ButterKnife.bind(this);
        initView();
        presenter=new TmcfPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("条码拆分");
        actionBar.setDisplayHomeAsUpEnabled(true);



        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @OnClick({R.id.print_btn,R.id.get_tm_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.print_btn:
                presenter.printEven();
                break;
            case R.id.get_tm_btn:
                if (!xtmxhText.getText().equals("")){
                    setShowMsgDialogEnable("已经获取新的条码序号",true);
                    break;
                }
                presenter.getTmxh(cftmText.getText().toString(),ytmsText.getText().toString(),
                        cftmsText.getText().toString());
                break;

        }
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
                    PreferenUtil preferenUtil=new PreferenUtil(TmcfActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }


    @Override
    public void setShowMsgDialogEnable(String msg, boolean enable) {
        if (enable){
            dialog.setMessage(msg);
            dialog.show();
        }else {
            dialog.dismiss();
        }
    }

    @Override
    public void setShowProgressDialogEnable(boolean enable) {
        if (enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void setOldCodeMsg(String ytms, String cftm) {
        ytmsText.setText(ytms);
        cftmText.setText(cftm);
    }

    @Override
    public void setNewCodeMsg(String cfmx, String xtmxh) {
        cfmxText.setText(cfmx);
        xtmxhText.setText(xtmxh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.setting:
                showBlueToothAddressDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tmcf_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScan();
    }


}
