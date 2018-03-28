package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.HlAdapter;
import com.rdypda.adapter.HlScanedAdapter;
import com.rdypda.presenter.HlPresenter;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.view.viewinterface.IHlView;
import com.rdypda.view.widget.PowerButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HlActivity extends BaseActivity implements IHlView {
    private HlPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sbmx)
    Spinner sbmxSp;
    @BindView(R.id.hl_1_list)
    RecyclerView hlList;
    @BindView(R.id.hl_2_list)
    RecyclerView scanedList;
    @BindView(R.id.save_btn)
    FloatingActionButton saveBtn;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private List<String>idData;

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

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("请稍后...");

        dialog=new AlertDialog.Builder(this)
                .setTitle("提示")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.uploadHl();
            }
        });
    }

    @Override
    public void showTmMsgDialog(String hljh, final String tmxh, String wlbh, String wlgg, final String tmsl){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_hl,null);
            final AlertDialog msgDilaog=new AlertDialog.Builder(this)
                    .setView(view)
                    .setCancelable(false)
                    .create();
            msgDilaog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            TextView hljhText=(TextView)view.findViewById(R.id.hljh);
            TextView tmxhText=(TextView)view.findViewById(R.id.tmbh);
            TextView wlbhText=(TextView)view.findViewById(R.id.wlbh);
            TextView wlggText=(TextView)view.findViewById(R.id.wlgg);
            TextView tmslText=(TextView)view.findViewById(R.id.tmsl);
            final EditText tlslText=(EditText) view.findViewById(R.id.tlsl);
            PowerButton sureBtn=(PowerButton)view.findViewById(R.id.sure_btn);
            PowerButton cancelBtn=(PowerButton)view.findViewById(R.id.cancel_btn);
            hljhText.setText(hljh);
            tmxhText.setText(tmxh);
            wlbhText.setText(wlbh);
            wlggText.setText(wlgg);
            tmslText.setText(tmsl);
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.uploadQty(tmxh,tlslText.getText().toString(),tmsl);
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
    public void refreshScanedList(List<Map<String, String>> data) {
        HlScanedAdapter adapter=new HlScanedAdapter(HlActivity.this,R.layout.item_hl_scaned,data);
        scanedList.setLayoutManager(new GridLayoutManager(HlActivity.this,1));
        scanedList.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new HlScanedAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, final Map<String, String> map) {
                AlertDialog dialog=new AlertDialog.Builder(HlActivity.this)
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.delScanedData(map.get("lab_1"));
                            }
                        })
                        .setNeutralButton("全部删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.delScanedData("");
                            }
                        })
                        .setTitle("提示")
                        .setMessage("确定删除“"+map.get("lab_1")+"”此记录？")
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public void refreshHlList(List<Map<String, String>> data) {
        HlAdapter adapter=new HlAdapter(HlActivity.this,R.layout.item_hl_scaned,data);
        hlList.setAdapter(adapter);
        hlList.setLayoutManager(new GridLayoutManager(HlActivity.this,1));
    }

    @Override
    public void setSbmcSelect(String sbbh) {
        if (idData!=null){
            for (int i=0;i<idData.size();i++){
                if (idData.get(i).equals(sbbh)){
                    sbmxSp.setSelection(i,true);
                }
            }
        }

    }

    @Override
    public void setSbmcEnable(boolean enable) {
        sbmxSp.setEnabled(enable);
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
    public void refreshSbmx(final List<String> idData, final List<String>mcData) {
        this.idData=idData;
        presenter.setHljh("");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(HlActivity.this,android.R.layout.simple_spinner_dropdown_item,mcData);
        sbmxSp.setAdapter(adapter);
        sbmxSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setHljh(idData.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                if (scanedList.getChildCount()>0){
                    showReturnDialog();
                }else {
                    finish();
                }
                break;
            case R.id.add:
                showAddDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAddDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_tm,null);
            final android.support.v7.app.AlertDialog deleteDialog=new android.support.v7.app.AlertDialog.Builder(this).setView(view).create();
            deleteDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            PowerButton delBtn=(PowerButton)view.findViewById(R.id.sure_btn);
            PowerButton cancelBtn=(PowerButton) view.findViewById(R.id.cancel_btn);
            final TextInputEditText tmEd=(TextInputEditText)view.findViewById(R.id.tm_ed);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmxh=tmEd.getText().toString();
                    if (tmxh.equals("")){
                        Toast.makeText(HlActivity.this,"条码序号不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        presenter.isValidCode(tmxh);
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

    public void showReturnDialog(){
        AlertDialog returnDialog=new AlertDialog.Builder(HlActivity.this).setTitle("提示")
                .setCancelable(false)
                .setMessage("是否清除扫描记录？")
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.cancelScaned();
                    }
                })
                .create();
        returnDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (scanedList.getChildCount()>0){
            showReturnDialog();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScanUtil();
    }
}
