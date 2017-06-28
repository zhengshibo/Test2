/*Top Secret*/
package com.dollyphin.kidszone.guide;

/**
 * Created by feng.shen on 2016/12/17.
 */

public interface GuideCallbacks {
    void change(int page);

    void exit();

    void close();

    void check();

    void show(String date);

    void complete();

    void setNextEnable(boolean enable);
}
