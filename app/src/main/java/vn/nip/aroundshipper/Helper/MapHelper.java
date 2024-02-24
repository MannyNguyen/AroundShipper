package vn.nip.aroundshipper.Helper;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.HomeActivity;
import vn.nip.aroundshipper.Interface.LatLngInterpolator;
import vn.nip.aroundshipper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by viminh on 10/7/2016.
 */

public class MapHelper {

    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";

    //region Check location permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static List<MarkerOptions> markers = new ArrayList<>();
    //public static GoogleMap map;

    //endregion

    //region Lấy vị trí hiện tại
    public static BeanPoint getCurrentPosition(Activity activity) {
        BeanPoint retValue = null;
        Location location = null;
        try {

            LocationManager locationManager = (LocationManager) GlobalClass.getContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            for (String provider : providers) {

                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l;
                }
            }
            retValue = new BeanPoint();
            retValue.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            retValue.setLatitude(location.getLatitude());
            retValue.setLongitude(location.getLongitude());
            //retValue.setPlaceid(new GetPlaceIDByLatLng().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location.getLatitude(), location.getLongitude()).get());
        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "getCurrentPosition", e.getMessage());
        }
        return retValue;
    }

    //endregion

    //region Get Place ID
    private static String getPlaceIDByLatLng(Double lat, Double lng) {
        String retValue = null;
        try {
            Double latitude = lat;
            Double longitude = lng;
            String key = "key=" + CmmVariable.GOOGLE_KEY;
            String parameters = latitude + "," + lng + "&" + "&" + key;
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + parameters;
            String data = downloadUrl(url);
            JSONArray jsonData = new JSONArray(new JSONObject(data).getString("results"));

            JSONObject jObject = jsonData.getJSONObject(0);
            retValue = jObject.getString("place_id");
        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "getPlaceIDByLatLng", e.getMessage());
        }
        return retValue;
    }
    //endregion

    //region Lấy URL từ start - end
    public static String getUrl(LatLng origin, LatLng dest) {
        String url = null;
        try {

            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            String sensor = "sensor=false";
            String key = "key=" + CmmVariable.GOOGLE_KEY;
            String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;
            String output = "json";
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        } catch (Exception e) {
            //Log.e(getClass().getName(), "ViMT - getUrl: " + e.getMessage());
        }
        return url;
    }
    //endregion

    //region Lấy data từ URL
    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            //Log.d(getClass().getName(), "ViMT - downloadUrl: " + e.getMessage());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static String downloadUrl(LatLng start, LatLng end) {
        String data = null;
        try {
            data = new DowloadURL(start, end).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "downloadUrl", e.getMessage());
        }
        return data;
    }
    //endregion

    //region Nhận Json Object trả về danh sách LatLog
    public static List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //Log.e(getClass().getName(), "ViMT - parse: " + e.getMessage());
        }
        return routes;
    }
    //endregion

    //region Decode & draw
    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        try {
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        } catch (Exception e) {
            //Log.e(getClass().getName(), "ViMT - decodePoly: " + e.getMessage());
        }
        return poly;
    }

    //region get distance
    public static String getDistance(JSONObject jsonObject) {
        String retValue = "";
        try {
            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            retValue = distance.getString("text");
        } catch (Exception e) {

        }

        return retValue;
    }
    //endregion

    public static Double getValueDistance(JSONObject jsonObject) {
        double retValue = 0;
        try {
            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            retValue = distance.getDouble("value");
            return retValue;
        } catch (Exception e) {
        }
        return retValue;
    }


    public static boolean checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // getActivity() thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    //endregion

    //region Lấy địa chỉ dựa vào LatLong
    public static String getAddressByLatLong(Activity activity, LatLng latLng) throws IOException {
        String address = null;
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=" + CmmVariable.GOOGLE_KEY);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String data = sb.toString();
            br.close();

            JSONArray jsonArray = new JSONArray(new JSONObject(data).getString("results"));
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            address = jsonObject.getString("formatted_address");
            iStream.close();
            urlConnection.disconnect();
        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "getAddressByLatLong", e.getMessage());
        } finally {

        }
        return address;
    }

    //endregion

    //region Đưa camera về giữa các điểm


    public static void setCenter(final Activity activity, final GoogleMap map, final List<BeanPoint> points) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    int count = 0;
                    for (final BeanPoint bean : points) {
                        float offsetY = 1f;
                        for (int i = 0; i < points.indexOf(bean); i++) {
                            if (bean.getLatLng() != null) {
                                if (bean.getLatLng().equals(points.get(i).getLatLng())) {
                                    offsetY += 0.5f;
                                }
                            }

                        }
                        if (bean.getLatLng() != null) {
                            final float finalOffsetY = offsetY;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapHelper.addPoint(map, bean,points.indexOf(bean), false, finalOffsetY);
                                }
                            });

                            builder.include(bean.getLatLng());
                            count += 1;
                        }
                    }
                    if (count == 1) {
                        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points.get(0).getLatLng(), 17.0f);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                map.moveCamera(cameraUpdate);
                            }
                        });
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LatLngBounds bounds = builder.build();
                            int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 80);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, HomeActivity.WINDOW_WIDTH, HomeActivity.WINDOW_WIDTH, padding);
                            map.moveCamera(cameraUpdate);
                        }
                    });



                } catch (Exception e) {
                    CmmFunc.showError("MapHelper", "animateCenter", e.getMessage());
                }
            }
        }).start();

    }
    public static void animateCenter(final Activity activity, final GoogleMap map, final List<BeanPoint> points, final int pad) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    int count = 0;
                    for (final BeanPoint bean : points) {
                        float offsetY = 1f;
                        for (int i = 0; i < points.indexOf(bean); i++) {
                            if (bean.getLatLng() != null) {
                                if (bean.getLatLng().equals(points.get(i).getLatLng())) {
                                    offsetY += 0.5f;
                                }
                            }

                        }
                        if (bean.getLatLng() != null) {
                            final float finalOffsetY = offsetY;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapHelper.addPoint(map, bean,points.indexOf(bean), false, finalOffsetY);
                                }
                            });

                            builder.include(bean.getLatLng());
                            count += 1;
                        }
                    }
                    if (count == 1) {
                        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(points.get(0).getLatLng(), 17.0f);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                map.moveCamera(cameraUpdate);
                            }
                        });
                        return;
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 80);
                    final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, HomeActivity.WINDOW_WIDTH, HomeActivity.WINDOW_WIDTH, padding);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            map.animateCamera(cameraUpdate);
                        }
                    });


                } catch (Exception e) {
                    CmmFunc.showError("MapHelper", "animateCenter", e.getMessage());
                }
            }
        }).start();

    }

    public static void animateCenter(GoogleMap map, LatLng latLng) {
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng) // Sets the center of the map to
                    .zoom(17f)                   // Sets the zoom
                    .bearing(0) // Sets the orientation of the camera to east
                    //.tilt(30)    // Sets the tilt of the camera to 30 degrees
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);
        } catch (Exception e) {

        }
    }

    public static void setCenter(GoogleMap map, LatLng latLng) {
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng) // Sets the center of the map to
                    .zoom(17f)                   // Sets the zoom
                    .bearing(0) // Sets the orientation of the camera to east
                    //.tilt(30)    // Sets the tilt of the camera to 30 degrees
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        } catch (Exception e) {

        }
    }
    //endregion

    //region Thêm điểm vào map và set Camera center
    public static void addPoint(GoogleMap map, BeanPoint bean,int index, boolean setCenter, float offsetY) {
        try {
            boolean pinCenter = false;
            BitmapDescriptor icon = null;
            switch (bean.getPickup_type()) {

                case BeanPoint.PURCHASE:
                case BeanPoint.TRANSPORT:
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(R.layout.marker_pickup, (index + 1) + "", bean.getAddress()));
                    offsetY = offsetY - 0.15f;
                    break;
                case BeanPoint.COD:
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(R.layout.marker_pickup, (index) + "", bean.getAddress()));
                    offsetY = offsetY - 0.15f;
                    break;
                case BeanPoint.DROP:
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(R.layout.marker_drop, "", bean.getAddress()));
                    break;
                //SHOP
                case BeanPoint.SHOP:
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(R.layout.marker_shop, "", bean.getAddress()));
                    offsetY = offsetY - 0.15f;
                    break;
                default:
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                    break;
            }


            MarkerOptions markerOptions = null;
            if (pinCenter) {
                markerOptions = new MarkerOptions().position(bean.getLatLng())
                        //.title(bean.getAddress())
                        .anchor(0.5f, 0.5f)
                        .flat(false)
                        .icon(icon);
            } else {
                markerOptions = new MarkerOptions().position(bean.getLatLng())
                        //.title(bean.getAddress())
                        .anchor(0.5f, offsetY)
                        .flat(false)
                        .icon(icon);
            }

            if (setCenter) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bean.getLatLng())
                        .zoom(17f)
                        .bearing(0)
                        .tilt(0)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);

            }

            map.addMarker(markerOptions);


        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "addPointCenter", e.getMessage());
        }
    }
    //endregion

    //region Thêm vị trí Shipper vào map
    public static Marker addShipper(GoogleMap map, Marker marker, LatLng latLng) {
        try {
            if (marker == null) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_shipper2);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.5f)
                        .icon(icon);
                marker = map.addMarker(markerOptions);

            } else {
                marker.setPosition(latLng);
            }

        } catch (Exception e) {
        }
        return marker;
    }

    //endregion

    //region Kiểm tra Pin tồn tai trong map
    public static boolean checkExistPin(GoogleMap map) {
        try {
            for (MarkerOptions marker : markers) {
                if (marker.getIcon() == BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)) {
                    return true;
                }
            }
        } catch (Exception e) {
            CmmFunc.showError("MapHelper", "checkExistPin", e.getMessage());
        }
        return false;
    }
    //endregion

    //region Actions
    public static class GetAddressByLatLng extends AsyncTask<Void, Void, String> {
        String data = null;
        Activity activity;
        LatLng latLng;

        public GetAddressByLatLng() {

        }

        public GetAddressByLatLng(Activity activity, LatLng latLng) {
            this.activity = activity;
            this.latLng = latLng;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                data = getAddressByLatLong(activity, latLng);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    public static class GetDistance2Point extends AsyncTask<Void, Void, Double> {
        String data = null;
        Activity activity;
        LatLng latLng;
        LatLng start;
        LatLng end;

        public GetDistance2Point() {

        }

        public GetDistance2Point(LatLng start, LatLng end) {
            this.start = start;
            this.end = end;
        }

        public GetDistance2Point(Activity activity, LatLng latLng) {
            this.activity = activity;
            this.latLng = latLng;
        }

        @Override
        protected Double doInBackground(Void... voids) {
            Double retValue = null;
            try {
                data = MapHelper.downloadUrl(MapHelper.getUrl(start, end));
                JSONObject jsonObject = new JSONObject(data);
                retValue = getValueDistance(jsonObject);
            } catch (Exception e) {

            }
            return retValue;
        }
    }

    public static class GetPlaceIDByLatLng extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... objects) {
            String retValue = null;
            try {
                Double lat = (Double) objects[0];
                Double lng = (Double) objects[1];
                //retValue = getPlaceIDByLatLng(lat, lng);
            } catch (Exception e) {

            }
            return retValue;
        }
    }

    public static class DowloadURL extends AsyncTask<Void, Void, String> {
        String data = null;
        LatLng start;
        LatLng end;

        public DowloadURL() {

        }

        public DowloadURL(LatLng start, LatLng end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                data = MapHelper.downloadUrl(MapHelper.getUrl(start, end));

            } catch (Exception e) {

            }
            return data;
        }
    }

    public static class ActionDraw extends AsyncTask<Void, Void, Void> {

        JSONObject data;
        List<List<HashMap<String, String>>> routes = null;
        GoogleMap map;
        private LatLng start;
        private LatLng end;
        private int color;

        public ActionDraw() {

        }

        public ActionDraw(GoogleMap map, JSONObject data, LatLng start, LatLng end) {
            this.start = start;
            this.end = end;
            this.data = data;
            this.map = map;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                routes = MapHelper.parse(data);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(GlobalClass.getActivity().getResources().getColor(R.color.main));

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    map.addPolyline(lineOptions);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            } catch (Exception e) {
                CmmFunc.showError("MapHelper", "ActionDraw", e.getMessage());
            }
        }
    }
    //endregion

    //region Tạo marker từ view
    private static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static Bitmap createMarker(int idView, String index, String address) {
        View markerView = ((LayoutInflater) GlobalClass.getActivity()
                .getSystemService(GlobalClass.getActivity().LAYOUT_INFLATER_SERVICE))
                .inflate(idView, null);

        TextView addressView = (TextView) markerView.findViewById(R.id.address);
        if (addressView != null) {
            addressView.setText(address);
        }


        TextView indexView = (TextView) markerView.findViewById(R.id.index);
        if (indexView != null) {
            indexView.setText(index);
        }


        return createDrawableFromView(GlobalClass.getActivity(), markerView);
    }
    //endregion

    //region Move marker
    public static void animateMarker(final Marker marker, final LatLng destination) {

        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();
            final float rotation = (float) SphericalUtil.computeHeading(startPosition, destination);

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // done
                }
            });
            valueAnimator.start();
        } else {

        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }
    //endregion

    //region Draw new
    private static List<LatLng> decodePolyPoint(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        for (int i = 0; i < poly.size(); i++) {
            Log.i("Location", "Point sent: Latitude: " + poly.get(i).latitude + " Longitude: " + poly.get(i).longitude);
        }
        return poly;
    }

    public static class ActionDrawCode extends AsyncTask<Object, Void, PolylineOptions> {
        GoogleMap map;

        @Override
        protected PolylineOptions doInBackground(Object... objects) {
            map = (GoogleMap) objects[0];
            PolylineOptions lineOptions = new PolylineOptions();
            try {
                String mapData = (String) objects[1];
                List<LatLng> latLngs = PolyUtil.decode(mapData);//decodePolyPoint(mapData);
                lineOptions.addAll(latLngs);
                lineOptions.width(5);
                lineOptions.color(GlobalClass.getActivity().getResources().getColor(R.color.main));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions polylineOptions) {
            super.onPostExecute(polylineOptions);
            map.addPolyline(polylineOptions);
        }
    }
    //endregion

}


