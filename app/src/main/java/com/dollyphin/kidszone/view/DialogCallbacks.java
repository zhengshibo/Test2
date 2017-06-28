/*Top Secret*/
package com.dollyphin.kidszone.view;

/**
 * Created by feng.shen on 2016/12/17.
 */

public interface DialogCallbacks {
    void setEnable(boolean back, boolean next);

    void updateNext(boolean next);

    void change(int page);

    void close();

    void closeApplication();

    void check();
}
