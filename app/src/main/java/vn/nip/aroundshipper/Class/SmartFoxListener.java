package vn.nip.aroundshipper.Class;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.squareup.picasso.Picasso;

import sfs2x.client.util.ConfigData;
import vn.nip.aroundshipper.Bean.BeanChat;
import vn.nip.aroundshipper.Bean.BeanItem;
import vn.nip.aroundshipper.Bean.BeanOrderPartner;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Bean.BeanRate;
import vn.nip.aroundshipper.Bean.BeanSchedule;
import vn.nip.aroundshipper.Bean.BeanShipper;
import vn.nip.aroundshipper.Bean.BeanUser;
import vn.nip.aroundshipper.Fragment.BaseFragment;
import vn.nip.aroundshipper.Fragment.BillFragment;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.ChatFragment;
import vn.nip.aroundshipper.Fragment.DetailLocationFragment;
import vn.nip.aroundshipper.Fragment.Dialog.MessageDialogFragment;
import vn.nip.aroundshipper.Fragment.FollowJourneyFragment;
import vn.nip.aroundshipper.Fragment.FullOrderCommonFragment;
import vn.nip.aroundshipper.Fragment.HomeFragment;
import vn.nip.aroundshipper.Fragment.IncomeFragment;
import vn.nip.aroundshipper.Fragment.LoginFragment;
import vn.nip.aroundshipper.Fragment.OrderCODFragment;
import vn.nip.aroundshipper.Fragment.OrderConfirmFragment;
import vn.nip.aroundshipper.Fragment.OrderHistoryFragment;
import vn.nip.aroundshipper.Fragment.Partner.MapPartnerFragment;
import vn.nip.aroundshipper.Fragment.Partner.OrderPartnerFragment;
import vn.nip.aroundshipper.Fragment.ProfileFragment;
import vn.nip.aroundshipper.Fragment.RatingInfoFragment;
import vn.nip.aroundshipper.Fragment.ReceiptFragment;
import vn.nip.aroundshipper.Fragment.ScheduleFragment;
import vn.nip.aroundshipper.Fragment.AroundWalletFragment;
import vn.nip.aroundshipper.Helper.ErrorHelper;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.NotificationRouteHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Helper.TimerHelper;
import vn.nip.aroundshipper.HomeActivity;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.MainActivity;
import vn.nip.aroundshipper.R;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.entities.User;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LeaveRoomRequest;

/**
 * Created by viminh on 10/20/2016.
 */

public abstract class SmartFoxListener {

    int numberReconnect = 0;
    boolean domainReconnect = false;
    boolean isLogin = false;

    public void connection(final BaseEvent event) {
        if ((boolean) event.getArguments().get("success")) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    numberReconnect = 0;
                    domainReconnect = false;
                    if (!CmmVariable.isFirstApp) {
                        SmartFoxHelper.ActionLogin(StorageHelper.getPhone(), StorageHelper.getPassword());
                        return;
                    }
                    Intent intent = new Intent(GlobalClass.getActivity(), HomeActivity.class);
                    Bundle bundle = GlobalClass.getActivity().getIntent().getExtras();
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    GlobalClass.getActivity().startActivity(intent);
                    GlobalClass.getActivity().finish();
                }
            });
        } else {
            if (!domainReconnect) {
                domainReconnect = true;

                ConfigData configData = new ConfigData();
                configData.setHost(CmmVariable.smartFoxIP);
                configData.setDebug(true);
                configData.setPort(Integer.parseInt(CmmVariable.getSmartFoxPort));
                configData.setZone("AroundAppZone");
                configData.setUseBBox(false);
                SmartFoxHelper.resetInstance();
                SmartFoxHelper.getInstance().connect(configData);
            } else {
//                GlobalClass.getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        CustomDialog.showMessage(GlobalClass.getActivity(), "", event.getArguments().get("errorMessage").toString());
//                    }
//                });
            }

        }
    }


    public void connectionLost(BaseEvent event) {
        isLogin = false;
        reConnect();
        if (timer != null) {
            timer.cancel();
        }
        //timer.scheduleAtFixedRate(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private void reConnect() {
        if (numberReconnect > 12) {
            Intent intent = new Intent(GlobalClass.getContext(), MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(GlobalClass.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long[] pattern = {0, 400, 1000};
            NotificationCompat.Builder b = new NotificationCompat.Builder(GlobalClass.getContext());
            b.setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setWhen(System.currentTimeMillis())
                    .setVibrate(pattern)
                    .setLargeIcon(BitmapFactory.decodeResource(GlobalClass.getActivity().getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.ic_launcher)
                    //.setTicker("Hearty365")
                    .setContentTitle(GlobalClass.getActivity().getString(R.string.app_name))
                    .setContentText(GlobalClass.getActivity().getString(R.string.notify_disconnect_content))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                    .setContentIntent(contentIntent);
            //.setContentInfo("Info");

            NotificationManager notificationManager = (NotificationManager) GlobalClass.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
            GlobalClass.getActivity().finishAffinity();
            CmmVariable.isFirstApp = true;
            return;
        }
        if (!SmartFoxHelper.getInstance().isConnected()) {
            //Khởi tạo lái SM
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        //Tránh trường hợp commit Fragment lúc change Activity
                        if (SmartFoxHelper.getInstance().isConnected()) {
                            return;
                        }
                        ConfigData configData = SmartFoxHelper.getInstance().getConfig();
                        configData.setPort(Integer.parseInt(CmmVariable.getSmartFoxPort));
                        configData.setUseBBox(false);
                        SmartFoxHelper.resetInstance();
                        SmartFoxHelper.getInstance().connect(configData);
                        numberReconnect += 1;

                        if (SmartFoxHelper.getInstance().isConnected()) {
                            return;
                        }
                        Thread.sleep(5000);
                        reConnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public void login(final BaseEvent event) {

        isLogin = true;
        SmartFoxHelper.updateDeviceToken(FirebaseInstanceId.getInstance().getToken());
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override

            public void run() {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SFSObject sfsObject = new SFSObject();
                            sfsObject.putUtfString("command", "GET_PROFILE");
                            SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SFSObject sfsObject = new SFSObject();
                            sfsObject.putUtfString("command", "GET_GOOGLE_KEY");
                            SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                        }
                    }).start();

                    SFSObject data = (SFSObject) event.getArguments().get("data");
                    SFSObject config = (SFSObject) data.getSFSObject("config");
                    TimerHelper.updatePositionTimeInJourney = config.getInt("update_postion_time_in_journey");
                    TimerHelper.updatePositionTimeOutJourney = config.getInt("update_postion_time_out_journey");
                    TimerHelper.responseShipperTimeout = config.getInt("response_shipper_timeout");
                    CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeOutJourney * 1000;
                    //Nếu đang ở fragment login thì set lại value Storge
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof LoginFragment) {
                        View view = fragment.getView();
                        EditText username = (EditText) view.findViewById(R.id.username);
                        EditText password = (EditText) view.findViewById(R.id.password);
                        if (BeanShipper.getCurrent() == null) {
                            BeanShipper.setCurrent(new BeanShipper());
                        }
                        BeanShipper.getCurrent().setShipper_phone(username.getText().toString());
                        BeanShipper.getCurrent().setPassword(password.getText().toString());
                        StorageHelper.saveShipper();
                    }
                    timer = new Timer();
                    UPDATE_POSITION(true);

                    if (fragment instanceof BillFragment) {
                        ((BillFragment) fragment).hideProgress();
                    }


                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "login", e.getMessage());
                }
            }
        });


    }

    public void logout(final BaseEvent event) {
        isLogin = false;
    }

    public void loginError(final BaseEvent event) {

        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(event.getArguments().get("errorMessage").toString());
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(jsonObject.getString("message"));
                    messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                } catch (Exception e) {
                    try {
                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(event.getArguments().get("errorMessage").toString());
                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    public void publishMessage(BaseEvent event) {
        PUBLIC_MESSAGE(event);
    }

    public void roomJoin(BaseEvent event) {

    }

    public void roomJoinError(BaseEvent event) {

    }

    public void extensionResponse(BaseEvent event) {
        try {
            String cmd = (String) event.getArguments().get("cmd");
            if (cmd.equals("shipper")) {
                final SFSObject params = (SFSObject) event.getArguments().get("params");
                String command = params.getUtfString("command");
                switch (command) {
                    case "UPDATE_POSITION":
                        //UPDATE_POSITION(false);
                        break;

                    case "RESPONSE_SHIPPER":
                        RESPONSE_SHIPPER(params);
                        break;
                    case "GET_PROFILE":
                        GET_PROFILE(params);
                        break;
                    case "UPDATE_LOCATION_STATUS":
                        UPDATE_LOCATION_STATUS(params);
                        break;
                    case "FINISH_DELIVERY":
                        FINISH_DELIVERY(params);
                        break;
                    case "GET_CHAT_HISTORY":
                        GET_CHAT_HISTORY(params);
                        break;
                    case "RECONNECT_JOURNEY":
                        RECONNECT_JOURNEY(params);
                        break;
                    case "GET_ORDER_HISTORY":
                        GET_ORDER_HISTORY(params);
                        break;
                    case "GET_RECEIPT":
                        GET_RECEIPT(params);
                        break;
                    case "CANCEL_ORDER":
                        CANCEL_ORDER(params);
                        break;
                    case "UPDATE_PROFILE":
                        UPDATE_PROFILE(params);
                        break;
                    case "GET_INCOME":
                        GET_INCOME(params);
                        break;
                    case "GET_FULL_ORDER":
                        GET_FULL_ORDER(params);
                        break;
                    case "SERVER_INFO":
                        SERVER_INFO(params);
                        break;
                    case "GET_ALL_SCHEDULE_ORDER":
                        GET_ALL_SCHEDULE_ORDER(params, false);
                        break;
                    case "GET_MY_SCHEDULE_ORDER":
                        GET_ALL_SCHEDULE_ORDER(params, true);
                        break;
                    case "ACCEPT_SCHEDULE_ORDER":
                        ACCEPT_SCHEDULE_ORDER(params);
                        break;
                    case "START_SCHEDULE_ORDER":
                        START_SCHEDULE_ORDER(params);
                        break;
                    case "GET_RATING_HISTORY":
                        GET_RATING_HISTORY(params);
                        break;
                    case "VERIFY_ORDER":
                        VERIFY_ORDER(params);
                        break;
                    case "RECEIVE_SHOP_PRODUCT":
                        RECEIVE_SHOP_PRODUCT(params);
                        break;
                    case "FINDING_SHIPPER":
                        FINDING_SHIPPER(params);
                        break;
                    case "GET_FINDING_SHIPPER_INFO":
                        GET_FINDING_SHIPPER_INFO(params);
                        if (TimerHelper.dialogTimeout != null) {
                            TimerHelper.dialogTimeout.dismiss();
                        }
                        break;
                    case "GET_ASSIGNED_PARTNER_ORDER":
                        GET_ASSIGNED_PARTNER_ORDER(params);
                        break;
                    case "CHECK_FOLLOW_JOURNEY":
                        CHECK_FOLLOW_JOURNEY(params);
                        break;
                    case "GET_COD_ORDER":
                        GET_COD_ORDER(params);
                        break;
                    case "GET_AROUND_PAY":
                        GET_AROUND_PAY(params);
                        break;
                    case "GET_AROUND_PAY_TRANSACTION":
                        GET_AROUND_PAY_TRANSACTION(params);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //region Extends function
    Timer timer = new Timer();
    boolean isTimer = false;

    private void UPDATE_POSITION(final boolean isFirst) {
        if (!isLogin) {
            return;
        }
        if (timer == null) {
            timer = new Timer();
        }


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isTimer = false;
                            final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(GlobalClass.getActivity());
                            final LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(final LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (isTimer == true) {
                                                    return;
                                                }
                                                isTimer = true;
                                                Location location = locationResult.getLastLocation();
                                                SFSObject sfsObject = new SFSObject();
                                                sfsObject.putUtfString("command", "UPDATE_POSITION");
                                                sfsObject.putDouble("latitude", location.getLatitude());
                                                sfsObject.putDouble("longitude", location.getLongitude());
                                                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                                                Log.d("time", "" + new DateTime().getSecondOfMinute());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                //mFusedLocationClient.removeLocationUpdates(new LocationCallback());
                                                //UPDATE_POSITION(false);

                                            }
                                        }
                                    }).start();
                                }
                            };
                            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    mFusedLocationClient.requestLocationUpdates(new LocationRequest(), locationCallback, null);
                                }
                            });


                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "UPDATE_POSITION", e.getMessage());
                        } finally {
                            Log.d("UpdatePosition", new DateTime().getSecondOfMinute() + "");
                        }
                    }
                }).start();
            }

        }, 0, CmmVariable.TIMER_UPDATE_POSITION);


    }

    private void FINDING_SHIPPER(final SFSObject params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = OrderConfirmFragment.newInstance();
                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "requestShipper", e.getMessage());
                }
            }
        }).start();
    }

    private void RECEIVE_SHOP_PRODUCT(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FullOrderCommonFragment fullOrderCommonFragment = (FullOrderCommonFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FullOrderCommonFragment.class.getName());
                if (fullOrderCommonFragment != null) {
                    fullOrderCommonFragment.hideProgress();

                }
            }
        });
    }

    private void GET_FINDING_SHIPPER_INFO(final SFSObject params) {
        final SFSObject data = (SFSObject) params.getSFSObject("data");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanUser user = new BeanUser();
                    user.setFullName(data.getUtfString("user_fullname"));
                    user.setPhone(data.getUtfString("user_phone"));
                    user.setUsername(data.getUtfString("user_name"));
                    user.setAvatar(data.getUtfString("user_avatar"));
                    BeanUser.setCurrent(user);
                    CmmVariable.avatarUser = data.getUtfString("user_avatar");
                    CmmVariable.room = data.getUtfString("room_name");
                    CmmVariable.points.clear();
                    SFSArray locations = (SFSArray) data.getSFSArray("locations");
                    for (int i = 0; i < locations.size(); i++) {
                        Object item = locations.getElementAt(i);
                        BeanPoint point = (BeanPoint) CmmFunc.tryParseJson(item.toString(), BeanPoint.class);
                        CmmVariable.points.add(point);
                    }
                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OrderConfirmFragment orderConfirmFragment = (OrderConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), OrderConfirmFragment.class.getName());
                            if (orderConfirmFragment != null) {
                                orderConfirmFragment.distance = data.getDouble("distance");
                                orderConfirmFragment.idRequest = data.getUtfString("id_request");
                                orderConfirmFragment.setData();

                            }
                        }
                    });

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "requestShipper", e.getMessage());
                }
            }
        }).start();
        SmartFoxHelper.receiveRequestShipper(data.getUtfString("id_request"));
    }

    private void GET_PROFILE(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            if (fragment instanceof ProfileFragment) {
                                final View view = fragment.getView();
                                EditText fullName = (EditText) view.findViewById(R.id.full_name);
                                EditText phone = (EditText) view.findViewById(R.id.phone);
                                EditText idNumber = (EditText) view.findViewById(R.id.id_number);
                                EditText address = (EditText) view.findViewById(R.id.address);
                                CircleImageView avatar = (CircleImageView) view.findViewById(R.id.avatar);
                                fullName.setText(params.getUtfString("fullname") + "");
                                phone.setText(params.getUtfString("phone") + "");
                                idNumber.setText(params.getUtfString("id_no") + "");
                                address.setText(params.getUtfString("address") + "");
                                Picasso.with(GlobalClass.getActivity()).load(params.getUtfString("avatar")).into(avatar);
                            }
                            CmmVariable.avatarShipper = params.getUtfString("avatar");
                            NavigationView navigationView = (NavigationView) GlobalClass.getActivity().findViewById(R.id.nav_view);
                            CircleImageView avatar = (CircleImageView) navigationView.findViewById(R.id.avatar);
                            Picasso.with(GlobalClass.getActivity()).load(params.getUtfString("avatar")).into(avatar);
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "GET_PROFILE", e.getMessage());
                        }

                    }
                });
            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "GET_PROFILE", e.getMessage());
        }
    }

    private void UPDATE_PROFILE(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            if (fragment instanceof ProfileFragment) {
                                CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getString(R.string.success), GlobalClass.getActivity().getString(R.string.update_profile));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SFSObject sfsObject = new SFSObject();
                                        sfsObject.putUtfString("command", "GET_PROFILE");
                                        SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                                    }
                                }).start();
                            }

                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "GET_PROFILE", e.getMessage());
                        }

                    }
                });
            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "GET_PROFILE", e.getMessage());
        }
    }

    private void GET_ORDER_HISTORY(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof OrderHistoryFragment) {
                    OrderHistoryFragment orderHistoryFragment = (OrderHistoryFragment) fragment;
                    orderHistoryFragment.executeHistoryData(params);
                }
            }
        });
    }

    private void GET_INCOME(final SFSObject params) {
        int code = params.getInt("code");
        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof IncomeFragment) {
                        View view = fragment.getView();
                        TextView count = (TextView) view.findViewById(R.id.count);
                        count.setText(params.getInt("number") + "");
                        TextView total = (TextView) view.findViewById(R.id.total);
                        total.setText(CmmFunc.formatMoney(params.getInt("total")));
                    }
                }
            });
        }
    }

    private void GET_FULL_ORDER(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof FullOrderFragment) {
                        FullOrderFragment fullOrderFragment = (FullOrderFragment) fragment;
                        fullOrderFragment.executeFullOrder(params);
                        return;

                    }
                    if (fragment instanceof BillFragment) {
                        BillFragment billFragment = (BillFragment) fragment;
                        billFragment.onGetBill((SFSArray) params.getSFSArray("locations"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GET_FULL_ORDER_1(final SFSObject params) {
        int code = params.getInt("code");
        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());

                    if (fragment instanceof BillFragment) {
                        try {
                            BillFragment billFragment = (BillFragment) fragment;
                            billFragment.onGetBill((SFSArray) params.getSFSArray("locations"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (fragment instanceof FullOrderCommonFragment) {
                        final FullOrderCommonFragment fullOrderCommonFragment = (FullOrderCommonFragment) fragment;
                        final View view = fullOrderCommonFragment.getView();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final boolean isGift = params.getBool("show_gift");
                                    final String orderCode = params.getUtfString("order_code");

                                    final int day = params.getInt("delivery_day");
                                    int month = params.getInt("delivery_month");
                                    int year = params.getInt("delivery_year");
                                    int hour = params.getInt("delivery_hour");
                                    int minute = params.getInt("delivery_minute");
                                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                                    LocalDateTime dateTime = LocalDateTime.parse(day + "-" + month + "-" + year + " " + hour + ":" + minute, formatter);
                                    final JSONObject formatDate = CmmFunc.formatDate(dateTime, false);

                                    fullOrderCommonFragment.locations = new ArrayList<BeanPoint>();

                                    final ISFSArray arr = params.getSFSArray("locations");
                                    for (int i = 0; i < arr.size(); i++) {
                                        ISFSObject sfsObject = arr.getSFSObject(i);
                                        BeanPoint bean = new BeanPoint();
                                        bean.setId(sfsObject.getInt("id"));
                                        bean.setAddress(sfsObject.getUtfString("address"));
                                        bean.setNote(sfsObject.getUtfString("note"));
                                        bean.setRecipent_name(sfsObject.getUtfString("recipent_name"));
                                        bean.setPhone(sfsObject.getUtfString("phone"));
                                        bean.setPickup_type(sfsObject.getInt("pickup_type"));
                                        bean.setItem_cost(sfsObject.getInt("item_cost"));
                                        bean.setReceive_shop_product_status(sfsObject.getInt("receive_shop_product_status"));
                                        bean.setX15(sfsObject.getBool("x15"));
                                        bean.setBill_price(sfsObject.getInt("bill_price"));
                                        bean.setBill_image(sfsObject.getUtfString("bill_image"));
                                        bean.setShipper_fullname(sfsObject.getUtfString("shipper_fullname"));
                                        bean.setShipper_phone(sfsObject.getUtfString("shipper_phone"));
                                        List<BeanItem> items = (List<BeanItem>) CmmFunc.tryParseList(sfsObject.getUtfString("location_items"), BeanItem.class);
                                        bean.setLocation_items(items);
                                        fullOrderCommonFragment.locations.add(bean);
                                    }
                                    final String paymentType = params.getUtfString("payment_type");
                                    final String ship = CmmFunc.formatMoney(params.getInt("shipping_fee"));
                                    final String service = CmmFunc.formatMoney(params.getInt("service_fee"));
                                    final String itemFee = CmmFunc.formatMoney(params.getInt("item_cost"));
                                    final String totalFee = CmmFunc.formatMoney(params.getInt("total"));
                                    final int status = params.getInt("status");
                                    //view.findViewById(R.id.fee_shipping).setOnClickListener(FullOrderCommonFragment.this);

                                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                TextView title = (TextView) view.findViewById(R.id.title);
                                                if (fragment.getArguments().getBoolean("is_done")) {
                                                    title.setText(fragment.getString(R.string.your_receipt));
                                                } else {
                                                    if (isGift) {
                                                        title.setText(fragment.getString(R.string.gifting));
                                                    } else {
                                                        title.setText(fragment.getString(R.string.pickup_delivery));
                                                    }
                                                }

                                                TextView orderCodeView = (TextView) view.findViewById(R.id.order_code);
                                                orderCodeView.setText(orderCode);

                                                TextView deliveryTime = (TextView) view.findViewById(R.id.delivery_time);
                                                deliveryTime.setText(formatDate.getString("value"));


                                                ImageView icPayment = (ImageView) view.findViewById(R.id.ic_payment);
                                                TextView textPayment = (TextView) view.findViewById(R.id.text_payment);
                                                switch (paymentType) {
                                                    case "CASH":
                                                        icPayment.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_cash_selected));
                                                        textPayment.setText(fragment.getString(R.string.cast));
                                                        break;
                                                    case "ONLINE":
                                                        icPayment.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_online_selected));
                                                        textPayment.setText(fragment.getString(R.string.online_payment));
                                                        break;
                                                    case "AROUND_PAY":
                                                        icPayment.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_around_box_selected));
                                                        textPayment.setText(fragment.getString(R.string.around_payment));
                                                        break;
                                                }

                                                DetailLocationFragment detailLocationFragment = (DetailLocationFragment) fragment.getChildFragmentManager().findFragmentById(R.id.detail_location_fragment);
                                                detailLocationFragment.getArguments().putString("data", CmmFunc.tryParseObject(fullOrderCommonFragment.locations));
                                                detailLocationFragment.getArguments().putInt("status", status);
                                                List<Integer> types = new ArrayList<Integer>();
                                                TextView itemCostName = (TextView) view.findViewById(R.id.item_fee_name);
                                                if (isGift) {
                                                    detailLocationFragment.getArguments().putString("type", DetailLocationFragment.GIFTING);
                                                    itemCostName.setText(fragment.getString(R.string.item_cost));
                                                    view.findViewById(R.id.container_purchase_fee).setVisibility(View.VISIBLE);
                                                } else {
                                                    detailLocationFragment.getArguments().putString("type", DetailLocationFragment.PICKUP);
                                                    for (BeanPoint bean : fullOrderCommonFragment.locations) {
                                                        types.add(bean.getPickup_type());
                                                    }
                                                    itemCostName.setText(fragment.getString(R.string.purchase_cost));
                                                    if (types.contains(BeanPoint.PURCHASE)) {
                                                        view.findViewById(R.id.container_purchase_fee).setVisibility(View.VISIBLE);
                                                    } else {
                                                        view.findViewById(R.id.container_purchase_fee).setVisibility(View.GONE);
                                                    }
                                                }
                                                detailLocationFragment.onResume();

                                                final BeanPoint last = fullOrderCommonFragment.locations.get(fullOrderCommonFragment.locations.size() - 1);
                                                TextView dropAddress = (TextView) view.findViewById(R.id.drop_address);
                                                dropAddress.setText(last.getAddress() + "");

                                                TextView dropRecipient = (TextView) view.findViewById(R.id.drop_recipent_name);
                                                dropRecipient.setText(last.getRecipent_name() + "");
                                                final ImageButton dropCall = (ImageButton) view.findViewById(R.id.drop_call);
                                                if (!last.getPhone().equals("")) {
                                                    dropCall.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_call_now));
                                                    dropCall.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", last.getPhone(), null));
                                                            GlobalClass.getActivity().startActivity(intent);
                                                        }
                                                    });

                                                } else {
                                                    dropCall.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_uncall));
                                                }


                                                if (last.getNote().equals("")) {
                                                    view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                                                } else {
                                                    TextView dropNote = (TextView) view.findViewById(R.id.drop_note);
                                                    dropNote.setText(last.getNote() + "");
                                                }

                                                if (fullOrderCommonFragment.locations.get(0).getShipper_fullname().equals(StringUtils.EMPTY)) {
                                                    view.findViewById(R.id.shipper_area).setVisibility(View.GONE);
                                                } else {
                                                    view.findViewById(R.id.shipper_area).setVisibility(View.VISIBLE);
                                                    TextView shipperName = (TextView) view.findViewById(R.id.shipper_name);
                                                    shipperName.setText(fullOrderCommonFragment.locations.get(0).getShipper_fullname());
                                                    view.findViewById(R.id.call_shipper).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", fullOrderCommonFragment.locations.get(0).getShipper_phone(), null));
                                                            fullOrderCommonFragment.startActivity(intent);
                                                        }
                                                    });
                                                }

                                                TextView shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                                                shippingFee.setText(ship);
                                                TextView serviceFee = (TextView) view.findViewById(R.id.service_fee);
                                                serviceFee.setText(service);
                                                TextView itemCost = (TextView) view.findViewById(R.id.item_cost);
                                                itemCost.setText(itemFee);
                                                TextView total = (TextView) view.findViewById(R.id.total);
                                                total.setText(totalFee);
                                                ((FullOrderCommonFragment) fragment).hideProgress();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            });
        }
    }

    private void UPDATE_LOCATION_STATUS(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {

                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            OrderDetailFragment fragment = (OrderDetailFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
//                            final View view = fragment.getView();
//                            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
//                            int status = 0;
//                            if (radioGroup.getCheckedRadioButtonId() == R.id.done) {
//                                status = 1;
//                            }
//                            BeanPoint bean = BeanPoint.getByTag(OrderDetailFragment.currentTag);
//                            if (bean != null) {
//                                bean.setStatus(status);
//                                int index = CmmVariable.points.indexOf(bean);
//                                CmmVariable.points.set(index, bean);
//                            }
//                            GlobalClass.getActivity().getSupportFragmentManager().popBackStack();
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "GET_PROFILE", e.getMessage());
                        }

                    }
                });

            }


        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "UPDATE_LOCATION_STATUS", e.getMessage());
        }
    }

    private void GET_CHAT_HISTORY(final SFSObject params) {

        class getHistory extends AsyncTask<Object, Void, Void> {

            @Override
            protected Void doInBackground(Object... objects) {
                try {
                    CmmVariable.chats.clear();
                    ISFSArray array = (ISFSArray) objects[0];
                    for (int i = 0; i < array.size(); i++) {
                        ISFSObject object = array.getSFSObject(i);
                        BeanChat bean = new BeanChat();
                        bean.setMessage(object.getUtfString("message"));
                        if (bean.getMessage().equals("TEXT_TYPE")) {
                            bean.setChat_description(object.getUtfString("chat_description"));
                        } else if (bean.getMessage().equals("IMAGE_TYPE")) {
                            byte[] arr = object.getByteArray("chat_description");
                            bean.setImage(object.getByteArray("chat_description"));
                            bean.setBitmap(CmmFunc.getBitmapFromByteArray(arr));
                        } else {
                            bean.setUrlImage(object.getUtfString("chat_description"));
                        }
                        String sender = object.getUtfString("sender");
                        if (sender.equals(SmartFoxHelper.getInstance().getMySelf().getName())) {
                            bean.setType(0);
                        } else {
                            bean.setType(1);
                        }
                        CmmVariable.chats.add(bean);
                    }
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "GET_CHAT_HISTORY", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ChatFragment fragment = (ChatFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            RecyclerView recyclerView = (RecyclerView) fragment.view.findViewById(R.id.recycler);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            recyclerView.scrollToPosition(CmmVariable.chats.size() - 1);
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "GET_CHAT_HISTORY", e.getMessage());
                        }
                    }
                });

            }
        }

        ISFSArray array = params.getSFSArray("chat_history");
        if (array == null)
            return;
        new getHistory().execute(array);
    }

    private void RESPONSE_SHIPPER(final SFSObject params) {
        int status = params.getInt("status");
        final int code = params.getInt("code");
        if (code == 1) {
            if (status == 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentHelper.popRoot(GlobalClass.getActivity());
                        FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, FollowJourneyFragment.newInstance(params.getInt("order_id")));
                    }
                });
            } else if (status == 0) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeanUser.setCurrent(new BeanUser());
                        CmmVariable.points.clear();
                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                        if (fragment instanceof OrderConfirmFragment) {
                            ((OrderConfirmFragment) fragment).mediaPlayer.stop();
                            ((OrderConfirmFragment) fragment).vibrator.cancel();
                            ((OrderConfirmFragment) fragment).isCancel = true;
                        }
                        try {
                            FragmentHelper.popRoot(GlobalClass.getActivity());
                            FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }

        }

        //Shipper không nhấn - sau dtime tự động vào đây
        //Không thành công từ server
        if (code != 1) {
            if (!CmmVariable.IS_PROGRESS) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OrderConfirmFragment orderConfirmFragment = (OrderConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), OrderConfirmFragment.class.getName());
                        if (orderConfirmFragment != null) {
                            orderConfirmFragment.vibrator.cancel();
                            orderConfirmFragment.mediaPlayer.stop();
                        }
                        MessageDialogFragment fragment = new MessageDialogFragment();
                        fragment.setMessage(ErrorHelper.getValueByKey(code));
                        fragment.setRunnable(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeOutJourney * 1000;
                                    CmmVariable.points.clear();
                                    CmmVariable.chats.clear();
                                    BeanUser.setCurrent(null);
                                    GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(HomeFragment.class.getName(), 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        fragment.show(GlobalClass.getActivity().getSupportFragmentManager(), fragment.getClass().getName());
                    }
                });
            }
        }
    }

    private void GET_RECEIPT(final SFSObject params) {
        int code = params.getInt("code");
        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                        if (fragment instanceof ReceiptFragment) {
                            View view = fragment.getView();
                            TextView distance = (TextView) view.findViewById(R.id.distance);
                            TextView duration = (TextView) view.findViewById(R.id.duration);
                            TextView shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                            TextView itemCost = (TextView) view.findViewById(R.id.item_cost);
                            TextView serviceFee = (TextView) view.findViewById(R.id.service_fee);
                            TextView returnToPickup = (TextView) view.findViewById(R.id.return_fee);
                            TextView total = (TextView) view.findViewById(R.id.total);

                            distance.setText(params.getDouble("distance") + "km");
                            Double d = params.getDouble("duration");
                            Double mduration = d / 60;
                            duration.setText(String.valueOf(mduration).split("\\.")[0] + " min");

                            if (mduration < 60) {
                                duration.setText(String.valueOf(mduration).split("\\.")[0] + fragment.getString(R.string.minute));
                            } else {
                                Double hduration = mduration / 60;
                                int hour = (int) Math.round(hduration);
                                int minute = (int) (mduration - (hour * 60));
                                duration.setText(hour + " " + fragment.getString(R.string.hour) + minute + " " + fragment.getString(R.string.minute));
                            }

                            shippingFee.setText(CmmFunc.formatMoney(params.getInt("shipping_fee")));
                            itemCost.setText(CmmFunc.formatMoney(params.getInt("item_cost")));
                            serviceFee.setText(CmmFunc.formatMoney(params.getInt("service_fee")));
                            returnToPickup.setText(CmmFunc.formatMoney(params.getInt("return_to_pickup_fee")));
                            total.setText(CmmFunc.formatMoney(params.getInt("total")));
                        }

                    } catch (Exception e) {

                    }
                }
            });
        } else {
            CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), ErrorHelper.getValueByKey(code));
        }

    }

    private void PUBLIC_MESSAGE(final BaseEvent event) {
        class get extends AsyncTask<Object, Void, Void> {
            boolean isContinue = false;

            @Override
            protected Void doInBackground(Object... objects) {
                try {
                    BaseEvent event = (BaseEvent) objects[0];
                    User user = (User) event.getArguments().get("sender");

                    if (user.getName().equals(SmartFoxHelper.getInstance().getMySelf().getName()))
                        return null;

                    BeanChat beanChat = new BeanChat();
                    beanChat.setMessage(event.getArguments().get("message") + "");
                    beanChat.setType(1);
                    SFSObject object = (SFSObject) event.getArguments().get("data");
                    if (beanChat.getMessage().equals("TEXT_TYPE")) {
                        String content = object.getUtfString("chat_description");
                        beanChat.setChat_description(content + "");
                    } else if (beanChat.getMessage().equals("IMAGE_TYPE")) {
                        byte[] arr = object.getByteArray("chat_description");
                        beanChat.setImage(arr);
                        beanChat.setBitmap(CmmFunc.getBitmapFromByteArray(arr));

                    } else {
                        beanChat.setUrlImage(object.getUtfString("chat_description"));
                    }
                    CmmVariable.chats.add(beanChat);
                    isContinue = true;

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "PUBLIC_MESSAGE", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isContinue) {
                                ChatFragment fragment = (ChatFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ChatFragment.class.getName());
                                if (fragment != null) {
                                    RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycler);
                                    recyclerView.getAdapter().notifyItemInserted(CmmVariable.chats.size());
                                    recyclerView.scrollToPosition(CmmVariable.chats.size() - 1);
                                }
                            }
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "PUBLIC_MESSAGE", e.getMessage());
                        }
                    }
                });
            }
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (GlobalClass.getActivity() instanceof HomeActivity) {
                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                        if (fragment instanceof ChatFragment) {
                            new get().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event);
                            return;
                        }
                        FollowJourneyFragment follow = (FollowJourneyFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FollowJourneyFragment.class.getName());
                        if (follow != null) {
                            CmmVariable.numberChat += 1;
                            TextView numberChat = (TextView) follow.view.findViewById(R.id.number_chat);
                            if (numberChat != null) {
                                numberChat.setVisibility(View.VISIBLE);
                                numberChat.setText(CmmVariable.numberChat + "");
                                StorageHelper.set("numberChat", CmmVariable.numberChat + "");
                            }
                        }
                    }

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "", e.getMessage());
                }
            }
        });

    }

    private void SERVER_INFO(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                ISFSObject data = params.getSFSObject("data");
                final ISFSObject maintenance = data.getSFSObject("maintenance");
                int statusMaintenance = maintenance.getInt("status");
                if (statusMaintenance == 1) {
                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = maintenance.getUtfString("message");
                            if (StorageHelper.getLanguage().equals("vi")) {
                                message = maintenance.getUtfString("vn_message");
                            }
                            CustomDialog.showMessage(GlobalClass.getActivity(), "", message, new ICallback() {
                                @Override
                                public void excute() {
                                    if (SmartFoxHelper.getInstance().isConnected()) {
                                        SmartFoxHelper.getInstance().disconnect();
                                    }

                                    GlobalClass.getActivity().finishAffinity();
                                }
                            });
                        }
                    });

                    return;
                }

                final ISFSObject update = data.getSFSObject("update");
                int statusUpdate = update.getInt("status");
                if (statusUpdate == 1) {
                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = update.getUtfString("message");
                            if (StorageHelper.getLanguage().equals("vi")) {
                                message = update.getUtfString("vn_message");
                            }
                            CustomDialog.Dialog2Button(GlobalClass.getActivity(), "", message, GlobalClass.getActivity().getResources().getString(R.string.ok), GlobalClass.getActivity().getResources().getString(R.string.cancel), new ICallback() {
                                @Override
                                public void excute() {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(update.getUtfString("url")));
                                    GlobalClass.getActivity().startActivity(i);
                                    GlobalClass.getActivity().finish();
                                }
                            }, null);
                        }
                    });

                } else if (statusUpdate == 2) {
                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = update.getUtfString("message");
                            if (StorageHelper.getLanguage().equals("vi")) {
                                message = update.getUtfString("vn_message");
                            }
                            CustomDialog.showMessage(GlobalClass.getActivity(), "", message, new ICallback() {
                                @Override
                                public void excute() {
                                    try {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(update.getUtfString("url")));
                                        GlobalClass.getActivity().startActivity(i);
                                        GlobalClass.getActivity().finish();
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        }
                    });

                }
            }
        } catch (Exception e) {

        }
    }

    private void GET_ALL_SCHEDULE_ORDER(final SFSObject params, final boolean isMe) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof ScheduleFragment) {
                    try {
                        ScheduleFragment scheduleFragment = (ScheduleFragment) fragment;
                        int code = params.getInt("code");
                        if (code == 1) {
                            SFSArray data = (SFSArray) params.getSFSArray("data");
                            for (int i = 0; i < data.size(); i++) {
                                BeanSchedule bean = (BeanSchedule) CmmFunc.tryParseJson(data.getUtfString(i), BeanSchedule.class);
                                scheduleFragment.schedules.add(bean);
                            }
                            if (scheduleFragment.schedules.size() > 0) {
                                scheduleFragment.getView().findViewById(R.id.status).setVisibility(View.GONE);
                                scheduleFragment.getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
                                scheduleFragment.page += 1;
                                scheduleFragment.recycler.getAdapter().notifyDataSetChanged();
                            } else {
                                if (scheduleFragment.page == 1) {
                                    TextView status = (TextView) scheduleFragment.getView().findViewById(R.id.status);
                                    if (isMe) {
                                        status.setText(fragment.getString(R.string.empty_my_schedule));
                                    } else {
                                        status.setText(fragment.getString(R.string.empty_all_schedule));
                                    }
                                    status.setVisibility(View.VISIBLE);
                                }

                            }
                        } else {
                            CustomDialog.showMessage(GlobalClass.getActivity(), "", ErrorHelper.getValueByKey(code));
                        }
                    } catch (Exception e) {

                    } finally {
                        ((ScheduleFragment) fragment).hideProgress();
                        ((ScheduleFragment) fragment).refreshLayout.setRefreshing(false);
                    }
                }
            }
        });

    }

    private void ACCEPT_SCHEDULE_ORDER(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof ScheduleFragment) {
                    try {
                        final ScheduleFragment scheduleFragment = (ScheduleFragment) fragment;
                        int code = params.getInt("code");
                        if (code == 1) {
                            SFSObject data = (SFSObject) params.getSFSObject("data");
                            int id = data.getInt("id_order");
                            BeanSchedule bean = BeanSchedule.getByID(id, scheduleFragment.schedules);
                            if (bean != null) {
                                scheduleFragment.schedules.remove(bean);
                                scheduleFragment.recycler.getAdapter().notifyDataSetChanged();
                            }
                        } else {
                            CustomDialog.showMessage(GlobalClass.getActivity(), "", ErrorHelper.getValueByKey(code), new ICallback() {
                                @Override
                                public void excute() {
                                    scheduleFragment.schedules.clear();
                                    scheduleFragment.recycler.getAdapter().notifyDataSetChanged();
                                    scheduleFragment.page = 1;
                                    scheduleFragment.request("GET_ALL_SCHEDULE_ORDER", scheduleFragment.page);
                                }
                            });
                        }
                    } catch (Exception e) {

                    } finally {
                        ((ScheduleFragment) fragment).hideProgress();
                    }
                }
            }
        });

    }

    private void START_SCHEDULE_ORDER(final SFSObject params) {
        final int code = params.getInt("code");
        if (code != 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseFragment fragment = (BaseFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment != null) {
                        fragment.hideProgress();
                    }
                    CustomDialog.showMessage(GlobalClass.getActivity(), "", ErrorHelper.getValueByKey(code));
                }
            });
            return;
        }

        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof ScheduleFragment) {
                    try {
                        SFSObject data = (SFSObject) params.getSFSObject("data");
                        BeanUser user = new BeanUser();
                        user.setFullName(data.getUtfString("user_fullname"));
                        user.setPhone(data.getUtfString("user_phone"));
                        user.setUsername(data.getUtfString("user_name"));
                        user.setAvatar(data.getUtfString("user_avatar"));
                        BeanUser.setCurrent(user);
                        CmmVariable.avatarUser = params.getUtfString("user_avatar");
                        CmmVariable.points.clear();
                        SFSArray locations = (SFSArray) data.getSFSArray("locations");
                        for (int i = 0; i < locations.size(); i++) {
                            BeanPoint point = (BeanPoint) CmmFunc.tryParseJson(locations.getUtfString(i), BeanPoint.class);
                            CmmVariable.points.add(point);
                        }
                        FragmentHelper.popRoot(GlobalClass.getActivity());
                        FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, FollowJourneyFragment.newInstance(data.getInt("id_order")));
                    } catch (Exception e) {

                    } finally {
                        ((ScheduleFragment) fragment).hideProgress();
                    }
                    return;
                }

                OrderPartnerFragment orderPartnerFragment = (OrderPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), OrderPartnerFragment.class.getName());
                if (orderPartnerFragment != null) {
                    orderPartnerFragment.listenerStart();
                    MapPartnerFragment mapPartnerFragment = (MapPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MapPartnerFragment.class.getName());
                    if (mapPartnerFragment != null) {
                        mapPartnerFragment.listenerStart();
                    }
                    return;
                }

            }
        });

    }

    private void CANCEL_ORDER(final SFSObject params) {
        final int code = params.getInt("code");
        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MapPartnerFragment mapPartnerFragment = (MapPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MapPartnerFragment.class.getName());
                    if (mapPartnerFragment != null) {
                        //BillFragment is active
                        FragmentHelper.pop(GlobalClass.getActivity());
                        mapPartnerFragment.listenerCancel();
                        return;
                    }


                    OrderPartnerFragment orderPartnerFragment = (OrderPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), OrderPartnerFragment.class.getName());
                    if (orderPartnerFragment != null) {
                        //BillFragment is active
                        FragmentHelper.pop(GlobalClass.getActivity());
                        orderPartnerFragment.listenerCancel();
                        return;
                    }


                    CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeOutJourney * 1000;
                    CmmVariable.points.clear();
                    CmmVariable.chats.clear();
                    CmmVariable.room = null;
                    BeanUser.setCurrent(null);
                    StorageHelper.set("numberChat", "");
                    FragmentHelper.popRoot(GlobalClass.getActivity());
                    FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
                }
            });
        } else {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseFragment fragment = (BaseFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment != null) {
                        fragment.hideProgress();
                    }
                    CustomDialog.showMessage(GlobalClass.getActivity(), "", ErrorHelper.getValueByKey(code));
                }
            });
        }

    }

    private void FINISH_DELIVERY(final SFSObject params) {
        final int code = params.getInt("code");
        if (code != 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseFragment fragment = (BaseFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment != null) {
                        fragment.hideProgress();
                    }
                    new ErrorHelper().excute(code);
                }
            });
            return;
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapPartnerFragment mapPartnerFragment = (MapPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MapPartnerFragment.class.getName());
                    if (mapPartnerFragment != null) {
                        //BillFragment is active
                        FragmentHelper.pop(GlobalClass.getActivity());
                        mapPartnerFragment.listenerCancel();
                        return;
                    }


                    OrderPartnerFragment orderPartnerFragment = (OrderPartnerFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), OrderPartnerFragment.class.getName());
                    if (orderPartnerFragment != null) {
                        //BillFragment is active
                        FragmentHelper.pop(GlobalClass.getActivity());
                        orderPartnerFragment.listenerCancel();
                        return;
                    }

                    CmmVariable.TIMER_UPDATE_POSITION = TimerHelper.updatePositionTimeOutJourney * 1000;
                    CmmVariable.points.clear();
                    CmmVariable.chats.clear();
                    CmmVariable.room = null;
                    BeanUser.setCurrent(null);
                    StorageHelper.set("numberChat", "");
                    GlobalClass.getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
                    SmartFoxHelper.getInstance().send(new LeaveRoomRequest());
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "RESET_APP", e.getMessage());
                }
            }
        });
    }

    private void GET_RATING_HISTORY(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof RatingInfoFragment) {
                    RatingInfoFragment ratingInfoFragment = (RatingInfoFragment) fragment;
                    ratingInfoFragment.hideProgress();
                    int code = params.getInt("code");
                    if (code == 1) {
                        SFSObject data = (SFSObject) params.getSFSObject("data");
                        SFSArray histories = (SFSArray) data.getSFSArray("history");

                        TextView fullName = (TextView) ratingInfoFragment.getView().findViewById(R.id.full_name);
                        fullName.setText(data.getUtfString("fullname"));
                        if (histories.size() > 0) {

                            for (int i = 0; i < histories.size(); i++) {
                                BeanRate bean = (BeanRate) CmmFunc.tryParseJson(histories.getUtfString(i), BeanRate.class);
                                ratingInfoFragment.rates.add(bean);
                            }
                            ratingInfoFragment.recycler.getAdapter().notifyDataSetChanged();


                            int rating = data.getInt("rating");
                            RatingBar ratingBar = (RatingBar) ratingInfoFragment.getView().findViewById(R.id.full_rating);
                            ratingBar.setProgress(rating);

                            ImageView icon = (ImageView) ratingInfoFragment.getView().findViewById(R.id.icon);
                            switch (rating) {
                                case 1:
                                    icon.setImageDrawable(ratingInfoFragment.getResources().getDrawable(R.drawable.ic_rate_0));
                                    break;
                                case 2:
                                    icon.setImageDrawable(ratingInfoFragment.getResources().getDrawable(R.drawable.ic_rate_1));
                                    break;
                                case 3:
                                    icon.setImageDrawable(ratingInfoFragment.getResources().getDrawable(R.drawable.ic_rate_2));
                                    break;
                                case 4:
                                    icon.setImageDrawable(ratingInfoFragment.getResources().getDrawable(R.drawable.ic_rate_3));
                                    break;
                                case 5:
                                    icon.setImageDrawable(ratingInfoFragment.getResources().getDrawable(R.drawable.ic_rate_4));
                                    break;
                            }
                        }

                        if (((RatingInfoFragment) fragment).rates.size() > 0) {
                            fragment.getView().findViewById(R.id.status).setVisibility(View.GONE);
                            fragment.getView().findViewById(R.id.recycler).setVisibility(View.VISIBLE);
                        } else {
                            fragment.getView().findViewById(R.id.status).setVisibility(View.VISIBLE);
                            fragment.getView().findViewById(R.id.recycler).setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void VERIFY_ORDER(final SFSObject params) {
        final int code = params.getInt("code");
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BillFragment fragment = (BillFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), BillFragment.class.getName());
                if (fragment != null) {
                    fragment.hideProgress();

                    if (code == -86) {
                        fragment.messageVerifyCode.setVisibility(View.VISIBLE);
                        fragment.scrollView.smoothScrollTo(0, 0);
                        return;
                    }

                    if (code == 1) {
                        int type = fragment.getArguments().getInt("type");
                        if (type == fragment.FINISH) {
                            SmartFoxHelper.finish();
                        } else if (type == fragment.CANCEL) {
                            SmartFoxHelper.cancel(fragment.getArguments().getInt("order_id"));
                        }

                    }
                }
            }
        });
    }

    private void GET_ASSIGNED_PARTNER_ORDER(final SFSObject params) {
        final int code = params.getInt("code");
        if (code != 1) {
            return;
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof OrderPartnerFragment) {
                        final OrderPartnerFragment orderPartnerFragment = (OrderPartnerFragment) fragment;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<BeanOrderPartner> items = new ArrayList<>();
                                    SFSArray arr = (SFSArray) params.getSFSArray("data");
                                    for (int i = 0; i < arr.size(); i++) {
                                        BeanOrderPartner beanOrderPartner = (BeanOrderPartner) CmmFunc.tryParseJson(arr.getUtfString(i), BeanOrderPartner.class);
                                        items.add(beanOrderPartner);
                                    }
                                    orderPartnerFragment.onGetPartnerOrder(items);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                    if (fragment instanceof MapPartnerFragment) {
                        final MapPartnerFragment mapPartnerFragment = (MapPartnerFragment) fragment;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<BeanOrderPartner> items = new ArrayList<>();
                                    SFSArray arr = (SFSArray) params.getSFSArray("data");
                                    for (int i = 0; i < arr.size(); i++) {
                                        BeanOrderPartner beanOrderPartner = (BeanOrderPartner) CmmFunc.tryParseJson(arr.getUtfString(i), BeanOrderPartner.class);
                                        items.add(beanOrderPartner);
                                    }
                                    mapPartnerFragment.onGetPartnerOrder(items);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CHECK_FOLLOW_JOURNEY(final SFSObject params) {
        try {
            final int code = params.getInt("code");
            if (code != 1) {
                return;
            }
            SFSObject data = (SFSObject) params.getSFSObject("data");
            boolean status = data.getBool("status");
            if (status) {
                Intent intent = new Intent(GlobalClass.getActivity(), MainActivity.class);
                GlobalClass.getActivity().startActivity(intent);
                GlobalClass.getActivity().finish();

            } else {
                FragmentHelper.popRoot(GlobalClass.getActivity());
                FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GET_COD_ORDER(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof OrderCODFragment) {
                    OrderCODFragment orderCODFragment = (OrderCODFragment) fragment;
                    orderCODFragment.excuteData(params);
                }
            }
        });
    }

    private void GET_AROUND_PAY(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof AroundWalletFragment) {
                    AroundWalletFragment aroundWalletFragment = (AroundWalletFragment) fragment;
                    aroundWalletFragment.excutePayData(params);
                }
            }
        });
    }

    private void GET_AROUND_PAY_TRANSACTION(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof AroundWalletFragment) {
                    AroundWalletFragment aroundWalletFragment = (AroundWalletFragment) fragment;
                    aroundWalletFragment.excuteWalletData(params);
                }
            }
        });
    }
    //endregion

    //region Trường hợp disconnect
    //reconnect
    private void RECONNECT_JOURNEY(final SFSObject params) {
        try {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int code = params.getInt("code");
                    if (code != 1) {
                        FragmentHelper.popRoot(GlobalClass.getActivity());
                        FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
                        Bundle bundle = GlobalClass.getActivity().getIntent().getExtras();
                        if (bundle != null) {
                            new NotificationRouteHelper().route(GlobalClass.getActivity(), bundle);
                        }
                        return;
                    }

                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment == null) {
                        router(params);
                        return;
                    }
                    if (!CmmVariable.isFirstApp) {
                        return;
                    }
                    router(params);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void router(final SFSObject params) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BeanUser user = new BeanUser();
                    user.setFullName(params.getUtfString("user_fullname"));
                    user.setPhone(params.getUtfString("user_phone"));
                    user.setUsername(params.getUtfString("user_name"));
                    user.setAvatar(params.getUtfString("user_avatar"));
                    CmmVariable.room = params.getUtfString("room_name");
                    BeanUser.setCurrent(user);
                    CmmVariable.points.clear();
                    SFSArray locations = (SFSArray) params.getSFSArray("locations");
                    for (int i = 0; i < locations.size(); i++) {
                        Object item = locations.getElementAt(i);
                        BeanPoint point = (BeanPoint) CmmFunc.tryParseJson(item.toString(), BeanPoint.class);
                        CmmVariable.points.add(point);
                    }
                    CmmVariable.numberChat = params.getInt("new_chat_number");
                    FragmentHelper.popRoot(GlobalClass.getActivity());
                    FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, FollowJourneyFragment.newInstance(params.getInt("order_id")));
                    Bundle bundle = GlobalClass.getActivity().getIntent().getExtras();
                    if (bundle != null) {
                        new NotificationRouteHelper().route(GlobalClass.getActivity(), bundle);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //endregion


}
