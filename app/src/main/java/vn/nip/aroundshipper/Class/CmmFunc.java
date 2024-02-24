package vn.nip.aroundshipper.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by viminh on 10/4/2016.
 */

public class CmmFunc {

    //region Create bitmap from url
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    //endregion

    //region Show error
    public static void showError(String className, String funcName, String message) {
        Log.e(className, "ViMT - " + funcName + ": " + message);
    }
    //endregion

    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    //endregion

    //region Convert met to kilomet
    public static String metToKilomet(Double value) {
        try {
            Double retValue = value * 0.001;
            if (value == 0)
                return "0";
            return new DecimalFormat("#.0").format(retValue);
        } catch (Exception e) {
            return null;
        }
    }
    //endregion

    //region Get current fragment
    public static Fragment getActiveFragment(FragmentActivity activity) {
        try {
            activity.getSupportFragmentManager().executePendingTransactions();
            if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return null;
            }
            String tag = activity.getSupportFragmentManager().getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            return activity.getSupportFragmentManager().findFragmentByTag(tag);
        } catch (Exception e) {
            return null;
        }
    }
    //endregion

    //region Get fragment by Tag
    public static Fragment getFragmentByTag(FragmentActivity activity, String tag) {
        try {
            activity.getSupportFragmentManager().executePendingTransactions();
            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
            return fragment;
        } catch (Exception e) {
            CmmFunc.showError("CmmFunc", "getFragmentByTag", e.getMessage());
            return null;
        }
    }
    //endregion

    //region Parse Object thành json string
    public static String tryParseObject(Object object) {
        if (object == null)
            return null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(object, Object.class);
            return json;
        } catch (Exception ex) {

        }
        return null;
    }

    //region Parse string json thành Object
    @Nullable
    public static Object tryParseJson(String jsonString, Class<?> clazz) {
        Object obj = null;
        if (jsonString == null || jsonString == "")
            return null;
        try {
            Gson gson = new Gson();
            obj = gson.fromJson(jsonString, clazz);

        } catch (Exception ex) {
        }
        return obj;
    }
    //endregion

    //region Delay sau delta time
    public static void setDelay(int deltaTime, final ICallback callback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                callback.excute();
                return;
            }
        }, deltaTime);
    }


    //region Lấy Bitmap từ Byte Array
    public static Bitmap getBitmapFromByteArray(byte[] value) {
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length);
            Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
            return mutableBitmap;
        } catch (Exception e) {
            showError("CmmFunc", "getBitmapFromByteArray", e.getMessage());
        }
        return null;
    }
    //endregion

    //region Bitmap to byte array
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e) {
            CmmFunc.showError("CmmFunc", "bitmapToByteArray", e.getMessage());
        }
        return null;
    }
//endregion

    //region Giảm kích thước Bitmap
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        if (bitmap == null)
            return null;
        try {
            int actualWidth = bitmap.getWidth();
            int actualHeight = bitmap.getHeight();
            float rate = 1;
            if (actualHeight > maxSize || actualWidth > maxSize) {
                if (actualWidth > actualHeight) {
                    rate = (float) maxSize / actualWidth;
                } else {
                    rate = (float) maxSize / actualHeight;
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * rate), Math.round(bitmap.getHeight() * rate), false);
                return bitmap;
            } else {
                return bitmap;
            }

        } catch (Exception e) {
            CmmFunc.showError("CmmFunc", "bitmapToByteArray", e.getMessage());
        }
        return null;
    }
    //endregion

    //region MD5
    public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            showError("CmmFunc", "encryptMD5", e.getMessage());
        }
        return null;
    }
    //endregion

    //region Parse String json thành List
    public static List<?> tryParseList(String jsonString, Class<?> clazz) {
        List<?> retValue = null;

        if (TextUtils.isEmpty(jsonString))
            return null;
        JSONArray jArr;
        List<Object> objs = new ArrayList<>();
        try {
            jArr = new JSONArray(jsonString);
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject row = jArr.getJSONObject(i);
                Object obj = null;
                if (clazz.getName().contains("JSONObject")) {
                    obj = row;
                } else {
                    obj = CmmFunc.tryParseJson(row.toString(), clazz);
                }
                objs.add(obj);
            }

            retValue = objs;
        } catch (Exception e) {

        }


        return retValue;
    }
    //endregion

    //region Format money
    public static String formatMoney(Object value) {
        String str = value + "";
        try {
            for (int i = str.length() - 3; i > 0; i -= 3) {
                str = new StringBuilder(str).insert(i, ",").toString();
            }
            return str + "đ";
            //return String.format("%,.0f", Double.parseDouble(value + "")) + "đ";

        } catch (Exception e) {
            showError("CmmFunc", "formatNumber", e.getMessage());
        }
        return "";
    }

    public static String formatDotMoney(Object value){
        String str = value + "";
        try {
            for (int i = str.length() - 3; i > 0; i -= 3) {
                str = new StringBuilder(str).insert(i, ".").toString();
            }
            return str + "đ";
        } catch (Exception e) {
            showError("CmmFunc", "formatNumber", e.getMessage());
        }
        return "";
    }

    public static String formatMoney(Object value, boolean isPrefix) {
        String str = value + "";
        try {
            for (int i = str.length() - 3; i > 0; i -= 3) {
                str = new StringBuilder(str).insert(i, ",").toString();
            }
            if (isPrefix) {
                str = str + "đ";
            }
            return str;

        } catch (Exception e) {
            showError("CmmFunc", "formatNumber", e.getMessage());
        }
        return "";
    }
    //endregion

    //region format time
    public static String formatTime(Double value) {
        String retValue = "";
        try {

        } catch (Exception e) {

        }
        return retValue;
    }
    //endregion

    //region Hide keyboard
    public static void hideKeyboard(Activity activity) {
        try {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            View v = GlobalClass.getActivity().getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) GlobalClass.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Dp to px
    public static int convertDpToPx(Activity activity, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics()));
    }
    //endregion

    //region Device ID
    public static String getDeviceID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }
    //endregion

    //region Format date
    public static JSONObject formatDate(LocalDateTime dateTime) {
        try {
            int year = dateTime.getYear();
            int month = dateTime.getMonthOfYear();
            int day = dateTime.getDayOfMonth();
            int hour = dateTime.getHourOfDay();
            int minute = dateTime.getMinuteOfHour();
            //DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
            //LocalDateTime formatDateTime;
            int newMinute = Math.round(minute / 15) * 15;
            //Số dư
            int molo = minute % 15;
            SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = dtf.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + newMinute);
            long mili = date.getTime();
            if (molo != 0) {
                mili = date.getTime() + 900000;
            }
            Date newDate = new Date(mili);
            LocalDateTime localDateTime = new LocalDateTime(newDate);
            String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            String[] monthNames = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            if (StorageHelper.getLanguage().equals("vi")) {
                dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
                monthNames = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            }
            int h = localDateTime.getHourOfDay();
            String ampm = "AM";
            if (h == 0) {
                h = 12;
                ampm = "AM";
            } else if (h == 12) {
                h = 12;
                ampm = "PM";
            } else if (h > 12) {
                h = h - 12;
                ampm = "PM";
            } else {
                ampm = "AM";
            }
            String value = StringUtils.leftPad(h + "", 2, "0") + ":" + StringUtils.leftPad(localDateTime.getMinuteOfHour() + "", 2, "0") + " " + ampm + " " +
                    dayNames[localDateTime.getDayOfWeek() - 1] + ", " + StringUtils.leftPad(localDateTime.getDayOfMonth() + "", 2, "0") + "/" + monthNames[localDateTime.getMonthOfYear() - 1] + "/" + localDateTime.getYear();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("year", localDateTime.getYear());
            jsonObject.put("month", localDateTime.getMonthOfYear());
            jsonObject.put("day", localDateTime.getDayOfMonth());
            jsonObject.put("hour", localDateTime.getHourOfDay());
            jsonObject.put("minute", localDateTime.getMinuteOfHour());
            jsonObject.put("value", value);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject formatDate(LocalDateTime dateTime, boolean isFormat) {
        try {
            LocalDateTime localDateTime;

            if (isFormat) {
                int year = dateTime.getYear();
                int month = dateTime.getMonthOfYear();
                int day = dateTime.getDayOfMonth();
                int hour = dateTime.getHourOfDay();
                int minute = dateTime.getMinuteOfHour();
                //DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
                //LocalDateTime formatDateTime;
                int newMinute = Math.round(minute / 15) * 15;
                SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = dtf.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + newMinute);
                long mili = date.getTime() + 900000;
                Date newDate = new Date(mili);
                localDateTime = new LocalDateTime(newDate);
            } else {
                localDateTime = dateTime;
            }

            String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            String[] monthNames = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            if (StorageHelper.getLanguage().equals("vi")) {
                dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
                monthNames = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            }
            int h = localDateTime.getHourOfDay();
            String ampm = "AM";
            if (h == 0) {
                h = 12;
                ampm = "AM";
            } else if (h == 12) {
                h = 12;
                ampm = "PM";
            } else if (h > 12) {
                h = h - 12;
                ampm = "PM";
            } else {
                ampm = "AM";
            }
            String value = StringUtils.leftPad(h + "", 2, "0") + ":" + StringUtils.leftPad(localDateTime.getMinuteOfHour() + "", 2, "0") + " " + ampm + " " +
                    dayNames[localDateTime.getDayOfWeek() - 1] + ", " + StringUtils.leftPad(localDateTime.getDayOfMonth() + "", 2, "0") + "/" + monthNames[localDateTime.getMonthOfYear() - 1] + "/" + localDateTime.getYear();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("year", localDateTime.getYear());
            jsonObject.put("month", localDateTime.getMonthOfYear());
            jsonObject.put("day", localDateTime.getDayOfMonth());
            jsonObject.put("hour", localDateTime.getHourOfDay());
            jsonObject.put("minute", localDateTime.getMinuteOfHour());
            jsonObject.put("value", value);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region getPathFromUri
    public static String getPathFromUri(final Context context, final Uri uri) {

        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                    // ExternalStorageProvider
                    if (isExternalStorageDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        }

                        // TODO handle non-primary volumes
                    }
                    // DownloadsProvider
                    else if (isDownloadsDocument(uri)) {

                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                        return getDataColumn(context, contentUri, null, null);
                    }
                    // MediaProvider
                    else if (isMediaDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{
                                split[1]
                        };

                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }
                // MediaStore (and general)
                else if ("content".equalsIgnoreCase(uri.getScheme())) {

                    // Return the remote address
                    if (isGooglePhotosUri(uri))
                        return uri.getLastPathSegment();

                    return getDataColumn(context, uri, null, null);
                }
                // File
                else if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    //endregion
}
