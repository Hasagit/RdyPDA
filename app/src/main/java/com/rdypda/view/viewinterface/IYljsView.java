package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/9.
 */

public interface IYljsView extends IBaseView{
    void initKwSpinner(List<Map<String,String>>data);

    void initCwSpinner(List<Map<String,String>>data);

    void setShowProgressDialogEnable(Boolean enable);

    void setErrorMsg(String msg);
}
