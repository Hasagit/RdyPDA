package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.rdypda.R;
import com.rdypda.presenter.LlddrMsgPresenter;
import com.rdypda.view.viewinterface.ILlddrMsgView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LlddrMsgActivity extends BaseActivity implements ILlddrMsgView{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private AlertDialog msgDialog;
    private LlddrMsgPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llddr_msg);
        ButterKnife.bind(this);
        initView();
        presenter=new LlddrMsgPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.more_msg));
        msgDialog=new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.tip))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msgDialog.dismiss();
                    }
                })
                .create();
    }


    @OnClick({R.id.print_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.print_btn:
                presenter.printEven();
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
    public void showMessage(String message) {
        msgDialog.setMessage(message);
        msgDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.closePrint();
    }
}
