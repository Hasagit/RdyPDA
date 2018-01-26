package com.rdypda.view.viewinterface;

/**
 * Created by DengJf on 2018/1/25.
 */

public interface ITmcfView{
    void setShowMsgDialogEnable(String msg,boolean enable);

    void setShowProgressDialogEnable(boolean enable);

    void setOldCodeMsg(String ytms,String cftm);

    void setNewCodeMsg(String cfmx,String xtmxh);

    void showBlueToothAddressDialog();
}
