package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/31.
 */

public interface IYljsflView {
    void setShowProgressDialogEnable(boolean enable);

    void setShowDialogMsg(String msg);

    void refreshKcddSp(List<String>data);

    void refreshWldRecycler(List<Map<String,String>>data);

    void addYljstlRecyclerItem(Map<String,String>item);

    void removeYljstlRecyclerItem(Map<String,String>item);
}
