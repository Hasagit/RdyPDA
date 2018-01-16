package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
    private WldAdapter adapter;
    private WldPresenter presenter;
    private String wldm,djbh;
    private ProgressDialog progressDialog;
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
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("加载中...");

        wldm=getIntent().getStringExtra("wldm");
        djbh=getIntent().getStringExtra("djbh");
    }

    @Override
    public void refreshWldRecycler(List<Map<String, String>> data) {
        adapter=new WldAdapter(WldActivity.this,R.layout.wld_item,data);
        recyclerView.setLayoutManager(new GridLayoutManager(WldActivity.this,1));
        recyclerView.setAdapter(adapter);
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
}
