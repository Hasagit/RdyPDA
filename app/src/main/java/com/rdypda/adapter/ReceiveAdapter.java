package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/12.
 */

public class ReceiveAdapter extends BaseRecyclerAdapter<ReceiveAdapter.ReceiveViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private Context context;
    private int resources;


    public ReceiveAdapter( Context context, int resources,List<Map<String, String>> data) {
        super(data);
        this.data = data;
        this.context = context;
        this.resources = resources;
    }

    @Override
    public ReceiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(resources,null);
        ReceiveViewHolder holder=new ReceiveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReceiveViewHolder receiveViewHolder, int i, Map<String, String> map) {
        receiveViewHolder.btn.setText(map.get("id"));
    }


    public void addData(Map<String,String>map){
        data.add(0,map);
        notifyDataSetChanged();
    }




    public class ReceiveViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView btn;
        public ReceiveViewHolder(View itemView) {
            super(itemView);
            btn=(TextView) itemView.findViewById(R.id.test_btn);
        }
    }

}
