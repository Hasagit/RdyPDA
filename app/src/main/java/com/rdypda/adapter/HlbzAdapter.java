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
 * Created by DengJf on 2018/3/5.
 */

public class HlbzAdapter extends BaseRecyclerAdapter<HlbzAdapter.ViewHolder,Map<String,String>> {
    private Context context;
    private int resources;
    private List<Map<String,String>>data;
    private OnItemClickListener onItemClickListener;
    public HlbzAdapter(Context context,int resources,List<Map<String, String>> mDataList) {
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
    public void onBindViewHolder(ViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("ylgg"));
        viewHolder.lab_2.setText(map.get("szgg"));
        viewHolder.lab_3.setText(map.get("dbzsl"));
        viewHolder.lab_4.setText(map.get("hldh"));
        viewHolder.lab_5.setText(map.get("zyry"));

        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i,map);
                }
            }
        });
    }

    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView lab_1,lab_2,lab_3,lab_4,lab_5;
        public LinearLayout content;
        public ViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
            lab_5=(TextView)itemView.findViewById(R.id.lab_5);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position,Map<String,String>item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
