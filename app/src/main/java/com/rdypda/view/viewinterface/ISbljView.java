package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/8.
 */

public interface ISbljView {

    void setShowProgressDialogEnable(boolean enable);

    void  showMsgDialog(String msg);

    void refreshSblj(List<Map<String,String>>data);

    void showConnectDialog(String jtbh,String klbh);

    void setJtKlText(String jtbh,String klbh);

    String getJtbhText();

    String getklbhText();
}
