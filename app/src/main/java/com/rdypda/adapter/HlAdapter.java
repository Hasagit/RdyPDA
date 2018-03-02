package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/2.
 */

public class HlAdapter extends BaseRecyclerAdapter<HlAdapter.ViewHolder,Map<String,String>> {
    private Context context;
    private List<Map<String,String>>data;
    private int resources;
    public HlAdapter(Context context,int resources,List<Map<String, String>> mDataList) {
        super(mDataList);
        this.data=mDataList;
        this.context=context;
        this.resources=resources;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i, Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("wlgg"));
        viewHolder.lab_2.setText(map.get("hlsl"));
        viewHolder.lab_3.setText(map.get("wlbh"));
    }

    public class ViewHolder extends  BaseRecyclerAdapter.BaseRecyclerViewHolder{
        TextView lab_1,lab_2,lab_3;
        LinearLayout content;
        public ViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }

}
