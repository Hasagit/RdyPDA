package com.rdypda.view.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdypda.R;

/**
 * Created by 少雄 on 2018-04-11.
 */

public class PddyFragmentHL extends Fragment {
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_hl,container,false);
        return rootView;

    }
}
