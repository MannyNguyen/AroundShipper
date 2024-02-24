package vn.nip.aroundshipper.Util;

import android.content.Context;
import android.content.Intent;

public class NotificationUtils {
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static final int NOTIFICATION_ID = 100;

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message) {
        showNotificationMessage(title, message);
    }
}
