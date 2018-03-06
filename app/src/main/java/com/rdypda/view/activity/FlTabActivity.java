package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.view.viewinterface.IFlTabView;
import com.rdypda.view.widget.PowerButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlTabActivity extends BaseActivity implements IFlTabView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_top)
    FrameLayout layoutTop;
    @BindView(R.id.layout_bottom)
    FrameLayout layoutBottom;

    private String djbh,wldm;
    private LocalActivityManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fl_tab);
        ButterKnife.bind(this);
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void initView() {
        djbh=getIntent().getStringExtra("djbh");
        wldm=getIntent().getStringExtra("wldm");

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("生产单号："+djbh);
        List<View> views=new ArrayList<>();
        Intent intent_2=new Intent(this,FlActivity.class);
        intent_2.putExtra("djbh",djbh);
        intent_2.putExtra("wldm",wldm);
        View viewFl=getView("1",intent_2);
        Intent intent_1=new Intent(this,WldActivity.class);
        intent_1.putExtra("djbh",djbh);
        intent_1.putExtra("wldm",wldm);
        intent_1.putExtra("startType",WldActivity.START_TYPE_FLTAB);
        View viewWld=getView("0",intent_1);
        viewFl.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        viewWld.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layoutTop.addView(viewWld);
        layoutBottom.addView(viewFl);


    }

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fltab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                showAddDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroyActivity("0",true);
        manager.destroyActivity("1",true);
    }

    public void showAddDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_tm,null);
            final AlertDialog deleteDialog=new AlertDialog.Builder(this).setView(view).create();
            deleteDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            PowerButton delBtn=(PowerButton)view.findViewById(R.id.sure_btn);
            PowerButton cancelBtn=(PowerButton) view.findViewById(R.id.cancel_btn);
            final TextInputEditText tmEd=(TextInputEditText)view.findViewById(R.id.tm_ed);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmxh=tmEd.getText().toString();
                    if (tmxh.equals("")){
                        Toast.makeText(FlTabActivity.this,"条码序号不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent=new Intent();
                        intent.setAction("com.rdypda.TMXH");
                        intent.putExtra("tmxh",tmxh);
                        sendBroadcast(intent);
                        deleteDialog.dismiss();
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.show();
        }
    }

}