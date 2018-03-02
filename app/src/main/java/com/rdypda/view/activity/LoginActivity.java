package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rdypda.R;
import com.rdypda.presenter.LoginPresenter;
import com.rdypda.view.viewinterface.ILoginView;
import com.rdypda.view.widget.PowerButton;

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
    @BindView(R.id.remember)
    CheckBox rememberCheckBox;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.setting_btn)
    PowerButton settingBtn;
    private AlertDialog ipSettingDialog;
    private ProgressDialog progressDialog;
    private LoginPresenter presenter;
    private int factoryPosition=-1;
    private TextView currentIpText;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        rememberCheckBox.setChecked(true);
        rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setRemember(isChecked);
            }
        });
        //userIdEd.setInputType(InputType.TYPE_NULL);
        //userPwdEd.setInputType(InputType.TYPE_NULL|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_ip_setting,null);
            ipSettingDialog=new AlertDialog.Builder(this).setView(view).create();
            ipSettingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            PowerButton sureBtn=view.findViewById(R.id.sure_btn);
            PowerButton cancleBtn=view.findViewById(R.id.cancle_btn);
            final TextInputEditText ipEd=view.findViewById(R.id.ip_ed);
            final TextInputLayout ipLayout=view.findViewById(R.id.ip_layout);
            currentIpText=view.findViewById(R.id.current_ip);
            cancleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ipSettingDialog.dismiss();
                }
            });
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ipEd.getText().toString().equals("")){
                        ipLayout.setErrorEnabled(true);
                        ipLayout.setError("ip地址不能为空");
                    }else {
                        presenter.setIpAddress(ipEd.getText().toString());
                        presenter=new LoginPresenter(LoginActivity.this,LoginActivity.this);
                        ipSettingDialog.dismiss();
                    }
                }
            });
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

    @OnClick({R.id.login_btn,R.id.setting_btn})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                presenter.login(factoryPosition,userIdEd.getText().toString(),userPwdEd.getText().toString());
                break;
            case R.id.setting_btn:
                currentIpText.setText("当前服务器地址："+presenter.getCurrentIp());
                ipSettingDialog.show();
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

    @Override
    public void setDefaultUser(String userId, String userPwd) {
        userIdEd.setText(userId);
        userPwdEd.setText(userPwd);
    }
}
