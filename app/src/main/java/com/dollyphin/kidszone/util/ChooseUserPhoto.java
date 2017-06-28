/*Top Secret*/
package com.dollyphin.kidszone.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dollyphin.kidszone.R;
import com.dollyphin.kidszone.application.BaseActivity;

import java.io.File;

/**
 * Created by hong.wang on 2016/11/30.
 */
public class ChooseUserPhoto extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private final int TO_TAKE_PHOTO = 1 << 2;
    private final int TO_GALLERY = 1 << 3;
    private final int CROP_PHOTO = 1 << 4;
    private View mViewContent;
    private boolean isEnableFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mContent = LayoutInflater.from(this).inflate(R.layout.activity_choose_img, null);
        setContentView(mContent);
        mContent.setOnClickListener(this);
        ListView mList = (ListView) findViewById(R.id.choose_header);
        mViewContent = findViewById(R.id.choose_content);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.choose_item);
        mList.setAdapter(adapter);
        adapter.addAll(getResources().getStringArray(R.array.check_photo));
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isEnableFinish = false;
        Intent intent = null;
        mViewContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewContent.setVisibility(View.GONE);
            }
        }, 500);
        switch (position) {
            case 0:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                intent.putExtra("return-data", true);
                startActivityForResult(intent, TO_TAKE_PHOTO);
                break;
            case 1:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, TO_GALLERY);
                break;
            default:
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "click not to do");
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isEnableFinish = true;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_TAKE_PHOTO:
                    cropPhotos(getImageUri());
                    break;
                case TO_GALLERY:
                    cropPhotos(getImgPath(data.getData()));
                    break;
                case CROP_PHOTO:
                    try {
                        Uri uri = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(uri));
                        KidsZoneUtil.saveFile(bitmap);
                        Intent intent = getIntent();
                        intent.setData(uri);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "onActivityResult: e" + Log.getStackTraceString(e));
                    }
                    break;
                default:
                    break;
            }
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri getImgPath(Uri uri) {
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getImgPath: uri  == 000 == " + uri);
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            if (path != null) {
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getImgPath: path  == 000 == " + path);
                path = Uri.decode(path);
                KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getImgPath: path  == 111 == " + path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(")
                        .append(MediaStore.Images.ImageColumns.DATA)
                        .append("=")
                        .append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, path);
                    uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        KidsZoneLog.d(KidsZoneLog.KIDS_CONTROL_DEBUG, "getImgPath: uri  == 111 == " + uri);
        return uri;
    }

    private void cropPhotos(Uri imageUri) {
        if (imageUri == null) {
            KidsZoneLog.e(KidsZoneLog.KIDS_CONTROL_DEBUG, "cropPhotos:imageUri is null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        startActivityForResult(intent, CROP_PHOTO);
    }


    private Uri getImageUri() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + KidsZoneUtil.HEAD_PHOTOS_PATH;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(new File(path, KidsZoneUtil.IMAGE_FILE_NAME));
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, dirFile.getAbsolutePath());
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        return getImgPath(Uri.fromFile(new File(path, KidsZoneUtil.IMAGE_FILE_NAME)));
    }

    @Override
    public void onClick(View v) {
        if (!isEnableFinish) return;
        finish();
    }
}
