package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/7.
 */

public interface ISbtlView {

    void setShowProgressDialogEnable(boolean enable);

    void showMsgDialog(String msg);

    void setShowScanDialogEnable(boolean enable,String type);

    void setSbbText(String sbbh);

    void setWltmText(String wltm);

    void showScanDialog(String tmbh, String ylbh, String ylgg, String tmsl,String trzs);

    void refreshScanList(List<Map<String,String>>data);

    void refreshZsList(List<Map<String,String>>data);

    void setSbRadioCheck(boolean check);

    void showQueryList(String[] sbdm,String[] sbmc);
}
