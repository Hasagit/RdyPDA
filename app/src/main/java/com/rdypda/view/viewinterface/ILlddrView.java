package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/8.
 */

public interface ILlddrView extends IBaseView{
    void showList(List<Map<String,String>> data);

    void showToast(String msg);

    void setProgressDialogEnable(boolean enable);
}
