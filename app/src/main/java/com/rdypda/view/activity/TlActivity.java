package com.rdypda.view.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Spinner;

import com.rdypda.R;
import com.rdypda.presenter.TlPresenter;
import com.rdypda.view.viewinterface.ITlView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TlActivity extends BaseActivity implements ITlView {
    private TlPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dd_sp)
    Spinner ddSpinner;
    @BindView(R.id.jt_sp)
    Spinner jySpinner;
    @BindView(R.id.gd_list)
    RecyclerView gdRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tl);
        ButterKnife.bind(this);
        initView();
        presenter=new TlPresenter(this,this);
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
