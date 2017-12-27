package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.rdypda.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private CardView loginBtn;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void initView(){
        loginBtn=(CardView)findViewById(R.id.login_btn);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.logining));


        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                loginBtn.startAnimation(anim);
                progressDialog.show();
                Intent intent_main=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent_main);
                progressDialog.dismiss();
                finish();
                break;
        }
    }
}
