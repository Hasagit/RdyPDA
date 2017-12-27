package com.rdypda.view.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wld);
        ButterKnife.bind(this);
        initView();
        presenter=new WldPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void refreshWldRecycler(List<Map<String, String>> data) {
        adapter=new WldAdapter(WldActivity.this,R.layout.wld_item,data);
        recyclerView.setLayoutManager(new GridLayoutManager(WldActivity.this,1));
        recyclerView.setAdapter(adapter);
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
