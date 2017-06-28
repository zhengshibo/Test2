/*Top Secret*/
package com.dollyphin.kidszone.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by feng.shen on 2016/12/6.
 */

public class Welcome extends TextView {
    public Welcome(Context context) {
        this(context,null);
    }

    public Welcome(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Welcome(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
