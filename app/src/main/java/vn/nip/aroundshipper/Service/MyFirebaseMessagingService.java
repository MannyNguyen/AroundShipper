package vn.nip.aroundshipper.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.Dialog.BaseDialogFragment;
import vn.nip.aroundshipper.Fragment.Dialog.MessageDialogFragment;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.MainActivity;
import vn.nip.aroundshipper.R;
import vn.nip.aroundshipper.Util.NotificationUtils;

/**
 * Created by viminh on 5/16/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        try {
            if (remoteMessage == null) {
                return;
            }
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                handleDataMessage(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDataMessage(final JSONObject data) {
        Log.e(TAG, "push json: " + data.toString());
        try {
            String title = data.getString("title");
            final String message = data.getString("body");
//            GlobalClass.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
//                        messageDialogFragment.setMessage(message);
//                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            String type = data.optString("deep_link_type");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            intent.putExtra("type", type); //ViAround
            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
            //inboxStyle.addLine(message);
            //inboxStyle.setSummaryText(message);
            inboxStyle.bigText(message);
            inboxStyle.setBigContentTitle(title);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(inboxStyle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentText(message);

            Notification notification = mBuilder.build();
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(1000), notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
