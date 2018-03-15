package com.rdypda.view.viewinterface;

import android.widget.ExpandableListView;

import java.util.List;

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

    void refreshExpandableListVie(List<String> groupTitles, List<List<String>> titles, List<List<Integer>> imgs, ExpandableListView.OnChildClickListener onChildClickListener);
}
