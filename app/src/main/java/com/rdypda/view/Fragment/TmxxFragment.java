package com.rdypda.view.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdypda.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TmxxFragment extends Fragment {
    TextView wjbhText;
    TextView wldmText;
    TextView phText;
    TextView slText;
    TextView pmggText;
    TextView scrqText;
    TextView gcText;
    TextView kcddText;
    TextView dycsText;
    TextView qrCodeText;

    public TmxxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tmxx, container, false);
        initView(view);
        return view;
    }
    public void initView(View view){
        wjbhText=(TextView)view.findViewById(R.id.wjbh);
        wldmText=(TextView)view.findViewById(R.id.wldm);
        phText=(TextView)view.findViewById(R.id.ph);
        pmggText=(TextView)view.findViewById(R.id.pmgg);
        slText=(TextView)view.findViewById(R.id.sl);
        scrqText=(TextView)view.findViewById(R.id.scrq);
        gcText=(TextView)view.findViewById(R.id.gc);
        kcddText=(TextView)view.findViewById(R.id.kcdd);
        dycsText=(TextView)view.findViewById(R.id.dycs);
        qrCodeText=(TextView)view.findViewById(R.id.qrcode);
    }

    public void setTmxx(Map<String,String>map){
        if (map==null){
            wjbhText.setText("");
            wldmText.setText("");
            phText.setText("");
            slText.setText("");
            pmggText.setText("");
            scrqText.setText("");
            gcText.setText("");
            kcddText.setText("");
            dycsText.setText("");
            qrCodeText.setText("");
        }else {
            wjbhText.setText(map.get("wjbh"));
            wldmText.setText(map.get("wldm"));
            phText.setText(map.get("ph"));
            slText.setText(map.get("sl"));
            pmggText.setText(map.get("pmgg"));
            scrqText.setText(map.get("scrq"));
            gcText.setText(map.get("gc"));
            kcddText.setText(map.get("kcdd"));
            dycsText.setText(map.get("dycs"));
            qrCodeText.setText(map.get("qrcode"));
        }

    }

}
