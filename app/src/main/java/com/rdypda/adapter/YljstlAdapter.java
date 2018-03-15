package com.rdypda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;
import com.rdypda.R;
import com.rdypda.view.viewinterface.IFlTabView;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/31.
 */

public class YljstlAdapter extends BaseRecyclerAdapter<YljstlAdapter.ViewHolder,Map<String,String>> {
    private List<Map<String,String>>data;
    private Context context;
    private int res;
    private OnItemClickListener listener;

    public YljstlAdapter(Context context,int res,List<Map<String, String>> mDataList) {
        super(mDataList);
        this.data=mDataList;
        this.context=context;
        this.res=res;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(res,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("wlbh"));
        viewHolder.lab_2.setText(map.get("wlgg"));
        viewHolder.lab_3.setText(map.get("tmsl"));
        viewHolder.lab_4.setText(map.get("tmbh"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onItemClick(i,viewHolder,map);
                }
            }
        });
    }



    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        TextView lab_1,lab_2,lab_3,lab_4;
        LinearLayout content;
        public ViewHolder(View itemView) {
            super(itemView);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
            content=(LinearLayout)itemView.findViewById(R.id.content);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position,ViewHolder holder,Map<String,String>map);
    }

    public void addData(Map<String,String>map){
        data.add(map);
        notifyDataSetChanged();
    }

    public void removeData(String tmxh){
        for (int i=0;i<data.size();i++){
            if (data.get(i).get("tmbh").equals(tmxh)){
                data.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

}
