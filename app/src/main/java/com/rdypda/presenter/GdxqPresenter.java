package com.rdypda.presenter;

import android.content.Context;

import com.rdypda.view.viewinterface.IGdxqView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/15.
 */

public class GdxqPresenter extends BasePresenter {
    private IGdxqView view;
    public GdxqPresenter(Context context,IGdxqView view) {
        super(context);
        this.view=view;
        List<Map<String,String>>data=new ArrayList<>();
        data.add(new HashMap<String, String>());
        data.add(new HashMap<String, String>());
        view.refreshRecyclerView(data);
    }
}
