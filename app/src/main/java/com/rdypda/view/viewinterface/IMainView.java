package com.rdypda.view.viewinterface;

/**
 * Created by DengJf on 2017/12/8.
 */

public interface IMainView extends IBaseView{
    void setUserName(String userName);

    void showMsgDialog(String msg);

    void setShowProgressDialogEnable(boolean enable);

    void showDownloadDialog(String url);

    void showToastMsg(String msg);

    void setShowDownloadProgressDialogEnable(boolean enable);

    void setProgressDownloadProgressDialog(int size);
}
