package com.rdypda.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.adapter.SbtlScanAdapter;
import com.rdypda.adapter.SbtlZsAdapter;
import com.rdypda.model.cache.PreferenUtil;
import com.rdypda.presenter.HlbzPresenter;
import com.rdypda.presenter.SbxlPresenter;
import com.rdypda.view.viewinterface.ISbxlView;
import com.rdypda.view.viewinterface.OnItemClickListener;
import com.rdypda.view.widget.PowerButton;

import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SbxlActivity extends BaseActivity implements ISbxlView {
    private SbxlPresenter presenter;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private boolean hadUpload;
    public static int START_TYPE_SBXL=0,
            START_TYPE_SYTL=1,
            START_TYPE_ZZTL=2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.xlkw)
    Spinner xlkwSp;
    @BindView(R.id.tl_list)
    RecyclerView zsList;
    @BindView(R.id.scaned_list)
    RecyclerView scanedList;
    @BindView(R.id.sbbh_ed)
    EditText sbbhEd;
    @BindView(R.id.hl_btn)
    FloatingActionButton hlBtn;
    @BindView(R.id.ql_btn)
    FloatingActionButton qlBtn;
    @BindView(R.id.sbbh_text)
    TextView sbbhText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbxl);
        ButterKnife.bind(this);
        initView();
        presenter=new SbxlPresenter(this,this);
        presenter.setStartType(getIntent().getIntExtra("start_type",0));
    }

    @Override
    protected void initView() {
        dialog=new AlertDialog.Builder(this).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setTitle("提示")
                .create();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("提示...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        hlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMsgDialog("请先输入并验证设备编号");
            }
        });
        switch (getIntent().getIntExtra("start_type",0)){
            case 0:
                actionBar.setTitle("设备下料");
                sbbhText.setText("设备编号：");
                sbbhEd.setHint("请输入设备编号");
                break;
            case 1:
                actionBar.setTitle("丝印退料");
                sbbhText.setText("型号&品名：");
                sbbhEd.setHint("请输入型号&品名");
                hlBtn.setVisibility(View.GONE);
                break;
            case 2:
                actionBar.setTitle("组装退料");
                sbbhText.setText("线别：");
                sbbhEd.setHint("请输入线别");
                hlBtn.setVisibility(View.GONE);
                break;
        }

    }

    @OnClick({R.id.sb_sure_btn,R.id.ql_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sb_sure_btn:
                int startType=getIntent().getIntExtra("start_type",0);
                if (startType==SbxlActivity.START_TYPE_ZZTL|startType==SbxlActivity.START_TYPE_SYTL){
                    presenter.isValidDevice(sbbhEd.getText().toString());
                }else if (startType==SbxlActivity.START_TYPE_SBXL){
                    presenter.getScanList(sbbhEd.getText().toString());
                }
                break;
            case R.id.ql_btn:
                showClearDialog(presenter.getSbbh());
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
    public void refreshXlkwSp(final List<String> data, List<String> dataMc) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SbxlActivity.this,android.R.layout.simple_spinner_dropdown_item,dataMc);
        xlkwSp.setAdapter(adapter);
        presenter.setFtyIdAndstkId(";");
        xlkwSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setFtyIdAndstkId(data.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void refreshScanList(List<Map<String, String>> data) {
        SbtlScanAdapter adapter=new SbtlScanAdapter(SbxlActivity.this,R.layout.item_sbtl_scan,data);
        scanedList.setAdapter(adapter);
        scanedList.setLayoutManager(new GridLayoutManager(SbxlActivity.this,1));
    }

    @Override
    public void refreshZsList(final List<Map<String, String>> data) {
        SbtlZsAdapter adapter=new SbtlZsAdapter(SbxlActivity.this,R.layout.item_sbtl_zs,data);
        zsList.setAdapter(adapter);
        zsList.setLayoutManager(new GridLayoutManager(SbxlActivity.this,1));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Map<String, String> map, BaseRecyclerAdapter.BaseRecyclerViewHolder holder) {
                if (getIntent().getIntExtra("start_type",0)!=START_TYPE_SYTL&
                        getIntent().getIntExtra("start_type",0)!=START_TYPE_ZZTL){
                    if (!presenter.getFtyIdAndstkId().equals(";")){
                        showScanDialog(map,presenter.HL);
                    }else {
                        showMsgDialog("请先选择退料库位");
                    }
                }

            }
        });
        if (data.size()>1){
            hlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!presenter.getFtyIdAndstkId().equals(";")){
                        Map<String,String>map=data.get(0);
                        map.put("ylgg",data.get(0).get("ylgg")+"(MIX)");
                        showScanDialog(map,presenter.HLS);
                    }else {
                        showMsgDialog("请先选择退料库位");
                    }
                }
            });

        }else {
            hlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //hlBtn.setEnabled(false);
                    Toast.makeText(SbxlActivity.this,"料筒未投料，不能执行此操作",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void showScanDialog(final Map<String,String>map, final int type) {
        hadUpload=false;
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_sbxl,null);
        final AlertDialog msgDilaog=new AlertDialog.Builder(this)
                .setView(view)
                .create();
        final TextView sbbhText=(TextView)view.findViewById(R.id.sbbh);
        final TextView ylggText=(TextView)view.findViewById(R.id.ylgg);
        final TextView szggText=(TextView)view.findViewById(R.id.szgg);
        final TextView tmbhText=(TextView)view.findViewById(R.id.tmbh);
        final TextView zyryText=(TextView)view.findViewById(R.id.zyry);
        TextView scrqText=(TextView)view.findViewById(R.id.scrq);
        final EditText bzslEd=(EditText) view.findViewById(R.id.bzsl);
        PowerButton getTmBtn=(PowerButton)view.findViewById(R.id.get_tm_btn);
        PowerButton printBtn=(PowerButton)view.findViewById(R.id.print_btn);
        sbbhText.setText(map.get("sbbh"));
        ylggText.setText(map.get("ylgg"));
        scrqText.setText(map.get("scrq"));
        zyryText.setText(map.get("zyry"));
        szggText.setText(map.get("szgg"));
        getTmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getTmxh(sbbhText.getText().toString(),
                        bzslEd.getText().toString(),
                        map.get("dw"),
                        tmbhText,
                        type
                );
            }
        });
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.printEven(ylggText.getText().toString(),
                        szggText.getText().toString(),
                        zyryText.getText().toString(),
                        bzslEd.getText().toString(),
                        tmbhText.getText().toString(),
                        new HlbzPresenter.OnPrintListener() {
                            @Override
                            public void onFinish() {
                                if (!hadUpload){
                                    hadUpload=true;
                                    presenter.xlPacking(sbbhText.getText().toString(),
                                            tmbhText.getText().toString());
                                }
                            }
                        });
            }
        });
        msgDilaog.show();
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
                    PreferenUtil preferenUtil=new PreferenUtil(SbxlActivity.this);
                    preferenUtil.setString("blueToothAddress",itemAddress[which]);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void showPackErrorDialog(final String sbbh, final String tmbh) {
        AlertDialog errorDialog=new AlertDialog.Builder(SbxlActivity.this)
                .setTitle("提示")
                .setMessage("打印提交失败，请重试")
                .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.xlPacking(sbbh,tmbh);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        errorDialog.show();
    }


    public void showClearDialog(final String sbbh){
        if (sbbh.equals("")){
            showMsgDialog("请先输入并验证设备");
            return;
        }
        AlertDialog clearDialog=new AlertDialog.Builder(SbxlActivity.this)
                .setTitle("提示")
                .setMessage("确认"+sbbh+"清料吗？\n清料后会将料筒已投料数据清除，但余料不会自动回仓。")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.xlClear(sbbh);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        clearDialog.show();
    }

    @Override
    protected void onDestroy() {
        presenter.closeScanUtil();
        super.onDestroy();
    }

    @Override
    public void showQueryList(final String[] sbdm, final String[] sbmc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setItems(sbmc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.setSbbh(sbdm[which]);
                setSbbhText(sbmc[which]);
                presenter.getScanList(sbdm[which]);
                dialog.dismiss();

            }
        });
        builder.create().show();
    }

    @Override
    public void setSbbhText(String sbbhStr) {
        sbbhEd.setText(sbbhStr);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(SbxlActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

}
