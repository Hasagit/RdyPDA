package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/10.
 */

public interface IHlView extends IBaseView{

    void setShowProgressDialogEnable(boolean enable);

    void showMsgDialog(String msg);

    void refreshSblb(List<String> data);

    void refreshSbmx(List<String> data);

    void showTmMsgDialog(String hljh,String tmxh,String wlbh,String wlgg,String tmsl);

    void refreshScanedList(List<Map<String,String>>data);

    void refreshHlList(List<Map<String,String>>data);


}
