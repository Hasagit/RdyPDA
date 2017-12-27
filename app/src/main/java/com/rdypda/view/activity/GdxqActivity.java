package com.rdypda.view.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.rdypda.R;
import com.rdypda.adapter.GdxqAdapter;
import com.rdypda.presenter.GdxqPresenter;
import com.rdypda.view.viewinterface.IGdxqView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GdxqActivity extends BaseActivity implements IGdxqView{
    private GdxqAdapter adapter;
    private GdxqPresenter presenter;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.gdxq_list)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdxq);
        ButterKnife.bind(this);
        initView();
        presenter=new GdxqPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
    public void refreshRecyclerView(List<Map<String, String>> data) {
        adapter=new GdxqAdapter(GdxqActivity.this,R.layout.gdxq_item,data);
        recyclerView.setLayoutManager(new GridLayoutManager(GdxqActivity.this,1));
        recyclerView.setAdapter(adapter);
    }
}
