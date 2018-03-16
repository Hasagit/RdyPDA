package com.rdypda.adapter;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;

/**
 * Created by DengJf on 2018/3/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/15.
 */

public class WydrckScanAdapter extends BaseRecyclerAdapter<WydrckScanAdapter.ViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private int resources;
    private Context context;
    private com.rdypda.view.viewinterface.OnItemClickListener onItemClickListener;

    public WydrckScanAdapter(Context context, int resources, List<Map<String, String>> mDataList) {
        super(mDataList);
        this.data=mDataList;
        this.resources=resources;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("tmbh"));
        viewHolder.lab_2.setText(map.get("sl"));
        viewHolder.lab_3.setText(map.get("wlbh"));
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

    public void setOnItemClickListener(com.rdypda.view.viewinterface.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(Map<String,String>map){
        data.add(map);
        notifyDataSetChanged();
    }

    public void removeData(String tmxh){
        for (int i=0;i<data.size();i++){
            if (data.get(i).get("tmbh").equals(tmxh)){
                data.remove(i);
                break;
            }
        }
    }

    public List<Map<String, String>> getData() {
        return data;
    }
}
