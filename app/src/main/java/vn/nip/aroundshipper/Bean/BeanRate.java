package vn.nip.aroundshipper.Bean;

/**
 * Created by viminh on 10/6/2016.
 */

public class BeanRate {
    private String id_order;
    private String create_date;
    private int rating;

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
