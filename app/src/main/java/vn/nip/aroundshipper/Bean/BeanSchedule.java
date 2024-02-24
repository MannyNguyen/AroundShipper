package vn.nip.aroundshipper.Bean;

import java.util.List;

import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.R;

/**
 * Created by viminh on 11/25/2016.
 */

public class BeanSchedule {
    private int id;
    private Double distance;
    private int total;
    private Double duration;
    private String create_date;
    private int[] order_type;
    private String order_code;
    private String type;
    private boolean is_show_start;
    private String address;
    private String recipent_name;

    public static String getStringType(BeanSchedule bean) {
        String retValue = "";
        for (int i = 0; i < bean.getOrder_type().length; i++) {
            switch (bean.getOrder_type()[i]) {
                case 0:
                    retValue += " - " + GlobalClass.getActivity().getString(R.string.gifting);
                    break;
                case 1:
                    retValue += " - " + GlobalClass.getActivity().getString(R.string.transport);
                    break;
                case 2:
                    retValue += " - " + GlobalClass.getActivity().getString(R.string.purchase);
                    break;
                case 3:
                    retValue += " - " + GlobalClass.getActivity().getString(R.string.collect);
                    break;
            }
        }
        return retValue.substring(2, retValue.length()).trim();
    }


    public static BeanSchedule getByID(int id, List<BeanSchedule> list) {
        for (BeanSchedule item : list) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }


    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public boolean is_show_start() {
        return is_show_start;
    }

    public void setIs_show_start(boolean is_show_start) {
        this.is_show_start = is_show_start;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int[] getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int[] order_type) {
        this.order_type = order_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecipent_name() {
        return recipent_name;
    }

    public void setRecipent_name(String recipent_name) {
        this.recipent_name = recipent_name;
    }
}

