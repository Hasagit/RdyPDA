package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/30.
 */

public interface ITmcxView {
    void setShowMsgDialogEnable(String msg);

    void setShowProgressDialogEnable(boolean enable);

    void setTmxxMsg(Map<String,String>map);

    void refreshKcsw(List<Map<String,String>>data);
}
