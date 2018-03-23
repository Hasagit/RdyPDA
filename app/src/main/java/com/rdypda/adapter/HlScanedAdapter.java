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

public class HlScanedAdapter extends BaseRecyclerAdapter<HlScanedAdapter.ViewHolder,Map<String,String>> {
    private Context context;
    private List<Map<String,String>>data;
    private int resources;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public HlScanedAdapter(Context context,int resources,List<Map<String, String>> mDataList) {
        super(mDataList);
        this.context=context;
        this.resources=resources;
        data=mDataList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("lab_1"));
        viewHolder.lab_2.setText(map.get("lab_2"));
        viewHolder.lab_3.setText(map.get("lab_3"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i,map);
                }
            }
        });

        viewHolder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener!=null){
                    onItemLongClickListener.onItemLongClick(i,map);
                }
                return true;
            }
        });
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener{
        void  onItemLongClick(int position,Map<String,String>map);
    }

    public interface OnItemClickListener{
        void  onItemClick(int position,Map<String,String>map);
    }
}
