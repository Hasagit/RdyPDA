package com.rdypda.view.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Spinner;

import com.rdypda.R;
import com.rdypda.presenter.YlzckPresenter;
import com.rdypda.view.viewinterface.IYljsView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YlzckActivity extends BaseActivity implements IYljsView{
    public static int START_TYPE_YLJS=0,
            START_TYPE_YLTL=1,
            START_TYPE_GDTLDYLZ=2,
            STRAT_TYPE_CPSMCR=3,
            START_TYPE_YKLL=4;
    private String title;
    private YlzckPresenter presenter;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolBar;
    @BindView(R.id.kw_sp)
    Spinner kwSpinner;
    @BindView(R.id.cw_sp)
    Spinner cwSpinner;
    @BindView(R.id.receive_list)
    RecyclerView receiveList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ylzck);
        ButterKnife.bind(this);
        initView();
        presenter=new YlzckPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getIntent().getIntExtra("startType",0)==0){
            title="原料接收";
            actionBar.setTitle(title);
        }else if (getIntent().getIntExtra("startType",0)==1){
            title="原料退料";
            actionBar.setTitle(title);
        }else if (getIntent().getIntExtra("startType",0)==2){
            title="工单退料到原料组";
            actionBar.setTitle(title);
        } else if (getIntent().getIntExtra("startType",0)==3){
            title="产品扫描入库";
            actionBar.setTitle(title);
        }else if (getIntent().getIntExtra("startType",0)==4){
            title="移库领料";
            actionBar.setTitle(title);
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
    public void initKwSpinner(List<Map<String, String>> data) {

    }

    @Override
    public void initCwSpinner(List<Map<String, String>> data) {

    }

    @Override
    public void setShowProgressDialogEnable(Boolean enable) {

    }

    @Override
    public void setErrorMsg(String msg) {

    }
}
