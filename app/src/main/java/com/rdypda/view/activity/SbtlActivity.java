package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.SbtlScanAdapter;
import com.rdypda.adapter.SbtlZsAdapter;
import com.rdypda.presenter.SbtlPresenter;
import com.rdypda.view.viewinterface.ISbtlView;
import com.rdypda.view.widget.PowerButton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SbtlActivity extends BaseActivity implements ISbtlView {
    private SbtlPresenter presenter;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private AlertDialog scanDialog;
    @BindView(R.id.tl_list)
    RecyclerView zsList;
    @BindView(R.id.scaned_list)
    RecyclerView scanedList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sbbh_ed)
    EditText sbbhEd;
    @BindView(R.id.wltm_ed)
    EditText wltmEd;
    @BindView(R.id.sb_radio)
    RadioButton sbRadio;
    @BindView(R.id.tm_radio)
    RadioButton tmRadio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbtl);
        ButterKnife.bind(this);
        initView();
        presenter=new SbtlPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请稍后...");
        dialog=new AlertDialog.Builder(this).setTitle("提示")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        scanDialog=new AlertDialog.Builder(this).setTitle("扫描")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        presenter.closeScanUtil();
                    }
                }).create();

        sbRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    presenter.setType(presenter.SCAN_TYPE_SB);
                }else {
                    presenter.setType(presenter.SCAN_TYPE_TM);
                }
            }
        });
    }

    @OnClick({R.id.tm_sure_btn,R.id.sb_sure_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tm_sure_btn:
                presenter.isValidCode(wltmEd.getText().toString());
                break;
            case R.id.sb_sure_btn:
                presenter.isValidDevice(sbbhEd.getText().toString());
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
    public void setShowProgressDialogEnable(boolean enable) {
        if (enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMsgDialog(String msg) {
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    public void setShowScanDialogEnable(boolean enable,String type) {
        if (enable){
            scanDialog .setMessage("请点击扫描键开始扫描"+type);
            scanDialog.show();
        }else {
            scanDialog.dismiss();
            presenter.closeScanUtil();
        }
    }

    @Override
    public void setSbbText(String sbbh) {
        sbbhEd.setText(sbbh);
    }

    @Override
    public void setWltmText(String wltm) {
        wltmEd.setText(wltm);
    }

    @Override
    public void showScanDialog(final String tmbh, String ylbh, String ylgg, final String tmsl, String trzs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_sbtl,null);
            final AlertDialog msgDilaog=new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(false)
                    .create();
            msgDilaog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            TextView tmbhText=(TextView)view.findViewById(R.id.tmbh);
            TextView ylbhText=(TextView)view.findViewById(R.id.ylbh);
            TextView ylggText=(TextView)view.findViewById(R.id.ylgg);
            TextView tmslText=(TextView)view.findViewById(R.id.tmsl);
            TextView trzsText=(TextView)view.findViewById(R.id.trzs);
            final EditText bzslEd=(EditText)view.findViewById(R.id.bzsl);
            PowerButton jlBtn=(PowerButton)view.findViewById(R.id.jl__btn);
            PowerButton cancelBtn=(PowerButton)view.findViewById(R.id.cancel_btn);
            tmbhText.setText(tmbh);
            ylbhText.setText(ylbh);
            ylggText.setText(ylgg);
            tmslText.setText(tmsl);
            trzsText.setText(trzs);
            jlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.tlSure(tmbh,bzslEd.getText().toString(),tmsl);
                    msgDilaog.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgDilaog.dismiss();
                }
            });

            msgDilaog.show();
        }
    }

    @Override
    public void refreshScanList(List<Map<String, String>> data) {
        SbtlScanAdapter adapter=new SbtlScanAdapter(SbtlActivity.this,R.layout.item_sbtl_scan,data);
        scanedList.setAdapter(adapter);
        scanedList.setLayoutManager(new GridLayoutManager(SbtlActivity.this,1));
    }

    @Override
    public void refreshZsList(List<Map<String, String>> data) {
        SbtlZsAdapter adapter=new SbtlZsAdapter(SbtlActivity.this,R.layout.item_sbtl_zs,data);
        zsList.setAdapter(adapter);
        zsList.setLayoutManager(new GridLayoutManager(SbtlActivity.this,1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScanUtil();
    }
}