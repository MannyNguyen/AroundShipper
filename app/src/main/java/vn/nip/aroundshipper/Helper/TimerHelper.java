package vn.nip.aroundshipper.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import vn.nip.aroundshipper.Bean.BeanUser;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

/**
 * Created by viminh on 12/13/2016.
 */

public class TimerHelper {
    public static int pingTime;
    public static int responseShipperTimeout;
    public static int updatePositionTimeInJourney;
    public static int updatePositionTimeOutJourney;

    public static AlertDialog dialogTimeout;

}
