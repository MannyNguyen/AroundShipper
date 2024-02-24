package vn.nip.aroundshipper.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import vn.nip.aroundshipper.Bean.BeanShipper;
import vn.nip.aroundshipper.Class.CmmFunc;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by viminh on 10/24/2016.
 */

public class StorageHelper {

    private static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void init(Context context) {
        String PREF_FILE_NAME = "Around_shipper";
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    }

    public static void set(String key, String value) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public static String get(String key) {
        return preferences.getString(key, "");

    }

    public static void saveShipper() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone", BeanShipper.getCurrent().getShipper_phone());
            editor.putString("password", BeanShipper.getCurrent().getPassword());
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveUser", e.getMessage());
        }
    }

    public static void resetShipper() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            BeanShipper.setCurrent(new BeanShipper());
            editor.putString("phone", "");
            editor.putString("password", "");
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveUser", e.getMessage());
        }
    }

    public static String getPhone() {
        return preferences.getString("phone", "");
    }

    public static void resetPhone() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone", "");
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "setPhone", e.getMessage());
        }
    }

    public static String getPassword() {
        return preferences.getString("password", "");
    }


    public static void saveConfigVersion(int version) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("config_version", version);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "config_version", e.getMessage());
        }
    }

    public static void saveContentError(String content) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("content_error", content);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "content_error", e.getMessage());
        }
    }

    public static String getContentError() {
        return preferences.getString("content_error", "");
    }

    public static String getContentVNError() {
        return preferences.getString("content_vn_error", "");
    }

    public static void saveContentVnError(String content) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("content_vn_error", content);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "content_vn_error", e.getMessage());
        }
    }

    public static String getLanguage() {
        return preferences.getString("language", "vi");
    }

    public static void saveLanguage(String counryCode) {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", counryCode);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveCountryCode", e.getMessage());
        }
    }

    public static int getConfigVersion() {
        return preferences.getInt("config_version", 0);
    }
}
