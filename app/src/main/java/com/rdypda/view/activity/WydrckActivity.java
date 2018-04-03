package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.adapter.WydrckScanAdapter;
import com.rdypda.adapter.WydrckZsAdapter;
import com.rdypda.presenter.WydrckPresenter;
import com.rdypda.view.viewinterface.IWydrckView;
import com.rdypda.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.PUT;

public class WydrckActivity extends BaseActivity implements IWydrckView {
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private WydrckPresenter presenter;
    private WydrckScanAdapter scanAdapter;
    private WydrckZsAdapter zsAdapter;
    private int startType;
    public static int START_TYPE_WYDCK=1;
    public static int START_TYPE_WYDRK=0;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.zs_list)
    RecyclerView zsList;
    @BindView(R.id.scaned_list)
    RecyclerView scanedList;
    @BindView(R.id.jskw)
    Spinner jskwSp;
    @BindView(R.id.tmbh_ed)
    EditText tmbhEd;
    @BindView(R.id.kw_text)
    TextView kwText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wydrck);
        ButterKnife.bind(this);
        initView();
        presenter=new WydrckPresenter(this,this);
        presenter.setStartType(startType);
    }

    @Override
    protected void initView() {
        startType=getIntent().getIntExtra("startType",0);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");
        dialog=new AlertDialog.Builder(this).setTitle("提示")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        refreshScanList(new ArrayList<Map<String, String>>());
        refreshZsList(new ArrayList<Map<String, String>>());
        if (startType==START_TYPE_WYDRK){
            actionBar.setTitle("无源单入库");
            kwText.setText("接收储位：");
        }else {
            actionBar.setTitle("无源单出库");
            kwText.setText("出库库位：");
        }
    }

    @OnClick({R.id.tm_sure_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tm_sure_btn:
                presenter.isValidCode(tmbhEd.getText().toString());
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
    public void refreshJskwSp(List<String> mcData, final List<String> idData) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(WydrckActivity.this,android.R.layout.simple_spinner_dropdown_item,mcData);
        jskwSp.setAdapter(adapter);
        presenter.setFtyIdAndstkId(";");
        jskwSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setFtyIdAndstkId(idData.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void refreshZsList(List<Map<String, String>> data) {
        zsAdapter=new WydrckZsAdapter(WydrckActivity.this,R.layout.item_wydrck_zs,data);
        zsList.setLayoutManager(new GridLayoutManager(WydrckActivity.this,1));
        zsList.setAdapter(zsAdapter);
    }

    @Override
    public void refreshScanList(List<Map<String, String>> data) {
        scanAdapter=new WydrckScanAdapter(WydrckActivity.this,R.layout.item_wydrck_scan,data);
        scanedList.setLayoutManager(new GridLayoutManager(WydrckActivity.this,1));
        scanedList.setAdapter(scanAdapter);
        scanAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Map<String, String> map, BaseRecyclerAdapter.BaseRecyclerViewHolder holder) {
                showDelDialog(map.get("tmbh"),map.get("wlbh"),map.get("sl"));
            }
        });
    }

    @Override
    public void addScanData(Map<String, String> map) {
        scanAdapter.addData(map);
    }

    @Override
    public void addZsData(Map<String, String> map) {
        zsAdapter.addData(map);
    }

    @Override
    public void removeScanData(String tmxh) {
        if (scanAdapter!=null){
            scanAdapter.removeData(tmxh);
            refreshScanList(scanAdapter.getData());
        }
    }

    @Override
    public void removeZsData(String wlbh, String tmsl) {
        if (zsAdapter!=null){
            zsAdapter.removeData(wlbh,tmsl);
            refreshZsList(zsAdapter.getData());
        }
    }

    @Override
    public void setTmEd(String tmbh) {
        tmbhEd.setText(tmbh);
    }

    public void showDelDialog(final String tmxh, final String wlbh, final String tmsl){
        AlertDialog delDialog=new AlertDialog.Builder(WydrckActivity.this).setTitle("提示")
                .setMessage("是否删除条码"+tmxh)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();;
                        presenter.cancelScan(tmxh,wlbh,tmsl);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        delDialog.show();
    }

    @Override
    protected void onDestroy() {
        presenter.closeScanUtil();
        super.onDestroy();
    }
}
