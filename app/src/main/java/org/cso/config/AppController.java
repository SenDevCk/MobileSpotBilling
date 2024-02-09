package org.cso.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Locale;


public class AppController extends Application {

    public static final String TAG = "LOC_BROADCASTER";
    static SharedPreferences sharedpreferences;
    public static final String MYPREFERENCES = "MyPrefs";
    public static final String SELECTLANGUAGE = "language";
    private static Context context;
    private static AppController mInstance;
    private RequestQueue mRequestQueue;




    public static void changeLocale() {
        sharedpreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String language_code=sharedpreferences.getString(SELECTLANGUAGE, "en");
        Resources res = getAppContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language_code.toLowerCase());
        res.updateConfiguration(conf, dm);

    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppController.context = getApplicationContext();
        registerActivityLifecycleCallbacks(new LbaActivityLifecycleCallbacks());
        sharedpreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        String language_code=sharedpreferences.getString(SELECTLANGUAGE, "en");
        editor.putString(SELECTLANGUAGE,language_code);
        editor.commit();
        changeLocale();

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private static final class LbaActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity, Bundle bundle) {
            Log.e(TAG, "onActivityCreated:" + activity.getLocalClassName());
        }

        public void onActivityDestroyed(Activity activity) {
            Log.e(TAG, "onActivityDestroyed:" + activity.getLocalClassName());
        }

        public void onActivityPaused(Activity activity) {
            Log.e(TAG, "onActivityPaused:" + activity.getLocalClassName());
        }

        public void onActivityResumed(Activity activity) {
            Log.e(TAG, "onActivityResumed:" + activity.getLocalClassName());
        }

        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.e(TAG, "onActivitySaveInstanceState:" + activity.getLocalClassName());
        }

        public void onActivityStarted(Activity activity) {
            Log.e(TAG, "onActivityStarted:" + activity.getLocalClassName());
        }

        public void onActivityStopped(Activity activity) {
            Log.e(TAG, "onActivityStopped:" + activity.getLocalClassName());
        }
    }

}
