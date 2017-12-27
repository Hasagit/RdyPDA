package com.rdypda.view.activity;

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
import com.rdypda.util.ScanUtil;
import com.rdypda.view.viewinterface.IYljsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YljsActivity extends BaseActivity implements IYljsView{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.receive)
    RecyclerView recyclerView;
    @BindView(R.id.save_btn)
    FloatingActionButton saveBtn;
    @BindView(R.id.num)
    TextView num;
    private ReceiveAdapter adapter;
    private ScanUtil scanUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yljs);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        adapter=new ReceiveAdapter(this,R.layout.lllddr_item,new ArrayList<Map<String, String>>());
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(adapter);


        scanUtil=new ScanUtil(this);
        scanUtil.open();
        scanUtil.setOnScanListener(new ScanUtil.OnScanListener() {
            @Override
            public void onSuccess(String result) {
                Map<String,String>map=new HashMap<>();
                map.put("id",result);
                refreshReceive(map);
                num.setText("数量："+adapter.getItemCount());
            }

            @Override
            public void onFail(String error) {

            }
        });
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


    public void refreshReceive(Map<String, String> data) {
        adapter.addData(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanUtil.close();
    }
}
