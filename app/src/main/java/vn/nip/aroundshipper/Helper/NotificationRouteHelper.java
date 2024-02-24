package vn.nip.aroundshipper.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import vn.nip.aroundshipper.Fragment.AroundWalletFragment;
import vn.nip.aroundshipper.R;

/**
 * Created by HOME on 04/04/2018.
 */

public class NotificationRouteHelper {

    final String WALLET = "ViAround";

    public void route(FragmentActivity activity, Bundle bundle) {
        try {
            String type = bundle.getString("type");
            switch (type) {
                case WALLET:
                    FragmentHelper.addFragment(activity, R.id.home_content, AroundWalletFragment.newInstance());
                    break;
            }
            activity.getIntent().removeExtra("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
