package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/31.
 */

public interface IYljsflView {
    void setShowProgressDialogEnable(boolean enable);

    void setShowDialogMsg(String msg);

    void refreshKcddSp(List<String>data,List<String>dataDm);


    void addYljstlRecyclerItem(Map<String,String>item);

    void removeYljstlRecyclerItem(String tmxh);
}
