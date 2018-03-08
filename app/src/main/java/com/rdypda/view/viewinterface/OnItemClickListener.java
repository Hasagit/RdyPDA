package com.rdypda.view.viewinterface;

import com.example.liangmutian.airrecyclerview.swipetoloadlayout.BaseRecyclerAdapter;

import java.util.Map;

/**
 * Created by DengJf on 2018/3/8.
 */

public interface OnItemClickListener {
    void onItemClick(int position, Map<String,String>map, BaseRecyclerAdapter.BaseRecyclerViewHolder holder);
}
