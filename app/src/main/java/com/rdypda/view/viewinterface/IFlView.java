package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/13.
 */

public interface IFlView extends IBaseView{
    void refreshReceive(List<Map<String, String>>data);

    void setShowMsgDialogEnable(String msg,boolean enable);

    void setShowProgressEnable(boolean enable);

    void addReceiveData(Map<String,String>item);

}
