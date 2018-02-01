package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.adapter.LlddrAdapter;
import com.rdypda.presenter.LlddrPresenter;
import com.rdypda.presenter.MainPresenter;
import com.rdypda.view.viewinterface.ILlddrView;
import com.rdypda.view.widget.PowerButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LlddrActivity extends BaseActivity implements ILlddrView{
    private LlddrAdapter adapter;
    private List<Map<String,String>>data;
    private int startType;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lld_list)
    RecyclerView lldListView;
    LlddrPresenter presenter;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.edit_lldh)
    EditText lldhEd;
    @BindView(R.id.edit_wldm)
    EditText wldmEd;
    @BindView(R.id.edit_ddbh)
    EditText ddbhEd;
    @BindView(R.id.btn_query)
    PowerButton queryBtn;
    @BindView(R.id.finish_box)
    CheckBox finishBox;
    @BindView(R.id.unfinish_box)
    CheckBox unFinishBox;
    @BindView(R.id.title)
    TextView title;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llddr);
        ButterKnife.bind(this);
        initView();
        presenter=new LlddrPresenter(this,this);
    }

    @OnClick({R.id.btn_query})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_query:
                showList(new ArrayList<Map<String, String>>());
                presenter.queryDataByKey(lldhEd.getText().toString(),wldmEd.getText().toString(),ddbhEd.getText().toString());
                break;
        }
    }

    @Override
    protected void initView() {
        startType=getIntent().getIntExtra("type",0);
        if (startType== MainPresenter.TMDY){
            title.setText("选择生产单号查看明细清单");
        }else if (startType==MainPresenter.FL){
            title.setText("选择生产单号扫描发料");
        }else if (startType==MainPresenter.YLJS){
            title.setText("选择生产单号扫描接收");
        }else if (startType==MainPresenter.YLTL){
            title.setText("选择生产单号扫描退料");
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("查询中...");

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
    public void showList(List<Map<String, String>> data) {
        adapter=new LlddrAdapter(LlddrActivity.this,R.layout.lllddr_item,data,startType);
        lldListView.setLayoutManager(new GridLayoutManager(LlddrActivity.this,1));
        lldListView.setAdapter(adapter);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(LlddrActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgressDialogEnable(boolean enable) {
        if (enable){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean isFinishCheck() {
        return finishBox.isChecked();
    }

    @Override
    public boolean isUnFinishCheck() {
        return unFinishBox.isChecked();
    }
}
