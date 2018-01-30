package com.rdypda.view.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdypda.R;

import java.util.List;
import java.util.Map;

public class KcswFragment extends Fragment {


    public KcswFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kcsw, container, false);
    }

    public void refreshKcsw(List<Map<String,String>>data){

    }

}
