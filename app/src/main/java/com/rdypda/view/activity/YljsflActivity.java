package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.WldAdapter;
import com.rdypda.adapter.YljstlAdapter;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.presenter.YljsflPresenter;
import com.rdypda.view.viewinterface.IYljsflView;
import com.rdypda.view.widget.PowerButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YljsflActivity extends BaseActivity implements IYljsflView{
    private YljsflPresenter presenter;
    private AlertDialog dialog;
    private String djbh,wldm;
    private ProgressDialog progressDialog;
    private YljstlAdapter adapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.kcdd_sp)
    Spinner kcddSp;
    @BindView(R.id.wld_recycler)
    RecyclerView wldRecycler;
    @BindView(R.id.jstl_recycler)
    RecyclerView jstlRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yljsfl);
        ButterKnife.bind(this);
        initView();
        presenter=new YljsflPresenter(this,this);
        presenter.getLldDet(djbh,wldm);
        presenter.setStartType(getIntent().getIntExtra("startType",0));
    }

    @Override
    protected void initView() {
        wldm=getIntent().getStringExtra("wldm");
        djbh=getIntent().getStringExtra("djbh");

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (getIntent().getIntExtra("startType",0)== MainPresenter.YLJS){
            actionBar.setTitle("原料接收");
        }else if(getIntent().getIntExtra("startType",0)== MainPresenter.YLTL){
            actionBar.setTitle("原料退料");
        }
        dialog=new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("请稍后");


        adapter=new YljstlAdapter(YljsflActivity.this,R.layout.item_yljstl,new ArrayList<Map<String, String>>());
        jstlRecycler.setLayoutManager(new GridLayoutManager(YljsflActivity.this,1));
        jstlRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new YljstlAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, YljstlAdapter.ViewHolder holder, Map<String, String> map) {
                showDeleteDialog(map.get("wlbh"),map.get("tmsl"),map.get("tmbh"));
            }
        });

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
    public void setShowDialogMsg(String msg) {
        dialog.setMessage(msg);
        dialog.show();
    }


    @Override
    public void refreshKcddSp(final List<String> data, final List<String> dataDm) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(YljsflActivity.this,android.R.layout.simple_spinner_dropdown_item,data);
        kcddSp.setAdapter(adapter);
        kcddSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setKcdd(dataDm.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (data.size()>0){
            presenter.setKcdd(dataDm.get(0));
        }else {
            presenter.setKcdd("");
        }
    }

    @Override
    public void refreshWldRecycler(List<Map<String, String>> data) {
        WldAdapter adapter=new WldAdapter(YljsflActivity.this,R.layout.item_wld,data);
        adapter.setLldh(djbh);
        wldRecycler.setLayoutManager(new GridLayoutManager(YljsflActivity.this,1));
        wldRecycler.setAdapter(adapter);
        adapter.setOnClickEnable(false);
    }

    @Override
    public void addYljstlRecyclerItem(Map<String, String> item) {
        adapter.addData(item);
    }

    @Override
    public void removeYljstlRecyclerItem(String tmxh) {
        adapter.removeData(tmxh);
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
                        Toast.makeText(YljsflActivity.this,"条码序号不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent=new Intent();
                        intent.setAction("com.rdypda.TMXH");
                        intent.putExtra("tmxh",tmxh);
                        sendBroadcast(intent);
                        if (getIntent().getIntExtra("startType",0)==MainPresenter.YLTL){
                            presenter.isValidCode(tmxh,"MTR_OUT",presenter.getKcdd());
                        }else if (getIntent().getIntExtra("startType",0)==MainPresenter.YLJS){
                            presenter.isValidCode(tmxh,"MTR_IN",presenter.getKcdd());
                        }
                        presenter.isValidCode(tmxh,"",presenter.getKcdd());
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

    public void showDeleteDialog(String wldm,String tmsl,String tmxh){
        if (getIntent().getIntExtra("startType",0)==MainPresenter.YLTL){
            //setShowDialogMsg("原料退料不能取消扫描");
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View view= LayoutInflater.from(this).inflate(R.layout.dialog_wld,null);
                final android.app.AlertDialog deleteDialog=new android.app.AlertDialog.Builder(this).setView(view).create();
                deleteDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                TextView wldmText=(TextView) view.findViewById(R.id.wlbh);
                TextView tmslText=(TextView) view.findViewById(R.id.tmsl);
                TextView tmxhText=(TextView) view.findViewById(R.id.tmbh);
                PowerButton delBtn=(PowerButton)view.findViewById(R.id.del_btn);
                PowerButton cancelBtn=(PowerButton) view.findViewById(R.id.cancel_btn);
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //presenter.deleteData(getIntent().getStringExtra("djbh"),getIntent().getStringExtra("wldm"));
                        deleteDialog.dismiss();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });
                wldmText.setText(wldm);
                tmslText.setText(tmsl);
                tmxhText.setText(tmxh);
                deleteDialog.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closeScan();

    }

}
