package com.rdypda.adapter;

/**
 * Created by 少雄 on 2018-04-13.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rdypda.R;
import com.rdypda.view.widget.PddyDialog;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by DengJf on 2017/12/12.
 */

public class PddyAdapter extends RecyclerView.Adapter<PddyAdapter.Holder> {

    private List<Map<String, String>> data;
    private Context context;
    private OnItemLongClickListener listener;

    public PddyAdapter(List<Map<String, String>> data, Context context, OnItemLongClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pddy, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.tvWlbh.setText(data.get(position).get(PddyDialog.STR_WLBH));
        holder.tvWlgg.setText(data.get(position).get(PddyDialog.STR_WLGG));
        holder.tvWlbl.setText(data.get(position).get(PddyDialog.STR_WLBL));
        holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        if (listener != null){
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(holder,position,data.get(position));
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Map<String, String> map) {

        data.add(map);
        notifyDataSetChanged();
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Holder holder, int position, Map<String, String> map);
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_wlbh_item_pddy)
        TextView tvWlbh;
        @BindView(R.id.tv_wlgg_item_pddy)
        TextView tvWlgg;
        @BindView(R.id.tv_wlbl_item_pddy)
        TextView tvWlbl;
        @BindView(R.id.ibtn_delete_item_pddy)
        ImageButton ibtnDelete;
        View content;
        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            content = itemView;


        }
    }

}

