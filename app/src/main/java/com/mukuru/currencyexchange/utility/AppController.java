package com.mukuru.currencyexchange.utility;


import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Build;

public class AppController extends Application {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String TAG = AppController.class
            .getSimpleName();

    private static AppController mInstance;

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = ONE_SECOND * 60;
    private static final int ONE_HOUR = ONE_MINUTE * 60;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void lockOrientation(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void unlockOrientation(Activity act) {
        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
}

