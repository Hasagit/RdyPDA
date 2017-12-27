package com.rdypda.view.viewinterface;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/12/8.
 */

public interface ILlddrView {
    void showList(List<Map<String,String>> data);

    void setProgressBarVisibility(int visibility);

    void setSwipeVisibility(int visibility);

    void setRefreshing(boolean refreshing);
}
