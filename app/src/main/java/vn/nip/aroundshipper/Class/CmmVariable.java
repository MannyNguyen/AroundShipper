package vn.nip.aroundshipper.Class;

import com.google.android.gms.maps.GoogleMap;

import vn.nip.aroundshipper.Bean.BeanChat;
import vn.nip.aroundshipper.Bean.BeanPoint;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viminh on 10/11/2016.
 */

public class CmmVariable {

    //public static String linkServer = "http://config.around.vn/android/info.json";
    public static String linkServer = "http://config.around.vn/dev/info.json";

    //Lần đầu mở app
    public static boolean isFirstApp = true;
    public static boolean IS_PROGRESS = false;

    public static final String version = "1.0.3";
    //code request
    public static final int MY_PERMISSION_ACCESS_LOCATION = 104;
    public static final int MY_PERMISSION_ACCESS_WIFI = 105;
    public static String GOOGLE_KEY = "";
    //Thời gian update lại position
    public static int TIMER_UPDATE_POSITION = 10000;

    //Server
    public static String host;
    public static String port;
    public static String smartFoxIP;
    public static String smartFoxDomain;
    public static String getSmartFoxPort;

    public static List<BeanPoint> points = new ArrayList<>();
    public static List<BeanChat> chats = new ArrayList<>();
    public static int numberChat = 0;
    //timeout
    public static int timeout = 30;
    //Place hien tai khi chon tu google
    public static String room;
    //public static String idRequest;

    public static JSONArray jsonError;
    //Avatar
    public static String avatarShipper;
    public static String avatarUser;

    public static String getDomain() {
        return host + ":" + port;
    }

    public static String phoneService;

    public static String giftBoxFee;

    public static final int IMAGE_RESIZE_COMMON = 512;
}
