package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rdypda.R;
import com.rdypda.adapter.WldAdapter;
import com.rdypda.adapter.YljstlAdapter;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.presenter.YljsflPresenter;
import com.rdypda.view.viewinterface.IYljsflView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YljsflActivity extends BaseActivity implements IYljsflView{
    private YljsflPresenter presenter;
    private AlertDialog dialog;
    private String djbh,wldm;
    private ProgressDialog progressDialog;
    private YljstlAdapter adapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.kcdd_sp)
    Spinner kcddSp;
    @BindView(R.id.wld_recycler)
    RecyclerView wldRecycler;
    @BindView(R.id.jstl_recycler)
    RecyclerView jstlRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yljsfl);
        ButterKnife.bind(this);
        initView();
        presenter=new YljsflPresenter(this,this);
        presenter.getLldDet(djbh,wldm);
        presenter.setStartType(getIntent().getIntExtra("startType",0));
    }

    @Override
    protected void initView() {
        wldm=getIntent().getStringExtra("wldm");
        djbh=getIntent().getStringExtra("djbh");

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getIntent().getIntExtra("startType",0)== MainPresenter.YLJS){
            actionBar.setTitle("原料接收");
        }else if(getIntent().getIntExtra("startType",0)== MainPresenter.YLTL){
            actionBar.setTitle("原料退料");
        }
        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("请稍后");


        adapter=new YljstlAdapter(YljsflActivity.this,R.layout.yljstl_item,new ArrayList<Map<String, String>>());
        jstlRecycler.setLayoutManager(new GridLayoutManager(YljsflActivity.this,1));
        jstlRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new YljstlAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, YljstlAdapter.ViewHolder holder, Map<String, String> map) {

            }
        });

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
    public void setShowDialogMsg(String msg) {
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    public void refreshKcddSp(final List<String> data) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(YljsflActivity.this,android.R.layout.simple_spinner_dropdown_item,data);
        kcddSp.setAdapter(adapter);
        kcddSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setKcdd(data.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (data.size()>0){
            presenter.setKcdd(data.get(0));
        }else {
            presenter.setKcdd(null);
        }
    }

    @Override
    public void refreshWldRecycler(List<Map<String, String>> data) {
        WldAdapter adapter=new WldAdapter(YljsflActivity.this,R.layout.wld_item,data);
        adapter.setLldh(djbh);
        wldRecycler.setLayoutManager(new GridLayoutManager(YljsflActivity.this,1));
        wldRecycler.setAdapter(adapter);
        adapter.setOnClickEnable(false);
    }

    @Override
    public void addYljstlRecyclerItem(Map<String, String> item) {
        adapter.addData(item);
    }

    @Override
    public void removeYljstlRecyclerItem(Map<String, String> item) {
        adapter.removeData(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScan();

    }

}
