/*Top Secret*/
package com.dollyphin.kidszone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dollyphin.kidszone.R;

import java.lang.reflect.Field;

/**
 * Created by hong.wang on 2017/2/9.
 */

public class StrokeTextView extends TextView {

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Exterior layer
        setTextColorUseReflection(getResources().getColor(R.color.blue_txt_095));
        getPaint().setStrokeWidth(2);
        getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        getPaint().setFakeBoldText(true);
        getPaint().setShadowLayer(1, 0, 0, 0);
        super.onDraw(canvas);

        // Tracing layer
        setTextColorUseReflection(getResources().getColor(R.color.blue_txt_82f));
        getPaint().setStrokeWidth(0);
        getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        getPaint().setFakeBoldText(false);
        getPaint().setShadowLayer(0, 0, 0, 0);
        super.onDraw(canvas);
    }

    private void setTextColorUseReflection(int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        getPaint().setColor(color);
    }
}
