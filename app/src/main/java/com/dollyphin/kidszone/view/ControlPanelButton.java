/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by feng.shen on 2016/12/12.
 */

public class ControlPanelButton extends ImageView {
    public ControlPanelButton(Context context) {
        this(context, null);
    }

    public ControlPanelButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlPanelButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
