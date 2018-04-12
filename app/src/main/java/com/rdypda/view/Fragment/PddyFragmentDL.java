package com.rdypda.view.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.view.activity.WydrckActivity;
import com.rdypda.view.viewinterface.IPddyView;
import com.rdypda.view.widget.PowerButton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 少雄 on 2018-04-11.
 */

public class PddyFragmentDL extends Fragment {
    private static final String TAG = PddyFragmentDL.class.getSimpleName();
    @BindView(R.id.ed_wlbh_fragment_dl)
    EditText edWlbh;
    @BindView(R.id.tv_wlgg_fragment_dl)
    TextView tvWlgg;
    @BindView(R.id.et_scpc_fragment_dl)
    EditText etScpc;
    @BindView(R.id.et_bzsl_fragment_dl)
    EditText etBzsl;
    @BindView(R.id.tv_tmbh_fragmeng_dl)
    TextView tvTmbh;
    @BindView(R.id.btn_getbarcode_fragment_dl)
    Button btnGetbarcode;
    @BindView(R.id.btn_print_fragment_dl)
    Button btnPrint;
    @BindView(R.id.btn_query_wlbh_fragment_dl)
    PowerButton btnQueryWlbh;
    @BindView(R.id.sp_ylkw_fragment_dl)
    Spinner spYlkw;

    private View rootView;
    private IPddyView iPddyView;
    //库位信息
    private Map<String,String> mapKw = null;
    //单位
    private String strDw;
    //二维码
    private String qrCode;
    private String wlpmChinese;
    private String wlpmEnlight;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dl, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IPddyView) {
            iPddyView = (IPddyView) context;
        }
    }

    @OnClick({R.id.btn_getbarcode_fragment_dl, R.id.btn_print_fragment_dl, R.id.btn_query_wlbh_fragment_dl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getbarcode_fragment_dl:
                iPddyView.getBarCode(edWlbh.getText().toString(),etScpc.getText().toString(),etBzsl.getText().toString(),strDw,mapKw);
                break;
            case R.id.btn_print_fragment_dl:
                iPddyView.printEvent(qrCode,wlpmChinese,wlpmEnlight);
                break;
            case R.id.btn_query_wlbh_fragment_dl:
                iPddyView.queryWlbh(edWlbh.getText().toString().trim());
                break;
            /*case R.id.sp_ylkw_fragment_dl:
                iPddyView.getKwData();
                break;*/
        }
    }

    public void onQueryWlbhSucceed(final String[] wldmArr, final List<Map<String, String>> wlbhData) {
        if (wlbhData.size() == 1){
            edWlbh.setText(wldmArr[0]);
            //Log.d(TAG, "onClick: "+wlbhData.get(which).get("itm_wlgg"));
            tvWlgg.setText(wlbhData.get(0).get("itm_wlgg"));
            strDw = wlbhData.get(0).get("itm_unit");
            wlpmChinese = wlbhData.get(0).get("itm_wlgg");
            wlpmEnlight = wlbhData.get(0).get("itm_ywwlpm");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
        builder.setItems(wldmArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edWlbh.setText(wldmArr[which]);
                //Log.d(TAG, "onClick: "+wlbhData.get(which).get("itm_wlgg"));
                tvWlgg.setText(wlbhData.get(which).get("itm_wlgg"));
                strDw = wlbhData.get(which).get("itm_unit");
                wlpmChinese = wlbhData.get(which).get("itm_wlpm");
                wlpmEnlight = wlbhData.get(which).get("itm_ywwlpm");
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onGetKwdataSucceed(List<String> dataMc, final List<Map<String, String>> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dataMc);
        spYlkw.setAdapter(adapter);
        spYlkw.setSelection(0);
        spYlkw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    mapKw = null;
                }else {
                    mapKw = data.get(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mapKw = null;
            }
        });
    }

    public void onGetBarCodeSucceed(String barCode, String qrCode) {
        tvTmbh.setText(barCode);
        this.qrCode = qrCode;
    }
}
