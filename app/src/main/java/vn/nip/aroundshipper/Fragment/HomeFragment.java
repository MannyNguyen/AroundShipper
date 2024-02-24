package vn.nip.aroundshipper.Fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.MapHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;

import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Helper.TimerHelper;
import vn.nip.aroundshipper.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    //region Private variables
    final int NO_CENTER = 0;
    final int ANIMTE_CENTER = 1;
    final int SET_CENTER = 2;

    public Marker markerShipper;
    GoogleApiClient mGoogleApiClient;
    GoogleMap map;

    Timer timer;

    SupportMapFragment mapFragment;
    //endregion

    //region Contructors
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //endregion

    //region Create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        CmmVariable.isFirstApp = false;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            CmmVariable.IS_PROGRESS = false;
            CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeOutJourney * 1000;
            if (!isLoaded) {
                getMap();
                SmartFoxHelper.getProfile();
                view.findViewById(R.id.my_location).setOnClickListener(HomeFragment.this);
                isLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void manualResume() {
        try {
            if (StorageHelper.getPhone().equals("")){
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //endregion

    //region Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            markerShipper = null;
            map.clear();

            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(HomeFragment.this)
                    .build();
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_location:
                getLastLocation(ANIMTE_CENTER);
                break;
        }
    }
    //endregion

    //region Methods
    private void getMap() {
        try {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .add(R.id.map_container, mapFragment, StringUtils.EMPTY)
                    .commitAllowingStateLoss();
            mapFragment.getMapAsync(HomeFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateLocation() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
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
                public void onLocationChanged(Location location) {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation(SET_CENTER);
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    //endregion

}
