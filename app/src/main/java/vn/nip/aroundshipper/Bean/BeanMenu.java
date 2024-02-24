package vn.nip.aroundshipper.Bean;

/**
 * Created by viminh on 10/6/2016.
 */

public class BeanMenu extends BeanBase {

    private int icon;

    public BeanMenu(){

    }

    public BeanMenu(String id, String title, int icon){
        this.setId(id);
        this.setTitle(title);
        this.setIcon(icon);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
