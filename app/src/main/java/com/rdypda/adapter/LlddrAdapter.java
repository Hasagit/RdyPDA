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
import com.rdypda.presenter.MainPresenter;
import com.rdypda.view.activity.FlActivity;
import com.rdypda.view.activity.FlTabActivity;
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
    private int startType;

    public LlddrAdapter( Context context, int resource,List<Map<String, String>> data,int type) {
        super(data);
        this.data = data;
        this.context = context;
        this.resource = resource;
        this.startType=type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(resource,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, final Map<String, String> map) {
        viewHolder.lab_1.setText(map.get("djbh"));
        viewHolder.lab_2.setText(map.get("xsdh"));
        viewHolder.lab_3.setText(map.get("klrq"));
        viewHolder.lab_4.setText(map.get("zt"));
        viewHolder.lab_5.setText(map.get("kcdd"));
        viewHolder.lab_6.setText(map.get("wldm"));
        viewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startType== MainPresenter.TMDY){
                    Intent intent=new Intent(context,WldActivity.class);
                    intent.putExtra("djbh",map.get("djbh"));
                    intent.putExtra("wldm",map.get("wldm"));
                    intent.putExtra("startType",WldActivity.START_TYPE_LLD);
                    context.startActivity(intent);
                }else if (startType== MainPresenter.FL){
                    Intent intent=new Intent(context,FlTabActivity.class);
                    intent.putExtra("djbh",map.get("djbh"));
                    intent.putExtra("wldm",map.get("wldm"));
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends BaseRecyclerAdapter.BaseRecyclerViewHolder{
        public LinearLayout content;
        public TextView lab_1;
        public TextView lab_2;
        public TextView lab_3;
        public TextView lab_4;
        public TextView lab_5;
        public TextView lab_6;

        public ViewHolder(View itemView) {
            super(itemView);
            content=(LinearLayout)itemView.findViewById(R.id.content);
            lab_1=(TextView)itemView.findViewById(R.id.lab_1);
            lab_2=(TextView)itemView.findViewById(R.id.lab_2);
            lab_3=(TextView)itemView.findViewById(R.id.lab_3);
            lab_4=(TextView)itemView.findViewById(R.id.lab_4);
            lab_5=(TextView)itemView.findViewById(R.id.lab_5);
            lab_6=(TextView)itemView.findViewById(R.id.lab_6);
        }
    }

}
