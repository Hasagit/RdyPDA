package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.WldAdapter;
import com.rdypda.presenter.WldPresenter;
import com.rdypda.view.viewinterface.IWldView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WldActivity extends BaseActivity implements IWldView{
    @BindView(R.id.wld_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    private WldAdapter adapter;
    private WldPresenter presenter;
    private String wldm,djbh;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver;

    public static int START_TYPE_FLTAB=0,START_TYPE_LLD=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wld);
        ButterKnife.bind(this);
        initView();
        presenter=new WldPresenter(this,this);
        presenter.getLldDet(djbh,wldm);
    }

    @Override
    protected void initView() {
        if (getIntent().getIntExtra("startType",1)==START_TYPE_LLD){
            setSupportActionBar(toolbar);
            ActionBar actionBar=getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            title.setText("选择物料号开始打印发料条码");
        }else {
            title.setText("选择物料号开始发料");
            toolbar.setVisibility(View.GONE);
        }
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("加载中...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        wldm=getIntent().getStringExtra("wldm");
        djbh=getIntent().getStringExtra("djbh");

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenter.getLldDet(djbh,wldm);
            }
        };


        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.rdypda.UPDATEWLD");
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public void refreshWldRecycler(List<Map<String, String>> data) {
        adapter=new WldAdapter(WldActivity.this,R.layout.item_wld,data);
        adapter.setLldh(djbh);
        recyclerView.setLayoutManager(new GridLayoutManager(WldActivity.this,1));
        recyclerView.setAdapter(adapter);
        if (!(getIntent().getIntExtra("startType",1)==START_TYPE_LLD)){
            adapter.setOnClickEnable(false);
        }
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(WldActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgressDialogEnable(boolean enable) {
        if (enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
