package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2018/1/12.
 */

public interface ILoginView extends IBaseView{
    void showFactoryList(List<Map<String,String>>data,int position);

    void showToastMsg(String msg);

    void setShowProgressDialogEnable(boolean enable);

    void setUserIdErrorEnable(boolean enable);

    void setUserIdError(String error);

    void setPwdErrorEnable(boolean enable);

    void setPwdError(String error);

    void setDefaultUser(String userId,String userPwd);
}
