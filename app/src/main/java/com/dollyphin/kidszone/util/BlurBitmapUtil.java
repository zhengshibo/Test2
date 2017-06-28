/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v8.renderscript.*;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;

import com.dollyphin.kidszone.application.BaseActivity;

import java.lang.reflect.Method;

public class BlurBitmapUtil {
    private static final boolean IS_PP7F = true;

    public static Bitmap getBlurBackground(Context context, View view) {
        return blurBitmap(context, getDecorView(view), 25f);
    }

    public static Bitmap getBackground(View view) {
        return getDecorView(view);
    }

    private static Bitmap getDecorView(View workspace) {
        workspace.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(workspace.getDrawingCache());
        workspace.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        if (image == null) {
            throw new IllegalStateException("image == null");
        }
        Bitmap outputBitmap = Bitmap.createBitmap(image);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, image);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        blurScript.setRadius(blurRadius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);

        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static Bitmap getBlurBackground(BaseActivity activity, Display display) {
        Bitmap bitmap = updateBackgroundForPanelView(activity, display);
        if (bitmap == null) {
            bitmap = getDecorView(activity);
        }

        return bitmap;
    }

    private static Bitmap getDecorView(BaseActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        Bitmap bitmap = decorView.getDrawingCache();

        if (bitmap.isRecycled()) {
            decorView.setDrawingCacheEnabled(false);
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap saveBmp;
        if (IS_PP7F) {
            saveBmp = Bitmap.createBitmap(bitmap, 0, 0,
                    width - KidsZoneUtil.getNavigationBarHeight(activity.getApplicationContext()), height, null, false);
        } else {
            saveBmp = Bitmap.createBitmap(bitmap, 0, 0,
                    width, height - KidsZoneUtil.getNavigationBarHeight(activity.getApplicationContext()), null, false);
        }
        return saveBmp;
    }

    private static float getDegreesForRotation(int value) {
        switch (value) {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    private static Bitmap updateBackgroundForPanelView(BaseActivity activity, Display display) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        float[] dims = {displayMetrics.widthPixels, displayMetrics.heightPixels};
        float degrees = getDegreesForRotation(display.getRotation());
        boolean requiresRotation = (degrees > 0);
        Matrix mDisplayMatrix = new Matrix();
        if (requiresRotation) {
            mDisplayMatrix.reset();
            mDisplayMatrix.preRotate(-degrees);
            mDisplayMatrix.mapPoints(dims);
            dims[0] = Math.abs(dims[0]);
            dims[1] = Math.abs(dims[1]);
        }
        Bitmap screenShotBitmap = null;
        Class<?> demo = null;
        try {
            demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getMethod("screenshot", new Class[]{int.class, int.class});
            screenShotBitmap = (Bitmap) method.invoke(demo, new Object[]{(int) dims[0], (int) dims[1]});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (screenShotBitmap == null) {
            return null;
        }
        if (requiresRotation) {
            Bitmap ss = Bitmap.createBitmap(displayMetrics.widthPixels,
                    displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(ss);
            c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
            c.rotate(degrees);
            c.translate(-dims[0] / 2, -dims[1] / 2);
            c.drawBitmap(screenShotBitmap, 0, 0, null);
            c.setBitmap(null);
            screenShotBitmap.recycle();
            screenShotBitmap = ss;
        }
        screenShotBitmap.setHasAlpha(false);
        screenShotBitmap.prepareToDraw();

        int width = screenShotBitmap.getWidth();
        int height = screenShotBitmap.getHeight();

        Bitmap saveBmp;
        if (IS_PP7F) {
            saveBmp = Bitmap.createBitmap(screenShotBitmap, 0, 0,
                    width - KidsZoneUtil.getNavigationBarHeight(activity.getApplicationContext()), height, null, false);
        } else {
            saveBmp = Bitmap.createBitmap(screenShotBitmap, 0, 0,
                    width, height - KidsZoneUtil.getNavigationBarHeight(activity.getApplicationContext()), null, false);
        }
        return saveBmp;
    }
}
