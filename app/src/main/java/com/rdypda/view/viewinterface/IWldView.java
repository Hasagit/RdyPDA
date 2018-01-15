package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/20.
 */

public interface IWldView extends IBaseView{
    void refreshWldRecycler(List<Map<String,String >>data);
}
