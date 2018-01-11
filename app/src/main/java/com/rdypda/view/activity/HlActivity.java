package com.rdypda.view.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rdypda.R;
import com.rdypda.presenter.HlPresenter;
import com.rdypda.view.viewinterface.IHlView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HlActivity extends BaseActivity implements IHlView {
    private HlPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.save_btn)
    FloatingActionButton saveBtn;
    @BindView(R.id.print_btn)
    FloatingActionButton printBtn;
    @BindView(R.id.hl_list)
    RecyclerView hlReceiclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hl);
        ButterKnife.bind(this);
        initView();
        presenter=new HlPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
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
}
