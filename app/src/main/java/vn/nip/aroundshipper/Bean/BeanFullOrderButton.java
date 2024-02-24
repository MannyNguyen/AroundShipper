package vn.nip.aroundshipper.Bean;

public class BeanFullOrderButton extends BeanBase {
    String textLocation, id;

    public BeanFullOrderButton() {
    }

    public BeanFullOrderButton(String id,String textLocation) {
        this.id = id;
        this.textLocation = textLocation;
    }

    public String getTextLocation() {
        return textLocation;
    }

    public void setTextLocation(String textLocation) {
        this.textLocation = textLocation;
    }
}
