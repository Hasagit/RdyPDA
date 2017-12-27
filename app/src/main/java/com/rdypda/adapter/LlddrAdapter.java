package com.rdypda.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.view.activity.LlddrMsgActivity;
import com.rdypda.view.activity.WldActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/8.
 */

public class LlddrAdapter extends BaseRecyclerAdapter<LlddrAdapter.ViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private Context context;
    private int resource;

    public LlddrAdapter( Context context, int resource,List<Map<String, String>> data) {
        super(data);
        this.data = data;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(resource,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, Map<String, String> map) {
        viewHolder.button.setText(map.get("lld"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,WldActivity.class);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView button;
        public LinearLayout content;
        public ViewHolder(View itemView) {
            super(itemView);
            button=(TextView) itemView.findViewById(R.id.test_btn);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }

}
