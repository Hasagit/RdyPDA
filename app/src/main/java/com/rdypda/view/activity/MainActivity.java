package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.MainAdapter;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.view.viewinterface.IMainView;
import com.rdypda.view.widget.MyExpandableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;

public class MainActivity extends BaseActivity implements IMainView{

    private MainPresenter presenter;
    private String arrayStr;
    private AlertDialog dialog,downloadDialog;
    private ProgressDialog progressDialog;
    private ProgressDialog downloadProgressDialog;

    //侧滑菜单
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

    @BindView(R.id.user_name)
    TextView userNameText;
    @BindView(R.id.expanded_menu)
    MyExpandableListView expandableListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        presenter=new MainPresenter(this,this);
        presenter.initPermissionList(arrayStr);
    }

    @Override
    protected void initView(){
        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true)
                .create();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("正在检查更新...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.home);

        arrayStr=getIntent().getStringExtra("permissionList");


        List<View> views = new ArrayList<>();
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_2));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_3));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_4));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.banner_1));
        banner.setData(views);

        downloadProgressDialog=new ProgressDialog(this);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialog.setCancelable(false);
        downloadProgressDialog.setCanceledOnTouchOutside(false);
        downloadProgressDialog.setTitle("下载中");
        downloadProgressDialog.setMax(100);
    }


   @OnClick({R.id.switch_layout,R.id.exit_layout})
    public void onClick(View view) {
        switch (view.getId()){
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
            case R.id.about:
                presenter.checkToUpdate(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserName(String userName) {
        userNameText.setText(userName);
    }

    @Override
    public void showMsgDialog(String msg) {
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
    public void showDownloadDialog(final String url) {
        downloadDialog=new AlertDialog.Builder(this).setTitle("提示").setMessage("发现新的版本，是否现在下载更新").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.downloadInstallApk(url);
                //Toast.makeText(MainActivity.this,"已创建下载任务",Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        downloadDialog.show();
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setShowDownloadProgressDialogEnable(boolean enable) {
        if (enable){
            downloadProgressDialog.show();
        }else {
            downloadProgressDialog.dismiss();
        }
    }

    @Override
    public void setProgressDownloadProgressDialog(int size) {
        downloadProgressDialog.setProgress(size);
    }

    @Override
    public void refreshExpandableListVie(List<String> groupTitles, List<List<String>> titles, List<List<Integer>> imgs,ExpandableListView.OnChildClickListener onChildClickListener) {
        MainAdapter adapter=new MainAdapter(MainActivity.this,R.layout.item_main_title,
                R.layout.item_main_group_title,groupTitles,titles,imgs);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(onChildClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeTimer();
    }
}
