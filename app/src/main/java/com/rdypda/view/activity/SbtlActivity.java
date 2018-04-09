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
    private int startType;
    public static int START_TYPE_SBTL=0,
            START_TYPE_SYTOUL=1,
            START_TYPE_SYTUIL=2,
            START_TYPE_ZZFL=3,
            START_TYPE_ZZTL=4;
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
    @BindView(R.id.sp_title)
    TextView spTitleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbtl);
        ButterKnife.bind(this);
        initView();
        presenter=new SbtlPresenter(this,this);
        presenter.setStartType(startType);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
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
        //设置设备的扫描类型
        sbRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //设置扫描类型，条码，还是设备
                    presenter.setType(presenter.SCAN_TYPE_SB);
                    sbbhEd.setFocusable(true);
                    sbbhEd.setFocusableInTouchMode(true);
                    sbbhEd.requestFocus();
                }else {
                    presenter.setType(presenter.SCAN_TYPE_TM);
                    wltmEd.setFocusable(true);
                    wltmEd.setFocusableInTouchMode(true);
                    wltmEd.requestFocus();
                }
            }
        });
        startType=getIntent().getIntExtra("start_type",START_TYPE_SBTL);
        switch (startType){
            case 0:
                actionBar.setTitle("设备投料");
                spTitleText.setText("设备编号：");
                sbbhEd.setHint("请输入或扫描设备编号");
                break;
            case 1:
                actionBar.setTitle("丝印投料");
                spTitleText.setText("型号&品名：");
                sbbhEd.setHint("请输入或扫描型号&品名");
                break;
            case 2:
                actionBar.setTitle("丝印退料");
                spTitleText.setText("型号&品名：");
                sbbhEd.setHint("请输入或扫描型号&品名");
                break;
            case 3:
                actionBar.setTitle("组装发料");
                spTitleText.setText("线别：");
                sbbhEd.setHint("请输入或扫描线别");
                break;
            case 4:
                actionBar.setTitle("组装退料");
                spTitleText.setText("线别：");
                sbbhEd.setHint("请输入或扫描线别");
                break;
        }
    }

    /**
     * 点击进行设备，条码验证
     * @param view
     */
    @OnClick({R.id.tm_sure_btn,R.id.sb_sure_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tm_sure_btn:
                presenter.isValidCode(wltmEd.getText().toString());
                break;
            case R.id.sb_sure_btn:
                presenter.isValidDevice(sbbhEd.getText().toString(),startType);
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

    /**
     * 显示扫描结果界面
     * @param tmbh 条码编号
     * @param ylbh 原料编号
     * @param ylgg 原料规格
     * @param tmsl 条码数量
     * @param trzs 加料总数
     */
    @Override
    public void showScanDialog(final String tmbh, String ylbh, String ylgg, final String tmsl, String trzs) {
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
        bzslEd.setText(tmsl);
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
                presenter.cancelScan(tmbh,msgDilaog);
            }
        });

        msgDilaog.show();
    }

    /**
     * 刷新加料数
     * @param data
     */
    @Override
    public void refreshScanList(List<Map<String, String>> data) {
        SbtlScanAdapter adapter=new SbtlScanAdapter(SbtlActivity.this,R.layout.item_sbtl_scan,data);
        scanedList.setAdapter(adapter);
        scanedList.setLayoutManager(new GridLayoutManager(SbtlActivity.this,1));
    }

    /**
     * 刷新原料总数
     * @param data
     */
    @Override
    public void refreshZsList(List<Map<String, String>> data) {
        SbtlZsAdapter adapter=new SbtlZsAdapter(SbtlActivity.this,R.layout.item_sbtl_zs,data);
        zsList.setAdapter(adapter);
        zsList.setLayoutManager(new GridLayoutManager(SbtlActivity.this,1));
    }

    @Override
    public void setSbRadioCheck(boolean check) {
        sbRadio.setChecked(check);
        tmRadio.setChecked(!check);
    }

    /**
     * 显示设备搜索结果
     * @param sbdm 设备代码
     * @param sbmc 设备名称
     */
    @Override
    public void showQueryList(final String[] sbdm, final String[] sbmc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setItems(sbmc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.setSbbh(sbdm[which]);
                setSbbText(sbmc[which]);
                presenter.getScanList(sbdm[which]);
                dialog.dismiss();

            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScanUtil();
    }
}
