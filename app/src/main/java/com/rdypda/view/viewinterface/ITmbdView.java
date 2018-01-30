package com.rdypda.view.viewinterface;

/**
 * Created by DengJf on 2018/1/26.
 */

public interface ITmbdView {

    void setShowProgressDialogEnable(boolean enable);

    void setShowMsgDialog(String msg);

    void setTmMsg(String tmxh,String wlbh,String tmsl);

    void showBlueToothAddressDialog();
}
