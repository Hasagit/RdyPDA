package com.rdypda.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.rdypda.R;
import com.rdypda.adapter.LlddrAdapter;
import com.rdypda.presenter.LlddrPresenter;
import com.rdypda.view.viewinterface.ILlddrView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LlddrActivity extends BaseActivity implements ILlddrView{
    private LlddrAdapter adapter;
    private List<Map<String,String>>data;
    private int startType;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lld_list)
    RecyclerView lldListView;
    LlddrPresenter presenter;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.edit_query)
    EditText queryEd;
    @BindView(R.id.btn_query)
    ImageView queryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llddr);
        ButterKnife.bind(this);
        initView();
        presenter=new LlddrPresenter(this,this);
    }

    @Override
    protected void initView() {
        startType=getIntent().getIntExtra("type",0);


        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshListData();
            }
        });

        queryEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeedit",s.toString());
                presenter.queryDataByKey(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onedit",s.toString());
                presenter.queryDataByKey(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afteredit",s.toString());
                presenter.queryDataByKey(s.toString());

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

    @Override
    public void showList(List<Map<String, String>> data) {
        adapter=new LlddrAdapter(LlddrActivity.this,R.layout.lllddr_item,data,startType);
        lldListView.setLayoutManager(new GridLayoutManager(LlddrActivity.this,1));
        lldListView.setAdapter(adapter);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void setSwipeVisibility(int visibility) {
        swipe.setVisibility(visibility);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        swipe.setRefreshing(refreshing);
    }
}
