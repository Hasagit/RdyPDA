package com.rdypda.view.activity;

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
    FrameLayout rkBtn;
    @BindView(R.id.switch_layout)
    LinearLayout switchLayout;
    @BindView(R.id.exit_layout)
    LinearLayout exitLayout;
    @BindView(R.id.banner)
    BGABanner banner;

    //功能按钮
    @BindView(R.id.tmdy)
    LinearLayout tmdy_btn;
    @BindView(R.id.fl)
    LinearLayout fl_btn;
    @BindView(R.id.yljs)
    LinearLayout yljs_btn;
    @BindView(R.id.yltl)
    LinearLayout yltl_btn;
    @BindView(R.id.hl)
    LinearLayout hl_btn;
    @BindView(R.id.aldfl)
    LinearLayout aldfl_btn;
    @BindView(R.id.tl)
    LinearLayout tl_btn;
    @BindView(R.id.gdtldylz)
    LinearLayout gdtldylz_btn;
    @BindView(R.id.cpsmrk)
    LinearLayout cpsmrk_btn;
    @BindView(R.id.ykll)
    LinearLayout ykll_btn;
    @BindView(R.id.adfl)
    LinearLayout adfl_btn;
    @BindView(R.id.adtl)
    LinearLayout adtl_btn;
    @BindView(R.id.yktldck)
    LinearLayout yctldck_btn;
    @BindView(R.id.tmcf)
    LinearLayout tmcf_btn;
    @BindView(R.id.tmbd)
    LinearLayout tmbd_btn;
    @BindView(R.id.kcpd)
    LinearLayout kcpd_btn;
    @BindView(R.id.tmcx)
    LinearLayout tmcx_btn;


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


    @OnClick({R.id.switch_layout,R.id.exit_layout,R.id.tmdy,R.id.fl,R.id.yljs,
            R.id.yltl,R.id.hl,R.id.aldfl,R.id.tl,R.id.gdtldylz,R.id.cpsmrk,R.id.ykll,
            R.id.adfl,R.id.adtl,R.id.yktldck,R.id.tmcf,R.id.tmbd,R.id.kcpd,R.id.tmcx})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tmdy:
                presenter.goToLlddr();
                break;
            case R.id.switch_layout:
                presenter.goToLogin();
                break;
            case R.id.exit_layout:
                finish();
                break;
            case R.id.fl:
                presenter.goToYljs();
                break;
            case R.id.yljs:
                break;
            case R.id.yltl:
                break;
            case R.id.hl:
                break;
            case R.id.aldfl:
                break;
            case R.id.tl:
                break;
            case R.id.gdtldylz:
                break;
            case R.id.cpsmrk:
                break;
            case R.id.ykll:
                break;
            case R.id.adfl:
                break;
            case R.id.adtl:
                break;
            case R.id.yktldck:
                break;
            case R.id.tmcf:
                break;
            case R.id.tmbd:
                break;
            case R.id.kcpd:
                break;
            case R.id.tmcx:
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
