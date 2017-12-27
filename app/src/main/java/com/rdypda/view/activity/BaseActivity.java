package com.rdypda.view.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rdypda.R;

public abstract class BaseActivity extends AppCompatActivity {
    protected Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        anim= AnimationUtils.loadAnimation(this, R.anim.btn_apha);
    }


    protected abstract void initView();
}
