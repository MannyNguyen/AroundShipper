package vn.nip.aroundshipper;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.CustomDialog;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.Dialog.MessageDialogFragment;
import vn.nip.aroundshipper.Helper.HttpHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Interface.ICallback;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    final int ALL_PERMISSION = 1001;
    MessageDialogFragment messageDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        GlobalClass.setActivity(this);
        StorageHelper.init(getApplicationContext());

        if (StorageHelper.getLanguage().equals(StringUtils.EMPTY)) {
            String lang = Locale.getDefault().getLanguage();
            StorageHelper.saveLanguage(lang);
        }

        Locale locale = new Locale(StorageHelper.getLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        routers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CmmVariable.MY_PERMISSION_ACCESS_LOCATION:
            case CmmVariable.MY_PERMISSION_ACCESS_WIFI:
            case ALL_PERMISSION:
                routers();
                break;

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ALL_PERMISSION:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            boolean isPerpermissionForAllGranted = true;
//                            for (int i = 0; i < permissions.length; i++) {
//                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                    isPerpermissionForAllGranted = false;
//                                    break;
//                                }
//                            }
//                            if (!isPerpermissionForAllGranted) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this, "Please turn on all permission in app!", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else {
//                                routers();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                routers();

                break;
        }

    }

    //region Routers
    void routers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //region Check Network
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                    boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnected();
                    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .isConnected();
                    if (!isWifi && !is3g) {
                        openNetwork();
                        return;
                    }


                    //endregion

                    //region Check Location
                    final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        buildAlertMessageNoGps();
                        return;
                    }
                    //endregion

                    //region Check permission
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                                !ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    ALL_PERMISSION);
                        } else {
                            openSetting();
                        }

                        return;
                    }

                    //endregion

                    new ActionGetIP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                } catch (Exception e) {

                }

            }
        }).start();
    }
    //endregion

    //region Methods
    private void openNetwork() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomDialog.Dialog2Button(MainActivity.this,
                        "", getString(R.string.enable_network), "Wifi", "3G",
                        new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_WIFI);
                            }
                        }, new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_WIFI);
                            }
                        }
                );

            }
        });

    }

    private MessageDialogFragment openSetting() {
        messageDialogFragment = MessageDialogFragment.newInstance();
        messageDialogFragment.setMessage("Please turn on permission in app!");
        messageDialogFragment.setRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, ALL_PERMISSION);

                } catch (Exception e) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivityForResult(intent, ALL_PERMISSION);

                }
            }
        });
        messageDialogFragment.show(getSupportFragmentManager(), messageDialogFragment.getClass().getName());
        return messageDialogFragment;
    }


    private void buildAlertMessageNoGps() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomDialog.Dialog2Button(MainActivity.this,
                        "", getString(R.string.enable_location), getString(R.string.setting), getString(R.string.exit),
                        new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_LOCATION);
                            }
                        }, new ICallback() {
                            @Override
                            public void excute() {
                                finishAffinity();
                            }
                        }
                );
            }
        });

    }
    //endregion

    //region Actions
    private class ActionGetAppConfig extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("error_version", StorageHelper.getConfigVersion() + ""));
                String response = HttpHelper.get(CmmVariable.getDomain() + "/shipper/get_app_config", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    Toast.makeText(getApplicationContext(), "Can't get config error", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    JSONObject data = jsonObject.getJSONObject("data");

                    JSONObject googleKey = data.getJSONObject("google_key");
                    CmmVariable.GOOGLE_KEY = googleKey.getString("android_key");

                    JSONObject support = data.getJSONObject("support");
                    CmmVariable.phoneService = support.getString("customer_service_phone");

                    JSONObject error = data.getJSONObject("error");

                    int code = error.getInt("status");
                    if (code == 1) {
                        StorageHelper.saveConfigVersion(error.getInt("version"));
                        StorageHelper.saveContentError(error.getString("content"));
                        StorageHelper.saveContentVnError(error.getString("vn_content"));

                    }
                    if (StorageHelper.getLanguage().equals("vi")) {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentVNError());
                    } else {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentError());
                    }

                    CmmVariable.isFirstApp = true;
                    new SmartFoxHelper().initSmartFox();

                }
            } catch (Exception e) {

            }
        }
    }

    class ActionGetIP extends AsyncTask<Object, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... objects) {
            try {
                String response = HttpHelper.get(CmmVariable.linkServer, null);
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("version").equals(CmmVariable.version)) {
                        CmmVariable.host = jsonObject.getString("restful_ip");
                        CmmVariable.port = jsonObject.getInt("restful_port") + "";
                        CmmVariable.smartFoxIP = jsonObject.getString("smartfox_ip");
                        CmmVariable.smartFoxDomain = jsonObject.getString("smartfox_domain");
                        CmmVariable.getSmartFoxPort = jsonObject.getInt("smartfox_port") + "";
                    }
                }
                return jsonArray;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if (jsonArray != null) {
                try {
                    new ActionGetAppConfig().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //endregion
}
