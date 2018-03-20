package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.rdypda.adapter.HlbzAdapter;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.HlbzPresenter;
import com.rdypda.util.HlQrCodeUtil;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.view.viewinterface.IHlbzView;
import com.rdypda.view.widget.PowerButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HlbzActivity extends BaseActivity implements IHlbzView{
    private HlbzPresenter presenter;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private boolean isUpload=false;
    private String kcdd="";
    private int i;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sbmx)
    Spinner sbmxSp;
    @BindView(R.id.bz_list)
    RecyclerView bzList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hlbz);
        ButterKnife.bind(this);
        initView();
        presenter=new HlbzPresenter(this,this);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        dialog=new AlertDialog.Builder(this)
                .setTitle("提示")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

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
    public void showMsgToast(String msg) {
        Toast.makeText(HlbzActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshSbmx(final List<String> data) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(HlbzActivity.this,android.R.layout.simple_spinner_dropdown_item,data);
        sbmxSp.setAdapter(adapter);
        sbmxSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[]item=data.get(position).split(",");
                if (item.length>0){
                    presenter.setHljh(item[0]);
                    if (position!=0){
                        presenter.getHlqd();
                    }
                }else {
                    showMsgDialog("数据解析出错");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void refreshBzList(List<Map<String, String>> data) {
        HlbzAdapter adapter=new HlbzAdapter(HlbzActivity.this,R.layout.item_hlbz,data);
        bzList.setAdapter(adapter);
        bzList.setLayoutManager(new GridLayoutManager(HlbzActivity.this,1));
        adapter.setOnItemClickListener(new HlbzAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Map<String, String> item) {
            presenter.getKc(item);
            }
        });
    }

    @Override
    public void showPrintDialog(final Map<String, String> map, final String gsdm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isUpload=false;
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_hlbz,null);
            final AlertDialog printDilaog=new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            printDilaog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            TextView hljhText=(TextView)view.findViewById(R.id.hljh);
            final TextView ylggText=(TextView)view.findViewById(R.id.ylgg);
            final TextView szggText=(TextView)view.findViewById(R.id.szgg);
            TextView hlzlText=(TextView)view.findViewById(R.id.hlzl);
            TextView ybzslText=(TextView)view.findViewById(R.id.ybzsl);
            TextView dbzslText=(TextView)view.findViewById(R.id.dbzsl);
            final TextView bzslEd=(EditText)view.findViewById(R.id.bzsl);
            final TextView tmbhText=(TextView)view.findViewById(R.id.tmbh);
            final TextView qrCode=(TextView)view.findViewById(R.id.qrcode);
            PowerButton getTmBtn=(PowerButton)view.findViewById(R.id.get_tm__btn);
            PowerButton printBtn=(PowerButton)view.findViewById(R.id.print_btn);
            PowerButton continPrintBtn=(PowerButton)view.findViewById(R.id.contin_print_btn);
            hljhText.setText(map.get("hljh"));
            ylggText.setText(map.get("ylgg"));
            szggText.setText(map.get("szgg"));
            hlzlText.setText(map.get("hlzl"));
            ybzslText.setText(map.get("ybzsl"));
            dbzslText.setText(map.get("dbzsl"));
            printDilaog.show();
            getTmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.getTmxh(map.get("hldh"),
                            bzslEd.getText().toString(),
                            gsdm,
                            kcdd,
                            tmbhText,
                            qrCode
                            );
                }
            });
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.printEven(qrCode.getText().toString(),
                            ylggText.getText().toString(),
                            szggText.getText().toString(),
                            map.get("zyry"),
                            bzslEd.getText().toString()+new QrCodeUtil(qrCode.getText().toString()).getDw(),
                            tmbhText.getText().toString(),
                            new HlbzPresenter.OnPrintListener() {
                                @Override
                                public void onFinish() {
                                    if (!isUpload){
                                        isUpload=true;
                                        presenter.hlPacking(map.get("hldh"),tmbhText.getText().toString());
                                    }
                                }
                            }
                    );

                }
            });
            continPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tmbhText.getText().toString().equals("")){
                        showMsgDialog("已经获取条码编号，请不要重复操作");
                        return;
                    }
                    if (bzslEd.getText().toString().equals("")){
                        showMsgDialog("请先输入包装数量");
                        return;
                    }
                    showContinPrintDialog(map,gsdm,kcdd,bzslEd.getText().toString());
                }
            });
        }
    }

    @Override
    public void showKcDialog(final Map<String, String> map, final List<String>data,List<String>dataMc, final String gsdm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            kcdd="";
            if (data.size()>0){
                kcdd=data.get(0);
            }
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_kc,null);
            final AlertDialog kcDilaog=new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            kcDilaog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            Spinner jskwSp=(Spinner)view.findViewById(R.id.jskw);
            PowerButton sureBtn=(PowerButton)view.findViewById(R.id.sure_btn);
            PowerButton cancelBtn=(PowerButton)view.findViewById(R.id.cancel_btn);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(HlbzActivity.this,android.R.layout.simple_spinner_dropdown_item,dataMc);
            jskwSp.setAdapter(adapter);
            jskwSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    kcdd=data.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    kcDilaog.dismiss();
                }
            });
            sureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (kcdd.equals("")){
                        showMsgDialog("请先选择接收库位");
                        return;
                    }
                    showPrintDialog(map,gsdm);
                    kcDilaog.dismiss();
                }
            });

            kcDilaog.show();
        }
    }

    @Override
    public void showBlueToothAddressDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this,3);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this,3);
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
                    PreferenUtil preferenUtil=new PreferenUtil(HlbzActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }

    @Override
    public void showReloadHlPackingDialog(final String hlbh, final String tmxh) {
        AlertDialog dialog=new AlertDialog.Builder(HlbzActivity.this)
                .setTitle("提示")
                .setMessage(tmxh+"打印提交失败！")
                .setCancelable(false)
                .setPositiveButton("重新提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.hlPacking(hlbh,tmxh);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void showContinPrintDialog(final Map<String, String> map, final String gsdm, final String kw, final String bzsl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view= LayoutInflater.from(this).inflate(R.layout.dialog_hlbz_contin_print,null);
            isUpload=false;
            final AlertDialog continPrintDilaog=new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            continPrintDilaog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            final EditText dyfs=(EditText) view.findViewById(R.id.dyfs);
            final TextView tmxhText=(TextView)view.findViewById(R.id.tmbh);
            PowerButton getContinTm=(PowerButton)view.findViewById(R.id.get_tm_btn);
            PowerButton printBtn=(PowerButton)view.findViewById(R.id.print_btn);
            final List<String>printMsgs=new ArrayList<>();
            final List<String>tmbhs=new ArrayList<>();
            getContinTm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tmxhText.getText().toString().equals("")){
                        showMsgDialog("已经获取条码编号，请不要重复操作");
                        return;
                    }
                    if (dyfs.getText().toString().equals("")){
                        showMsgDialog("请先输入打印份数");
                        return;
                    }
                    if (Integer.parseInt(dyfs.getText().toString())>10){
                        showMsgDialog("最大连打份数为10");
                        return;
                    }
                    presenter.getContinueTm(map,gsdm,kw,bzsl,Integer.parseInt(dyfs.getText().toString()),tmxhText,printMsgs,tmbhs);
                }
            });
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tmxhText.getText().equals("")){
                        showMsgDialog("请先获取条码编号");
                        return;
                    }
                    i=0;
                    continPrintEven(printMsgs,
                            map.get("ylgg"),
                            map.get("szgg"),
                            map.get("zyry"),
                            bzsl,tmbhs,map.get("hldh"));
                }
            });
            continPrintDilaog.show();
        }
    }

    public void continPrintEven(final List<String> printMsg, final String ylgg, final String szgg, final String zyry, final String bzsl, final List<String> tmbh, final String hldh){
        if (i<printMsg.size()){
            final int j=i;
            presenter.printEven(printMsg.get(i),
                    ylgg,
                    szgg,
                    zyry,
                    bzsl,
                    tmbh.get(i),
                    new HlbzPresenter.OnPrintListener() {
                        @Override
                        public void onFinish() {
                            continPrintEven(printMsg,ylgg,szgg,zyry,bzsl,tmbh,hldh);
                            if (!isUpload){
                                presenter.hlPacking(hldh,tmbh.get(j));
                            }
                            if (j==printMsg.size()-1){
                                isUpload=true;
                            }

                        }
                    }
            );
            i++;
        }
    }
}
