package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.rdypda.R;
import com.rdypda.adapter.FragmentViewPagerAdapter;
import com.rdypda.presenter.TmcxPresenter;
import com.rdypda.view.Fragment.KcswFragment;
import com.rdypda.view.Fragment.TmxxFragment;
import com.rdypda.view.viewinterface.ITmcxView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TmcxActivity extends BaseActivity implements ITmcxView {
    private TmcxPresenter presenter;
    private TmxxFragment tmxxFragment;
    private KcswFragment kcswFragment;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private FragmentViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.edit_tmxh)
    EditText tmxhEd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmcx);
        ButterKnife.bind(this);
        initView();
        presenter=new TmcxPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //库存事务
        kcswFragment=new KcswFragment();
        //条码信息
        tmxxFragment=new TmxxFragment();
        viewPagerAdapter=new FragmentViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(tmxxFragment,"条码信息");
        viewPagerAdapter.addFragment(kcswFragment,"库存事务");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("请稍后");
    }

    @OnClick({R.id.btn_query})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_query:
                presenter.barcodeQuery(tmxhEd.getText().toString());
                break;
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
    public void setShowMsgDialogEnable(String msg) {
        dialog.setMessage(msg);
        dialog.show();
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
    public void setTmxxMsg(Map<String, String> map) {
        tmxxFragment.setTmxx(map);
    }

    @Override
    public void refreshKcsw(List<Map<String, String>> data) {
        kcswFragment.refreshKcsw(data);
    }

    @Override
    protected void onDestroy() {
        presenter.closeScan();
        super.onDestroy();
    }
}
