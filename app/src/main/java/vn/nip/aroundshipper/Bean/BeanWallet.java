package vn.nip.aroundshipper.Bean;

public class BeanWallet {
    private int id;
    private String title;
    private String vn_title;
    private String description;
    private String vn_description;
    private String create_date;

    public BeanWallet(int id, String title, String vn_title, String description, String vn_description, String create_date) {
        this.id = id;
        this.title = title;
        this.vn_title = vn_title;
        this.description = description;
        this.vn_description = vn_description;
        this.create_date = create_date;
    }

    public BeanWallet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVn_title() {
        return vn_title;
    }

    public void setVn_title(String vn_title) {
        this.vn_title = vn_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVn_description() {
        return vn_description;
    }

    public void setVn_description(String vn_description) {
        this.vn_description = vn_description;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }
}
