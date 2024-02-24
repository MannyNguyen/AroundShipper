package vn.nip.aroundshipper.Bean;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.R;

/**
 * Created by viminh on 11/25/2016.
 */

public class BeanOrderHistory {
    private int id;
    private String create_date;
    private int status;
    private int total;
    private int[] order_type;
    private String order_code;
    private String type;
    private int is_order_of_partner;
    private String partner_fullname;
    private String address;
    private String recipent_name;

    private List<BeanPoint> locations;

    public static boolean isGift(BeanOrderHistory bean) {
        for (int i = 0; i < bean.getOrder_type().length; i++) {
            if (bean.getOrder_type()[i] == 0) {
                return true;
            }
        }
        return false;
    }

    public static String getStringType(BeanOrderHistory beanOrder) {
        String retValue = StringUtils.EMPTY;
        List<Integer> repairOrderType = new ArrayList();
        for (int orderType : beanOrder.getOrder_type()) {
            if (repairOrderType.contains(orderType)) {
                continue;
            }
            repairOrderType.add(orderType);
            switch (orderType) {
                //GIFTING
                case 0:
                    retValue += GlobalClass.getActivity().getString(R.string.shopping_gifting) + " - ";
                    break;
                //TRANSPORT
                case 1:
                    retValue += GlobalClass.getActivity().getString(R.string.transport) + " - ";
                    break;
                //PURCHASE
                case 2:
                    retValue += GlobalClass.getActivity().getString(R.string.purchase) + " - ";
                    break;
                //COD
                case 3:
                    retValue += GlobalClass.getActivity().getString(R.string.collect) + " - ";
                    break;
            }
        }
        if (retValue.length() > 3) {
            retValue = retValue.substring(0, retValue.length() - 3);
        }
        return retValue;
    }

//    public static String getStringType(BeanOrderHistory bean) {
//        String retValue = "";
//        for (int i = 0; i < bean.getOrder_type().length; i++) {
//            switch (bean.getOrder_type()[i]) {
//                case 0:
//                   // retValue += " - " + GlobalClass.getActivity().getString(R.string.gifting);
//                    retValue=GlobalClass.getActivity().getString(R.string.shopping_gifting);
//                    break;
//                case 1:
//                  //  retValue += " - " + GlobalClass.getActivity().getString(R.string.transport);
//                    retValue=GlobalClass.getActivity().getString(R.string.transport);
//                    break;
//                case 2:
////                    retValue += " - " + GlobalClass.getActivity().getString(R.string.purchase);
//                    retValue=GlobalClass.getActivity().getString(R.string.purchase);
//                    break;
//                case 3:
////                    retValue += " - " + GlobalClass.getActivity().getString(R.string.collect);
//                    retValue=GlobalClass.getActivity().getString(R.string.collect);
//                    break;
//            }
//        }
//      //  return retValue.substring(2, retValue.length()).trim();
//        return retValue;
//    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int[] getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int[] order_type) {
        this.order_type = order_type;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BeanPoint> getLocations() {
        return locations;
    }

    public void setLocations(List<BeanPoint> locations) {
        this.locations = locations;
    }

    public int getIs_order_of_partner() {
        return is_order_of_partner;
    }

    public void setIs_order_of_partner(int is_order_of_partner) {
        this.is_order_of_partner = is_order_of_partner;
    }

    public String getPartner_fullname() {
        return partner_fullname;
    }

    public void setPartner_fullname(String partner_fullname) {
        this.partner_fullname = partner_fullname;
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

