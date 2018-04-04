package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/9.
 */

public interface ISbxlView {
    void setShowProgressDialogEnable(boolean enable);

    void showMsgDialog(String msg);

    void refreshXlkwSp(List<String>data,List<String>dataMc);

    void refreshScanList(List<Map<String,String>>data);

    void refreshZsList(List<Map<String,String>>data);

    void showScanDialog(Map<String,String>map,int type);

    void showBlueToothAddressDialog();

    void showPackErrorDialog(String sbbh,String tmbh);

    void setSbEditText(String sbbh);

}
