package vn.nip.aroundshipper.Fragment.Partner;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Bean.BeanOrderPartner;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.Dialog.ConfirmDialogFragment;
import vn.nip.aroundshipper.Fragment.OrderCODFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.MapHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPartnerFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks {

    GoogleMap map;
    public List<BeanOrderPartner> orders;
    BottomSheetBehavior mBottomSheetBehavior;
    ImageView doneIcon;
    TextView doneTitle;
    ImageButton cod;

    Marker oldMarker;
    public BeanOrderPartner beanOrderPartner;

    TextView nearestText, recentText;
    View nearestBottom, recentBottom, recentContainer, nearestContainer;

    final String TIME = "TIME";
    final String LOCATION = "LOCATION";
    String type = LOCATION;
    GoogleApiClient mGoogleApiClient;

    public MapPartnerFragment() {
        // Required empty public constructor
    }

    public static MapPartnerFragment newInstance() {
        Bundle args = new Bundle();
        MapPartnerFragment fragment = new MapPartnerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_map_partner, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getString(R.string.order_partner));
        orders = new ArrayList<>();
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                doneIcon = (ImageView) view.findViewById(R.id.done_icon);
                doneTitle = (TextView) view.findViewById(R.id.done_title);

                View bottomSheet = view.findViewById(R.id.bottom_sheet_partner);
                mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                nearestContainer = view.findViewById(R.id.nearest_container);
                nearestText = (TextView) view.findViewById(R.id.nearest_text);
                nearestBottom = view.findViewById(R.id.nearest_bottom);
                recentContainer = view.findViewById(R.id.recent_container);
                recentText = (TextView) view.findViewById(R.id.recent_text);
                recentBottom = view.findViewById(R.id.recent_bottom);
                cod = (ImageButton) view.findViewById(R.id.cod);

                cod.setOnClickListener(MapPartnerFragment.this);
                view.findViewById(R.id.partner_list).setOnClickListener(MapPartnerFragment.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBottomSheetBehavior.setPeekHeight(0);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        getMap();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    private void getMap() {
        try {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .add(R.id.partner_map_container, mapFragment, StringUtils.EMPTY)
                    .commitAllowingStateLoss();
            mapFragment.getMapAsync(MapPartnerFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void listenerCancel() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }
            orders.remove(beanOrderPartner);
            beanOrderPartner = null;
            map.clear();
            reloadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenerFinish() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }
            orders.remove(beanOrderPartner);
            beanOrderPartner = null;
            map.clear();
            reloadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenerStart() {
        try {
            hideProgress();
            if (beanOrderPartner == null) {
                return;
            }

            beanOrderPartner.setAction(BeanOrderPartner.ACTION_DONE);
            doneIcon.setImageResource(R.drawable.ic_done_order_partner);
            doneTitle.setText(getString(R.string.done_order_partner));
            //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadMap() {
        try {
            map.clear();
            oldMarker = null;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < orders.size(); i++) {
                BitmapDescriptor icon;
                if (orders.get(i).getAction() == BeanOrderPartner.ACTION_DONE) {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((i + 1) + "", 2, '0'), R.layout.marker_partner_orange));
                } else {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((i + 1) + "", 2, '0'), R.layout.marker_partner_blue));
                }

                MarkerOptions markerOptions = new MarkerOptions().position(orders.get(i).getLatLng())
                        .icon(icon);
                map.addMarker(markerOptions).setTag(orders.get(i));
                builder.include(orders.get(i).getLatLng());
            }
            LatLngBounds bounds = builder.build();
            int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 120);
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.moveCamera(cameraUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.setOnMarkerClickListener(MapPartnerFragment.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            view.findViewById(R.id.my_location).setOnClickListener(MapPartnerFragment.this);
            map = googleMap;

            nearestContainer.setOnClickListener(MapPartnerFragment.this);
            recentContainer.setOnClickListener(MapPartnerFragment.this);
            SmartFoxHelper.getPartnerOrder(type);
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(MapPartnerFragment.this)
                    .build();
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_location:
                getLastLocation();
                break;
            case R.id.partner_list:
                FragmentHelper.pop(getActivity());
                break;

            case R.id.recent_container:
                if (map == null) {
                    return;
                }
                nearestText.setTextColor(getResources().getColor(R.color.gray_900));
                nearestBottom.setVisibility(View.GONE);
                recentText.setTextColor(getResources().getColor(R.color.main));
                recentBottom.setVisibility(View.VISIBLE);
                type = TIME;
                SmartFoxHelper.getPartnerOrder(type);
                break;

            case R.id.nearest_container:
                if (map == null) {
                    return;
                }
                nearestText.setTextColor(getResources().getColor(R.color.main));
                nearestBottom.setVisibility(View.VISIBLE);
                recentText.setTextColor(getResources().getColor(R.color.gray_900));
                recentBottom.setVisibility(View.GONE);
                type = LOCATION;
                SmartFoxHelper.getPartnerOrder(type);
                break;
            case R.id.cod:
                FragmentHelper.replaceFragment(getActivity(), R.id.home_content, OrderCODFragment.newInstance());
                break;
        }
    }

    public Bitmap createMarker(String index, int layout) {
        View markerView = ((LayoutInflater) GlobalClass.getActivity()
                .getSystemService(GlobalClass.getActivity().LAYOUT_INFLATER_SERVICE))
                .inflate(layout, null);


        TextView indexView = (TextView) markerView.findViewById(R.id.index);
        if (indexView != null) {
            indexView.setText(index);
        }


        return createDrawableFromView(GlobalClass.getActivity(), markerView);
    }

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            if (oldMarker != null && !oldMarker.equals(marker)) {
                final BeanOrderPartner beanOrderPartner = (BeanOrderPartner) oldMarker.getTag();
                int index = orders.indexOf(beanOrderPartner) + 1;
                BitmapDescriptor icon;
                if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_DONE) {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_orange));
                } else {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_blue));
                }
                oldMarker.setIcon(icon);
            }

            beanOrderPartner = (BeanOrderPartner) marker.getTag();
            int index = orders.indexOf(beanOrderPartner) + 1;
            TextView position = (TextView) view.findViewById(R.id.bottom_position);
            TextView address = (TextView) view.findViewById(R.id.bottom_address);
            address.setText(beanOrderPartner.getAddress() + StringUtils.EMPTY);
            position.setText(index + StringUtils.EMPTY);

            if (oldMarker != null) {
                if (!oldMarker.equals(marker)) {
                    BitmapDescriptor icon;
                    if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_DONE) {
                        icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_orange));
                    } else {
                        icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_blue_large));
                    }
                    marker.setIcon(icon);
                }
            } else {
                BitmapDescriptor icon;
                if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_DONE) {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_orange));
                } else {
                    icon = BitmapDescriptorFactory.fromBitmap(createMarker(StringUtils.leftPad((index) + "", 2, '0'), R.layout.marker_partner_blue_large));
                }
                marker.setIcon(icon);
            }

            if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_DONE) {
                doneIcon.setImageResource(R.drawable.ic_done_order_partner);
                doneTitle.setText(getString(R.string.done_order_partner));
            } else if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_RUN) {
                doneIcon.setImageResource(R.drawable.ic_run_order_partner);
                doneTitle.setText(getString(R.string.run_order_partner));
            }


            view.findViewById(R.id.bottom_view_order).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //             FragmentHelper.addFragment(getActivity(), R.id.home_content, FullOrderCommonFragment.newInstance(beanOrderPartner.getId(), false));
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, FullOrderFragment.newInstance(beanOrderPartner.getId(), false));
                }
            });

            view.findViewById(R.id.bottom_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", beanOrderPartner.getPhone(), null));
                    getActivity().startActivity(intent);
                }
            });

            view.findViewById(R.id.bottom_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, BillFragment.newInstance(beanOrderPartner.getId(), BillFragment.CANCEL, StringUtils.EMPTY));
                }
            });

            view.findViewById(R.id.bottom_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_RUN) {
                        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
                        confirmDialogFragment.setMessage(getString(R.string.confirm_start));
                        confirmDialogFragment.setOkCallback(new Runnable() {
                            @Override
                            public void run() {
                                showProgress();
                                SmartFoxHelper.start(beanOrderPartner.getId());
                            }
                        });
                        confirmDialogFragment.show(getActivity().getSupportFragmentManager(), confirmDialogFragment.getClass().getName());
                    } else if (beanOrderPartner.getAction() == BeanOrderPartner.ACTION_DONE) {
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, BillFragment.newInstance(beanOrderPartner.getId(), BillFragment.FINISH, StringUtils.EMPTY));
                    }
                }
            });

            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapHelper.animateCenter(map, beanOrderPartner.getLatLng());
                }
            });
            oldMarker = marker;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    private void getLastLocation() {
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
                if (location != null && map != null) {

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MapHelper.animateCenter(map, latLng);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);
                }

            }
        });
    }

    //region Socket
    public void onGetPartnerOrder(final List<BeanOrderPartner> list) {
        try {
            if (list == null) {
                return;
            }
            orders.clear();
            orders.addAll(list);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (orders.size() == 0) {
                        getLastLocation();
                        return;
                    }
                    reloadMap();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    //endregion
}
