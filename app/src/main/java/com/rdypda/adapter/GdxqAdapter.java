package com.rdypda.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.view.activity.FlActivity;
import com.rdypda.view.widget.PowerButton;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/15.
 */

public class GdxqAdapter extends BaseRecyclerAdapter<GdxqAdapter.GdxqViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private Context context;
    private int resources;

    public GdxqAdapter( Context context, int resources, List<Map<String, String>> data) {
        super(data);
        this.data = data;
        this.context = context;
        this.resources = resources;
    }

    @Override
    public GdxqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        GdxqViewHolder holder=new GdxqViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GdxqViewHolder viewHolder, final int i, Map<String, String> map) {
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.slid_layout.closePane();
            }
        });
        viewHolder.slid_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.slid_layout.isOpen()){
                    viewHolder.slid_layout.closePane();
                }else {
                    viewHolder.slid_layout.openPane();
                }
            }
        });
        viewHolder.hl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FlActivity.class);
                context.startActivity(intent);
            }
        });
        viewHolder.tl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FlActivity.class);
                context.startActivity(intent);
            }
        });


    }

    public class GdxqViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public SlidingPaneLayout slid_layout;
        public LinearLayout content;
        public PowerButton slid_btn,tl_btn,hl_btn;
        public GdxqViewHolder(View itemView) {
            super(itemView);
            slid_layout=(SlidingPaneLayout) itemView.findViewById(R.id.sliding);
            content=(LinearLayout) itemView.findViewById(R.id.content);
            slid_btn=(PowerButton) itemView.findViewById(R.id.sliding_btn);
            tl_btn=(PowerButton)itemView.findViewById(R.id.tl_btn);
            hl_btn=(PowerButton)itemView.findViewById(R.id.hl_btn);
        }
    }
}
