package com.rdypda.view.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rdypda.R;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.view.viewinterface.IMainView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;

public class MainActivity extends BaseActivity implements IMainView{
    private MainPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.llddr)
    FrameLayout llddrBtn;
    @BindView(R.id.yljs)
    FrameLayout yljsBtn;
    @BindView(R.id.toul)
    FrameLayout toulBtn;
    @BindView(R.id.tuil)
    FrameLayout tuilBtn;
    @BindView(R.id.ck)
    FrameLayout ckBtn;
    @BindView(R.id.rk)
    FrameLayout rkBtn;
    @BindView(R.id.switch_layout)
    LinearLayout switchLayout;
    @BindView(R.id.exit_layout)
    LinearLayout exitLayout;
    @BindView(R.id.banner)
    BGABanner banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        presenter=new MainPresenter(this,this);
    }

    @Override
    protected void initView(){



        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.home);



        List<View> views = new ArrayList<>();
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_2));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_3));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_4));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_1));
        banner.setData(views);

    }


    @OnClick({R.id.llddr,R.id.yljs,R.id.toul,R.id.tuil,R.id.rk,R.id.ck,R.id.switch_layout,R.id.exit_layout})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llddr:
                presenter.goToLlddr();
                break;
            case R.id.yljs:
                presenter.goToYljs();
                break;
            case R.id.toul:
                presenter.goToGdxq();
                break;
            case R.id.tuil:
                presenter.goToGdxq();
                break;
            case R.id.rk:
                presenter.goToYljs();
                break;
            case R.id.ck:
                presenter.goToYljs();
                break;
            case R.id.switch_layout:
                presenter.goToLogin();
                break;
            case R.id.exit_layout:
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
