package com.rdypda.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rdypda.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 少雄 on 2018-04-12.
 */

public class PddyDialog extends AlertDialog {

    @BindView(R.id.sp_wltype_dialog_pddy_add)
    Spinner spWltype;
    @BindView(R.id.ed_wlbh_dialog_pddy_add)
    EditText edWlbh;
    @BindView(R.id.btn_query_wlbh_dialog_pddy_add)
    PowerButton btnQueryWlbh;
    @BindView(R.id.tv_wlgg_dialog_pddy_add)
    TextView tvWlgg;
    @BindView(R.id.ed_wlbl_dialog_pddy_add)
    EditText edWlbl;
    @BindView(R.id.pbtn_cancel_dialog_pddy_add)
    PowerButton pbtnCancel;
    @BindView(R.id.pbtn_add_dialog_pddy_add)
    PowerButton pbtnAdd;
    private View mRootView;
    private String wlType;
    List<String> dataWlType;
    private boolean isCheckWlbh;
    public static final String STR_UNIT = "itm_unit";
    public static final String STR_WLBH = "wlbh";
    public static final String STR_WLGG = "wlgg";
    public static final String STR_WLBL = "wlbl";
    public static final String STR_WlTYPE = "wltype";
    private Map<String, String> data;

    public void setOnPddyAddListener(OnPddyAddListener mOnPddyAddListener) {
        this.mOnPddyAddListener = mOnPddyAddListener;
    }

    private OnPddyAddListener mOnPddyAddListener;

    public PddyDialog(@NonNull Context context) {
        this(context, 0);
    }

    public PddyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }
    @OnClick({R.id.btn_query_wlbh_dialog_pddy_add,R.id.pbtn_cancel_dialog_pddy_add,R.id.pbtn_add_dialog_pddy_add})
    public void onClick(View view){
        switch (view.getId()){
            //点击查询
            case R.id.btn_query_wlbh_dialog_pddy_add:
                if (mOnPddyAddListener != null){
                    mOnPddyAddListener.onClickQueryWlbh(edWlbh.getText().toString());
                }
                break;
            //取消
            case R.id.pbtn_cancel_dialog_pddy_add:
                dismiss();
                if (mOnPddyAddListener != null){
                    mOnPddyAddListener.onPaddyAddListener(false,wlType,edWlbh.getText().toString(),tvWlgg.getText().toString(),edWlbl.getText().toString(),data.get("itm_unit"));
                }

                break;
            //确定
            case R.id.pbtn_add_dialog_pddy_add:
                if (!isCheckWlbh){
                    Snackbar.make(mRootView,"请验证物料编号",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edWlbl.getText().toString().equals("")){
                    Snackbar.make(mRootView,"请输入物料比例",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (mOnPddyAddListener != null){
                    mOnPddyAddListener.onPaddyAddListener(true,wlType,edWlbh.getText().toString(),tvWlgg.getText().toString(),edWlbl.getText().toString(), data.get("itm_unit"));
                }
                break;
        }
    }

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_pddy_add, null, false);
        setView(mRootView);
        ButterKnife.bind(this, mRootView);
        dataWlType = new ArrayList<>();
        dataWlType.add("物料");
        dataWlType.add("色种");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, dataWlType);
        spWltype.setAdapter(adapter);
        spWltype.setSelection(0);
        spWltype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*if (position == 0){
                    spWltype.setSelection(1);
                }else {

                }*/
                wlType = dataWlType.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                wlType = "";
            }
        });
        //一旦修改，必须先验证物料条码是否正确
        edWlbh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isCheckWlbh = false;
            }
        });
    }

    /**
     * 物料条码查询结果
     * @param wlbh
     * @param data
     */
    public void setQuetyWlbhResult(String wlbh, Map<String, String> data) {
        edWlbh.setText(wlbh);
        tvWlgg.setText(data.get("itm_wlgg"));
        this.data = data;
        isCheckWlbh = true;
    }

    /**
     * 初始化弹出框数据
     * @param type 物料/色种
     * @param wlbh 物料编号
     * @param wlgg 物料规格
     * @param bili 物料比例
     */
    public void setData(String type, String wlbh, String wlgg, String bili) {
        spWltype.setSelection(dataWlType.indexOf(type));
        edWlbh.setText(wlbh);
        tvWlgg.setText(wlgg);
        edWlbl.setText(bili);
        //长按修改，视为检查过物料代码
        isCheckWlbh = true;
    }

    public interface OnPddyAddListener {
        /**
         * 盘点打印监听器
         *  @param isOK 是否确定
         * @param wlType 物料类型
         * @param wlbh 物料编号
         * @param wlgg 物料规格
         * @param wlbl 物料比例
         * @param s
         */
        void onPaddyAddListener(boolean isOK, String wlType, String wlbh, String wlgg, String wlbl, String s);
        void onClickQueryWlbh(String wlbh);
    }

}
