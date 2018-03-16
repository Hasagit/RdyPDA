package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/16.
 */

public interface IYkView {

    void setShowProgressDialogEnable(boolean enable);

    void showMsgDialog(String msg);

    void refreshJskwSp(List<String> mcData, List<String>idData);

    void refreshZsList(List<Map<String,String>>data);

    void refreshScanList(List<Map<String,String>>data);

    void addScanData(Map<String,String>map);

    void addZsData(Map<String,String>map);

    void removeScanData(String tmxh);

    void removeZsData(String wlbh,String tmsl);

    void setTmEd(String tmbh);
}
