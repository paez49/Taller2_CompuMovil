package com.example.taller2_compumovilr.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;

@Getter
@Module
@InstallIn(ActivityComponent.class)
public class PermissionService {
    private static final String TAG = PermissionService.class.getName();

    static public final int PERMISSIONS_REQUEST_CAMERA = 1001;
    static public final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;

    private boolean mCameraPermissionGranted;
    private boolean mReadExternalStoragePermissionGranted;
    private boolean mContactsPermissionGranted;

    private Context context;

    @Inject
    PermissionService(@ApplicationContext Context context) {
        this.context = context;
        mCameraPermissionGranted = checkPermission(Manifest.permission.CAMERA);
        mReadExternalStoragePermissionGranted = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void getCameraPermission(Activity activity) {
        if(checkPermission(Manifest.permission.CAMERA)){
            mCameraPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    public void getReadExternalStoragePermission(Activity activity) {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            mReadExternalStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private boolean checkPermission(String manifestPermissions) {
        /*
         * Request the permission. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG, "checkPermission: attempting to get permission for ("+manifestPermissions+").");
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), manifestPermissions) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: permission "+manifestPermissions+" is already granted.");
            return true;
        } else {
            Log.d(TAG, "checkPermission: permission ("+manifestPermissions+") not granted, need to request it.");
            return false;
        }
    }
    public boolean isMCameraPermissionGranted(){
        return mCameraPermissionGranted;
    }
}
