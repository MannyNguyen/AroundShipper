package vn.nip.aroundshipper.Bean;

/**
 * Created by viminh on 10/7/2016.
 */

public class BeanOrder {
    private int number_order;
    private String order_code;
    private String date;
    private String address;
    private long cost ;
    private int status;
    private int id_order;

    public BeanOrder(String order_code, String date, String address, long cost, int status) {
        this.order_code = order_code;
        this.date = date;
        this.address = address;
        this.cost = cost;
        this.status = status;
    }

    public BeanOrder() {
    }

    public int getNumber_order() {
        return number_order;
    }

    public void setNumber_order(int number_order) {
        this.number_order = number_order;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }
}
