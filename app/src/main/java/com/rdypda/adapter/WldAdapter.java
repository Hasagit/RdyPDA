package com.rdypda.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.view.activity.LlddrMsgActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/20.
 */

public class WldAdapter extends BaseRecyclerAdapter<WldAdapter.WldViewHolder,Map<String,String>>{
    private List<Map<String,String>>data;
    private int resources;
    private Context context;
    private String lldh;

    public WldAdapter(Context context,int resources,List<Map<String, String>> data) {
        super(data);
        this.data=data;
        this.resources=resources;
        this.context=context;
    }


    @Override
    public WldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        WldViewHolder holder=new WldViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WldViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("djbh"));
        viewHolder.lab_2.setText(map.get("llm_wldm"));
        viewHolder.lab_3.setText(map.get("ftyId"));
        viewHolder.lab_4.setText(map.get("stkId"));
        viewHolder.lab_5.setText(map.get("wldm"));
        viewHolder.lab_6.setText(map.get("wlpm"));
        viewHolder.lab_7.setText(map.get("ywwlpm"));
        viewHolder.lab_8.setText(map.get("qty"));
        viewHolder.lab_9.setText(map.get("unit"));
        viewHolder.lab_10.setText(map.get("jlry"));
        viewHolder.lab_11.setText(map.get("jlrq"));
        viewHolder.lab_12.setText(map.get("id"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, LlddrMsgActivity.class);
                intent.putExtra("lldh",lldh);
                intent.putExtra("wldm",map.get("wldm"));
                intent.putExtra("dw",map.get("unit"));
                intent.putExtra("gch",map.get("ftyId"));
                intent.putExtra("kcdd",map.get("stkId"));
                intent.putExtra("ywwlpm",map.get("ywwlpm"));
                intent.putExtra("wlpm",map.get("wlpm"));
                context.startActivity(intent);
            }
        });
    }


    public class WldViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView lab_1;
        public TextView lab_2;
        public TextView lab_3;
        public TextView lab_4;
        public TextView lab_5;
        public TextView lab_6;
        public TextView lab_7;
        public TextView lab_8;
        public TextView lab_9;
        public TextView lab_10;
        public TextView lab_11;
        public TextView lab_12;
        public LinearLayout content;
        public WldViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
            lab_5=(TextView)itemView.findViewById(R.id.lab_5);
            lab_6=(TextView)itemView.findViewById(R.id.lab_6);
            lab_7=(TextView)itemView.findViewById(R.id.lab_7);
            lab_8=(TextView)itemView.findViewById(R.id.lab_8);
            lab_9=(TextView)itemView.findViewById(R.id.lab_9);
            lab_10=(TextView)itemView.findViewById(R.id.lab_10);
            lab_11=(TextView)itemView.findViewById(R.id.lab_11);
            lab_12=(TextView)itemView.findViewById(R.id.lab_12);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }

    public void setLldh(String lldh) {
        this.lldh = lldh;
    }
}
