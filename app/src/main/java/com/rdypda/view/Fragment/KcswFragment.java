package com.rdypda.view.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdypda.R;
import com.rdypda.adapter.KcswAdapter;

import java.util.List;
import java.util.Map;

public class KcswFragment extends Fragment {
    private RecyclerView recyclerView;
    public KcswFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_kcsw, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler);
        return view;
    }

    public void refreshKcsw(List<Map<String,String>>data){
        KcswAdapter adapter=new KcswAdapter(getContext(),R.layout.kcsw_item,data);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);
    }

}
