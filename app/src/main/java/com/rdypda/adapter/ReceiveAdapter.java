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
        receiveViewHolder.wld_text.setText(map.get("wld"));
        receiveViewHolder.time_text.setText(map.get("time"));
        receiveViewHolder.person_text.setText(map.get("person"));
        receiveViewHolder.need_text.setText(map.get("need"));
        receiveViewHolder.scaned_text.setText(map.get("scaned"));
        if (map.get("need").equals(map.get("scaned"))){
            receiveViewHolder.scaned_text.setTextColor(context.getResources().getColor(R.color.color_green));
        }else {
            receiveViewHolder.scaned_text.setTextColor(context.getResources().getColor(R.color.color_red));
        }
    }



    public class ReceiveViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView wld_text,person_text,time_text,need_text,scaned_text;
        public ReceiveViewHolder(View itemView) {
            super(itemView);
            wld_text=(TextView) itemView.findViewById(R.id.wld);
            person_text=(TextView)itemView.findViewById(R.id.person);
            time_text=(TextView)itemView.findViewById(R.id.time);
            need_text=(TextView)itemView.findViewById(R.id.need);
            scaned_text=(TextView)itemView.findViewById(R.id.scaned);
        }
    }

    public void addData(Map<String,String>map){
        data.add(0,map);
        notifyDataSetChanged();
    }

    public void setNum(TextView num_text){
        int scaned_num=0;
        int need_num=0;
        for (int i=0;i<data.size();i++){
            scaned_num+=Integer.parseInt(data.get(i).get("scaned"));
            need_num+=Integer.parseInt(data.get(i).get("need"));
        }
        num_text.setText(need_num+"/"+scaned_num);
    }


}
