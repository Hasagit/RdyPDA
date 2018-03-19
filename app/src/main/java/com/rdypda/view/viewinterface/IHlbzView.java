package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/3/5.
 */

public interface IHlbzView {

    void setShowProgressDialogEnable(boolean enable);

    void showMsgDialog(String msg);

    void showMsgToast(String msg);

    void refreshSbmx(List<String> data);

    void refreshBzList(List<Map<String,String>>data);

    void showPrintDialog(Map<String,String>map,String gsdm);

    void showKcDialog(Map<String,String>map,List<String>data,List<String>dataMc,String gsdm);

    void showBlueToothAddressDialog();

    void showReloadHlPackingDialog(String hlbh,String tmxh);

    void showContinPrintDialog(Map<String,String>map,String gsdm,String kw,String bzsl);
}
