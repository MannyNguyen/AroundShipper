package vn.nip.aroundshipper.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import vn.nip.aroundshipper.Helper.SmartFoxHelper;

import static vn.nip.aroundshipper.Helper.SmartFoxHelper.updateDeviceToken;


/**
 * Created by viminh on 5/16/2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        updateDeviceToken(refreshedToken);
    }
}
