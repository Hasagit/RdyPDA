package com.rdypda.view.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.rdypda.R;
import com.rdypda.adapter.FragmentViewPagerAdapter;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.PddyPresenter;
import com.rdypda.view.Fragment.PddyFragmentDL;
import com.rdypda.view.Fragment.PddyFragmentHL;
import com.rdypda.view.viewinterface.IPddyView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PddyActivity extends BaseActivity implements IPddyView {
    @BindView(R.id.tl_tablayout_kcpdactivity)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_viewpager_kcpdactivity)
    ViewPager viewPager;
    private PddyPresenter presenter;
    private PddyFragmentDL dlFragment;
    private PddyFragmentHL hlFragment;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private FragmentViewPagerAdapter viewPagerAdapter;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.activity_pddy);
        ButterKnife.bind(this);
        initView();
        presenter = new PddyPresenter(this, this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("盘点打印");
        //单料
        dlFragment = new PddyFragmentDL();
        //混料
        hlFragment = new PddyFragmentHL();
        viewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(dlFragment, "单料");
        viewPagerAdapter.addFragment(hlFragment, "混料");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PddyActivity.this.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        dialog = new AlertDialog.Builder(this).setTitle("提示").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("请稍后");

    }

    @OnClick({R.id.pbtn_bltooth_setting_activity_kcpd})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.pbtn_bltooth_setting_activity_kcpd:
                showBlueToothAddressDialog();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示蓝牙打印机选择框
     */
    public void showBlueToothAddressDialog() {
        final android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this,3);
        builder.setTitle("请选择蓝牙设备");
        builder.setPositiveButton("找不到设备？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()){
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this,3);
            dialog.setTitle("提示");
            dialog.setMessage("是否打开蓝牙");
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.create().show();
        }else {
            Set<BluetoothDevice> devicesSet = adapter.getBondedDevices();
            Object[] devices=devicesSet.toArray();
            final String[] itemName=new String[devices.length];
            final String[] itemAddress=new String[devices.length];
            for(int i=0;i<devices.length;i++){
                BluetoothDevice device=(BluetoothDevice)devices[i];
                itemName[i]=device.getName();
                itemAddress[i]=device.getAddress();
            }
            builder.setItems(itemName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenUtil preferenUtil=new PreferenUtil(PddyActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    /**
     * 获取混料条码
     * @param wlbhs 物料编号s
     * @param wlbls 物料比例s
     * @param scpc 生产批次
     * @param bzsl 包装数量
     * @param strDw 单位
     * @param mapKw 库位信息
     */
    @Override
    public void getHLBarCode(String wlbhs, String wlbls, String scpc, String bzsl, String strDw, Map<String, String> mapKw) {
        presenter.getHLBarCode(wlbhs,wlbls,scpc,bzsl,strDw,mapKw);
    }

    /**
     * 成功获取混料条码
     * @param map
     */
    @Override
    public void onGetHLBarCodeSucceed(Map<String, String> map) {
        hlFragment.onGetHLBarCodeSucceed(map);
    }

    /**
     * 查询物料编号（单料，混料通用）
     * @param wlbh 物料编号
     */
    @Override
    public void queryWlbh(String wlbh) {
        presenter.queryWlbh(wlbh);
    }

    /**
     * 查询物料编号成功（单料，混料通用）
     * @param wldmArr
     * @param wlbhData
     */
    @Override
    public void onQueryWlbhSucceed(String[] wldmArr, List<Map<String, String>> wlbhData) {
        //根据viewpager的index 判断回调那个Fragment
        if (position == 0){
            dlFragment.onQueryWlbhSucceed(wldmArr,wlbhData);
        }else {
            hlFragment.onQueryWlbhSucceed(wldmArr,wlbhData);
        }

    }

    /**
     * 获取库位信息（单料，混料通用）
     */
    @Override
    public void getKwData() {
        presenter.getKwData();
    }

    /**
     * 成功获取库存信息（单料，混料通用）
     * @param dataMc 库位名称
     * @param data 库位数据
     */
    @Override
    public void onGetKwdataSucceed(List<String> dataMc, List<Map<String, String>> data) {
        dlFragment.onGetKwdataSucceed(dataMc,data);
        hlFragment.onGetKwdataSucceed(dataMc,data);
    }

    /**
     * 获取单料条码
     * @param wlbh 物料编号
     * @param scpc 生产批次
     * @param bzsl 包装数量
     * @param strDw 单位
     * @param mapKw 库位
     */
    @Override
    public void getBarCode(String wlbh, String scpc, String bzsl, String strDw, Map<String, String> mapKw) {
        presenter.getBarCode(wlbh,scpc,bzsl,strDw,mapKw);
    }

    /**
     * 获取单料条码成功
     * @param barCode
     * @param qrCode
     */
    @Override
    public void onGetBarCodeSucceed(String barCode, String qrCode) {
        dlFragment.onGetBarCodeSucceed(barCode,qrCode);
    }

    /**
     * 单料打印
     * @param qrCode
     * @param wlpmChinese
     * @param wlpmEnlight
     */
    @Override
    public void printEvent(String qrCode, String wlpmChinese, String wlpmEnlight) {
        presenter.printEvent(qrCode,wlpmChinese,wlpmEnlight);
    }
    /**
     * 混料打印
     * @param qrCodeMap
     */
    @Override
    public void printHLEvent(Map<String, String> qrCodeMap) {
        presenter.printHLEvent(qrCodeMap);
    }

    @Override
    public void setShowMsgDialogEnable(String msg) {
        dialog.setMessage(msg);
        dialog.show();
    }

    @Override
    public void setShowProgressDialogEnable(boolean enable) {
        if (enable) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }



}
