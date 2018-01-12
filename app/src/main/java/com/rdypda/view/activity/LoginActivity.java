package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.presenter.LoginPresenter;
import com.rdypda.view.viewinterface.ILoginView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements ILoginView {
    @BindView(R.id.login_btn)
    CardView loginBtn;
    @BindView(R.id.factory_list)
    Spinner factoryList;
    @BindView(R.id.user_id_layout)
    TextInputLayout userIdLayout;
    @BindView(R.id.user_id_ed)
    TextInputEditText userIdEd;
    @BindView(R.id.user_pwd_layout)
    TextInputLayout userPwdLayout;
    @BindView(R.id.user_pwd_ed)
    TextInputEditText userPwdEd;
    private ProgressDialog progressDialog;
    private LoginPresenter presenter;
    private int factoryPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        presenter=new LoginPresenter(this,this);
    }

    @Override
    protected void initView(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.logining));


    }

    @OnClick({R.id.login_btn})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                presenter.login(factoryPosition,userIdEd.getText().toString(),userPwdEd.getText().toString());
                break;
        }
    }

    @Override
    public void showFactoryList(List<Map<String,String>> data) {
        List<String>spinnerData=new ArrayList<>();
        for (int i=0;i<data.size();i++){
            spinnerData.add(data.get(i).get("cmp_gsmc"));
        }
        if (data.size()>0){
            factoryPosition=0;
        }
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(LoginActivity.this,android.R.layout.simple_spinner_dropdown_item,spinnerData);
        factoryList.setAdapter(adapter);
        factoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                factoryPosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_SHORT).show();
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
    public void setUserIdErrorEnable(boolean enable) {
        userIdLayout.setErrorEnabled(enable);
    }

    @Override
    public void setUserIdError(String error) {
        userIdLayout.setError(error);
    }

    @Override
    public void setPwdErrorEnable(boolean enable) {
        userPwdLayout.setErrorEnabled(enable);
    }

    @Override
    public void setPwdError(String error) {
        userPwdLayout.setError(error);
    }
}
