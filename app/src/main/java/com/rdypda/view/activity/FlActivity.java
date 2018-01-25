package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.ReceiveAdapter;
import com.rdypda.presenter.FlPresenter;
import com.rdypda.presenter.LoginPresenter;
import com.rdypda.view.viewinterface.IFlView;
import com.rdypda.view.widget.PowerButton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlActivity extends BaseActivity implements IFlView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.receive)
    RecyclerView recyclerView;
    @BindView(R.id.save_btn)
    FloatingActionButton saveBtn;
    @BindView(R.id.num)
    TextView num;
    private ReceiveAdapter adapter;
    private FlPresenter presenter;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fl);
        ButterKnife.bind(this);
        initView();
        presenter=new FlPresenter(this,this);
        presenter.setWldm(getIntent().getStringExtra("wldm"));
        presenter.setLldh(getIntent().getStringExtra("djbh"));
        presenter.getScanedData(getIntent().getStringExtra("djbh"),getIntent().getStringExtra("wldm"));
    }

    @Override
    protected void initView() {
        //setSupportActionBar(toolbar);
        //ActionBar actionBar=getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setVisibility(View.GONE);
        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("加载中...");

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tmxh=intent.getStringExtra("tmxh");
                String qrCode="**N"+tmxh;
                presenter.isValidCode(qrCode);
            }
        };
        IntentFilter intentFilter=new IntentFilter("com.rdypda.TMXH");
        registerReceiver(receiver,intentFilter);

    }

    @OnClick({R.id.save_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.save_btn:
                if (adapter!=null){
                    presenter.uploadScanWld(adapter.getData());
                }else {
                    Toast.makeText(FlActivity.this,"扫描列表未初始化，请重新进入",Toast.LENGTH_SHORT).show();
                }
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
    public void refreshReceive(List<Map<String, String>> data) {
        adapter=new ReceiveAdapter(this,R.layout.fl_wl_item,data);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ReceiveAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ReceiveAdapter.ReceiveViewHolder receiveViewHolder, int position, Map<String, String> map) {
                showDeleteDialog(map.get("wldm"),map.get("tmsl"),map.get("tmxh"));
            }
        });
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
    public void setShowProgressEnable(boolean enable) {
        if (enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void addReceiveData(Map<String, String> item) {
        if (adapter==null){
            Toast.makeText(FlActivity.this,"扫描列表未初始化，请重新进入",Toast.LENGTH_SHORT).show();
        }else {
            adapter.addData(item);
        }
    }

    @Override
    public void removeReceiveData(Map<String, String> item) {
        if (adapter!=null){
            adapter.deleteData(item);
        }else {
            Toast.makeText(FlActivity.this,"扫描列表未初始化，请重新进入",Toast.LENGTH_SHORT).show();
        }
    }

    public void showDeleteDialog(String wldm,String tmsl,String tmxh){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.wld_dialog,null);
            final AlertDialog deleteDialog=new AlertDialog.Builder(this).setView(view).create();
            deleteDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            TextView wldmText=(TextView) view.findViewById(R.id.wlbh);
            TextView tmslText=(TextView) view.findViewById(R.id.tmsl);
            TextView tmxhText=(TextView) view.findViewById(R.id.tmbh);
            PowerButton delBtn=(PowerButton)view.findViewById(R.id.del_btn);
            PowerButton cancelBtn=(PowerButton) view.findViewById(R.id.cancel_btn);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.deleteData(getIntent().getStringExtra("djbh"),getIntent().getStringExtra("wldm"));
                    deleteDialog.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            wldmText.setText(wldm);
            tmslText.setText(tmsl);
            tmxhText.setText(tmxh);
            deleteDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScan();
        unregisterReceiver(receiver);
    }

}
