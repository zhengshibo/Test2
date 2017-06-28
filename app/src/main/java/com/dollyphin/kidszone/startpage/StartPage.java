/*Top Secret*/
package com.dollyphin.kidszone.startpage;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dollyphin.kidszone.application.BaseActivity;
import com.dollyphin.kidszone.util.KidsZoneUtil;

/**
 * Created by feng.shen on 2017/2/22.
 */

public class StartPage extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KidsZoneUtil.inKidsZoneReceiver(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        startHome(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.dollyphin.kidszone", "com.dollyphin.kidszone.home.KidsZoneHome");
                intent.setComponent(componentName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startHome(Runnable andThen) {
        if (andThen != null) {
            new Handler().postDelayed(andThen, 500);
        }
    }
}
