package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/30.
 */

public interface IPddyView {

    void setShowMsgDialogEnable(String msg);

    void setShowProgressDialogEnable(boolean enable);


    void printEvent(String qrCode, String wlpmChinese, String wlpmEnlight);

    void queryWlbh(String wlbh);

    void onQueryWlbhSucceed(String[] wldmArr, List<Map<String, String>> wlbhData);

    void getKwData();

    void onGetKwdataSucceed(List<String> dataMc, List<Map<String,String>> data);

    void getBarCode(String wlbh, String scpc, String bzsl, String strDw, Map<String, String> mapKw);

    void onGetBarCodeSucceed(String barCode, String qrCode);

    void showBlueToothAddressDialog();
}
