package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private OnItemClickListener listener;


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
    public void onBindViewHolder(final ReceiveViewHolder receiveViewHolder, final int i, final Map<String, String> map) {
        receiveViewHolder.lab_1.setText(map.get("wldm"));
        receiveViewHolder.lab_2.setText(map.get("tmsl"));
        receiveViewHolder.lab_3.setText(map.get("tmxh"));
        receiveViewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null) listener.onItemClick(receiveViewHolder,i,map);
            }
        });
    }



    public class ReceiveViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public TextView lab_1,lab_2,lab_3;
        public LinearLayout content;
        public ReceiveViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }

    }

    public void addData(Map<String,String>map){
        boolean isExist=false;
        for (int i=0;i<data.size();i++){
            if (map.get("tmxh").equals(data.get(i).get("tmxh"))&&
                    map.get("tmsl").equals(data.get(i).get("tmsl"))&&
                    map.get("wldm").equals(data.get(i).get("wldm"))){
                    isExist=true;
                    break;
            }
        }
        if (isExist){
            Toast.makeText(context,"改单已扫描在列表",Toast.LENGTH_SHORT).show();
        }else {
            data.add(0,map);
            notifyDataSetChanged();
        }
    }

    public void deleteData(Map<String,String>map){
        boolean isExist=false;
        for (int i=0;i<data.size();i++){
            if (map.get("tmxh").equals(data.get(i).get("tmxh"))&&
                    map.get("tmsl").equals(data.get(i).get("tmsl"))&&
                    map.get("wldm").equals(data.get(i).get("wldm"))){
                data.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(0,data.size());
                //notifyDataSetChanged();
                isExist=true;
                break;
            }
        }
        if (!isExist){
            Toast.makeText(context,"改单已在列表移除",Toast.LENGTH_SHORT).show();
        }
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(ReceiveViewHolder receiveViewHolder,int position,Map<String, String> map);
    }


}
