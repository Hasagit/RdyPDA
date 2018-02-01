package com.rdypda.adapter;

import android.app.Activity;
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
 * Created by DengJf on 2018/1/30.
 */

public class KcswAdapter extends BaseRecyclerAdapter<KcswAdapter.ViewHolder,Map<String,String>> {
    private int res;
    private Context context;
    private List<Map<String,String>>data;

    public KcswAdapter(Context context,int res,List<Map<String, String>> mDataList) {
        super(mDataList);
        this.res=res;
        this.context=context;
        this.data=mDataList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(res,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KcswAdapter.ViewHolder viewHolder, int i, Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("swm"));
        viewHolder.lab_2.setText(map.get("swdh"));
        viewHolder.lab_3.setText(map.get("rq"));
        viewHolder.lab_4.setText(map.get("wldm"));
        viewHolder.lab_5.setText(map.get("ph"));
        viewHolder.lab_6.setText(map.get("kcdd")+","+map.get("kw")+","+map.get("cw"));
        viewHolder.lab_7.setText("事务数量:"+map.get("swsl")+map.get("dw"));
        viewHolder.lab_8.setText(map.get("czry"));
        viewHolder.lab_9.setText(map.get("czrq"));
        if (i%2==0){
            viewHolder.content.setBackgroundColor(context.getResources().getColor(R.color.color_kcsw_1));
        }else {
            viewHolder.content.setBackgroundColor(context.getResources().getColor(R.color.color_kcsw_2));
        }
    }

    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        LinearLayout content;
        TextView lab_1,lab_2,lab_3,lab_4,lab_5,lab_6,lab_7,lab_8,lab_9;
        public ViewHolder(View itemView) {
            super(itemView);
            content=(LinearLayout)itemView.findViewById(R.id.content);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
            lab_5=(TextView)itemView.findViewById(R.id.lab_5);
            lab_6=(TextView)itemView.findViewById(R.id.lab_6);
            lab_7=(TextView)itemView.findViewById(R.id.lab_7);
            lab_8=(TextView)itemView.findViewById(R.id.lab_8);
            lab_9=(TextView)itemView.findViewById(R.id.lab_9);
        }
    }
}
