package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.adapter.ReceiveAdapter;
import com.rdypda.presenter.FlPresenter;
import com.rdypda.view.viewinterface.IFlView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fl);
        ButterKnife.bind(this);
        initView();
        presenter=new FlPresenter(this,this);
        presenter.setWldm(getIntent().getStringExtra("wldm"));
        presenter.setLldh(getIntent().getStringExtra("djbh"));
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("验证中...");
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScan();
    }

}
