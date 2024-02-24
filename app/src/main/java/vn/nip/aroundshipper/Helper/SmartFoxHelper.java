package vn.nip.aroundshipper.Helper;

import android.content.res.Resources;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import sfs2x.client.util.ConfigData;
import vn.nip.aroundshipper.Bean.BeanPoint;
import vn.nip.aroundshipper.Bean.BeanUser;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Class.SmartFoxListener;
import vn.nip.aroundshipper.Fragment.OrderConfirmFragment;
import vn.nip.aroundshipper.R;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LoginRequest;

/**
 * Created by viminh on 10/20/2016.
 */

public class SmartFoxHelper {

    //region Variables
    private static SmartFox sfsClient = new SmartFox(true);
    //endregion

    public static SmartFox getInstance() {
        if (sfsClient == null) {
            sfsClient = new SmartFox(false);
            initListener();
        }
        return sfsClient;
    }

    public static void resetInstance() {
        sfsClient = null;
    }
    //endregion

    //region Connect
    public static void connect(String ip, String port) {
        sfsClient = new SmartFox(false);
        if (port.length() > 0) {

            int serverPortValue = Integer.parseInt(port);
            sfsClient.connect(ip, serverPortValue);

        } else {
            sfsClient.connect(ip);
        }
    }
    //endregion

    //region Init
    static SmartFoxListener smartfoxListener = new SmartFoxListener() {
    };

    static SmartFoxListener getSmartFoxListener() {
        if (smartfoxListener == null) {
            smartfoxListener = new SmartFoxListener() {
            };
        }
        return smartfoxListener;
    }

    public static void initSmartFox() {
        sfsClient = new SmartFox();
        initListener();
        ConfigData configData = new ConfigData();
        configData.setHost(CmmVariable.smartFoxDomain);
        configData.setDebug(true);
        configData.setPort(Integer.parseInt(CmmVariable.getSmartFoxPort));
        configData.setZone("AroundAppZone");
        configData.setUseBBox(false);
        sfsClient.connect(configData);

    }

    private static void initListener() {
        sfsClient.removeAllEventListeners();
        // Add event listeners
        sfsClient.addEventListener(SFSEvent.CONNECTION, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().connection(baseEvent);
            }
        });

        sfsClient.addEventListener(SFSEvent.CONFIG_LOAD_FAILURE, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().connection(baseEvent);
            }
        });

        sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().connectionLost(baseEvent);

            }
        });
        sfsClient.addEventListener(SFSEvent.LOGOUT, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().logout(baseEvent);

            }
        });
        sfsClient.addEventListener(SFSEvent.LOGIN, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().login(baseEvent);

            }
        });
        sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().loginError(baseEvent);
            }
        });

        sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().publishMessage(baseEvent);
            }
        });
        sfsClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, new IEventListener() {
            @Override
            public void dispatch(final BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().extensionResponse(baseEvent);

            }
        });

        sfsClient.addEventListener(SFSEvent.ROOM_JOIN_ERROR, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().roomJoinError(baseEvent);
            }
        });
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                getSmartFoxListener().roomJoin(baseEvent);
            }
        });
    }


    //region Request shipper
    static void requestShipper(final SFSObject sfsObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanUser user = new BeanUser();
                    user.setFullName(sfsObject.getUtfString("user_name"));
                    user.setPhone(sfsObject.getUtfString("user_phone")); //user_name
                    user.setUsername(sfsObject.getUtfString("user_name"));
                    CmmVariable.room = sfsObject.getUtfString("room_name");
                    BeanUser.setCurrent(user);
                    SFSArray locations = (SFSArray) sfsObject.getSFSArray("locations");
                    for (int i = 0; i < locations.size(); i++) {
                        Object item = locations.getElementAt(i);
                        BeanPoint point = (BeanPoint) CmmFunc.tryParseJson(item.toString(), BeanPoint.class);
                        CmmVariable.points.add(point);
                    }
                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, new OrderConfirmFragment());
                } catch (Exception e) {
                    CmmFunc.showError("SmartfoxHelper", "requestShipper", e.getMessage());
                }
            }
        }).start();
    }
    //endregion

    private static void updatePosition() {
        //Cập nhật lại vị trí khi Login thành công
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanPoint bean = MapHelper.getCurrentPosition(GlobalClass.getActivity());
                    LatLng latLng = bean.getLatLng();
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "UPDATE_POSITION");
                    sfsObject.putDouble("latitude", latLng.latitude);
                    sfsObject.putDouble("longitude", latLng.longitude);
                    sfsClient.send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    updatePosition();
                }
            }
        }).start();
    }
    //endregion

    //region Login
    public static void ActionLogin(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ISFSObject sObject = new SFSObject();
                sObject.putInt("type", 1);
                sObject.putUtfString("deviceid", CmmFunc.getDeviceID(GlobalClass.getActivity()));
                sObject.putUtfString("devicetoken", FirebaseInstanceId.getInstance().getToken()); //FirebaseInstanceId.getInstance().getToken()
                sObject.putUtfString("os", "ANDROID");
                sObject.putUtfString("version", CmmVariable.version);
                sObject.putUtfString("os_version", "ANDROID" + "|" + Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.VERSION.SDK_INT + "|" + Build.VERSION.RELEASE);
                String pass = CmmFunc.encryptMD5(password);
                SmartFoxHelper.getInstance().send(new LoginRequest(name, pass, "AroundAppZone", sObject));
            }
        }).start();
    }
    //endregion

    //region Response shipper
    public static void responseShipper(final int status, final String idRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "RESPONSE_SHIPPER");
                    sfsObject.putInt("status", status);
                    sfsObject.putUtfString("id_request", idRequest);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
//                    if(status == 0){
//                        BeanUser.setCurrent(null);
//                    }
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "responseShipper", e.getMessage());
                }
            }
        }).start();
    }
    //endregion

    //region Receipt
    public static void getReceipt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_RECEIPT");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "getReceipt", e.getMessage());
                }
            }
        }).start();
    }
    //endregion

    //region Rating info
    public static void getRatingInfo(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_RATING_HISTORY");
                    sfsObject.putInt("page", page);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "getRatingInfo", e.getMessage());
                }
            }
        }).start();
    }
    //endregion

    //region Rating info
    public static void updateDeviceToken(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "UPDATE_DEVICE_TOKEN");
                    sfsObject.putUtfString("devicetoken", token);
                    sfsObject.putUtfString("firebase_devicetoken", token);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {

                }
            }
        }).start();
    }
    //endregion

    //region Receive request shipper
    public static void receiveRequestShipper(final String idRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "RECEIVE_REQUEST_SHIPPER");
                    sfsObject.putUtfString("id_request", idRequest);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {

                }
            }
        }).start();
    }
    //endregion

    //region Receive request shipper
    public static void receiveShopShipper(final int idLocation, final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "RECEIVE_SHOP_PRODUCT");
                    sfsObject.putInt("id_location", idLocation);
                    sfsObject.putInt("status", status);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void getFindingShipperInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_FINDING_SHIPPER_INFO");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void verifyCode(final int orderID, final int type, final String note, final byte[] image, final String verifyCode, final SFSObject location, final SFSArray bills) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "VERIFY_ORDER");
                    sfsObject.putInt("id_order", orderID);
                    sfsObject.putInt("type", type);
                    if (image != null) {
                        sfsObject.putByteArray("image", image);
                    }
                    sfsObject.putUtfString("note", note);
                    if (!verifyCode.equals("")) {
                        sfsObject.putUtfString("verify_code", verifyCode);
                    }
                    sfsObject.putSFSObject("location", location);
                    if (bills != null) {
                        sfsObject.putSFSArray("bills", bills);
                    }
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void getProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_PROFILE");
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }

    public static void finish() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "FINISH_DELIVERY");
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }


    public static void cancel(final int orderId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "CANCEL_ORDER");
                sfsObject.putInt("id_order", orderId);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }

    public static void start(final int orderId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "START_SCHEDULE_ORDER");
                sfsObject.putInt("id_order", orderId);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }
    //endregion

    //region Order partner
    public static void getPartnerOrder(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_ASSIGNED_PARTNER_ORDER");
                    sfsObject.putUtfString("type", type);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();
    }
    //endregion

    //region Follow
    public static void checkFollowJourney() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "CHECK_FOLLOW_JOURNEY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();
    }
    //endregion

    //region get Full Order
    public static void getFullOrder(final int orderID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_FULL_ORDER");
                sfsObject.putInt("id_order", orderID);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }
    //endregion

    //region get COD order
    public static Thread getCODOrder(final String type, final int page, final String start_date, final String end_date) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_COD_ORDER");
                    sfsObject.putUtfString("type", type);
                    sfsObject.putInt("page", page);
                    sfsObject.putUtfString("start_date", start_date);
                    sfsObject.putUtfString("end_date", end_date);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //endregion

    //region get history order
    public static void getHistoryOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_ORDER_HISTORY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getAroudPay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_AROUND_PAY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //region Around wallet
    public static void getAroundWallet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_AROUND_PAY_TRANSACTION");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
