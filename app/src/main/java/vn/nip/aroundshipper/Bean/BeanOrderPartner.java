package vn.nip.aroundshipper.Bean;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.util.internal.StringUtil;

import vn.nip.aroundshipper.R;

/**
 * Created by HOME on 1/25/2018.
 */

public class BeanOrderPartner {
    public static final int ACTION_DONE = 2;
    public static final int ACTION_RUN = 1;

    public static final int TYPE_GIFT = 0;
    public static final int TYPE_TRANSPORT = 1;
    public static final int TYPE_PURCHASE = 2;
    public static final int TYPE_COD = 3;


    private int id;
    private String order_code;
    private int total;
    private String date;
    private int order_type;
    private String address;
    private double latitude;
    private double longitude;
    private String phone;
    private int action;

    public static int getNameIDByType(int type) {
        switch (type) {
            case TYPE_TRANSPORT:
                return R.string.transport;
            case TYPE_GIFT:
                return R.string.gifting;
            case TYPE_PURCHASE:
                return R.string.purchase;
            case TYPE_COD:
                return R.string.collect;
        }
        return 0;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
