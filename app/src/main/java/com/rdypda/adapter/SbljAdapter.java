package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.view.viewinterface.OnItemClickListener;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/8.
 */

public class SbljAdapter extends BaseRecyclerAdapter<SbljAdapter.ViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private int resources;
    private Context context;
    private com.rdypda.view.viewinterface.OnItemClickListener onItemClickListener;

    public SbljAdapter(Context context,int resources,List<Map<String, String>> mDataList) {
        super(mDataList);
        this.context=context;
        this.resources=resources;
        this.data=mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("jtbh"));
        viewHolder.lab_2.setText(map.get("ybdkl"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i,map,viewHolder);
                }
            }
        });
    }


    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        TextView lab_1,lab_2;
        LinearLayout content;
        public ViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }
}
