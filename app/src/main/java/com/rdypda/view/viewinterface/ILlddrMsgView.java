package com.rdypda.view.viewinterface;

/**
 * Created by DengJf on 2017/12/26.
 */

public interface ILlddrMsgView extends IBaseView{
    void showMessage(String message);

    void showBlueToothAddressDialog();

    void setTmxhText(String tmxh);

    void setProgressDialogEnable(String title,boolean enable);
}
