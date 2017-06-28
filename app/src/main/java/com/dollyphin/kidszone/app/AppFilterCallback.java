/*Top Secret*/
package com.dollyphin.kidszone.app;

/**
 * Created by hong.wang on 2017/2/17.
 */

public interface AppFilterCallback {
    void addApp(int position, AppInfo info);

    void removeApp(AppInfo info);
}
