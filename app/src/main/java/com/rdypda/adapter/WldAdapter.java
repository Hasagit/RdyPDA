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
    public void onBindViewHolder(WldViewHolder viewHolder, final int i, Map<String, String> map) {
        viewHolder.wld_text.setText(map.get("id"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, LlddrMsgActivity.class);
                context.startActivity(intent);
            }
        });
    }


    public class WldViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView wld_text,person_text,time_text;
        public LinearLayout content;
        public WldViewHolder(View itemView) {
            super(itemView);
            wld_text=(TextView) itemView.findViewById(R.id.wld);
            person_text=(TextView)itemView.findViewById(R.id.person);
            time_text=(TextView)itemView.findViewById(R.id.time);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }
}
