package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/8.
 */

public class SbtlZsAdapter extends BaseRecyclerAdapter<SbtlZsAdapter.ViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private int resources;
    private Context context;


    public SbtlZsAdapter(Context context,int resources,List<Map<String, String>> mDataList) {
        super(mDataList);
        data=mDataList;
        this.resources=resources;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i, Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("ylgg"));
        viewHolder.lab_2.setText(map.get("sbbh"));
        viewHolder.lab_3.setText(map.get("zjls"));
        viewHolder.lab_4.setText(map.get("yjys"));
    }


    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        TextView lab_1,lab_2,lab_3,lab_4;
        public ViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
        }
    }
}
