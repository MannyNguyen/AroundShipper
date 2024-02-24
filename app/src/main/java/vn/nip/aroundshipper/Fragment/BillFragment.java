package vn.nip.aroundshipper.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sfs2x.client.requests.ExtensionRequest;
import vn.nip.aroundshipper.Adapter.BillAdapter;
import vn.nip.aroundshipper.Bean.BeanBill;
import vn.nip.aroundshipper.Bean.BeanItem;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.MapHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillFragment extends BaseFragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    public static final int CANCEL = 0;
    public static final int FINISH = 1;
    final int CAPTURE = 100;
    final int GALLERY = 101;
    public int ID_REQUEST;

    Uri captureURI;
    EditText verifyCode, commonNote;
    public TextView messageVerifyCode;
    ImageView commonCamera;
    RecyclerView recycler;
    BillAdapter adapter;
    public ScrollView scrollView;
    View container;
    public View popup;
    List<BeanBill> bills = new ArrayList<>();
    LatLng latLng;
    GoogleApiClient mGoogleApiClient;

    byte[] commonImage;

    public BillFragment() {
        // Required empty public constructor
    }

    public static BillFragment newInstance(int orderID, int type, String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("type", type);
        args.putInt("order_id", orderID);
        BillFragment fragment = new BillFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_bill, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments().getInt("type") == FINISH) {
            if (!getArguments().getString("data").equals(StringUtils.EMPTY)) {
                bills = (List<BeanBill>) CmmFunc.tryParseList(getArguments().getString("data"), BeanBill.class);
            } else {
                SmartFoxHelper.getFullOrder(getArguments().getInt("order_id"));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TextView title = (TextView) view.findViewById(R.id.title);
                    container = view.findViewById(R.id.container);
                    popup = view.findViewById(R.id.popup);
                    scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
                    verifyCode = (EditText) view.findViewById(R.id.verify_code);
                    commonNote = (EditText) view.findViewById(R.id.common_note);
                    messageVerifyCode = (TextView) view.findViewById(R.id.message_verify_code);
                    commonCamera = (ImageView) view.findViewById(R.id.common_camera);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    view.findViewById(R.id.cancel).setOnClickListener(BillFragment.this);
                    view.findViewById(R.id.confirm).setOnClickListener(BillFragment.this);
                    view.findViewById(R.id.open_camera).setOnClickListener(BillFragment.this);
                    view.findViewById(R.id.open_gallery).setOnClickListener(BillFragment.this);
                    view.findViewById(R.id.outside_popup).setOnClickListener(BillFragment.this);
                    view.findViewById(R.id.common_remove).setOnClickListener(BillFragment.this);
                    //view.findViewById(R.id.outside).setOnClickListener(BillFragment.this);
                    commonCamera.setOnClickListener(BillFragment.this);
                    container.setOnClickListener(BillFragment.this);
                    adapter = new BillAdapter(BillFragment.this, recycler, bills);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getArguments().getInt("type") == FINISH) {
                                title.setText(getString(R.string.title_verify_code));
                                verifyCode.setVisibility(View.VISIBLE);
                                recycler.setVisibility(View.VISIBLE);
                            } else {
                                title.setText(getString(R.string.cancel_order));
                            }
                            TextWatcher textWatcher = new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    if (messageVerifyCode.getVisibility() == View.GONE) {
                                        return;
                                    }
                                    messageVerifyCode.setVisibility(View.GONE);
                                }
                            };
                            verifyCode.addTextChangedListener(textWatcher);
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recycler.setAdapter(adapter);
                            recycler.setItemViewCacheSize(3);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadInit.start();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(BillFragment.this)
                .build();
        mGoogleApiClient.connect();
        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                FragmentHelper.pop(getActivity());
                break;
            case R.id.common_remove:
                if (commonImage == null) {
                    return;
                }
                commonImage = null;
                commonCamera.setImageResource(R.drawable.ic_image_bill);
                break;
            case R.id.common_camera:
                ID_REQUEST = CAPTURE;
                openCamera();
                break;
            case R.id.open_camera:
                openCamera();
                break;
            case R.id.open_gallery:
                openGallery();
                break;

            case R.id.outside_popup:
                if (popup.getVisibility() == View.GONE) {
                    return;
                }
                popup.setVisibility(View.GONE);
                break;

            case R.id.confirm:
                final String code = verifyCode.getText().toString().trim();
                if (getArguments().getInt("type") == FINISH) {
                    if (code.equals(StringUtils.EMPTY)) {
                        messageVerifyCode.setVisibility(View.VISIBLE);
                        scrollView.smoothScrollTo(0, 0);
                        return;
                    }
                }

                final String message = commonNote.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showProgress();
                            SFSArray sfsArrayBills = null;
                            if (bills != null) {
                                if (bills.size() > 0) {
                                    sfsArrayBills = new SFSArray();
                                    for (final BeanBill beanBill : bills) {

                                        if (beanBill.getImage() == null && beanBill.getPrice() == 0) {
                                            continue;
                                        }

                                        if (beanBill.getImage() != null && beanBill.getPrice() != 0) {
                                            SFSObject sfsObject = new SFSObject();
                                            sfsObject.putInt("id", beanBill.getId());
                                            sfsObject.putInt("price", beanBill.getPrice());
                                            sfsObject.putByteArray("image", beanBill.getImage());
                                            sfsArrayBills.addSFSObject(sfsObject);
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                    scrollView.smoothScrollTo(0, scrollView.getHeight());
                                                }
                                            });
                                            hideProgress();
                                            return;
                                        }
                                    }
                                }
                            }

                            SFSObject location = new SFSObject();
                            if (latLng == null) {
                                location.putUtfString("placeid", StringUtils.EMPTY);
                                location.putDouble("latitude", 0);
                                location.putDouble("longitude", 0);
                                location.putUtfString("address", StringUtils.EMPTY);
                            } else {
                                String address = MapHelper.getAddressByLatLong(getActivity(), latLng);
                                if (address == null) {
                                    location.putUtfString("placeid", StringUtils.EMPTY);
                                    location.putDouble("latitude", latLng.latitude);
                                    location.putDouble("longitude", latLng.longitude);
                                    location.putUtfString("address", StringUtils.EMPTY);
                                } else {
                                    location.putUtfString("placeid", StringUtils.EMPTY);
                                    location.putDouble("latitude", latLng.latitude);
                                    location.putDouble("longitude", latLng.longitude);
                                    location.putUtfString("address", address);
                                }
                            }

                            SmartFoxHelper.verifyCode(getArguments().getInt("order_id"), getArguments().getInt("type"), message, commonImage, code, location, sfsArrayBills);
                        } catch (Exception e) {
                            hideProgress();
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAPTURE:
                    capture(ID_REQUEST);
                    break;
                case GALLERY:
                    gallery(ID_REQUEST, data);
                    break;

            }

            popup.setVisibility(View.GONE);
        }
    }

    public void openCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Around" + System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DESCRIPTION, "Around" + System.currentTimeMillis());
                captureURI = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureURI);
                startActivityForResult(intent, CAPTURE);
            }
        }).start();
    }

    private void openGallery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
            }
        }).start();
    }

    private void capture(final int idRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;
                    String realPath = CmmFunc.getPathFromUri(getActivity(), captureURI);
                    if (realPath != null) {
                        ExifInterface exif = new ExifInterface(realPath);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (rotation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotation = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotation = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotation = 270;
                                break;
                            default:
                                rotation = 0;
                                break;
                        }
                        Matrix matrix = new Matrix();
                        if (rotation != 0f) {
                            matrix.preRotate(rotation);
                        }

                        bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), captureURI);
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                    } else {
                        bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), captureURI);
                    }


                    bmp = CmmFunc.resizeBitmap(bmp, CmmVariable.IMAGE_RESIZE_COMMON);
                    byte[] bytes = CmmFunc.bitmapToByteArray(bmp);
                    if (idRequest == CAPTURE) {
                        commonImage = CmmFunc.bitmapToByteArray(bmp);
                        final Bitmap finalBmp = bmp;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commonCamera.setImageBitmap(finalBmp);
                            }
                        });
                    } else {
                        bills.get(idRequest).setImage(bytes);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void gallery(final int idRequest, final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;

                    Uri uri = Uri.parse(data.getDataString());
                    if (uri != null) {
                        String realPath = CmmFunc.getPathFromUri(getActivity(), uri);
                        if (realPath != null) {
                            ExifInterface exif = new ExifInterface(realPath);
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            switch (rotation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotation = 90;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotation = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotation = 270;
                                    break;
                                default:
                                    rotation = 0;
                                    break;
                            }
                            Matrix matrix = new Matrix();
                            if (rotation != 0f) {
                                matrix.preRotate(rotation);
                            }

                            bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                        } else {
                            if (data.getData() == null) {
                                bmp = (Bitmap) data.getExtras().get("data");
                            } else {
                                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                            }
                        }
                    } else {
                        if (data.getData() == null) {
                            bmp = (Bitmap) data.getExtras().get("data");
                        } else {
                            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                            bmp = BitmapFactory.decodeStream(bufferedInputStream);
                        }
                    }

                    bmp = CmmFunc.resizeBitmap(bmp, CmmVariable.IMAGE_RESIZE_COMMON);
                    byte[] bytes = CmmFunc.bitmapToByteArray(bmp);
                    bills.get(idRequest).setImage(bytes);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }

            }
        });
    }

    //Socket
    public void onGetBill(SFSArray arr) {
        try {
            bills.clear();
            for (int i = 0; i < arr.size(); i++) {
                ISFSObject sfsObject = arr.getSFSObject(i);
                if (sfsObject.getInt("pickup_type") != BeanPoint.PURCHASE) {
                    continue;
                }
                BeanBill beanBill = new BeanBill();
                beanBill.setId(sfsObject.getInt("id"));
                beanBill.setAddress(sfsObject.getUtfString("address"));
                beanBill.setImage(null);
                beanBill.setPrice(0);
                beanBill.setPosition(i);
                bills.add(beanBill);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

}
