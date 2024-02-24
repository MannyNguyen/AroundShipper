package vn.nip.aroundshipper.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.squareup.picasso.Picasso;

import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Bean.BeanUser;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.HttpHelper;
import vn.nip.aroundshipper.Helper.MapHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Helper.TimerHelper;
import vn.nip.aroundshipper.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import sfs2x.client.requests.ExtensionRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowJourneyFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {


    //region Private variables
    final int NO_CENTER = 0;
    final int ANIMTE_CENTER = 1;
    final int SET_CENTER = 2;

    Marker markerShipper;
    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    Timer timer;
    SupportMapFragment mapFragment;
    //endregion

    //region Contructors
    public FollowJourneyFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_follow_journey, container, false);
            getProfile();

        }
        return view;
    }


    public static FollowJourneyFragment newInstance(int orderID) {
        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        FollowJourneyFragment fragment = new FollowJourneyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CmmVariable.isFirstApp = false;
        CmmVariable.IS_PROGRESS = true;
        CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeInJourney * 1000;
        if (!isLoaded) {
            init();
            getMap();
        }
    }


    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        try {
            View bottomSheet = view.findViewById(R.id.bottom_sheet1);
            final BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

            ImageButton hideBottom = (ImageButton) view.findViewById(R.id.hide_bottom);
            hideBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            ImageButton myLocation = (ImageButton) view.findViewById(R.id.my_location);
            myLocation.setOnClickListener(FollowJourneyFragment.this);

            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(BeanUser.getCurrent().getFullName());


            CircleImageView avatar = (CircleImageView) view.findViewById(R.id.avatar);
            Picasso.with(getActivity()).load(BeanUser.getCurrent().getAvatar()).into(avatar);


            view.findViewById(R.id.full_order).setOnClickListener(FollowJourneyFragment.this);
            view.findViewById(R.id.call).setOnClickListener(FollowJourneyFragment.this);
            FrameLayout chat = (FrameLayout) view.findViewById(R.id.chat);
            chat.setOnClickListener(FollowJourneyFragment.this);

            CardView cancel = (CardView) view.findViewById(R.id.cancel);
            CardView finish = (CardView) view.findViewById(R.id.finish);
            cancel.setOnClickListener(FollowJourneyFragment.this);
            finish.setOnClickListener(FollowJourneyFragment.this);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBottomSheetBehavior1.setPeekHeight(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())));
                }
            });

            isLoaded = true;
        } catch (Exception e) {

        }
    }

    private void getMap() {

        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .add(R.id.map_container_follow, mapFragment, StringUtils.EMPTY)
                .commitAllowingStateLoss();
        mapFragment.getMapAsync(FollowJourneyFragment.this);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            TextView numberChat = (TextView) view.findViewById(R.id.number_chat);

            if (StorageHelper.get("numberChat").equals("")) {
                CmmVariable.numberChat = 0;
            }
            if (CmmVariable.numberChat < 1) {
                if (StorageHelper.get("numberChat").equals("")) {
                    numberChat.setVisibility(View.GONE);
                } else {
                    numberChat.setVisibility(View.VISIBLE);
                    CmmVariable.numberChat = Integer.parseInt(StorageHelper.get("numberChat"));
                    numberChat.setText(CmmVariable.numberChat + "");
                }

            } else {
                numberChat.setVisibility(View.VISIBLE);
                numberChat.setText(CmmVariable.numberChat + "");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void manualResume() {
        super.manualResume();
        try {
            TextView numberChat = (TextView) view.findViewById(R.id.number_chat);

            if (StorageHelper.get("numberChat").equals("")) {
                CmmVariable.numberChat = 0;
            }
            if (CmmVariable.numberChat < 1) {
                if (StorageHelper.get("numberChat").equals("")) {
                    numberChat.setVisibility(View.GONE);
                } else {
                    numberChat.setVisibility(View.VISIBLE);
                    CmmVariable.numberChat = Integer.parseInt(StorageHelper.get("numberChat"));
                    numberChat.setText(CmmVariable.numberChat + "");
                }

            } else {
                numberChat.setVisibility(View.VISIBLE);
                numberChat.setText(CmmVariable.numberChat + "");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    //endregion

    //region Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            map.clear();
            drawToMap();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(FollowJourneyFragment.this)
                    .build();
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation(SET_CENTER);
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLocation() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLastLocation(NO_CENTER);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 10000, 10000);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (mapFragment != null) {
            FragmentHelper.removeFragment(getChildFragmentManager(), mapFragment);
        }
    }

    //endregion

    //region Draw to map
    public void drawToMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < CmmVariable.points.size() - 1; i++) {
                        try {
                            BeanPoint origin = CmmVariable.points.get(i);
                            BeanPoint dest = CmmVariable.points.get(i + 1);
                            if (origin.getLatLng() != null && dest.getLatLng() != null) {
                                String url = MapHelper.getUrl(origin.getLatLng(), dest.getLatLng());
                                JSONObject jsonObject = new JSONObject(HttpHelper.get(url, null));
                                List<List<HashMap<String, String>>> routes = MapHelper.parse(jsonObject);
                                ArrayList<LatLng> latLngs;
                                PolylineOptions lineOptions = null;
                                // Traversing through all the routes
                                for (int j = 0; j < routes.size(); j++) {
                                    latLngs = new ArrayList<>();
                                    lineOptions = new PolylineOptions();

                                    // Fetching i-th route
                                    List<HashMap<String, String>> path = routes.get(j);

                                    // Fetching all the CmmVariable.points in i-th route
                                    for (int t = 0; t < path.size(); t++) {
                                        HashMap<String, String> point = path.get(t);

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng position = new LatLng(lat, lng);
                                        latLngs.add(position);
                                    }

                                    // Adding all the CmmVariable.points in the route to LineOptions
                                    lineOptions.addAll(latLngs);
                                    lineOptions.width(5);
                                    lineOptions.color(getResources().getColor(R.color.main));

                                }

                                // Drawing polyline in the Google Map for the i-th route
                                if (lineOptions != null) {
                                    final PolylineOptions finalLineOptions = lineOptions;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            map.addPolyline(finalLineOptions);
                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    MapHelper.setCenter(getActivity(), map, CmmVariable.points);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            //Nếu là marker shipper
            if (marker.getId().equals(markerShipper.getId())) {
                return false;
            } else {

            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "onMarkerClick", e.getMessage());
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                try {
                    String phone = BeanUser.getCurrent().getPhone() + "";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "onClick", e.getMessage());
                }
                break;
            case R.id.my_location:
                getLastLocation(ANIMTE_CENTER);
                break;
            case R.id.chat:
                try {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, ChatFragment.newInstance(getArguments().getInt("order_id")));
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "onClick", e.getMessage());
                }
                break;

            case R.id.full_order:
//                Fragment fragment = FullOrderCommonFragment.newInstance(getArguments().getInt("order_id"), false);
                Fragment fragment = FullOrderFragment.newInstance(getArguments().getInt("order_id"), false);
                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
                break;

            case R.id.finish:
//                FragmentHelper.addFragment(getActivity(), R.id.home_content, FullOrderCommonFragment.newInstance(getArguments().getInt("order_id"), true));
                FragmentHelper.addFragment(getActivity(), R.id.home_content, FullOrderFragment.newInstance(getArguments().getInt("order_id"), true));
                break;

            case R.id.cancel:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, BillFragment.newInstance(getArguments().getInt("order_id"), BillFragment.CANCEL, StringUtils.EMPTY));
                break;
        }
    }
    //endregion

    //region Methods
    private void getLastLocation(final int setCenter) {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setNumUpdates(1)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(0);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (location != null) {
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (markerShipper != null) {
                                        MapHelper.animateMarker(markerShipper, latLng);
                                    } else {
                                        markerShipper = MapHelper.addShipper(map, markerShipper, latLng);
                                    }

                                    switch (setCenter) {
                                        case SET_CENTER:
                                            MapHelper.setCenter(map, latLng);
                                            break;
                                        case ANIMTE_CENTER:
                                            MapHelper.animateCenter(map, latLng);
                                            break;
                                    }
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_PROFILE");
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }


    //endregion
}
