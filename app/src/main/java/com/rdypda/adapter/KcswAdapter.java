package com.rdypda.adapter;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/30.
 */

public class KcswAdapter extends BaseRecyclerAdapter<LlddrAdapter.ViewHolder,Map<String,String>> {


    public KcswAdapter(List<Map<String, String>> mDataList) {
        super(mDataList);
    }

    @Override
    public void onBindViewHolder(LlddrAdapter.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(LlddrAdapter.ViewHolder viewHolder, int i, Map<String, String> map) {

    }
}
