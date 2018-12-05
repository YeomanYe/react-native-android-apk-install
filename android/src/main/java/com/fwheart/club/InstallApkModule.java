package com.fwheart.club;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;

public class InstallApkModule extends ReactContextBaseJavaModule {
    private String TAG = "InstallApk";
    private final int INSTALL_REQ_CODE = 0x99;
    private Promise promise;

    ReactApplicationContext rContext;

    public InstallApkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        rContext = reactContext;
        rContext.addActivityEventListener(activityEventListener);
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            //只要返回到当前页面，就说明安装失败了
            if (requestCode == INSTALL_REQ_CODE) {
                WritableMap map = Arguments.createMap();
                map.putInt("code",500);
                map.putString("msg","install fail");
                promise.resolve(map);
            }
        }
    };


    @ReactMethod
    private void install(String filePath,Promise p) {
        promise = p;
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            FileProvider7.setIntentDataAndType(rContext,intent,"application/vnd.android.package-archive",apkFile,true);
            /*Uri contentUri = FileProvider.getUriForFile(
                    rContext
                    , "com.madecare.mcloud.mobile.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");*/
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        getCurrentActivity().startActivityForResult(intent, INSTALL_REQ_CODE);
    }

    @Override
    public String getName() {
        return TAG;
    }
}
