package com.rdypda.view.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.adapter.PddyAdapter;
import com.rdypda.util.QrCodeUtil;
import com.rdypda.view.viewinterface.IPddyView;
import com.rdypda.view.widget.PddyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 少雄 on 2018-04-11.
 */

public class PddyFragmentHL extends Fragment implements PddyAdapter.OnItemLongClickListener {
    @BindView(R.id.rv_recycler_fragment_hl2)
    RecyclerView rvRecycler;
    @BindView(R.id.sp_ylkw_fragment_hl)
    Spinner spYlkw;
    @BindView(R.id.tv_tmbh_fragmeng_hl)
    TextView tvTmbh;
    @BindView(R.id.btn_getbarcode_fragment_hl)
    Button btnGetbarcode;
    @BindView(R.id.btn_print_fragment_hl)
    Button btnPrint;
    @BindView(R.id.fab_add_fragment_hl2)
    FloatingActionButton fabAdd;
    @BindView(R.id.et_scpc_fragment_hl)
    EditText etScpc;
    @BindView(R.id.et_bzsl_fragment_hl)
    EditText etBzsl;
    private PddyDialog dialog;
    IPddyView iPddyView;
    private View rootView;
    private PddyAdapter pddyAdapter;
    private Map<String, String> mapKw;
    private Map<String, String> qrCodeMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_hl, container, false);
        ButterKnife.bind(this, rootView);
        pddyAdapter = new PddyAdapter(new ArrayList<Map<String, String>>(), getContext(), this);
        rvRecycler.setAdapter(pddyAdapter);
        rvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @OnClick({R.id.fab_add_fragment_hl2, R.id.btn_getbarcode_fragment_hl,R.id.btn_print_fragment_hl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_fragment_hl2:
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                showAddDialog("", "", "", "");
                break;
            case R.id.btn_getbarcode_fragment_hl:
                getBarCode();
                break;
            case R.id.btn_print_fragment_hl:
                iPddyView.printHLEvent(qrCodeMap);
                break;
        }
    }


    private void getBarCode() {
        if (pddyAdapter == null ||pddyAdapter.getItemCount() <2){
            iPddyView.setShowMsgDialogEnable("请添加物料，已添加"+pddyAdapter.getItemCount()+"种");
            return;
        }
        if(etScpc.getText().toString().equals("")){
            iPddyView.setShowMsgDialogEnable("请输入生产批次");
            return;
        }
        if(etBzsl.getText().toString().equals("")){
            iPddyView.setShowMsgDialogEnable("请输入包装数量");
            return;
        }
        List<Map<String, String>> data = pddyAdapter.getData();
        int sum = 0;
        StringBuilder sbWlbh = new StringBuilder();
        StringBuilder sbWlbl = new StringBuilder();
        for (int i=0; i<data.size(); i++){
            Map<String, String> map  = data.get(i);
            sum += Integer.parseInt(map.get(PddyDialog.STR_WLBL));
            if (i == data.size()-1){
                sbWlbh.append(map.get(PddyDialog.STR_WLBH));
                sbWlbl.append(map.get(PddyDialog.STR_WLBL));
            }else{
                sbWlbh.append(map.get(PddyDialog.STR_WLBH)+",");
                sbWlbl.append(map.get(PddyDialog.STR_WLBL)+",");
            }
        }

        if (sum != 100){
            iPddyView.setShowMsgDialogEnable("请检查比例数是否正确");
            return;
        }
        if (mapKw == null){
            iPddyView.setShowMsgDialogEnable("请先选择原料库位");
            return;
        }
        //iPddyView.getBarCode();
        iPddyView.getHLBarCode(sbWlbh.toString(),sbWlbl.toString(),etScpc.getText().toString(),etBzsl.getText().toString(),data.get(0).get("itm_unit"),mapKw);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IPddyView) {
            iPddyView = (IPddyView) context;
        }
    }

    /**
     * 查询物料编号成功
     * @param wldmArr 物料代码
     * @param wlbhData 物料所有信息
     */
    public void onQueryWlbhSucceed(final String[] wldmArr, final List<Map<String, String>> wlbhData) {
        if (wlbhData.size() == 1) {
            dialog.setQuetyWlbhResult(wldmArr[0], wlbhData.get(0));
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
        builder.setItems(wldmArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PddyFragmentHL.this.dialog.setQuetyWlbhResult(wldmArr[which], wlbhData.get(which));
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 显示增加物料/色种窗口
     *
     * @param type 物料/色种
     * @param wlbh 物料编号
     * @param bili 比例
     */
    private void showAddDialog(String type, String wlbh, String wlgg, String bili) {
        if (dialog == null) {
            dialog = new PddyDialog(getContext());
            dialog.setOnPddyAddListener(new PddyDialog.OnPddyAddListener() {
                @Override
                public void onPaddyAddListener(boolean isOK, String wlType, String wlbh, String wlgg, String wlbl, String itm_unit) {
                    if (isOK) {
                        Map<String, String> map = new HashMap<>();
                        map.put(PddyDialog.STR_WlTYPE, wlType);
                        map.put(PddyDialog.STR_WLBH, wlbh);
                        map.put(PddyDialog.STR_WLGG, wlgg);
                        map.put(PddyDialog.STR_WLBL, wlbl);
                        map.put(PddyDialog.STR_UNIT,itm_unit);
                        addWl(map);
                    }
                }

                @Override
                public void onClickQueryWlbh(String wlbh) {
                    iPddyView.queryWlbh(wlbh);
                }
            });
        }
        dialog.setData(type, wlbh, wlgg, bili);
        dialog.show();
    }

    /**
     * 增加物料到RecyclerView
     *
     * @param map
     */
    public void addWl(Map<String, String> map) {
        pddyAdapter.addItem(map);
    }

    /**
     * 长按修改
     *
     * @param holder
     * @param position
     * @param map
     */
    @Override
    public void onItemLongClick(PddyAdapter.Holder holder, int position, Map<String, String> map) {
        showAddDialog(map.get(PddyDialog.STR_WlTYPE), map.get(PddyDialog.STR_WLBH), map.get(PddyDialog.STR_WLGG), map.get(PddyDialog.STR_WLBL));
    }

    /**
     * 获取库位信息成功
     *
     * @param dataMc
     * @param data
     */
    public void onGetKwdataSucceed(List<String> dataMc, final List<Map<String, String>> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dataMc);
        spYlkw.setAdapter(adapter);
        spYlkw.setSelection(0);
        spYlkw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mapKw = null;
                } else {
                    mapKw = data.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mapKw = null;
            }
        });
    }


    public void onGetHLBarCodeSucceed(Map<String, String> map) {
        tvTmbh.setText(map.get("hl_tmbh"));
        this.qrCodeMap = map;
    }
}
