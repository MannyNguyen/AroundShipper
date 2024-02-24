package vn.nip.aroundshipper.Class;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.FirebaseApp;

/**
 * Created by viminh on 11/4/2016.
 */

public class GlobalClass extends MultiDexApplication {
    private static Context mContext;
    private static FragmentActivity activity;


    public static FragmentActivity getActivity() {
        return activity;
    }

    public static void setActivity(FragmentActivity activity) {
        GlobalClass.activity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(this);
    }
    public static Context getContext() {
        return mContext;
    }

}
