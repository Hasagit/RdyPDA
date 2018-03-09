package com.rdypda.view.viewinterface;

/**
 * Created by DengJf on 2018/1/12.
 */

public interface IFirstView extends IBaseView{
    void showToastMsg(String msg);


    void setShowDownloadProgressDialogEnable(boolean enable);

    void setProgressDownloadProgressDialog(int size);

    void showMsgDialog(String msg);

    void showDownloadDialog(String url);
}
